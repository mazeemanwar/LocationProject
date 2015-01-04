package com.driverconnex.incidents;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for previewing downloaded from the Parse database incident video.
 * @author Adrian Klimczak
 *
 */

public class IncidentVideoActivity extends Activity 
{
	private RelativeLayout loading;
	private VideoView videoView;
	private	DCIncident incident;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		videoView = (VideoView) findViewById(R.id.videoView);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		if (getIntent().getExtras() != null)
		{
			Bundle extras = getIntent().getExtras();
			incident = (DCIncident) extras.getSerializable("incident");
		}
		
		getActionBar().setTitle(R.string.title_activity_back);
		displayVideo();
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
	
	private void displayVideo()
	{
		loading.setVisibility(View.VISIBLE);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCIncident");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.getInBackground(incident.getId(),new GetCallback<ParseObject>() 
		{
			@Override
			public void done(ParseObject object, ParseException e) 
			{
				if(e == null)
				{	
					ParseFile videoFile = (ParseFile) object.get("incidentVideo");	
					
					videoFile.getDataInBackground(new GetDataCallback()
					{
						@Override
						public void done(byte[] data, ParseException e) 
						{
							if(e == null)
							{
								// Save file
								File file = AssetsUtilities.saveIncidentVideo(IncidentVideoActivity.this,data);
								
								// Play video
								MediaController controller = new MediaController(IncidentVideoActivity.this);
								
								videoView.setVideoPath(file.getPath());
								videoView.setMediaController(controller);		        
								videoView.start(); 
								
								loading.setVisibility(View.INVISIBLE);
							}
							
							loading.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		});
	}
}
