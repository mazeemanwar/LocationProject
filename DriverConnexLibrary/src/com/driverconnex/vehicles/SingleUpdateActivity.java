package com.driverconnex.vehicles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for updating just a single field of the DCVehicle, e.g. mileage or finance. 
 * 
 * @author Adrian Klimczak
 * 
 */

public class SingleUpdateActivity extends Activity 
{
	public static final int UPDATE_MILEAGE = 1;
	public static final int UPDATE_FINANCE = 2;
	
	private EditText editText;
	private RelativeLayout loading;
	
	private DCVehicle vehicle;
	
	private int update = UPDATE_MILEAGE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_single_update);

		editText = (EditText) findViewById(R.id.editText);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		if (getIntent().getExtras() != null) 
		{
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable("vehicle");
			update = getIntent().getExtras().getInt("update");
		}
		
		switch(update)
		{
		case UPDATE_MILEAGE:
			getActionBar().setTitle(R.string.vehicle_title_mileage);
			editText.setHint(R.string.vehicle_enter_mileage);
			break;
		case UPDATE_FINANCE:
			getActionBar().setTitle(R.string.vehicle_title_finance);
			editText.setHint(R.string.vehicle_enter_finance);
			break;
		}
		
		editText.requestFocus();		
		editText.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) 
			{
				// Check if "Done" key is pressed
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) 
				{
					Utilities.hideIM(SingleUpdateActivity.this, v);
					
					// Checks if user inputed any number
					if(!editText.getText().toString().isEmpty())
					{
						switch(update)
						{
						case UPDATE_MILEAGE:
							vehicle.setCurrentMileage(Long.parseLong(editText.getText().toString()));
							break;
						case UPDATE_FINANCE:
							vehicle.setMonthlyFinance(Float.parseFloat(editText.getText().toString()));
							break;
						}
						
						loading.setVisibility(View.VISIBLE);
						
						// Gets vehicle
						ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
						query.getInBackground(vehicle.getId(),	new GetCallback<ParseObject>() 
						{
							@Override
							public void done(ParseObject object, com.parse.ParseException e) 
							{
								if (e == null) 
								{
									// Updates field in the Parse
									switch(update)
									{
									case UPDATE_MILEAGE:
										object.put("vehicleOdometer", vehicle.getCurrentMileage());
										break;
									case UPDATE_FINANCE:
										object.put("vehicleMonthlyFinance", vehicle.getMonthlyFinance());
										break;
									}
									
									object.saveInBackground();
									
									loading.setVisibility(View.INVISIBLE);
									
									// Finishes this activity
									Intent intent = new Intent();
									setResult(RESULT_OK, intent);
									intent.putExtra("vehicle", vehicle);
									
									finish();
									overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
								}
							}
						});
					}
					else 
					{
						finish();
						overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
					}
				}
				
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
