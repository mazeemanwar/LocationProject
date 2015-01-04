package com.driverconnex.basicmodules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;

/**
 * Activity for displaying settings of the app.
 * @author Adrian Klimczak
 *
 */

public class SettingsActivity extends Activity 
{
	private EditText expensePerMileEditText;
	private TextView automaticTrackingText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		expensePerMileEditText = (EditText) findViewById(R.id.expensePerMileEditText);
		automaticTrackingText = (TextView) findViewById(R.id.automaticTrackingText);
		
		automaticTrackingText.setOnClickListener(onClickListener);
		
		expensePerMileEditText.setText(""+DriverConnexApp.getUserPref().getExpensePerMile());
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Update settings
		float expensePerMile = Float.parseFloat(expensePerMileEditText.getText().toString());        
        DriverConnexApp.getUserPref().setExpensePerMile(expensePerMile);
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
	
	/**
	 * Handles what happens when specific view of the item on the list is clicked/touched
	 */
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{	
			if(v == automaticTrackingText)
			{
				Intent intent = new Intent(SettingsActivity.this, BluetoothDevicesListActivity.class);
				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
		}
	};
}
