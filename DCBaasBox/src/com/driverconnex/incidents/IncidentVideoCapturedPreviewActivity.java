package com.driverconnex.incidents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.driverconnex.app.R;

/**
 * Activity for previewing captured incident video.
 * @author Adrian Klimczak
 *
 */

public class IncidentVideoCapturedPreviewActivity extends Activity
{
	private VideoView videoView;
	private TextView retakeText;
	private TextView useVideoText;
	private MediaController mediaController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.activity_incident_video_captured);

		videoView = (VideoView) findViewById(R.id.videoView);
		retakeText = (TextView) findViewById(R.id.retakeText);
		useVideoText = (TextView) findViewById(R.id.useVideoText);
		
		retakeText.setOnClickListener(onClickListener);
		useVideoText.setOnClickListener(onClickListener);
		
		//retakeText.
		
		RelativeLayout controllerSpace = (RelativeLayout) findViewById(R.id.mediaControllerSpace);
		
		if (getIntent().getData() != null)
		{	
			mediaController = new MediaController(this)
			{
				@Override
				public void setMediaPlayer(MediaPlayerControl player) {
					super.setMediaPlayer(player);
					this.show();
				}
				
				@Override
				public void show(int timeout) {
					super.show(0);
				}
				
				@Override
	            public void hide() {
	                super.show();
	            }
				
				@Override
				public void onFinishInflate()
				{
					super.onFinishInflate();
					
				}
			}; 

			mediaController.setAnchorView(controllerSpace);

			// Play video
			videoView.setVideoPath(getIntent().getData().getPath());//videoFile.getPath());
			videoView.setMediaController(mediaController);		        
			videoView.start(); 
		}
	}
	
    private OnClickListener onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			if(v == retakeText)
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
				finish();
				overridePendingTransition(R.anim.slide_out, R.anim.null_anim);
			}
			else if(v == useVideoText)
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("useVideo", true);
				setResult(RESULT_OK, returnIntent);
				finish();
				overridePendingTransition(R.anim.slide_out, R.anim.null_anim);
			}
		}  	
    };
}
