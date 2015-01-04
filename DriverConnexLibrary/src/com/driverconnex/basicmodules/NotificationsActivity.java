package com.driverconnex.basicmodules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.singletons.DCVehicleSingleton;
import com.driverconnex.utilities.ThemeUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for displaying a list of notifications, which are vehicle alerts and messages.
 * @author Adrian Klimczak
 *
 */

public class NotificationsActivity extends Activity 
{
	private ArrayList<HashMap<String, Object>> vehicleAlerts = new ArrayList<HashMap<String, Object>>();
	
	private ArrayList<ListAdapterItem> vehicleAlertsForAdapter = new ArrayList<ListAdapterItem>();
	private ArrayList<ListAdapterItem> messagesForAdapter = new ArrayList<ListAdapterItem>();
	
	private ListView list;
	private ListAdapter adapter;
	
	private LinearLayout messagesBtn;
	private LinearLayout vehicleAlertsBtn;
	
	private TextView messagesText;
	private TextView vehicleAlertsText;
	
	private ImageView messagesIcon;
	private ImageView vehicleAlertsIcon;
	
	private boolean displayVehicleAlerts = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		list = (ListView) findViewById(R.id.list);
		vehicleAlertsBtn = (LinearLayout) findViewById(R.id.vehicleAlertsButton);
		messagesBtn = (LinearLayout) findViewById(R.id.messagesButton);
		
		vehicleAlertsText = (TextView) findViewById(R.id.vehicleAlertsText);
		messagesText = (TextView) findViewById(R.id.messagesText);
		
		vehicleAlertsIcon = (ImageView) findViewById(R.id.vehicleAlertsIcon);
		messagesIcon = (ImageView) findViewById(R.id.messagesIcon);
		
		messagesBtn.setOnClickListener(onClickListener);
		vehicleAlertsBtn.setOnClickListener(onClickListener);	
		
		adapter = new ListAdapter(NotificationsActivity.this, messagesForAdapter, R.drawable.envelope,
				ThemeUtilities.getMainInterfaceColor(this));
		adapter.setTitleStyle(Typeface.BOLD);
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(itemClickListener);
		
		getMessagesByParse();
		getVehicleAlerts();
		
		selectCategory(messagesBtn);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    if (item.getItemId() == android.R.id.home) 
	    {
	        finish();
	        overridePendingTransition(R.anim.null_anim, R.anim.slide_out);   
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			// Check if it's displaying vehicle alerts
			if(displayVehicleAlerts)
			{
				Intent intent = new Intent(NotificationsActivity.this, VehicleAlertActivity.class);
				Bundle extras = new Bundle();

				String description = (String)vehicleAlerts.get(position).get("alertBody");
				ParseObject vehicle = (ParseObject)vehicleAlerts.get(position).get("alertVehicle");
				
				String make = vehicle.getString("vehicleMake");
				String model = vehicle.getString("vehicleModel");
				String regNumber = vehicle.getString("vehicleRegistration");
				
				extras.putString("description", description);
				extras.putString("vehicleId", vehicle.getObjectId());
				extras.putString("model", make + " " + model);
				extras.putString("regNumber", regNumber);
				intent.putExtras(extras);
				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
		}
	};
	
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) {
			selectCategory(v);
		}
	};
	
	private void selectCategory(View v)
	{
		// Display messages
		if(v == messagesBtn)
		{
			messagesIcon.setImageDrawable(getResources().getDrawable(R.drawable.account_white_56x56));
			vehicleAlertsIcon.setImageDrawable(getResources().getDrawable(R.drawable.warning_grey_56x52));
			
			messagesText.setTextColor(getResources().getColor(R.color.white));
			vehicleAlertsText.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
			
			this.getActionBar().setTitle(R.string.title_activity_message);
			
			adapter.setImageResource(R.drawable.envelope);
			adapter.setTitleColor(ThemeUtilities.getMainInterfaceColor(NotificationsActivity.this));
			adapter.setData(messagesForAdapter);
			
			displayVehicleAlerts = false;
		}
		// Display vehicle alerts
		else if(v == vehicleAlertsBtn)
		{
			messagesIcon.setImageDrawable(getResources().getDrawable(R.drawable.account_grey_56x56));
			vehicleAlertsIcon.setImageDrawable(getResources().getDrawable(R.drawable.warning_white_56x52));

			messagesText.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
			vehicleAlertsText.setTextColor(getResources().getColor(R.color.white));
			
			this.getActionBar().setTitle(R.string.vehicle_title_alerts);
			
			adapter.setImageResource(R.drawable.warning_grey_56x52);
			adapter.setTitleColor(ThemeUtilities.getRedTextColor(NotificationsActivity.this));
			adapter.setData(vehicleAlertsForAdapter);
			
			displayVehicleAlerts = true;
		}
		
		adapter.notifyDataSetChanged();
	}
	
	private void getMessagesByParse()
	{
		// Query for all messages that belong to user
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCMessage");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.whereEqualTo("messageRecipient", ParseUser.getCurrentUser());
		query.addDescendingOrder("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			public void done(final List<ParseObject> data, ParseException e) 
			{
				if (e == null) 
				{	
					// Loops through all messages
					for(int i=0; i<data.size(); i++)
					{
						ListAdapterItem message = new ListAdapterItem();
						message.title = data.get(i).getString("messageTitle");
						message.subtitle = data.get(i).getString("messageSubject");
						messagesForAdapter.add(message);
					}
					
					adapter.notifyDataSetChanged();
				}
				else
					Log.e("get messages", e.getMessage());
			}
		});
	}
	
	/**
	 * Gets alerts for the vehicle.
	 */
	private void getVehicleAlerts()
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.whereEqualTo("vehiclePrivateOwner", ParseUser.getCurrentUser());
		query.addDescendingOrder("createdAt");
		
		// Query for all vehicles that belong to the user
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			public void done(final List<ParseObject> data, ParseException e) 
			{
				if (e == null) 
				{	
					// Loop through all user's vehicles
					for(int i=0; i<data.size(); i++)
					{
						// Get all alerts for the given vehicle
						vehicleAlerts = DCVehicleSingleton.getAlertsForVehicle(data.get(i));
					}
					
					// Loop trough all alerts
					for(int i=0; i<vehicleAlerts.size(); i++)
					{
						ParseObject parseVehicle = (ParseObject) vehicleAlerts.get(i).get("alertVehicle");
						
						String vehicleMake = "";
						String vehicleModel = "";
						String vehicleDerivative = "";
						
						if(parseVehicle.getString("vehicleMake") != null)
							vehicleMake = parseVehicle.getString("vehicleMake");
						if(parseVehicle.getString("vehicleModel") != null)
							vehicleModel = parseVehicle.getString("vehicleModel");
						if(parseVehicle.getString("vehicleDerivative") != null)
							vehicleDerivative = parseVehicle.getString("vehicleDerivative");
						
						ListAdapterItem alert = new ListAdapterItem();
						alert.title = (String)vehicleAlerts.get(i).get("alertSubject");
						alert.subtitle = vehicleMake + " " + vehicleModel + " " + vehicleDerivative;
						
						vehicleAlertsForAdapter.add(alert);
					}
				}
				else
					Log.e("get vehicles", e.getMessage());
			}
		});
	}
}
