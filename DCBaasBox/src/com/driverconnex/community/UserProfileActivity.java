package com.driverconnex.community;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.ParseUtilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

/**
 * Activity for displaying user's friend's profile. It shows name of the friend, where they are and their current status.
 * 
 * NOTE: 
 * Entire community section is not working since Greg made some changes in the Parse database.
 * 
 * @author Adrian Klimczak
 * 
 */

public class UserProfileActivity extends Activity 
{
	private DCUser user;
	
	private TextView status;
	private TextView updateDate;
	private GoogleMap map;
	
	private long startTime = 0;
	private Handler timerHandler = new Handler();
	
	private ParseUser parseUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		status = (TextView) findViewById(R.id.statusTextView);
		updateDate = (TextView) findViewById(R.id.dateTextView);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		
		if(getIntent().getExtras() != null)
		{
			user = (DCUser)getIntent().getExtras().getSerializable("user");	
			getActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
			
			updateUserStatus();
			updateUserLocationOnMap();
			
			getUserByParse();
		}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		if(timerHandler != null)
			timerHandler.removeCallbacks(timerRunnable);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if(user != null)
		{
			// Restart timer
			startTime = System.currentTimeMillis();
	        timerHandler.postDelayed(timerRunnable, 0);
	        
	        // Refresh user
			refresh();
		}
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
	
    private Runnable timerRunnable = new Runnable() 
    {
        @Override
        public void run() 
        {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            
            // Every minute update user location on the map
            if(seconds > 60)
            {
            	refresh();
            	startTime = System.currentTimeMillis();
            }
            
            timerHandler.postDelayed(this, 500);
        }
    };
    
    /**
     * Refreshes user
     */
    private void refresh()
    {
    	if(parseUser != null)
    	{
    		parseUser.refreshInBackground(new RefreshCallback()
        	{
    			@Override
    			public void done(ParseObject object, ParseException e) 
    			{	
    				if(e == null)
    				{
    					user = ParseUtilities.convertUser(parseUser);
    					updateUserStatus();
    					updateUserLocationOnMap();
    				}
    			}
        	});	
    	}
    }
    
    /**
     * Updates location of the user on the map
     */
	private void updateUserLocationOnMap()
	{
		if(user.getLatitude() != 0 && user.getLongitude() != 0)
		{
			LatLng location = new LatLng(user.getLatitude(), user.getLongitude());
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15), 2000, null);
			
			MarkerOptions marker = new MarkerOptions();
			marker.position(location);
			map.addMarker(marker);
		}
	}
	
	/**
	 * Gets user's friend from the parse as we need ParseUser for refreshing
	 */
	private void getUserByParse() 
	{
		final ParseUser currentUser = ParseUser.getCurrentUser();
		ParseRelation<ParseUser> userFriends = currentUser.getRelation("userFriends"); 	 
		ParseQuery<ParseUser> query = userFriends.getQuery();
		
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseUser>() 
		{
			public void done(final List<ParseUser> userList, ParseException e) 
			{
				if (e == null) 
				{		
					// Loop through all users that are friends of the user
					for (int i = 0; i < userList.size(); i++)
					{
						// Get the user we want
						if(userList.get(i).getObjectId().equals(user.getId()))
						{
							parseUser = userList.get(i);
							break;
						}
					}
				}
				else 
					Log.e("Get User", e.getMessage());
			}
		});
	}
	
	private void updateUserStatus()
	{
		if(user.isTracking())
		{
			if(user.getStatus() != null)
				status.setText(user.getStatus());
			else
				status.setText("User is tracking");
		}
		else
			status.setText("Not currently tracking");
		
		if(user.getUpdateDate() != null)
			updateDate.setText("Last update at: "+user.getUpdateDate());
		else
			updateDate.setText("Last update at: Unknown");
	}
}
