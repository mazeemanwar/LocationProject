package com.driverconnex.incidents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.driverconnex.app.R;
import com.driverconnex.utilities.LocationUtilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * This activity is opened from IncidentsActivity. It is used to select location of occurred incident.
 * @author Adrian Klimczak
 *
 */

public class IncidentLocationActivity extends Activity
{	
	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_parking_location);
		
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapView)).getMap();
		map.setMyLocationEnabled(true);
		
		// Initialise location of the user
		locationInit();
	}
	
	/**
	 * Items on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    if (item.getItemId() == android.R.id.home) 
	    {
	    	// Go to parent activity
	        finish();
	        overridePendingTransition(R.anim.null_anim, R.anim.slide_out);   
	        return true;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Initialises location of the user on the map
	 */
	private void locationInit()
	{
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		// Checks if network and GPS is not enabled
		if (!networkEnabled && !gpsEnabled) 
		{
			AlertDialog.Builder builder = new Builder(this);
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
		else 
		{
	    	// Focus camera
	    	//-------------------------------------------------
			// Sets the criteria for a fine and low power provider
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			crit.setPowerRequirement(Criteria.POWER_HIGH);
			
			// Gets the best matched provider, and only if it's on
			provider = locationManager.getBestProvider(crit, true);
			Location myLocation = locationManager.getLastKnownLocation(provider);
			
			if (myLocation != null) 
			{	
				// Focus camera on parked location
				double dLatitude = myLocation.getLatitude();
				double dLongitude = myLocation.getLongitude();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						dLatitude, dLongitude), 15), 2000, null);	
						
				Address address = LocationUtilities.getAddress(IncidentLocationActivity.this, dLatitude, dLongitude);
				
				if(address != null)
				{
					// First line of address sometimes doesn't give street name, so
					// add a postal code to fill the gap and make it more informative
					String strAddress = address.getAddressLine(0) + ", " + address.getPostalCode();
					getActionBar().setSubtitle("Near " + strAddress);
				}
				else
					getActionBar().setSubtitle("Error getting address.");
			} 
			else 
			{
				Toast.makeText(this,
						"Unable to fetch your current location",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
