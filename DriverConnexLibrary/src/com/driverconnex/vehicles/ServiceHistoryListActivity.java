package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.utilities.ParseUtilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

/**
 * Activity for displaying list of covers
 * 
 * @author Created by Adrian Klimczak
 * 
 */

public class ServiceHistoryListActivity extends Activity 
{
	private final int SERVICE_HISTORY_REQUEST_CODE = 100;
	
	private ArrayList<DCServiceHistory> serviceHistoryArray = new ArrayList<DCServiceHistory>();
	private ListView list;
	private RelativeLayout listLayout;
	
	private ListAdapter adapter;
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	
	private RelativeLayout loading;	
	private DCVehicle vehicle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_margin);
		
		list = (ListView) findViewById(R.id.list);
		listLayout = (RelativeLayout) findViewById(R.id.listLayout);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		adapter = new ListAdapter(ServiceHistoryListActivity.this, adapterData, R.drawable.gear_grey_56x56);
		list.setAdapter(adapter);
		list.setOnItemClickListener(itemClickListener);
		
		if(getIntent().getExtras() != null)
		{
			// Get the id of the vehicle that was selected
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable("vehicle");
		}
	}
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Check which request we're responding to
		if (requestCode == this.SERVICE_HISTORY_REQUEST_CODE) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				int position;
				position = data.getIntExtra("position", -1);
				
				if(position != -1)
					adapterData.remove(position);
				
				adapter.notifyDataSetChanged();
			}
		}
	}*/
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if(!serviceHistoryArray.isEmpty())
			serviceHistoryArray.clear();
		
		if(!adapterData.isEmpty())
			adapterData.clear();
		
		// Enable loading bar
		loading.setVisibility(View.VISIBLE);
		
		// There are no items yet, so hide list
		listLayout.setVisibility(View.INVISIBLE);
		
		// Get objects from the parse
		getServiceHistoryByParse();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_add, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			Intent intent = new Intent();
			intent.putExtra("vehicle", vehicle);
			setResult(RESULT_OK, intent);
			
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if(item.getItemId() == R.id.action_add)
		{
			Intent intent = new Intent(ServiceHistoryListActivity.this, AddDCServiceHistoryActivity.class);
			intent.putExtra("vehicleId", vehicle.getId());
			startActivity(intent);			
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{	
			Intent intent = new Intent(ServiceHistoryListActivity.this, AddDCServiceHistoryActivity.class);
			
			Bundle extras = new Bundle();
			extras.putBoolean("isModify", true);
			extras.putString("objectId", serviceHistoryArray.get(position).getId());
			extras.putString("vehicleId", vehicle.getId());
			//extras.putInt("position", position);
			
			intent.putExtras(extras);
			
			//startActivityForResult(intent, SERVICE_HISTORY_REQUEST_CODE);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
		}
	};
	
	/**
	 * Gets list of insurance covers from the parse database
	 */
	private void getServiceHistoryByParse() 
	{
		// Query for the selected vehicle
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
		query.getInBackground(vehicle.getId(), new GetCallback<ParseObject>() 
		{
			public void done(ParseObject object, ParseException e) 
			{
				if (e == null) 
				{  	
					// We got a vehicle, so now get its service history
					final ParseObject parseObject = object;
		    	
					ParseRelation<ParseObject> relation = parseObject.getRelation("vehicleServiceHistory");
    			
					ParseQuery<ParseObject> query = relation.getQuery();
					query.findInBackground(new FindCallback<ParseObject>() 
					{
						@Override
						public void done(List<ParseObject> parseObjects, com.parse.ParseException e) 
						{
							if(e == null)
							{
								// Loop through all service history objects and convert them into readable format
								for(int i=0; i<parseObjects.size(); i++)
								{        		
									DCServiceHistory serviceHistory = ParseUtilities.convertServiceHistory(parseObjects.get(i));
									serviceHistoryArray.add(serviceHistory);
									
									ListAdapterItem service = new ListAdapterItem();
									service.title = serviceHistory.getType();
									service.subtitle = "£" + serviceHistory.getCost() + ", " + serviceHistory.getMileage() + " Miles, "
							        		+ serviceHistory.getDate();
									
									adapterData.add(service);
								}
								
								vehicle.setServiceHistory(serviceHistoryArray.size());

								adapter.notifyDataSetChanged();
								
								// Disable loading bar
								loading.setVisibility(View.GONE);
						
								if(serviceHistoryArray.size() > 0)
									listLayout.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}
		});
	}
}
