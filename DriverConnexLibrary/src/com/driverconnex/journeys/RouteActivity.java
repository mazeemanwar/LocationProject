package com.driverconnex.journeys;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.driverconnex.app.R;
import com.driverconnex.parking.JSONParser;
import com.driverconnex.utilities.LocationUtilities;
import com.driverconnex.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Activity for displaying a route of the journey.
 * @author Yin Lee(SGI)
 * @author Adrian Klimczak
 *
 */

public class RouteActivity extends Activity 
{
	private final static String EYEONSTATUS = "satellite.on";
	private final static String EYEOFFSTATUS = "satellite.off";
	private String eyeBtnTag;
	private GoogleMap map;
	private ArrayList<DCJourneyPoint> points;
	private boolean isFirst;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);

		eyeBtnTag = EYEOFFSTATUS;
		points = new ArrayList<DCJourneyPoint>();
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		isFirst = true;
		
		init();
	}

	private void init() 
	{
		if (getIntent().getExtras() != null) 
		{
			points = getIntent().getExtras().getParcelableArrayList("points");
			map.setOnCameraChangeListener(new OnCameraChangeListener() 
			{
			    @Override
			    public void onCameraChange(CameraPosition arg0) 
			    {	    	
			    	if (isFirst) 
			    	{
			    		centerIncidentRouteOnMap(points);
			    		isFirst = false;
					}
			    }
			});
		
			// Add makers for start and end points
			map.addMarker(new MarkerOptions().position(new LatLng(points.get(0).getLat(), points.get(0).getLng())));
			map.addMarker(new MarkerOptions().position(new LatLng(points.get(points.size() - 1).getLat(), points.get(points.size() - 1).getLng())));

			// If there are only two points, it will draw just a straight line, to avoid this let it draw default path between these points
			if(points.size() <= 2)
			{
				if(Utilities.checkDataConnection(RouteActivity.this))
				{
			    	new connectAsyncTask(LocationUtilities.makeURL(points.get(1).getLat(), points.get(1).getLng(),
			    			points.get(0).getLat(), points.get(0).getLng())).execute();	
				}
				// If there is no data connection to get the route from google maps, then draw just a straight line
				else
				{
					ArrayList<LatLng> list = new ArrayList<LatLng>();
					
					// Draw route from points
					for (int i = 0; i < points.size(); i++) 
						list.add(new LatLng(points.get(i).getLat(), points.get(i).getLng()));
					
					Polyline route = map.addPolyline(new PolylineOptions()
					.width(10.0f)
					.color(Color.BLUE)
					.geodesic(true));
					route.setPoints(list);
				}
			}
			// Otherwise given points probably create a path
			else
			{				
				ArrayList<LatLng> list = new ArrayList<LatLng>();
				
				// Draw route from points
				for (int i = 0; i < points.size(); i++) 
				{
					list.add(new LatLng(points.get(i).getLat(), points.get(i).getLng()));
					
					//map.addPolyline(new PolylineOptions().geodesic(true).add(
							//new LatLng(points.get(i).getLat(), points.get(i).getLng()), new LatLng(points.get(i+1).getLat(), points.get(i+1).getLng())).width(10.0f).color(Color.BLUE));
				}
				
				Polyline route = map.addPolyline(new PolylineOptions()
				.width(10.0f)
				.color(Color.BLUE)
				.geodesic(true));
				
				route.setPoints(list);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.route, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_eye) 
		{
			if (eyeBtnTag.equals(EYEONSTATUS)) 
			{
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				eyeBtnTag = EYEOFFSTATUS;
			} 
			else 
			{
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				eyeBtnTag = EYEONSTATUS;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void centerIncidentRouteOnMap(ArrayList<DCJourneyPoint> points) 
	{
		double minLat = Integer.MAX_VALUE;
		double maxLat = Integer.MIN_VALUE;
		double minLon = Integer.MAX_VALUE;
		double maxLon = Integer.MIN_VALUE;
		for (DCJourneyPoint point : points) {
			maxLat = Math.max(point.getLat(), maxLat);
			minLat = Math.min(point.getLat(), minLat);
			maxLon = Math.max(point.getLng(), maxLon);
			minLon = Math.min(point.getLng(), minLon);
		}
		LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon),
				new LatLng(maxLat, maxLon));
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
	}

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
