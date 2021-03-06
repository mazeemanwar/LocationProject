package com.driverconnex.incidents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.Utilities;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for reviewing a report.
 * @author Adrian Klimczak
 *
 */

public class ReviewReportActivity extends Activity 
{
	private RelativeLayout loading;
	
	private TextView dateText;
	private TextView locationText;
	private TextView vehicleText;
	private TextView photoBtn;
	private TextView videoBtn;
	private TextView witnessesBtn;
	private TextView descriptionText;

	private DCIncident incident;
	
	private boolean photosAttached = false;
	
	private ArrayList<File> photoFiles = new ArrayList<File>();
	private File videoFile;
	
	private int reportPosition;             // Position of the report in the list
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_report_details);
		
		// Get views 
		dateText = (TextView) findViewById(R.id.dateTextView);
		locationText = (TextView) findViewById(R.id.locationTextView);
		vehicleText = (TextView) findViewById(R.id.vehicleTextView);
		descriptionText = (TextView) findViewById(R.id.descTextView);
		
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		photoBtn = (TextView) findViewById(R.id.photoText);
		videoBtn = (TextView) findViewById(R.id.videoText);
		witnessesBtn = (TextView) findViewById(R.id.witnessesText);
		
		photoBtn.setOnClickListener(onClickListener);
		videoBtn.setOnClickListener(onClickListener);
		witnessesBtn.setOnClickListener(onClickListener);
		
		if (getIntent().getExtras() != null) 
		{
			Bundle extras = getIntent().getExtras();
			
			incident = (DCIncident) extras.getSerializable("report");
			reportPosition = extras.getInt("reportPosition");
			
			dateText.setText(incident.getDate());
			locationText.setText((float)incident.getLatitude() + ", " + (float)incident.getLongitude());			
			descriptionText.setText(incident.getDescription());
		}
		
		if(incident != null)
		{		
			if(incident.getWitnesses() != null)
				witnessesBtn.setText(incident.getWitnesses().size()+ " Witnesses");
			
			if(incident.isVideoAttached())
				videoBtn.setText("Video Attached");
			else
				videoBtn.setText("No Video Attached");
		}
		
		getIncidentByParse();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.incident_report_details, menu);
		return super.onCreateOptionsMenu(menu);
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
		else if (item.getItemId() == R.id.action_export) 
		{
			exportFiles();
		}
		else if (item.getItemId() == R.id.action_delete) 
		{
			new AlertDialog.Builder(ReviewReportActivity.this)
			.setTitle("Delete Report")
			.setMessage("Are you sure to delete this report?")
			.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,	int which) 
				{
					deleteReport();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			}).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if(v == photoBtn)
			{
				if(photosAttached)
				{
					Intent intent = new Intent(ReviewReportActivity.this, IncidentPhotosActivity.class);
					intent.putExtra("review", true);
					intent.putExtra("incidentId", incident.getId());
					startActivity(intent);
					overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
				}
			}
			else if(v == videoBtn)
			{
				if(incident.isVideoAttached())
				{
					Intent intent = new Intent(ReviewReportActivity.this, IncidentVideoActivity.class);
					intent.putExtra("incident", incident);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
				}
			}
			else if(v == witnessesBtn)
			{
				Intent intent = new Intent(ReviewReportActivity.this, WitnessesListActivity.class);
				
				if(!incident.getWitnesses().isEmpty())
				{					
					ArrayList<Map<String,String>> witnessesMap  =  (ArrayList<Map<String, String>>) incident.getWitnesses();
					ArrayList<String[]> witnesses = new ArrayList<String[]>();
					
					// Convert map to suit needs of adapter
					for(int i=0; i<witnessesMap.size(); i++)
					{
						String[] witness = new String[4];
						witness[0] = (witnessesMap.get(i).get("witnessName"));
						witness[1] = (witnessesMap.get(i).get("witnessNumber"));
						witness[2] = (witnessesMap.get(i).get("witnessEmail"));
						witness[3] = (witnessesMap.get(i).get("witnessStatement"));
						
						witnesses.add(witness);
					}
					
					intent.putExtra("witnesses", witnesses);
					intent.putExtra("incident", incident);
					intent.putExtra("review", true);
					
					startActivity(intent);
					overridePendingTransition(R.anim.slide_left_sub, R.anim.slide_left_main);
				}
			}
		}
	};
	
	/**
	 * Get incident from the Parse database to get the rest of the details
	 */
	private void getIncidentByParse() 
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.getInBackground(incident.getId(),new GetCallback<ParseObject>() 
		{
			@Override
			public void done(final ParseObject parseIncident, ParseException e) 
			{
				if(e == null)
				{	
					// Get vehicle
					//----------------------------
					ParseObject vehicle = parseIncident.getParseObject("incidentVehicle");				
					vehicle.fetchIfNeededInBackground(new GetCallback<ParseObject>()
					{
						@Override
						public void done(ParseObject vehicle, ParseException e) 
						{
							if(e == null)
							{
								incident.setVehicleReg(vehicle.getString("vehicleRegistration"));
								vehicleText.setText(vehicle.getString("vehicleRegistration"));
							}
						}	
					});

					//----------------------------
								
					// Get number of photos attached
					//----------------------------
					ParseQuery<ParseObject> queryPhotos = parseIncident.getRelation("incidentPhotos").getQuery();
					int countPhotos = 0;
	
					try {
						countPhotos = queryPhotos.count();
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					
					photoBtn.setText(countPhotos + " Photos");
					
					if(countPhotos != 0)
						photosAttached = true;
					else
						photosAttached = false;	
					//----------------------------
				}
				else
				{
					Log.e("Get Incident", e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Exports files to an email
	 */
	private void exportFiles()
	{
		loading.setVisibility(View.VISIBLE);
		
		// Report String
		//----------------
		String reportString = "Date,Location Latitude, Location Longitude,Vehicle,Description\n";	
		
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append(reportString);
		
		String objectString = ""+ 
				incident.getDate()+"," +
				incident.getLatitude() +  "," +
				incident.getLongitude()+"," +
				incident.getVehicleReg()+"," +
				incident.getDescription()+"," + "\n";
		
		reportBuilder.append(objectString);
		
		// Witnesses String
		//----------------
		String witnessString = "witnessName,witnessEmail,witnessNumber,witnessStatement\n";
		
		StringBuilder witnessesBuilder = new StringBuilder();
		witnessesBuilder.append(witnessString);
		
		for(int i=0; i<incident.getWitnesses().size(); i++)
		{
			objectString = ""+ 
					incident.getWitnesses().get(i).get("witnessName")+"," +
					incident.getWitnesses().get(i).get("witnessEmail") +  "," +
					incident.getWitnesses().get(i).get("witnessNumber")+"," +
					incident.getWitnesses().get(i).get("witnessStatement")+"," + "\n";
			
			witnessesBuilder.append(objectString);
		}
		//-------------------------------------------------
		
		// Create file
		//===========================================
		// Incident Report
		//-------------------------------------------------
		final File reportFile = new File(getFilesDir() + "/" + "incident_report.csv");
		reportFile.setReadable(true,false);
		
		if( reportFile != null )
		{
		    try 
		    {
		    	FileOutputStream fOut = openFileOutput("incident_report.csv",  Context.MODE_WORLD_READABLE);
		    	OutputStreamWriter osw = new OutputStreamWriter(fOut); 
		    	osw.write(reportBuilder.toString());
				osw.flush();
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//-------------------------------------------------
		
		// Witnesses file
		//-------------------------------------------------
		final File witnessesFile = new File(getFilesDir() + "/" + "witnesses.csv");
		witnessesFile.setReadable(true,false);
		
		if( witnessesFile != null )
		{
		    try 
		    {
		    	FileOutputStream fOut = openFileOutput("witnesses.csv",  Context.MODE_WORLD_READABLE);
		    	OutputStreamWriter osw = new OutputStreamWriter(fOut); 
		    	osw.write(witnessesBuilder.toString());
				osw.flush();
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//-------------------------------------------------
		
		// Media files
		//-------------------------------------------------	
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.getInBackground(incident.getId(),new GetCallback<ParseObject>() 
		{
			@Override
			public void done(final ParseObject parseIncident, ParseException e) 
			{
				if(e == null)
				{					
					// Get number of photos attached
					ParseQuery<ParseObject> queryPhotos = parseIncident.getRelation("incidentPhotos").getQuery();
					queryPhotos.findInBackground(new FindCallback<ParseObject>()
					{
						@Override
						public void done(List<ParseObject> parsePhotos, ParseException e) 
						{
							ArrayList<byte[]> bytes = new ArrayList<byte[]>();
							
							for(int i=0; i<parsePhotos.size(); i++)
							{
								// Get photo from the parse
								ParseFile photo = (ParseFile) parsePhotos.get(i).get("photoFile");
								
								try {
									bytes.add(photo.getData());
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							
							photoFiles = AssetsUtilities.saveIncidentPhoto(ReviewReportActivity.this, bytes);
							
							// Video
							ParseFile parseVideo = (ParseFile) parseIncident.get("incidentVideo");	
									
							if(parseVideo != null)
							{
								parseVideo.getDataInBackground(new GetDataCallback()
								{
									@Override
									public void done(byte[] data, ParseException e) 
									{
										if(e == null)
										{
											// Save file
											videoFile = AssetsUtilities.saveIncidentVideo(ReviewReportActivity.this,data);
											
											finishSendingEmail(reportFile, witnessesFile);
										}
									}
								});
							}
							else
								finishSendingEmail(reportFile, witnessesFile);
						}
					});
				}
			}
		});
	}	
	
	/**
	 * Finishes sending an email
	 * @param reportFile
	 * @param witnessesFile
	 */
	private void finishSendingEmail(File reportFile, File witnessesFile)
	{
		ArrayList<String> paths = new ArrayList<String>();
		
		// Attach report CSV file
		paths.add(reportFile.getAbsolutePath());
		
		// Attach witnesses CSV file if the are any 
		if(!incident.getWitnesses().isEmpty())
			paths.add(witnessesFile.getAbsolutePath());
		
		// Attach photo files if the are any
		for(int i=0; i<photoFiles.size(); i++)
			paths.add(photoFiles.get(i).getAbsolutePath());
		
		// Attach video if there is one
		if(videoFile != null)
			paths.add(videoFile.getAbsolutePath());
		
		String recipient = ParseUser.getCurrentUser().getEmail();
		String subject = "Your DriverConnex Incident Report";
		
		// Send email
		Utilities.sendEmail(ReviewReportActivity.this, recipient, "", subject, "", paths);
		
		loading.setVisibility(View.INVISIBLE);	
	}
	
	/**
	 * Deletes report from the Parse database
	 */
	public void deleteReport()
	{
		loading.setVisibility(View.VISIBLE);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.getInBackground(incident.getId(), new GetCallback<ParseObject>() 
		{
			public void done(ParseObject object, ParseException e) 
			{
				if (e == null) 
				{		
					object.deleteInBackground(new DeleteCallback()
					{
						@Override
						public void done(ParseException e) 
						{
							Intent returnIntent = new Intent();
							returnIntent.putExtra("reportPosition", reportPosition);
							setResult(RESULT_OK, returnIntent);
							
							//  Report is deleted, go back to previous screen
							loading.setVisibility(View.INVISIBLE);
							finish();
							overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
						}
					});
				}
			}
		});
	}
}
