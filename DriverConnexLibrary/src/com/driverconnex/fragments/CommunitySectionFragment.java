package com.driverconnex.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.community.DCUser;
import com.driverconnex.community.UserProfileActivity;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ParseUtilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * A community section fragment for the DriverConnex dashboard. It displays a map with marked locations of all friends that are tracking a journey
 * and it displays three closest to user friends.
 * 
 * @author Adrian Klimczak
 */
public class CommunitySectionFragment extends Fragment 
{
	private final float MAP_PADDING = 200.0f;
	private final String MAP_FRAGMENT_TAG = "map";
	
	private GoogleMap map;
	private SupportMapFragment mMapFragment;
	
	private TextView firstName[] = new TextView[3];
	private TextView lastName[] = new TextView[3];
	private ImageView photo[] = new ImageView[3];
	private LinearLayout[] userLayouts= new LinearLayout[3];
	
	private ArrayList<DCUser> closestFriends = new ArrayList<DCUser>();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);

        //------------------------------------------------
        // Create initial configuration for the map
        GoogleMapOptions options = new GoogleMapOptions()
                .compassEnabled(false)
                .mapType(GoogleMap.MAP_TYPE_NORMAL)
                .rotateGesturesEnabled(false)
                .scrollGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false);
        
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // Check if map doesn't exist already
        if (mMapFragment == null) 
        {
        	// Create map
            mMapFragment = SupportMapFragment.newInstance(options);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mapContainer, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }

        firstName[0] = (TextView) rootView.findViewById(R.id.firstName1);
        firstName[1] = (TextView) rootView.findViewById(R.id.firstName2);
        firstName[2] = (TextView) rootView.findViewById(R.id.firstName3);
        
        lastName[0] = (TextView) rootView.findViewById(R.id.lastName1);
        lastName[1] = (TextView) rootView.findViewById(R.id.lastName2);
        lastName[2] = (TextView) rootView.findViewById(R.id.lastName3);
        
        photo[0] = (ImageView) rootView.findViewById(R.id.photoImage1);
        photo[1] = (ImageView) rootView.findViewById(R.id.photoImage2);
        photo[2] = (ImageView) rootView.findViewById(R.id.photoImage3);
        
        userLayouts[0] = (LinearLayout) rootView.findViewById(R.id.user1);
        userLayouts[1] = (LinearLayout) rootView.findViewById(R.id.user2);
        userLayouts[2] = (LinearLayout) rootView.findViewById(R.id.user3);
        
        for(int i=0; i<userLayouts.length; i++)
        {
        	userLayouts[i].setVisibility(View.INVISIBLE);
        	userLayouts[i].setOnClickListener(onClickListener);
        }

        return rootView;
    }
    
    /**
     * This function is called when this page is currently being viewed
     */
    public void pageViewed()
    {	
    	if(map == null)
    	{
        	// Once view is created get a map
        	map = mMapFragment.getMap();
        	map.getUiSettings().setAllGesturesEnabled(false);
        	
        	// Disable marker listener
        	map.setOnMarkerClickListener(null);
        	map.setMyLocationEnabled(true);	
    	}
    	    	
    	// SetOnMyLoaded doesn't do the job, we need to be 100 % sure we are getting user's location
    	map.setOnMyLocationChangeListener(new OnMyLocationChangeListener()
    	{
    		@Override
 			public void onMyLocationChange(Location location) 
    		{
 				// Make sure we got a location
 				if(location != null)
 				{
 					// Once it's loaded get friends and display them on the map
 			        getFriendsByParse();
 			        
 			        // Get closest friends
 			        getClosestFriendsByParse(location.getLatitude(), location.getLongitude());
 			        
 			        // We need location only once, so disable this listener
 			        map.setOnMyLocationChangeListener(null);
 				}
 			}
         });
    }
    
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			Bundle extras = new Bundle();		
			
			if(v.getId() == R.id.user1)
			{
				extras.putSerializable("user",closestFriends.get(0));
			}
			else if(v.getId() == R.id.user2)
			{
				extras.putSerializable("user",closestFriends.get(1));
			}
			else if(v.getId() == R.id.user3)
			{
				extras.putSerializable("user",closestFriends.get(2));
			}
			
			Intent intent = new Intent(getActivity(), UserProfileActivity.class);			
			intent.putExtras(extras);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in, R.anim.null_anim);
		}
	};
    
	/**
	 * Gets all friends from the parse and marks their location on the map.
	 */
    private void getFriendsByParse()
    {
    	ParseRelation<ParseUser> userFriends = ParseUser.getCurrentUser().getRelation("userFriends"); 	 
		ParseQuery<ParseUser> query = userFriends.getQuery();
		
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<ParseUser>() 
		{
			public void done(final List<ParseUser> userList, ParseException e) 
			{
				if (e == null) 
				{
					markFriendsOnMap(userList);
				}
				else 
				{
					Log.e("Get User", e.getMessage());
				}	
			}
		});
    }
    
    /**
     * Marks position of friends on the map.
     * @param users
     */
    private void markFriendsOnMap(List<ParseUser> users)
    {        	
    	ArrayList<LatLng> locations = new ArrayList<LatLng>();
    	
        for (int i=0; i<users.size(); i++) 
        {    
            if (users.get(i).getBoolean("userIsTracking"))
            {   
            	ParseGeoPoint geoPoint = users.get(i).getParseGeoPoint("userCurrentLocation");
            	
            	if(geoPoint != null)
            	{
            		LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
            		
        			MarkerOptions marker = new MarkerOptions();
        			marker.position(location);
        			map.addMarker(marker);
        			locations.add(location);
            	}  
            }
        }
        
    	locations.add(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()));
        zoomToFitMapAnnotations(locations);
    }

    /**
     * Fixes zoom so that all marked positions on the map are visible.
     * @param locations
     */
    private void zoomToFitMapAnnotations(ArrayList<LatLng> locations)
    {    	
        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (LatLng item : locations) {
            bc.include(item);
        }

        // Somehow bug appeared that map fragment wasn't in the layout yet, therefore it crashed the app
        // So just to prevent any other unexpected crashes check if the map is in the layout.
        if(mMapFragment.isInLayout())
        	map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), (int) MAP_PADDING));
    }
    
    /**
     * Gets three the closest friends by Parse.
     */
    private void getClosestFriendsByParse(double latitude, double longitude)
    {    
		ParseGeoPoint currentUserLocation = new ParseGeoPoint(latitude, longitude);

		ParseRelation<ParseUser> userFriends = ParseUser.getCurrentUser().getRelation("userFriends");
    	ParseQuery<ParseUser> query = userFriends.getQuery();
    	query.whereNear("userCurrentLocation", currentUserLocation);
    	query.setLimit(3);
    	query.whereEqualTo("userIsTracking", true);
    	query.findInBackground(new FindCallback<ParseUser>()
    	{
			@Override
			public void done(List<ParseUser> users, ParseException e) 
			{
				if(e == null)
				{	
					for(int i=0; i<users.size(); i++)
					{
						final int position = i;
						
						closestFriends.add(ParseUtilities.convertUser(users.get(i)));
						
						// Get photo from the parse
						ParseFile photoFile = (ParseFile) users.get(i).get("userProfilePhoto");
								
						if (photoFile != null)
						{
							photoFile.getDataInBackground(new GetDataCallback() 
							{
								@Override
								public void done(byte[] data, ParseException e) 
								{
									if(e == null)
									{
										photo[position].setImageBitmap(AssetsUtilities.readBitmap(data, 80, 80));
									}
								}
							});
						}
						
			    		firstName[i].setText(users.get(i).getString("userFirstName"));
			    		lastName[i].setText(users.get(i).getString("userSurname"));
			    		userLayouts[i].setVisibility(View.VISIBLE);
					}
				}
				else
					Log.e("get users", e.getMessage());
			}	
    	});	
    }
}
