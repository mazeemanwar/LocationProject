package com.driverconnex.vehicles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for updating documentation of the vehicle i.e. MOT, Road Tax
 * 
 * @author Yin Li (SGI)
 * @author Adrian Klimczak
 * 
 */

public class UpdateDocumentationActivity extends Activity 
{
	private int[] textIds = {R.id.expiryText, R.id.statusText };
	private EditText dateEdit;
	private DCVehicle vehicle;
	private DatePickerDialog datePickerDialog;
	private int index;
	private RelativeLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_documentation_update);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		dateEdit = (EditText) findViewById(R.id.dateEdit);
		dateEdit.setOnClickListener(onClickListener);
		dateEdit.setOnFocusChangeListener(onFocusChangeListener);

		init();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		dateEdit.setInputType(InputType.TYPE_NULL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.update, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			intent.putExtra("vehicle", vehicle);
			finish();
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_update) 
		{
			// Check if user input date
			if (dateEdit.getText().length() != 0) 
			{
				loading.setVisibility(View.VISIBLE);
				vehicle.setDocumentations(index, dateEdit.getText().toString());

				ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
				// Retrieve the object by id
				query.getInBackground(vehicle.getId(),	new GetCallback<ParseObject>() 
				{
					@Override
					public void done(ParseObject object, com.parse.ParseException e) 
					{
						if (e == null) 
						{
							if (vehicle.getDocumentationByOrder(index) != null) 
							{
								String field = null;
								Date value = null;
								switch (index) 
								{
								case 0:
									field = "vehicleTaxDate";
									value = vehicle.getRoadTaxDate();
									break;
								case 1:
									field = "vehicleMotDate";
									value = vehicle.getMOTDate();
									break;
								case 2:
								
									field = "vehicleServiceDate";
									value = vehicle.getServiceDate();
									break;
								default:
									break;
								}
								
								object.put(field, value);
								object.saveInBackground();
							}
							
							loading.setVisibility(View.INVISIBLE);
							
							for (int i = 0; i < textIds.length; i++) 
							{
								TextView text = (TextView) findViewById(textIds[i]);
								setAllText(index, i, text);
							}
							
							dateEdit.setText(null);
						}
					}
				});
			}
			// User didn't input date, therefore display warning
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setTitle("Error")
			    	   .setMessage("No date selected")
		    		   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
		    	       {
		    	           public void onClick(DialogInterface dialog, int id) 
		    	           {
		    	           }
		    	       });
		    	
	    		AlertDialog dialog = builder.create();
	    		dialog.show();
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void init() 
	{
		if (getIntent().getExtras() != null) 
		{
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable("vehicle");
			index = getIntent().getExtras().getInt("index");
			getActionBar().setTitle(getResources().getStringArray(R.array.document_titles)[index]);
			
			for (int i = 0; i < textIds.length; i++) 
			{
				TextView text = (TextView) findViewById(textIds[i]);
				setAllText(index, i, text);
			}
		}
	}
	
	private void initDatePickerDialog() 
	{
		Calendar c = Calendar.getInstance();
		datePickerDialog = new DatePickerDialog(this, mDateSetListener,
				c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() 
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
			dateEdit.setText(new StringBuilder().append(Utilities.intToTime(dayOfMonth)).append("-")
					.append(Utilities.intToTime(monthOfYear + 1)).append("-")
					.append(year));
		}

	};

	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == dateEdit) {
				initDatePickerDialog();
				datePickerDialog.show();
			}
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() 
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			if (v == dateEdit) 
			{
				if (hasFocus) 
				{
					initDatePickerDialog();
					datePickerDialog.show();
				}
			}
		}
	};

	/*
	 *  Sets texts for documentation items
	 */
	private void setAllText(int index, int position, TextView view) 
	{
		String str = null;
		
		switch (position) 
		{
		case 0:
			if (vehicle.getDocumentationByOrder(index) != null) 
				str = vehicle.getDocumentationByOrder(index).toString();
			break;
		case 1:
			try 
			{
				if (vehicle.getDocumentationByOrder(index) != null) 
				{
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					Date today = new Date();
					Date date = format.parse(vehicle.getDocumentationByOrder(index).toString());
					
					if (date.before(today)) 
					{
						str = "Expired";
						view.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					} 
					else 
					{
						str = "Valid";
						view.setTextColor(getResources().getColor(android.R.color.holo_green_light));
					}
				} 
				else 
				{
					str = "Not Set";
					view.setTextColor(getResources().getColor(android.R.color.holo_red_light));
				}
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			break;
			
		default:
			break;
		}

		if (str != null) {
			view.setText(str);
		}
	}
}
