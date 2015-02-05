package com.driverconnex.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for selecting DC Service Items. 
 * @author Adrian Klimczak
 *
 */

public class SelectServiceItemActivity extends Activity 
{
	private ListView list;
	private ListAdapter adapter;
	//private ListDCServiceItemAdapter adapter;
	private RelativeLayout loading;
	private ArrayList<DCServiceItem> serviceItems = new ArrayList<DCServiceItem>();
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selectable);

		list = (ListView) findViewById(R.id.list);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		list.setOnItemClickListener(onItemClickListener);

		// Get all service items from the parse that are available to choose
		getServiceItemsByParse();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		return super.onCreateOptionsMenu(menu);
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
		else if( item.getItemId() == R.id.action_save)
		{
			Intent returnIntent = new Intent();
			
			ArrayList<DCServiceItem> selectedItems = new ArrayList<DCServiceItem>();
			
			for(int i=0; i<serviceItems.size(); i++)
			{
				if(adapterData.get(i).selected)
					selectedItems.add(serviceItems.get(i));
			}
			
			// Return selected service items
			returnIntent.putExtra("selectedItems", selectedItems);
			setResult(RESULT_OK, returnIntent);
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			if(!adapterData.get(position).selected)
				adapterData.get(position).selected = true;
			else		
				adapterData.get(position).selected = false;
			
			adapter.notifyDataSetChanged();
		}
	};
	
	private void getServiceItemsByParse() 
	{
		loading.setVisibility(View.VISIBLE);
		
		// Query for the DCServiceItem items
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCServiceItem");
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			@Override
			public void done(List<ParseObject> serviceList, com.parse.ParseException e) 
			{
				if (e == null)
				{
					// Loop through all service items we got
					for(int i=0; i<serviceList.size(); i++)
					{		
						DCServiceItem serviceItem = new DCServiceItem();
							        		
						// Get the info from database
						serviceItem.setId(serviceList.get(i).getObjectId());								
						serviceItem.setName(serviceList.get(i).getString("itemName"));
						serviceItem.setDescription(serviceList.get(i).getString("itemDescription"));
						
						ListAdapterItem item = new ListAdapterItem();
						item.title = serviceItem.getName();
						item.subtitle = serviceItem.getDescription();
						
						
						// Add it to the array
						adapterData.add(item);
						serviceItems.add(serviceItem);
					}
					
					// Set adapter
					adapter = new ListAdapter(SelectServiceItemActivity.this, adapterData);
					//adapter = new ListDCServiceItemAdapter(SelectServiceItemActivity.this, serviceItems);
					list.setAdapter(adapter);
					
					// If user is editing service history we will get selected service items
					if(getIntent().getExtras() != null)
					{
						ArrayList<DCServiceItem> items = (ArrayList<DCServiceItem>) getIntent()
								.getExtras().getSerializable("serviceItems");
						
						// Make sure we got items
						if(items != null)
						{
							// Loop through all service items that are available
							for(int i=0; i<serviceItems.size(); i++)
							{
								// Loop through all items that were selected from the list
								for(int j=0; j<items.size(); j++)
								{
									if(serviceItems.get(i).getId().equals(items.get(j).getId()))
									{
										// Select item that was selected
										adapterData.get(i).selected = true;
									}
								}
							}
							
							adapter.notifyDataSetChanged();
						}
					}
					
					// Disable loading bar
					loading.setVisibility(View.GONE);
				}
				else 
					Log.d("Get DCService Items", "Error: " + e.getMessage());
			}
		});
	}
}
