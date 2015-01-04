package com.driverconnex.journeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.driverconnex.app.R;
import com.driverconnex.singletons.TrackJourneySingleton;
import com.driverconnex.utilities.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseUser;

/**
 * Activity for tracking a journey.
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 */

public class TrackJourneyActivity extends Activity 
{
	private final static String PLAYSTATUS = "button.play";
	private final static String PAUSESTATUS = "button.pause";

	private TextView disVal;
	private Chronometer durVal;
	private Switch trackingSwitch;
	
	private ImageButton delete, play, stop;
	private GoogleMap map;
	private LocationClient locationClient = null;
	
	private boolean isTrackingStarted = false;
	private boolean isSharing = false;
	
	private Polyline route;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journey_track);
		
		dataInit();
		locationInit();
		handleIntent(getIntent());
		
		// Check if tracker is tracking
		if(TrackJourneySingleton.isTracking())
			startEvent(play);
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		if(locationClient != null)
			locationClient.disconnect();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// When user goes to JourneyDetailsActivity  and presses delete it will take him back to this screen
		// which it shouldn't. It should take him back to home activity. Setting flags didn't do it, so
		// the only way I found is to start activity for results and get the result in form of boolean "finish"
		if(requestCode == 100)
		{			
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				if(data.getExtras() != null)
				{
					if(data.getExtras().getBoolean("finish"))
						finish();
				}
			}	
		}
	}
	
	/**
	 * Initialises data
	 */
	private void dataInit() 
	{
		disVal = (TextView) findViewById(R.id.distanceValue);
		durVal = (Chronometer) findViewById(R.id.durationValue);
		delete = (ImageButton) findViewById(R.id.deleteBtn);
		play = (ImageButton) findViewById(R.id.playBtn);
		stop = (ImageButton) findViewById(R.id.stopBtn);
		trackingSwitch = (Switch) findViewById(R.id.switch1);
		
		delete.setOnClickListener(onClickListener);
		play.setOnClickListener(onClickListener);
		stop.setOnClickListener(onClickListener);
		trackingSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				isSharing = isChecked;
				
				// Check if tracking has started
				if(isTrackingStarted)
					setTrackingInParse(isChecked);
			}	
		});
		
		play.setTag(PAUSESTATUS);
		stop.setEnabled(false);

		// Get a handle to the Map Fragment
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		map.setMyLocationEnabled(true);
		
		// If user is tracking, we need to get the current status of the tracker
		durVal.setBase(TrackJourneySingleton.getTrackingDuration());
		disVal.setText(TrackJourneySingleton.getDistance());
		
		// Check if tracker has enough points to enable saving option
		if (TrackJourneySingleton.getJourneyPoints() >= 3 && !stop.isEnabled()) 
		{
			stop.setEnabled(true);
			stop.setImageResource(R.drawable.stop_white);
		}
		
		if(TrackJourneySingleton.getRoutePoints() != null)
		{
			route = map.addPolyline(new PolylineOptions()
			.width(10.0f)
			.color(Color.BLUE)
			.geodesic(true));
			
			route.setPoints(TrackJourneySingleton.getRoutePoints());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		// Must store the new intent otherwise getIntent() will return the old one
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent)
	{
		if (intent == null || intent.getAction() == null) 
			return;
		
		if (intent.getAction().equals(getPackageName() + ".TrackActivity.start")) 
		{
			if (play.getTag().equals(PAUSESTATUS)) 
				startEvent(play);
		} 
		else if (intent.getAction().equals(getPackageName() + ".TrackActivity.pause")) 
		{
			if (play.getTag().equals(PLAYSTATUS)) 
				pauseEvent(play);
		}
	}
	
	/**
	 * Initialises location of the user
	 */
	private void locationInit() 
	{
		if(!Utilities.checkNetworkEnabled(this) && !Utilities.checkGPSEnabled(this))
		{
			AlertDialog.Builder builder = new Builder(TrackJourneyActivity.this);
			builder.setMessage("Please turn on Location Services in your System Settings.");
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
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			builder.setCancelable(false);
			builder.create().show();
		}
		else
		{
			// Check if Google Play Services are available
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(TrackJourneyActivity.this)
					== ConnectionResult.SUCCESS) 
			{
				if(locationClient == null)
					locationClient = new LocationClient(TrackJourneyActivity.this
							, locationConnectionCallbacks, connectionFailedListener);
				
				locationClient.connect();
			}
		}
	}
	
	/**
	 * Handles what happens when location of the user is changed
	 */
	private LocationListener locationListener = new LocationListener() 
	{
		@Override
		public void onLocationChanged(Location location) 
		{	
			if (TrackJourneySingleton.getJourneyPoints() >= 3 && !stop.isEnabled()) 
			{
				stop.setEnabled(true);
				stop.setImageResource(R.drawable.stop_white);
			}
			
			// Do not draw polyline if accuracy is too low
			//if(location.getAccuracy() <= 50)
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
					location.getLongitude()), 15), 1000, null);
			
			if(TrackJourneySingleton.getRoutePoints() != null)
			{			
				// Makes sure route exists
				if(route != null)
				{
					// Makes sure that any route points were recorded
					if(TrackJourneySingleton.getRoutePoints() != null)
						route.setPoints(TrackJourneySingleton.getRoutePoints());
				}
				
				disVal.setText(TrackJourneySingleton.getDistance());
			}
		}
	};

	/**
	 * Pauses tracking.
	 * @param v
	 */
	private void pauseEvent(View v) 
	{
		((ImageButton) v).setImageResource(R.drawable.play_white);
		v.setTag(PAUSESTATUS);

		durVal.stop();
		TrackJourneySingleton.stopTracking();
		
		if(locationClient != null)
			locationClient.disconnect();
	}
	
	/**
	 * Starts tracking  
	 * @param v
	 */
	private void startEvent(View v) 
	{
		((ImageButton) v).setImageResource(R.drawable.pause_white);
		
		v.setTag(PLAYSTATUS);
		
		// Start tracking a journey
		// First launch tracker that will work in the background
		TrackJourneySingleton.startTracking(TrackJourneyActivity.this);
		durVal.setBase(TrackJourneySingleton.getTrackingDuration());
		
		// Then start local chronometer, so that it displays duration of the journey
		durVal.start();
		
		locationClient.connect();
		
		// Indicate that tracking has started
		isTrackingStarted = true;
		
		// Check if user wants to share his location
		if(isSharing)
			setTrackingInParse(true);
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == play) 
			{
				if (v.getTag().equals(PLAYSTATUS))
					pauseEvent(v);
				else 
					startEvent(v);
			}
			// Cancel tracking
			else if (v == delete) 
			{
				// Warn user before discarding journey
				AlertDialog.Builder builder = new AlertDialog.Builder(TrackJourneyActivity.this);
				builder.setTitle("Discard Journey");
				builder.setMessage("Do you want to discard this journey?");
				
				// Set positive button
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
				{	
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// User wants to discard this journey, so...
						if(locationClient != null)
							locationClient.disconnect();

						durVal.stop();
						
						// Delete journey from the database in the background
						if(TrackJourneySingleton.getJourney() != null)		
							new deleteTask(TrackJourneySingleton.getJourney().getId()).execute();
						
						// Reset tracker
						TrackJourneySingleton.reset();
						
						// Finish this activity
						finish();
						overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
					}
				});
				
				// Set cancel button
		    	builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
		    	{
		    		public void onClick(DialogInterface dialog, int id) 
		    		{
		    			// Close dialog
		    			dialog.dismiss();
		    		}
		    	});
		    	
		    	// Display dialog
		    	AlertDialog dialog = builder.create();
		    	dialog.show();
			}
			else if (v == stop) 
			{
				isTrackingStarted = false;
				setTrackingInParse(false);
				isSharing = false;
				
				// Stop tracking
				TrackJourneySingleton.stopTracking();

				if(locationClient != null)
					locationClient.disconnect();
				
				durVal.stop();
				
				disVal.setText(TrackJourneySingleton.getDistance());
				
				// Calculate average speed (m/h)
				// Get distance in miles
				double distance = Double.valueOf(disVal.getText().toString());
				// Get duration in minutes
				long duration = (TrackJourneySingleton.getEndTime() 
						- TrackJourneySingleton.getStartTime()) / (60 * 1000);
				
				double averageSpeed = distance/duration*60;
				
				// Get journey
				DCJourney journey = TrackJourneySingleton.getJourney();
				
				if(journey != null)
				{
					journey.setAvgSpeed((float)averageSpeed);
					
					// meters/second
					float topSpeed = TrackJourneySingleton.getTopSpeed();
					
					// Convert it into miles/hour
					topSpeed = ((topSpeed * 0.0006f)*60)*60;
					
					journey.setTopSpeed(topSpeed);	
					journey.setDistance(distance);
					journey.setDuration(duration);
					journey.setStartAddr("");
					journey.setEndAddr("");
					journey.setStartTime(TrackJourneySingleton.getStartTime());
					journey.setEndTime(TrackJourneySingleton.getEndTime());	
					journey.setBusiness(true);
					
					// We done, so go to next screen
					Intent intent = new Intent(TrackJourneyActivity.this,JourneyDetailsActivity.class);
					
					// Put the journey into intent
					intent.putExtra("journey", journey);
					//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					
					// Now, we can reset tracker
					TrackJourneySingleton.reset();
					
					// Start a new activity
					startActivityForResult(intent, 100);
					overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
				}
			}
		}
	};

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	private ConnectionCallbacks locationConnectionCallbacks = new ConnectionCallbacks() 
	{
	    @Override
	    public void onDisconnected() 
	    {
	    	locationClient.removeLocationUpdates(locationListener);
	    }

	    @Override
	    public void onConnected(Bundle arg0) 
	    {
			Location myLocation = locationClient.getLastLocation();
			
			if (myLocation != null) 
			{
				double dLatitude = myLocation.getLatitude();
				double dLongitude = myLocation.getLongitude();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						dLatitude, dLongitude), 15), 2000, null);
			}
			else 
			{
				Toast.makeText(TrackJourneyActivity.this, "Unable to fetch your current location",
						Toast.LENGTH_SHORT).show();
			}
			
	    	LocationRequest locationRequest = LocationRequest.create();
	        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);    
	        locationRequest.setInterval(1000);
	        locationRequest.setFastestInterval(1000);
	        
			locationClient.requestLocationUpdates(locationRequest, locationListener);
	    }
	};
	
	private OnConnectionFailedListener connectionFailedListener = new OnConnectionFailedListener() 
	{
	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) 
	    {
	        Log.e("Connection", "ConnectionFailed");
	    }
	};
	
	private void setTrackingInParse(boolean tracking)
	{
		ParseUser.getCurrentUser().put("userIsTracking", tracking);
		ParseUser.getCurrentUser().saveInBackground();
	}
	
	private class deleteTask extends AsyncTask<String, Void, Boolean> 
	{
		private long journeyId;
		
		public deleteTask(long journeyId)
		{
			this.journeyId = journeyId;
		}
		
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... keywords) 
		{			
			JourneyDataSource dataSource = new JourneyDataSource(TrackJourneyActivity.this);

			dataSource.open();
			dataSource.deleteJourney(journeyId);
			dataSource.deleteBehaviourPoints(journeyId);
			dataSource.close();
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{	        
			super.onPostExecute(result);
		}
	}
}
