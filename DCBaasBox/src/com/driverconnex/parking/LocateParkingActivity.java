package com.driverconnex.parking;

import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.driverconnex.app.R;
import com.driverconnex.utilities.LocationUtilities;
import com.driverconnex.utilities.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Activity for locating parked vehicle
 * @author Wizard
 *
 */

public class LocateParkingActivity extends Activity
{	
	private GoogleMap map;
	private LocationClient locationClient;
	private ParkingLocation parkedLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_parking_location);
        
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapView)).getMap();
		map.setMyLocationEnabled(true); 
		
        //-----------------------------------------------------
		// Create an instance of the database
		ParkingLocationDataSource dataSource = new ParkingLocationDataSource(this);
		// Open database
		dataSource.open();

		parkedLocation = dataSource.getLatestParkingLocation();

		// Close database
		dataSource.close();
		//-----------------------------------------------------
		
		if(parkedLocation == null)
		{
			new AlertDialog.Builder(LocateParkingActivity.this)
			.setTitle(R.string.title_error)
			.setMessage("No parking location has been saved")
			.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,	int which) 
				{
				}
			}).show();	
		}
		else
		{
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) 
			{
				locationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
				locationClient.connect();
		    }
			
			String expiryTime = Utilities.getTimeFromDate(parkedLocation.getDate());
			
			if(expiryTime == null)
				expiryTime = "";

			getActionBar().setSubtitle("Expires " + expiryTime);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(locationClient != null)
			locationClient.disconnect();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.action_open_external_app, menu);
		return true;
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
		else if(item.getItemId() == R.id.action_open_app)
		{
			// Opens Google Maps app
			String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr="+parkedLocation.getLatitude()+","+parkedLocation.getLongitude());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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
			LatLng LatStartingPoint = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
			LatLng LatParkedLocation = new LatLng(parkedLocation.getLatitude(),parkedLocation.getLongitude());
			
	        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatStartingPoint, 15), 2000, null);
	        
			// Marks location where driver parked the car
			MarkerOptions destinationMarker = new MarkerOptions();
			MarkerOptions startMarker = new MarkerOptions();
			
	    	destinationMarker.position(LatParkedLocation);
	    	startMarker.position(LatStartingPoint);
	    	
	    	// Marks destination on the map
	    	map.addMarker(destinationMarker);
	    	// Marks location of the user on the map
	    	map.addMarker(startMarker);
	              
	    	// Handles drawing directions
	    	new connectAsyncTask(LocationUtilities.makeURL(parkedLocation.getLatitude(), parkedLocation.getLongitude(),
	    			myLocation.getLatitude(), myLocation.getLongitude())).execute();	
	    }
	};
	
	private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() 
	{
	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) 
	    {
	        Log.e("Connection", "ConnectionFailed");
	        
	        AlertDialog.Builder builder = new Builder(LocateParkingActivity.this);
			builder.setMessage("Please turn on Location Services in your System Settings.");
			builder.setTitle("Notification");
			builder.setPositiveButton("Settings",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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
	
	/**
	 * Connects to the server to get the JSON file consisting of directions
	 *
	 */
	private class connectAsyncTask extends AsyncTask<Void, Void, String>
	{
	    String url;
	    
	    connectAsyncTask(String urlPass){
	        url = urlPass;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    
	    @Override
	    protected String doInBackground(Void... params) 
	    {
	        JSONParser jParser = new JSONParser();
	        String json = jParser.getJSONFromUrl(url);
	        return json;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) 
	    {
	        super.onPostExecute(result); 
      
	        if(result!=null)
	            drawDirection(result);    
	    }
	}
	
	/**
	 * Draws direction using JSON file
	 * @param result
	 */
	public void drawDirection(String result) 
	{
		try 
		{
			//Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = LocationUtilities.decodePoly(encodedString);

			Polyline route = map.addPolyline(new PolylineOptions()
			.width(10.0f)
			.color(Color.BLUE)
			.geodesic(true));
			
			route.setPoints(list);
	    } 
	    catch (JSONException e) {
	    }
	}
		
}
