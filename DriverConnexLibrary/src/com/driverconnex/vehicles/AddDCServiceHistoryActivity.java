package com.driverconnex.vehicles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ParseUtilities;
import com.driverconnex.utilities.Utilities;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * This activity is used to add a new DC Service History item to the Parse database or edit existing one, that
 * was selected from ServiceHistoryListActivity
 * @author Adrian Klimczak
 *
 */

public class AddDCServiceHistoryActivity extends Activity 
{
	private final int CAPTURE_IMAGE_REQUEST_CODE = 90;
	private final int SELECT_SERVICE_ITEM_REQUEST_CODE = 100;
	
	private TextView typeEdit;
	private static TextView dateEdit;
	private EditText costEdit;
	private EditText mileageEdit;
	private ListView addBtn;
	private ImageView photoEdit;

	private RelativeLayout loading;
	
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	private ListAdapter adapter;
	
	private ArrayList<DCServiceItem> serviceItems;   
	
	private String serviceHistoryId;          // ID of the selected DC Service History item from the list
	private String vehicleId;                 // ID of the selected vehicle
	private boolean isModify = false;         // Indicates if user is modifying item
	
	private ParseObject serviceHistoryParse;  // Stores reference of the DCServiceHistory item that is being updated by the user
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_add_new_service);

		// Get views
		//------------------------------------------------------
		typeEdit = (TextView) findViewById(R.id.typeEdit);
		dateEdit = (TextView) findViewById(R.id.dateEdit);
		costEdit = (EditText) findViewById(R.id.costEdit);
		mileageEdit = (EditText) findViewById(R.id.mileageEdit);
		addBtn = (ListView) findViewById(R.id.addBtn);
		
		photoEdit = (ImageView) findViewById(R.id.imageView);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		dateEdit.setOnClickListener(onClickListener);
		typeEdit.setOnClickListener(onClickListener);
		costEdit.setOnClickListener(onClickListener);
		mileageEdit.setOnClickListener(onClickListener);
		photoEdit.setOnClickListener(onClickListener);
		
		dateEdit.setOnFocusChangeListener(onFocusChangeListener);
		typeEdit.setOnFocusChangeListener(onFocusChangeListener);
		costEdit.setOnFocusChangeListener(onFocusChangeListener);
		mileageEdit.setOnFocusChangeListener(onFocusChangeListener);
		
		costEdit.setOnEditorActionListener(onEditorActionListener);
		mileageEdit.setOnEditorActionListener(onEditorActionListener);
		//------------------------------------------------------
		
		// Set add items button
		//------------------------------------------------------
		ListAdapterItem item = new ListAdapterItem();
		
		item.title = getResources().getString(R.string.vehicle_service_add_item);
		item.subtitle = "0 Items added";
		
		adapterData.add(item);
		
		adapter = new ListAdapter(this, adapterData);
		addBtn.setAdapter(adapter);
		addBtn.setOnItemClickListener(onItemClickListener);
		//------------------------------------------------------
		
		Bundle extras = getIntent().getExtras();
		
		// Get extras from previous activity
		if (extras != null) 
		{
			// We got some extras that means user wants to modify existing item
			isModify = extras.getBoolean("isModify");
			// Get object ID of the selected Service History item
			serviceHistoryId = extras.getString("objectId");
			// Get object ID of the selected vehicle
			vehicleId = extras.getString("vehicleId");
			
			if(isModify)
			{
				loading.setVisibility(View.VISIBLE);
				
				AssetsUtilities.cleanTempMediaFile();
				getServiceHistoryByParse();
			}
		}	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) 
		{
			if (resultCode == RESULT_OK) 
			{
				if (data != null) 
				{
					Bitmap bmp = null;
					
					int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

					bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmapFromUriInPortraitMode
							(AddDCServiceHistoryActivity.this, data.getData(), true), screenHeight);
					
					if(bmp != null)
						photoEdit.setImageBitmap(bmp);
				} 
			}
		}
		else if(requestCode == SELECT_SERVICE_ITEM_REQUEST_CODE)
		{
			if(resultCode == RESULT_OK)
			{
				if(data != null)
				{
					ArrayList<DCServiceItem> items = data.getParcelableArrayListExtra("selectedItems");
					serviceItems = items;
					adapterData.get(0).subtitle = items.size() + " Items added"; 
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		
		if(isModify)
			inflater.inflate(R.menu.add_new_service_history, menu);
		else
			inflater.inflate(R.menu.action_save, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_save) 
		{
			// User can't save without adding any item.
			if(serviceItems == null)
			{
				new AlertDialog.Builder(AddDCServiceHistoryActivity.this)
				.setTitle("Error")
				.setMessage(getResources().getString(R.string.vehicle_service_error))
				.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,	int which) 
					{
					}
				}).show();
			}
			// User added some items but still he hasn't completed all fields
			else if (typeEdit.getText().length() == 0 || dateEdit.getText().length() == 0 
					|| costEdit.getText().length() == 0 || mileageEdit.getText().length() == 0) 
			{
				new AlertDialog.Builder(AddDCServiceHistoryActivity.this)
						.setTitle("Error")
						.setMessage(getResources().getString(R.string.vehicle_insurance_error))
						.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog,	int which) 
							{
							}		
						}).show();
			} 
			// Otherwise user can save service history item
			else
			{
				// Save new DCServiceHistory object into Parse 
				new SaveTask().execute();
			}
		}
		else if (item.getItemId() == R.id.action_delete)
		{
			new AlertDialog.Builder(AddDCServiceHistoryActivity.this)
			.setTitle("Delete Item")
			.setMessage("Are you sure to delete this item?")
			.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,	int which) 
				{
					loading.setVisibility(View.VISIBLE);
		
					serviceHistoryParse.deleteInBackground(new DeleteCallback()
					{
						@Override
						public void done(ParseException e) 
						{
							loading.setVisibility(View.GONE);
							finish();
							overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
						}	
					});							
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			}).show();		
		}
		
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			startEditingText(costEdit, false);
			startEditingText(mileageEdit, false);
			
			Intent intent = new Intent(AddDCServiceHistoryActivity.this, SelectServiceItemActivity.class);
			
			// If user is editing service history, then we will get some service items 
			if(serviceItems != null)
			{
				Bundle extras = new Bundle();
				extras.putSerializable("serviceItems", serviceItems);
				intent.putExtras(extras);
			}
			
			startActivityForResult(intent, SELECT_SERVICE_ITEM_REQUEST_CODE);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
		}
	};

	/**
	 * Listener for the views
	 */
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == photoEdit) 
			{
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,CAPTURE_IMAGE_REQUEST_CODE);
			}
			else if(v == mileageEdit || v == costEdit)
			{
				startEditingText((EditText) v, true);
			}
		}
	};

	/**
	 * Somehow OnClickListener doesn't work very well with EditText, it takes two clicks to respond,
	 * so instead OnFocusChangeListener is used
	 */
	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() 
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			if (v == dateEdit) 
			{
				if (hasFocus) 
				{
					DialogFragment newFragment = new DatePickerFragment();
					newFragment.show(getFragmentManager(), "datePicker");
				}
			}
			else if(v == typeEdit)
			{
				if(hasFocus)
				{
					Builder builder = new AlertDialog.Builder(AddDCServiceHistoryActivity.this);
					builder.setTitle(R.string.vehicle_title_service_type).setItems(R.array.vehicle_service_type, new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							typeEdit.setText(getResources().getStringArray(R.array.vehicle_service_type)[which]);
						}
					});
					
					builder.create()
						   .show();
				}
			}
			else if(v == mileageEdit || v == costEdit)
			{
				if(hasFocus)
					startEditingText((EditText) v, true);
				else
					startEditingText((EditText) v, false);			
			}
		}
	};
	
	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) 
		{
			if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
					|| (actionId == EditorInfo.IME_ACTION_DONE)) 
			{
				startEditingText((EditText) v, false);
			}			
			return false;
		}
	};
	
	/**
	 * Gets service history item that was selected by the user in ServiceHistoryListActivity
	 */
	private void getServiceHistoryByParse() 
	{
		// Query for the DC service History
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCServiceHistory");
		query.getInBackground(serviceHistoryId, new GetCallback<ParseObject>() 
		{
			public void done(final ParseObject object, ParseException e) 
			{
				if (e == null) 
				{		
					serviceHistoryParse = object;
					
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					Date parseDate = object.getDate("serviceDate");
					String date = format.format(parseDate);
		        			
					typeEdit.setText(object.getString("serviceType"));
					dateEdit.setText(date);
					costEdit.setText("£" + object.getNumber("serviceCost"));
					mileageEdit.setText("" + object.getLong("serviceMileage") + " Miles");
		        			
					ParseRelation<ParseObject> relation = object.getRelation("serviceItems");
		        			
					// Query for the DC Service items
					ParseQuery<ParseObject> query = relation.getQuery();
					query.findInBackground(new FindCallback<ParseObject>() 
					{
						@Override
						public void done(List<ParseObject> parseItems, com.parse.ParseException e) 
						{
							if(e == null)
							{
								adapterData.get(0).subtitle = parseItems.size() + " Items added";
								adapter.notifyDataSetChanged();
								
								serviceItems = new ArrayList<DCServiceItem> ();
		        						
								for(int i=0; i<parseItems.size(); i++)
									serviceItems.add(ParseUtilities.convertServiceItem(parseItems.get(i)));
								
								// We got all the items, now get a photo if there is one
								//-------------------------
								// Get a photo file
								Bitmap bmp = null;
								try 
								{
									ParseFile photo = (ParseFile) object.get("serviceInvoicePhoto");
									byte[] bytes = photo.getData();

									if (bytes != null) 
									{
										int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
										bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmap(bytes), screenHeight);
									}
									
								} catch (Exception e1) {
									e1.printStackTrace();
								}

								if(bmp != null)
									photoEdit.setImageBitmap(bmp);

								loading.setVisibility(View.GONE);
							}
						}
					});
		        }
		        else 
		        	Log.d("Get Service History", "Error: " + e.getMessage());
			}
		});
	}
	
	/**
	 * Saves DCServiceHistory item into Parse database
	 *
	 */
	private class SaveTask extends AsyncTask<String, Void, String> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) 
		{
			updateServiceHistory(isModify);
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			
			loading.setVisibility(View.INVISIBLE);
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
		}
	}
	
	/**
	 * Adds a new service history item or updates existing one.
	 * @param addNewItem 
	 * true - updates existing item 
	 * false - adds a new item 
	 */
	private void updateServiceHistory(boolean update)
	{
		ParseObject serviceHistoryItem = null;
		
		if(update)
		{
			// If user is updating the DCServiceHistory item then we got the reference to it.
			serviceHistoryItem = serviceHistoryParse;
		}
		else
		{
			// Create a new DCServiceHistory object
			serviceHistoryItem = new ParseObject("DCServiceHistory");
		}
		
		// If for some reason we didn't get a DCServiceHistory object then it shouldn't go any further
		if(serviceHistoryItem == null)
			return;
		
		serviceHistoryItem.put("serviceType", typeEdit.getText().toString());
		
		// Convert date into format that Parse will read
		final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		
		try {
			date = format.parse(dateEdit.getText().toString());
		} catch (java.text.ParseException e1) {
			e1.printStackTrace();
		}
		
		String cost = Utilities.getFirstNumberDecimalFromText(costEdit.getText().toString());
		String mileage = Utilities.getFirstNumberDecimalFromText(mileageEdit.getText().toString());
		
		serviceHistoryItem.put("serviceDate", date);
		serviceHistoryItem.put("serviceCost", Integer.parseInt(cost));
		serviceHistoryItem.put("serviceMileage", Long.parseLong(mileage));
		
		Bitmap bmp = null;
		
		if(photoEdit.getDrawable() != null)
			bmp = ((BitmapDrawable)photoEdit.getDrawable()).getBitmap();
		
		// Check if there is a photo 
		if(bmp != null)
		{
			byte[] data = AssetsUtilities.getBytesFromBitmap(bmp);
			
			// Create ParseFile for the photo
			ParseFile file = new ParseFile("invoice.png", data);
			serviceHistoryItem.put("serviceInvoicePhoto", file);
			
			try 
			{
				file.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		// If user is creating a new DCServiceHistory item then it has to be saved before going any further.
		if(!update)
		{
			try {
				serviceHistoryItem.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		// Query for the DCServiceItem items
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCServiceItem");
		List<ParseObject> serviceList = null;
		try {
			serviceList = query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// If for some reason serviceList is null then stop here.
		if(serviceList == null)
			return;
		
		ParseRelation<ParseObject> relation = serviceHistoryItem.getRelation("serviceItems");
							
		// Loop trough all serviceItems
		for(int i=0; i<serviceList.size(); i++)
		{
			// Get the DCServiceItem that user selected			
			for(int j=0; j<serviceItems.size(); j++)
			{
				if(serviceList.get(i).getObjectId().equals(serviceItems.get(j).getId()))
					relation.add(serviceList.get(i));
			}
		}
		
		// Update DCServiceHistory item with new relation
		try {
			serviceHistoryItem.save();
			ParseUser.getCurrentUser().save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// If user is creating a new DCHistoryService item, then we need still do something else
		if(!update)
		{
			// Query for the selected vehicle
			ParseQuery<ParseObject> queryVehicle = ParseQuery.getQuery("DCVehicle");
			ParseObject vehicle = null;
			
			try {
				vehicle = queryVehicle.get(vehicleId);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(vehicle == null)
				return;
			
			// We got vehicle, now get relation
			ParseRelation<ParseObject> relationVehicle = vehicle.getRelation("vehicleServiceHistory");	
			// Add DCServiceHistory relation to the vehicle
			relationVehicle.add(serviceHistoryItem);
						
			// Save vehicle
			try {
				vehicle.save();
				ParseUser.getCurrentUser().save();
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener 
	{	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			dateEdit.setText(new StringBuilder().append(Utilities.intToTime(day)).append("-")
					.append(Utilities.intToTime(month + 1)).append("-")
					.append(year));	
		}
	}
	
	/**
	 * Handles what happens when text is being edited or if it stopped being edited.
	 * @param editText
	 * @param editing
	 */
	private void startEditingText(EditText editText, boolean editing)
	{	
		// Starts editing
		if(editing)
		{
			editText.setCursorVisible(true);			
			editText.setText(Utilities.getFirstNumberDecimalFromText(editText.getText().toString()));
		}
		// Stops editing
		else
		{
			editText.setCursorVisible(false);
			
			if(editText == mileageEdit)
			{
				if(!mileageEdit.getText().toString().contains("Miles") && !mileageEdit.getText().toString().isEmpty())
					mileageEdit.setText(mileageEdit.getText() + " Miles");
			}
			else if(editText == costEdit)
			{
				if(!costEdit.getText().toString().contains("£") && !costEdit.getText().toString().isEmpty())
					costEdit.setText("£" + costEdit.getText());
			}
		}
	}
}
