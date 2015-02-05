package com.driverconnex.journeys;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity for adding a location point for the journey.
 * @author Adrian Klimczak
 *
 */

public class AddLocationMapActivity extends Activity 
{
	private GoogleMap map;
	private LocationClient locationClient;
	private DCJourneyPoint point;
	private SearchView searchView;
	
	private MenuItem selectButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location_map);

		// Get a handle to the Map Fragment
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		searchView = (SearchView) findViewById(R.id.searchView1);
				
		map.setMyLocationEnabled(true);
		
		point = new DCJourneyPoint();
		
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) 
		{
			locationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
			locationClient.connect();
	    }

		map.setOnMapClickListener(onMapClickListener);	
		searchView.setOnQueryTextListener(new OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query) 
			{
				if(Utilities.checkDataConnection(AddLocationMapActivity.this))	
				{
					searchView.clearFocus();
					Utilities.hideIM(AddLocationMapActivity.this, searchView);
					new searchLocationTask().execute();
				}
				else
	            	Toast.makeText(getApplicationContext(), "Please enable your data connection.", 
	            			   Toast.LENGTH_LONG).show();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}			
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(locationClient != null)
			locationClient.disconnect();
	}
	
	private OnMapClickListener onMapClickListener = new OnMapClickListener() 
	{	
		@Override
		public void onMapClick(LatLng location) 
		{	
			map.clear();
			map.addMarker(new MarkerOptions().position(location));
			point.setLat(location.latitude);
			point.setLng(location.longitude);
			selectButton.setEnabled(true);
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub); 
			return true;
		}
		else if (item.getItemId() == R.id.action_select) 
		{
			Intent returnIntent = new Intent();
			returnIntent.putExtra("point", point);
			setResult(RESULT_OK, returnIntent);
			finish();	
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_location_map, menu);
		
		selectButton = menu.getItem(0);
		selectButton.setEnabled(false);
		
		return super.onCreateOptionsMenu(menu);
	}

	private class searchLocationTask extends AsyncTask<Void, Void, Address> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
		}

		@Override
		protected Address doInBackground(Void... params)
		{
			Geocoder geocoder = new Geocoder(AddLocationMapActivity.this);

			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocationName(searchView.getQuery().toString(),5);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(addresses != null)		
			{
                if(addresses.size() > 0)
                     return addresses.get(0);
                
			}
			return null;
		}

		@Override
		protected void onPostExecute(Address address) 
		{	        
			super.onPostExecute(address);
			
            if(address != null)
            {
                 map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(),
                		 address.getLongitude()), 15), 1000, null);
            }
            else
            {
            	Toast.makeText(getApplicationContext(), "Couldn't find the place.", 
            			   Toast.LENGTH_LONG).show();
            }
		}
	}
	
	private ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks() 
	{
	    @Override
	    public void onDisconnected() {
	    }

	    @Override
	    public void onConnected(Bundle arg0) 
	    {
	        LocationRequest locationRequest = LocationRequest.create();
	        locationRequest.setFastestInterval(0);
	        locationRequest.setInterval(0).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        
	        Location myLocation = locationClient.getLastLocation();
			LatLng myLocationLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
			
			if (myLocation != null) 
			{
				 map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 15), 1000, null);
			} 
	    }
	};
	
	private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() 
	{
	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) 
	    {
	        Log.e("Connection", "Connection Failed");

			AlertDialog.Builder builder = new Builder(AddLocationMapActivity.this);
			builder.setMessage(R.string.location_services_error);
			builder.setTitle("Notification");
			builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			builder.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			});
			builder.setCancelable(false);
			builder.create().show();
	    }
	};	
}
