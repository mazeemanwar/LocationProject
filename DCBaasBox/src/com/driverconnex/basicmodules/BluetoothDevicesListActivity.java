package com.driverconnex.basicmodules;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;

/**
 * Activity for displaying a list of paired Bluetooth devices and selecting a default device. 
 * @author Adrian Klimczak
 *
 */

public class BluetoothDevicesListActivity extends Activity
{
	private TextView text;
	private ListView list;
	private ListAdapter adapter;
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	
	private String defaultDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		adapter = new ListAdapter(this, adapterData);
		
		text = (TextView) findViewById(R.id.textView);
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(onItemClickListener);
		list.setAdapter(adapter);
		
    	text.setText("No Devices");
    	
    	defaultDevice = DriverConnexApp.getUserPref().getBluetoothDeviceName();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();	
		
		if(!adapterData.isEmpty())
			adapterData.clear();
		
	    Set<BluetoothDevice> bondedSet;
	    bondedSet = Utilities.getBluetoothPairedDevices();

	    if(bondedSet != null)
	    {
		    if(bondedSet.size() > 0)
		    {
		    	for(BluetoothDevice device : bondedSet)
		    	{
		    		ListAdapterItem item = new ListAdapterItem();
		    		item.title = device.getName();
		    		
		    		if(defaultDevice != null)
		    		{
			    		if(item.title.equals(defaultDevice))
			    			item.subtitle = "(Default Device)";
		    		}
		    		
		    		adapterData.add(item);
		    	}
		    	
				adapter.notifyDataSetChanged();
				text.setVisibility(View.INVISIBLE);
		    }
		    else
		    {
		    	text.setVisibility(View.VISIBLE);
		    }
	    }
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		DriverConnexApp.getUserPref().setBluetoothDeviceName(defaultDevice);
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
			for(int i=0; i<adapterData.size(); i++)
				adapterData.get(i).subtitle = "";
			
			adapterData.get(position).subtitle = "(Default Device)";
			defaultDevice = adapterData.get(position).title;
			
			adapter.notifyDataSetChanged();
		}
	};
}
