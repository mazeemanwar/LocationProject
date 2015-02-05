package com.driverconnex.incidents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;

/**
 * Activity for displaying a tutorial for reporting an incident.
 * 
 * NOTE:
 * It's no longer used. 
 * @author Adrian Klimczak
 * 
 */

public class TutorialActivity extends Activity 
{
	public static final int INCIDENT_TUTORIAL_REPORT = 0;
	public static final int INCIDENT_TUTORIAL_PHOTO = 1;
	public static final int INCIDENT_TUTORIAL_VIDEO = 2;
	public static final int INCIDENT_TUTORIAL_WITNESS_DETAILS = 3;
	public static final int INCIDENT_TUTORIAL_SUBMITTED = 4;
	
	private int tutorial;
	
	private TextView text;
	private TextView title;
	private ImageView icon;
	
	private int incidentActivity = IncidentActivity.INCIDENT_ACCIDENT;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_tutorial);

		text = (TextView) findViewById(R.id.tutorialText);
		icon = (ImageView) findViewById(R.id.tutorialIcon);	
		title = (TextView) findViewById(R.id.tutorialTitle);
		
		if(getIntent().getExtras() != null)
		{
			tutorial = getIntent().getExtras().getInt("tutorial");
			incidentActivity = getIntent().getExtras().getInt("incidentActivity");
			
			if(tutorial == INCIDENT_TUTORIAL_REPORT)
			{
				title.setText(R.string.incidents_tutorial_report_title);
				text.setText(R.string.incidents_tutorial_report_text);
				icon.setImageResource(R.drawable.document_icon_03);
			}
			else if(tutorial == INCIDENT_TUTORIAL_PHOTO)
			{
				title.setText(R.string.incidents_tutorial_photos_title);
				text.setText("");
				icon.setImageResource(R.drawable.camera_icon_03);
			}
			else if(tutorial == INCIDENT_TUTORIAL_VIDEO)
			{
				title.setText(R.string.incidents_tutorial_video_title);
				text.setText(R.string.incidents_tutorial_video_text);
				icon.setImageResource(R.drawable.report_icon_03);
			}
			else if(tutorial == INCIDENT_TUTORIAL_WITNESS_DETAILS)
			{
				title.setText(R.string.incidents_tutorial_witness_title);
				text.setText(R.string.incidents_tutorial_witness_text);
				icon.setImageResource(R.drawable.eye_icon_03);
			}
			else if(tutorial == INCIDENT_TUTORIAL_SUBMITTED)
			{
				title.setText(R.string.incidents_tutorial_sucess_title);
				text.setText(R.string.incidents_tutorial_submitted_text);
				icon.setImageResource(R.drawable.tick_icon_03);
			}
		}
		
		getActionBar().setTitle("Close");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			if(tutorial == INCIDENT_TUTORIAL_SUBMITTED)
			{
				Intent intent = new Intent(TutorialActivity.this, IncidentActivity.class);
				intent.putExtra("tutorial", false);
				//intent.putExtra("incidentActivity", incidentActivity);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			}
			else
			{
				finish();
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			}
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
