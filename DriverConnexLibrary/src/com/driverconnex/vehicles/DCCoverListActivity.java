package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListDCCoverAdapter;
import com.driverconnex.app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for displaying list of covers.
 * 
 * @author Adrian Klimczak
 * 
 */

public class DCCoverListActivity extends Activity 
{
	private ArrayList<DCCover> covers;
	private ListView list;
	private ListDCCoverAdapter adapter;
	
	private RelativeLayout loading;
	private RelativeLayout bottomActionBar;
	private RelativeLayout listLayout;
	private ImageButton addCoverBtn;
	
	private DCVehicle vehicle;
    private ParseObject vehicleParseObject;  
    private ParseObject coverParseObject;  
    
    private boolean isModify = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_cover_list);
		
		list = (ListView) findViewById(R.id.list);
		
		listLayout = (RelativeLayout) findViewById(R.id.listLayout);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		bottomActionBar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		
		addCoverBtn = (ImageButton) findViewById(R.id.addCoverBtn);		
		addCoverBtn.setOnClickListener(onClickListener);
		
		covers = new ArrayList<DCCover>();
		
		if (getIntent().getExtras() != null) 
		{
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable("vehicle");
		}
		
		getActionBar().setSubtitle(R.string.vehicle_select_policy);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		if (covers.size() != 0) 
			covers.clear();
		
		// Hide list, there is nothing there yet
		listLayout.setVisibility(View.INVISIBLE);
		
		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		
		// Get insurance cover from the parse
		getInsuranceCoverByParse();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			intent.putExtra("vehicle", vehicle);
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if(item.getItemId() == R.id.action_edit)
		{
			Animation anim = null;
			
			// If bottom action bar is not shown, then show it, otherwise hide it
			if(!bottomActionBar.isShown())
			{
				bottomActionBar.setVisibility(View.VISIBLE);
				anim = AnimationUtils.loadAnimation(this, R.anim.bottom_actionbar_show);
				isModify = true;
			}
			else
			{
				bottomActionBar.setVisibility(View.INVISIBLE);
				anim = AnimationUtils.loadAnimation(this, R.anim.bottom_actionbar_hide);	
				isModify = false;
			}
			
			bottomActionBar.startAnimation(anim);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Sets listener for views from bottom action bar
	 */
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == addCoverBtn) 
			{
				Intent intent = new Intent(DCCoverListActivity.this, AddNewDCCoverActivity.class);
				startActivity(intent);			
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
		}
	};
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{	
			vehicle.setInsurance(covers.get(position).getExpiryDate());
			
			// Check if user is not in editing mode
			if(!isModify)
			{
				// Position of the clicked item
				final int itemPosition = position;
				
				// Display message
				new AlertDialog.Builder(DCCoverListActivity.this)
				.setTitle("Change Policy")
				.setMessage(R.string.vehicle_change_policy)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,	int which) 
					{	
						// Display loading
						loading.setVisibility(View.VISIBLE);
						
						// Get current logged in user
						final ParseUser user = ParseUser.getCurrentUser();
						
						// Query for the vehicle
						ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
						query.findInBackground(new FindCallback<ParseObject>() 
						{
							@Override
							public void done(List<ParseObject> vehicleList, com.parse.ParseException e) 
							{
						        if (e == null)
						        {
						        	// Loop through all vehicles we got
						        	for(int i=0; i<vehicleList.size(); i++)
						        	{
						        		// Check if this is vehicle that user wants to put cover on
						        		if(vehicleList.get(i).getObjectId().equals(vehicle.getId()))
						        		{
						        			vehicleParseObject = vehicleList.get(i);
						        			break;
						        		}
						        	}
						        	
						        	// Query for the cover object
						        	ParseQuery<ParseObject> query = ParseQuery.getQuery("DCCover");
									query.findInBackground(new FindCallback<ParseObject>() 
									{
										@Override
										public void done(List<ParseObject> coverList, com.parse.ParseException e) 
										{
									        if (e == null)
									        {
									        	// Loop through all covers
									        	for(int i=0; i<coverList.size(); i++)
									        	{
									        		// Check if this is cover corresponding to the one user clicked
									        		if(coverList.get(i).getObjectId().equals
									        				(covers.get(itemPosition).getId()))
									        		{
									        			coverParseObject = coverList.get(i);
									        			break;
									        		}
									        	}
									        	
									        	// Give a new cover to vehicle
									        	vehicleParseObject.put("vehicleCover", coverParseObject);
									        	
												// Save the object and put it into Parse database
												try 
												{
													vehicleParseObject.save();
													user.save();
												} catch (com.parse.ParseException e1) {
													e1.printStackTrace();
												} 
												
												// Uncheck every box
												for(int i=0; i<adapter.getCheckBoxes().size(); i++)
													adapter.getCheckBoxes().get(i).setChecked(false);
												
												// Set a new box to be checked
												adapter.getCheckBoxes().get(itemPosition).setChecked(true);
												
												// Hide loading
												loading.setVisibility(View.INVISIBLE);
									        }
									        else 
									            Log.d("Get Vehicle", "Error: " + e.getMessage());
										}
									});
						        }
						        else 
						            Log.d("Get Vehicle", "Error: " + e.getMessage());
							}
						});
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,	int which) 
					{
					}
				}).show();
			}
			// User is in editing mode
			else
			{
				Intent intent = new Intent(DCCoverListActivity.this, AddNewDCCoverActivity.class);
				
				Bundle extras = new Bundle();
				extras.putSerializable("cover", covers.get(position));
				
				intent.putExtras(extras);
				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
		}
		
	};
	
	/**
	 * Gets list of insurance covers from the parse database
	 */
	private void getInsuranceCoverByParse() 
	{
		// Query for the vehicle
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			@Override
			public void done(List<ParseObject> vehicleList, com.parse.ParseException e) 
			{
		        if (e == null)
		        {
		        	// Loop through all vehicles we got
		        	for(int i=0; i<vehicleList.size(); i++)
		        	{
		        		// Check if this is vehicle that user wants to put cover on
		        		if(vehicleList.get(i).getObjectId().equals(vehicle.getId()))
		        		{
		        			vehicleParseObject = vehicleList.get(i);
		        			break;
		        		}
		        	}
		        	
		        	// Query for the cover object
		        	ParseQuery<ParseObject> query = ParseQuery.getQuery("DCCover");
					query.findInBackground(new FindCallback<ParseObject>() 
					{
						@Override
						public void done(List<ParseObject> coverList, com.parse.ParseException e) 
						{
					        if (e == null)
					        {		
					        	int defaultCoverIndex = -1;
					        	
					        	// Loop through all covers
					        	for(int i=0; i<coverList.size(); i++)
					        	{
					        		if(vehicleParseObject.has("vehicleCover"))
					        		{
					        			// Check if this is cover corresponding to the one user clicked
						        		if(vehicleParseObject.getParseObject("vehicleCover").getObjectId()
						        				.matches(coverList.get(i).getObjectId()))
						        		{
						        			// Set the index of this cover
						        			defaultCoverIndex = i;
						        		}
					        		}
					        		
					        		DCCover cover = new DCCover();
					        		
					        		// Get the info from database
									cover.setId(coverList.get(i).getObjectId());								
									cover.setExpiryDate(coverList.get(i).getDate("coverExpiryDate"));
									cover.setPolicyProvider(coverList.get(i).getString("coverProvider"));
									cover.setCoverBreakdown(coverList.get(i).getBoolean("coverBreakdown"));
									cover.setAnualCost(coverList.get(i).getInt("coverAnnualCost"));
									
									// Get photo from the parse
									ParseFile photo = (ParseFile) coverList.get(i).get("coverDocumentPhoto");
									byte[] data = null;
									
									try 
									{
										if (photo != null) 
											data = photo.getData();
									} 
									catch (ParseException e1) {
										e1.printStackTrace();
									}
									
									if (data != null) 
									{
										cover.setPhotoSrc(data);
									}
									
									covers.add(cover);
					        	}
							
								// Set adapter
					        	// Pass default cover index that will be used to tick the correct box
					        	// in checked box
								adapter = new ListDCCoverAdapter(DCCoverListActivity.this, 
										covers, defaultCoverIndex);
								list.setAdapter(adapter);
								list.setOnItemClickListener(itemClickListener);
								
								// Disable loading bar
								loading.setVisibility(View.GONE);
								
								// If there is any item on the cover list
								if(covers.size() > 0)
									listLayout.setVisibility(View.VISIBLE);
					        }
					        else 
					            Log.d("Get Cover", "Error: " + e.getMessage());
						}
					});
		        }
		        else 
		            Log.d("Get Vehicle", "Error: " + e.getMessage());
			}
		});
	}
}
