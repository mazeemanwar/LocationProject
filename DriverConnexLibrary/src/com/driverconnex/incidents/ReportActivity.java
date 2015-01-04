package com.driverconnex.incidents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.TimePicker;

import com.driverconnex.app.R;
import com.driverconnex.journeys.AddLocationMapActivity;
import com.driverconnex.journeys.DCJourneyPoint;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.Utilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.VehiclesListActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity for reporting incident.
 * 
 * @author Adrian Klimczak
 * 
 */

public class ReportActivity extends Activity 
{
	private final int REQUEST_LOCATION = 200;
	private final int REQUEST_VEHICLE = 300;
	private final int REQUEST_PHOTO = 400;
	private final int REQUEST_VIDEO = 500;
	private final int REQUEST_WITNESSES = 600;
	
	private static TextView dateText;
	private static TextView timeText;
	private TextView locationText;
	private TextView vehicleText;
	
	private EditText descEdit;
	
	private TextView photoText;
	private TextView videoText;
	private TextView witnessesText;
	
	private RelativeLayout loading;
	
	private DCJourneyPoint locationPoint;
	private DCVehicle vehicle;	
	private ArrayList<Uri> photoUri;
	private Uri videoUri;
	private ArrayList<String[]> witnesses;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_report);

		// Get views
		dateText = (TextView) findViewById(R.id.dateText);
		timeText = (TextView) findViewById(R.id.timeText);
		vehicleText = (TextView) findViewById(R.id.vehicleText);
		locationText = (TextView) findViewById(R.id.locationText);
		
		descEdit = (EditText) findViewById(R.id.descEdit);
		
		photoText = (TextView) findViewById(R.id.photoText);
		videoText = (TextView) findViewById(R.id.videoText);
		witnessesText = (TextView) findViewById(R.id.witnessesText);
		
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		// Set listeners
		vehicleText.setOnClickListener(onClickListener);
		dateText.setOnClickListener(onClickListener);
		timeText.setOnClickListener(onClickListener);
		locationText.setOnClickListener(onClickListener);
		descEdit.setOnClickListener(onClickListener);
		photoText.setOnClickListener(onClickListener);
		videoText.setOnClickListener(onClickListener);
		witnessesText.setOnClickListener(onClickListener);
		
		timeText.setOnFocusChangeListener(onFocusChangeListener);
		dateText.setOnFocusChangeListener(onFocusChangeListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Check if we got results from AddLocationMapActivity
		if (requestCode == REQUEST_LOCATION) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				locationPoint = data.getParcelableExtra("point");
				locationText.setText((float)locationPoint.getLat() + ", " + (float)locationPoint.getLng());
			}
		}
		// Check if we got results from VehicleListActivity
		else if (requestCode == REQUEST_VEHICLE) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{
				vehicle = (DCVehicle) data.getExtras().getSerializable("vehicle");
				
				if(vehicle != null)
					vehicleText.setText(vehicle.getRegistration());
			}
		}
		// Check if we got results from IncidentPhotosActivity
		else if (requestCode == REQUEST_PHOTO) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{			
				photoUri = (ArrayList<Uri>) data.getExtras().getSerializable("photos");

				if(photoUri != null)
					photoText.setText(photoUri.size() + " Photos");
			}
		}
		// Check if we got results from IncidentVideoCaptureActivity
		else if (requestCode == REQUEST_VIDEO) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{			
				videoUri = data.getExtras().getParcelable("video");
					
				if(videoUri != null)
					videoText.setText("Video Taken");
			}
		}
		// Check if we got results from WitnessesListActivity
		else if (requestCode == REQUEST_WITNESSES) 
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK) 
			{			
				witnesses = (ArrayList<String[]>) data.getExtras().getSerializable("witnesses");

				if(witnesses != null)
					witnessesText.setText(witnesses.size() + " Witnesses");
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_submit, menu);
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
		else if (item.getItemId() == R.id.action_submit) 
		{
			if (dateText.getText().length() == 0) 
			{
				new AlertDialog.Builder(ReportActivity.this)
						.setTitle("Error")
						.setMessage(getResources().getString(R.string.incidents_error_date))
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog,	int which) 
							{
							}
						}).show();
			}
			else if (timeText.getText().length() == 0) 
			{
				new AlertDialog.Builder(ReportActivity.this)
						.setTitle("Error")
						.setMessage(getResources().getString(R.string.incidents_error_time))
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog,	int which) 
							{
							}
						}).show();
			}
			else if(locationText.getText().length() == 0)
			{
				new AlertDialog.Builder(ReportActivity.this)
				.setTitle("Error")
				.setMessage(getResources().getString(R.string.incidents_error_location))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,	int which) 
					{
					}
				}).show();
			}
			else if(vehicleText.getText().length() == 0)
			{
				new AlertDialog.Builder(ReportActivity.this)
				.setTitle("Error")
				.setMessage(getResources().getString(R.string.incidents_error_vehicle))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,	int which) 
					{
					}
				}).show();
			}
			// Otherwise user provided all needed information, so submit report
			else 
			{
				new SaveIncidentTask().execute();
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			// Check if user touched date field
			if (v == dateText) 
			{
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
			else if (v == timeText) 
			{
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "timePicker");
			}
			else if(v == locationText)
			{
				Intent intent = new Intent(ReportActivity.this, AddLocationMapActivity.class);
				startActivityForResult(intent, REQUEST_LOCATION);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			else if(v == vehicleText)
			{
				Bundle extras = new Bundle();
				extras.putBoolean("vehiclePicker", true);
				
				Intent intent = new Intent(ReportActivity.this, VehiclesListActivity.class);
				intent.putExtras(extras);
				
				startActivityForResult(intent, REQUEST_VEHICLE);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			else if(v == photoText)
			{
				Intent intent = new Intent(ReportActivity.this, IncidentPhotosActivity.class);
					
				// Check if there are any photos
				if(photoUri != null)
				{
					Bundle extras = new Bundle();
					extras.putParcelableArrayList("photos", photoUri);
					intent.putExtras(extras);
				}
				
				startActivityForResult(intent, REQUEST_PHOTO);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			else if(v == videoText)
			{
				Bundle extras = new Bundle();
				
				Intent intent = new Intent(ReportActivity.this, IncidentVideoCaptureActivity.class);
				intent.putExtras(extras);
				
				startActivityForResult(intent, REQUEST_VIDEO);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
			else if(v == witnessesText)
			{
				Intent intent = new Intent(ReportActivity.this, WitnessesListActivity.class);
				
				// Check if there are any witnesses
				if(witnesses != null)
				{
					Bundle extras = new Bundle();
					extras.putSerializable("witnesses", witnesses);
					intent.putExtras(extras);
				}

				startActivityForResult(intent, REQUEST_WITNESSES);
				overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
			}
		}
	};

	/**
	 * This is needed, because views are not very responsive.
	 */
	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() 
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{	
			if (v == dateText) 
			{
				if (hasFocus) 
				{
					DialogFragment newFragment = new DatePickerFragment();
					newFragment.show(getFragmentManager(), "datePicker");
				}
			}
			else if (v == timeText) 
			{
				if (hasFocus) 
				{
					DialogFragment newFragment = new TimePickerFragment();
					newFragment.show(getFragmentManager(), "timePicker");
				}
			}
		}
	};
	
	/**
	 * Passes report to Parse database
	 *
	 */
	private class SaveIncidentTask extends AsyncTask<String, Void, String> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) 
		{
			// Save report into Parse
			saveInParse();
			
			return null;
		}
			
		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			loading.setVisibility(View.INVISIBLE);
			
			Intent intent = new Intent(ReportActivity.this, TutorialActivity.class);
			intent.putExtra("tutorial", TutorialActivity.INCIDENT_TUTORIAL_SUBMITTED);
			//intent.putExtra("incidentActivity", incidentActivity);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in, R.anim.null_anim);
		}
	}
	
	/**
	 * Saves incident into Parse database
	 */
	private void saveInParse()
	{
		loading.setVisibility(View.VISIBLE);
		
		// Get currently logged in user
		final ParseUser user = ParseUser.getCurrentUser();
					
		// Create DC Incident object
		final ParseObject parseIncident = new ParseObject("DCIncident");
		
		// Convert date into format that Parse will read
		final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date date = null;
					
		try {
			//date = format.parse(incident.getDate());
			date = format.parse(dateText.getText().toString() + " " + timeText.getText().toString());
		} catch (java.text.ParseException e1) {
			e1.printStackTrace();
		}
					
		// Convert coordinates into Geo Point
		ParseGeoPoint geoPoint = new ParseGeoPoint(locationPoint.getLat(), locationPoint.getLng());
					
		// Convert witnesses into format used in Parse database
		ArrayList<Map<String,String>> witnessesConverted = new ArrayList<Map<String,String>>();

		if(witnesses != null)
		{
			for(int i=0; i<witnesses.size(); i++)
			{
				witnessesConverted.add(new HashMap<String,String>());				
				witnessesConverted.get(i).put("witnessEmail", witnesses.get(i)[2]);
				witnessesConverted.get(i).put("witnessName", witnesses.get(i)[0]);
				witnessesConverted.get(i).put("witnessNumber", witnesses.get(i)[1]);
				witnessesConverted.get(i).put("witnessStatement", witnesses.get(i)[3]);
			}
		}

					
		// Get a video
		ParseFile videoFile = null;
					
		if(videoUri != null)
			videoFile = new ParseFile(AssetsUtilities.getBytesFromVideo(ReportActivity.this,videoUri));
					
		// Put data into Parse database
		parseIncident.put("incidentDescription", descEdit.getText().toString());
		parseIncident.put("incidentLocation", geoPoint);
		parseIncident.put("incidentDate", date);
		parseIncident.put("incidentWitnesses", witnessesConverted);
		parseIncident.put("incidentUser", user);
					
		// Put video if there is any
		if(videoFile != null)
			parseIncident.put("incidentVideo", videoFile);
		
		// We are done with putting all information of the incident,
		// so now we need to query for the vehicle that user was driving 
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
		query.whereMatches("vehicleRegistration", vehicle.getRegistration());
		query.getInBackground(vehicle.getId(), new GetCallback<ParseObject>() 
		{
			public void done(ParseObject vehicle, ParseException e) 
			{
				if (e == null) 
				{	
					parseIncident.put("incidentVehicle", vehicle);
					
					final ArrayList<ParseObject> photos = new ArrayList<ParseObject>();
					
					if(photoUri != null)
					{
						// Loop through all photos taken by user
						for(int i=0; i<photoUri.size(); i++)
						{					
							// Convert photo into bytes form
							byte[] bytes = AssetsUtilities.getBytesFromImage(ReportActivity.this, photoUri.get(i));	
											
							// Create parse file object
							ParseFile file = new ParseFile(bytes, "photo" + i + ".png");
											
							// Create parse DCPhoto object
							ParseObject parsePhoto = new ParseObject("DCPhoto");
											
							// Put a file into this object
							parsePhoto.put("photoFile", file);
							
							// Keep the reference of the photo, we need it for relations
							photos.add(parsePhoto);
						}
					}

					
					if(!photos.isEmpty())
					{
						// Loop through all photos again
						for(int i=0; i<photos.size(); i++)
						{
							final int position = i;
							
							// Make sure that photos are first saved
							photos.get(i).saveInBackground(new SaveCallback()
							{
								@Override
								public void done(ParseException e) 
								{
									// and once all photos are saved, we can save incident object
									if(e == null && position == photos.size()-1)
									{
										// Save DC Incident object
										parseIncident.saveInBackground(new SaveCallback()
										{
											// We are done with saving report
											@Override
											public void done(ParseException arg0) 
											{
												// Get relation
												ParseRelation<ParseObject> relation = parseIncident
														.getRelation("incidentPhotos");
												
												// Loop through all photos and add each into relation
												for(int i=0; i<photos.size(); i++)
													relation.add(photos.get(i));
												
												// Save final DC Incident object / report
												try {
													parseIncident.save();
													user.save();
												} catch (ParseException e) {
													e.printStackTrace();
												}
											}
										});
									}
								}
							});
						}
					}
					// There are no photos to save, so do a normal saving
					else
					{
						parseIncident.saveInBackground();
						
						try {
							user.save();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
				else
					Log.e("get vehicle", e.getMessage());
			}

		});
	}
	
	public static class DatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener 
	{	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			dateText.setText(new StringBuilder().append(Utilities.intToTime(day)).append("-")
					.append(Utilities.intToTime(month + 1)).append("-")
					.append(year));	
		}
	}

	public static class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener 
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			// Do something with the time chosen by the user
			timeText.setText(new StringBuilder()
			.append(Utilities.intToTime(hourOfDay)).append(":")
			.append(Utilities.intToTime(minute)));
		}
	}
}
