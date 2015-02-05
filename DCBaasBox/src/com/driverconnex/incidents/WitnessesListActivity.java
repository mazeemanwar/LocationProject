package com.driverconnex.incidents;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;

/**
 * Activity used to display list of witnesses of the incident.
 * 
 * @author Adrian Klimczak
 *
 */

public class WitnessesListActivity extends Activity 
{
	private final int REQUEST_WITNESS = 100;
	
	private ListView list;
	private ListAdapter adapter;
	private Button addWitnessBtn;
	
	private ArrayList<ListAdapterItem> adapterData = new ArrayList<ListAdapterItem>();
	private ArrayList<String[]> witnesses;
	
	private boolean isModify = false;
	private boolean review = false;                // Indicates if this activity is used to view witnesses
	
	private int witnessPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_witnesses);
		
		addWitnessBtn = (Button) findViewById(R.id.addWitnessBtn);
		list = (ListView) findViewById(R.id.list);
		
		addWitnessBtn.setOnClickListener(onClickListener);
		list.setOnItemClickListener(onItemClickListener);
		
		// We will get some extras if we launched this activity again, from service list to providers list
		if (getIntent().getExtras() != null) 
		{
			review = getIntent().getExtras().getBoolean("review");
			witnesses = (ArrayList<String[]>) getIntent().getExtras().getSerializable("witnesses");
			
			if(witnesses != null)
			{
				// Prepare data out of witnesses for the adapter
				for(int i=0; i<witnesses.size(); i++)
				{
					ListAdapterItem item = new ListAdapterItem();
					item.title = witnesses.get(i)[0];
					item.subtitle = witnesses.get(i)[1];
					
					adapterData.add(item);
				}
			}
		}
	
		if(witnesses == null)
			witnesses = new ArrayList<String[]>();
			
		// Set adapter
		adapter = new ListAdapter(WitnessesListActivity.this, adapterData, R.drawable.eye_grey_56x30);
		list.setAdapter(adapter);	
		
		getActionBar().setTitle(R.string.title_activity_back);
		
		// Check if witnesses are being reviewed rather than being created
		if(review)
		{
			RelativeLayout bottomActionBar = (RelativeLayout) findViewById(R.id.bottomActionBar);
			bottomActionBar.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			Intent returnIntent = new Intent();
			returnIntent.putExtra("witnesses", witnesses);
			setResult(RESULT_OK, returnIntent);
			
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Check if we got results from AddWitnessActivity
		if (requestCode == REQUEST_WITNESS) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				// Get a witness
				String[] witness = data.getExtras().getStringArray("witness");
				
				// Make sure we did get the witness
				if(witness != null)
				{
					// Check if it's new or modified
					if(!isModify)
						witnesses.add(witness);
					else
					{
						witnesses.set(witnessPosition,witness);
						isModify = false;
					}
					
					if(!adapterData.isEmpty())
						adapterData.clear();
					
					// Refresh adapterData
					for(int i=0; i<witnesses.size(); i++)
					{
						ListAdapterItem item = new ListAdapterItem();
						item.title = witnesses.get(i)[0];
						item.subtitle = witnesses.get(i)[1];
						
						adapterData.add(item);
					}
					
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	/**
	 * On click listener for the items in the list
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			Intent intent = new Intent(WitnessesListActivity.this, AddWitnessActivity.class);
				
			Bundle extras = new Bundle();
			extras.putStringArray("witness", witnesses.get(position));
			extras.putBoolean("review", review);
			intent.putExtras(extras);
				
			startActivityForResult(intent, REQUEST_WITNESS);
			overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			
			isModify = true;
			witnessPosition = position;
		}
	};
	
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == addWitnessBtn) 
			{	
				Intent intent = new Intent(WitnessesListActivity.this, AddWitnessActivity.class);
				startActivityForResult(intent, REQUEST_WITNESS);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
				
				isModify = false;
			}
		}
	};
}
