package com.driverconnex.vehicles;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.data.XMLServiceProviderParser;

/**
 * Activity for displaying list of services and service providers. 
 * User chooses a type of service and then they choose a provider, which it will take them to the provider's website.
 * @author Adrian Klimczak
 *
 */

public class ServiceListActivity extends Activity 
{
	private ListView list;
	private ListAdapter adapter;
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();

	private ArrayList<Service> services;
	private Service service;
	
	private boolean displayProviders = false; // Indicates if list of providers should be displayed
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		// We will get some extras if we launched this activity again, from service list to providers list
		if (getIntent().getExtras() != null) 
		{
			service = (Service) getIntent().getExtras().getSerializable("service");
			
			// Indicate that we are in providers list now
			if(service != null)
				displayProviders = true;
		}
		// Otherwise we are displaying list of services
		else
		{
			// Get services from the file
			services = XMLServiceProviderParser.getServicesFromXML(this);
		}
		
		adapter = new ListAdapter(ServiceListActivity.this, adapterData);
		
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(onItemClickListener);
		list.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();	
		
		if(!adapterData.isEmpty())
			adapterData.clear();
		
		// If we are no longer in services list but in providers list
		if(displayProviders)
		{
			for(int i=0; i<service.getProviders().size(); i++)
			{
				ListAdapterItem provider = new ListAdapterItem();		
				provider.title = service.getProviders().get(i).getName();
				
				adapterData.add(provider);
			}
			
			adapter.setImageResource(-1);
		}
		// If we are in services list
		else
		{
			for(int i=0; i<services.size(); i++)
			{
				ListAdapterItem service = new ListAdapterItem();
				service.title = services.get(i).getName();
				
				adapterData.add(service);
			}
			
			adapter.setImageResource(R.drawable.gear_grey_56x56);
		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			
			if(!displayProviders)	
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);	
			else
				overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
			
			return true;
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
			if(!displayProviders)
			{
				Intent intent = new Intent(ServiceListActivity.this, ServiceListActivity.class);
				
				Bundle extras = new Bundle();
				
				extras.putSerializable("service", services.get(position));
				
				intent.putExtras(extras);
				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			else
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(service.getProviders().get(position).getURL()));
				startActivity(browserIntent);
			}
		}
	};
}
