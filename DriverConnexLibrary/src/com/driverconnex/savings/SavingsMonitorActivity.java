package com.driverconnex.savings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;

/**
 * Savings menu with some options to select.
 * @author Adrian Klimczak
 *
 */
public class SavingsMonitorActivity extends Activity 
{
	private ListView list;
	private ListAdapter adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_margin);
		
		list = (ListView) findViewById(R.id.list);
		
		ArrayList<ListAdapterItem> options = new ArrayList<ListAdapterItem>();
		
		ListAdapterItem fuelUsage = new ListAdapterItem();
		ListAdapterItem serviceRepair = new ListAdapterItem();
		ListAdapterItem otherCosts = new ListAdapterItem(); 
		
		fuelUsage.title = "Fuel Usage";
		fuelUsage.subtitle = "View your vehicle fuel expenses.";
		
		serviceRepair.title = "Service & Repair";
		serviceRepair.subtitle = "View your maintenance expenses.";
		
		otherCosts.title = "Other Costs";
		otherCosts.subtitle = "View standing and miscellaneous vehicle costs.";
		
		options.add(fuelUsage);
		options.add(serviceRepair);
		options.add(otherCosts);
		
		list.setOnItemClickListener(itemClickListener);
		
		adapter = new ListAdapter(this, options);
		list.setAdapter(adapter);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.null_anim,	R.anim.slide_out);
			return true;
		}
		else if(item.getItemId() == R.id.action_settings)
		{
			Intent intent = new Intent(SavingsMonitorActivity.this, OptionsActivity.class);
			startActivity(intent);
			overridePendingTransition( R.anim.slide_left_sub, R.anim.slide_left_main);	
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			Intent intent = null;
			
			switch(position)
			{
			case 0:
				intent = new Intent(SavingsMonitorActivity.this, FuelStatisticsActivity.class);
				break;
			case 1:
				intent = new Intent(SavingsMonitorActivity.this, ServiceRepairStatisticsActivity.class);
				break;
			case 2:
				intent = new Intent(SavingsMonitorActivity.this, StandingStatisticsActivity.class);
				break;
			}
			
			if(intent != null)
			{
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);	
			}
		}
	};
}
