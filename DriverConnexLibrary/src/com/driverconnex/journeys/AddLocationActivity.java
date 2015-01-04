package com.driverconnex.journeys;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.utilities.LocationUtilities;

/**
 * Activity for adding a new location point.
 * @author modified by Adrian Klimczak
 *
 */

public class AddLocationActivity extends Activity 
{
	private ListView list;
	private ArrayList<DCJourneyPoint> points;
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		list = (ListView) findViewById(R.id.list);
		points = new ArrayList<DCJourneyPoint>();
		
		if (getIntent().getExtras() != null) 
		{
			points = getIntent().getExtras().getParcelableArrayList("points");

			for(int i=0; i<points.size(); i++)
			{
				ListAdapterItem point = new ListAdapterItem();
				point.title = points.get(i).getPostalCode();
				point.subtitle = points.get(i).getLocality();
				adapterData.add(point);
			}
		}
		
		adapter = new ListAdapter(AddLocationActivity.this, adapterData, R.drawable.location);
		list.setAdapter(adapter);
		
		(new GetAddressTask(this)).execute(points);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			Intent returnIntent = new Intent();
			returnIntent.putExtra("points", points);
			setResult(RESULT_OK, returnIntent);
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_plus) 
		{
			Intent intent = new Intent(AddLocationActivity.this, AddLocationMapActivity.class);
			startActivityForResult(intent, 200);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Check which request we're responding to
		if (requestCode == 200) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				DCJourneyPoint point = data.getParcelableExtra("point");
				if (point != null) 
				{
					points.add(point);
					
					ListAdapterItem adapterPoint = new ListAdapterItem();
					adapterPoint.title = point.getPostalCode();
					adapterPoint.subtitle = point.getLocality();
					adapterData.add(adapterPoint);
					
					adapter.notifyDataSetChanged();
					(new GetAddressTask(this)).execute(points);
				}
			}
		}
	}

	private class GetAddressTask extends AsyncTask<ArrayList<DCJourneyPoint>, Void, ArrayList<DCJourneyPoint>> 
	{
		Context mContext;

		public GetAddressTask(Context context) 
		{
			super();
			mContext = context;
		}

		@Override
		protected ArrayList<DCJourneyPoint> doInBackground(ArrayList<DCJourneyPoint>... points) 
		{
			for (int i = 0; i < points[0].size(); i++) 
			{
				Address address = LocationUtilities.getAddressFromPoint(AddLocationActivity.this, points[0].get(i));
				
				if(!points[0].get(i).isHasAddress())
				{
					if (address != null) 
					{
						points[0].get(i).setLocality(address.getLocality());//getAddressLine(0));//getThoroughfare());//getLocality());
						points[0].get(i).setPostalCode(address.getPostalCode());
					}
					else 
					{
						points[0].get(i).setLocality("");
						points[0].get(i).setPostalCode("Error getting address.");
					}
					
					if(adapterData != null)
					{
						adapterData.get(i).title = points[0].get(i).getPostalCode();
						adapterData.get(i).subtitle =  points[0].get(i).getLocality();
					}
					
					points[0].get(i).setHasAddress(true);
				}
			}
			
			return points[0];
		}

		@Override
		protected void onPostExecute(ArrayList<DCJourneyPoint> points) 
		{
			super.onPostExecute(points);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.vehicle, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
