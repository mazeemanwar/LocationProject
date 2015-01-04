package com.driverconnex.incidents;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;

/**
 * Custom created video capture to record incident.
 * 
 * The simplest way would be to just use third party video player for recording instead of implementing manually our own but 
 * since you don't have any control and no guarantee that selected options will be executed by the third party app
 * it's better to implement its own. E.g on Sony Xperia S when low quality setting is selected, video player restricted length of the video 
 * to 20 seconds, while on Samsung Galaxy this restriction did not occur. 
 * 
 * @author Adrian Klimczak
 *
 */

public class IncidentVideoCaptureActivity extends Activity implements SurfaceHolder.Callback 
{
	private static final String TAG = "IncidentVideoCaptureActivity";
	
    private TextView durationText;
    private TextView cancelText;
    
    private ImageView startButton;
    private SurfaceView surfaceView;
    
	private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private Camera camera;
    private File videoFile;
        
    // Duration
    private Handler timerHandler = new Handler();
    private long timeElapsed = 0;
    private int durationHours;      // Used to display how many hours elapsed
    private int durationMinutes;    // Used to display how many minutes elapsed 
    private int durationSeconds;    // Used to display how many seconds elapsed
    
    private boolean recording = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.activity_incident_video_capture);
             
               
        //-----------------------
        durationText = (TextView) findViewById(R.id.durationText);
        startButton = (ImageView)findViewById(R.id.startBtn);
        cancelText = (TextView)findViewById(R.id.cancelText);
        
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        
        startButton.setOnClickListener(onClickListener);
        cancelText.setOnClickListener(onClickListener);
        //-----------------------

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onResume()
    {
    	super.onResume();

    	if(camera == null)
    		camera = Camera.open();

    	resetTime();
    	startButton.setImageResource(R.drawable.play_white);
    	cancelText.setVisibility(View.VISIBLE);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == 100) 
		{
			if (resultCode != RESULT_OK)
				return;
			
			boolean useVideo = data.getBooleanExtra("useVideo", false);
			
			if(useVideo)
			{
				if(videoFile != null)
				{	
					Intent returnIntent = new Intent();
					Uri uri = Uri.fromFile(videoFile);
					returnIntent.putExtra("video", uri);
					setResult(RESULT_OK, returnIntent);
					
					finish();	
					overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
				}
			}
		}
	}
    
    private OnClickListener onClickListener = new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			if(v == startButton)
			{
				// Check if it's not recording already
				if(!recording)
				{
		            startRecording();
			        recording = true;
				}
				else
				{
					stopRecording();
			        recording = false;
				}
			}
			// cancel
			else if(v == cancelText)
			{
				finish();
				overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
			}
		}  	
    };
    
    /**
     * Starts recording video
     */
    protected void startRecording() 
    {
    	// Reset duration
    	timeElapsed = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    	
        resetTime();
        
        if(camera == null)
        	camera = Camera.open();
       
        // Stop previewing
        camera.stopPreview();
        
        mediaRecorder = new MediaRecorder();
        camera.unlock();

        mediaRecorder.setCamera(camera);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface()); 
        
        videoFile = initFile();
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());

        try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        mediaRecorder.start();
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        cancelText.setVisibility(View.INVISIBLE);
        startButton.setImageResource(R.drawable.stop_white);
    }

    /**
     * Stops recording video.
     */
    private void stopRecording() 
    {
    	// Remove timer
    	if(timerHandler != null)
			timerHandler.removeCallbacks(timerRunnable);
    	
    	assert this.mediaRecorder != null;
        
    	// Try to stop mediaRecorder
    	try {
            mediaRecorder.stop();
        } catch (RuntimeException e) 
        { 	
        	// Handles case when MediaRecorder failed to stop recording
            if (videoFile != null && videoFile.exists() && videoFile.delete()) 
            {
                Log.d(TAG, "Deleted " + videoFile.getAbsolutePath());
                
                // Since it failed to stop recording e.g when user quickly presses stop button twice , let it open camera again for a new recording. 
            	if(camera == null)
            		camera = Camera.open();
            	
            	// Resets time
            	resetTime();
            	// Displays play button
            	startButton.setImageResource(R.drawable.play_white);
            	cancelText.setVisibility(View.VISIBLE);
            }
            return;
        } finally {
        	// releases media recorder
           releaseMediaRecorder();
        }
        
        // Make sure that video file was created 
        if (videoFile != null || videoFile.exists()) 
        {
            // Screen doesn't need to be kept on anymore
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            
            Intent intent = new Intent(this, IncidentVideoCapturedPreviewActivity.class);
            intent.setData(Uri.fromFile(videoFile));
            
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) 
    {
    	if (camera != null)
        {
        	initCamera();
        }
        else 
        {
            Toast.makeText(getApplicationContext(), "Camera not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) 
    {
    	if(camera != null)
    	{
    		camera.stopPreview();
    		camera.release();
    		camera = null;
    	}
    }
    
    /**
     * Initialises file for the video
     * @return
     */
    private File initFile() 
    {
        File dir = new File(Environment.getExternalStorageDirectory(), AssetsUtilities.DRIVERCONNEX_PATH);
        File file;
        
        if (!dir.exists() && !dir.mkdirs()) 
        	file = null;
        else 
            file = new File(dir.getAbsolutePath(), "incident_video.mp4");
        
        return file;
    }
    
    /**
     * Initialises camera
     */
    private void initCamera()
    {
    	if(camera != null)
    	{
        	Parameters params = camera.getParameters();
            camera.setParameters(params);
        	
            // Camera orientation is set to landscape and this activity is in portrait, so adjust it
            camera.setDisplayOrientation(90);
            
            // Preview video
            try {
    			camera.setPreviewDisplay(surfaceHolder);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            camera.startPreview();
    	}
    }
    
    /**
     * Resets time
     */
    private void resetTime()
    {
    	durationHours = 0;
        durationMinutes = 0;
        durationSeconds = 0;
        
        durationText.setText("00:00:00");
    }
  
    /**
     * Timer used to calculate duration of the video. It also handles displaying time in this format
     * (00:00:00). 
     */
    private Runnable timerRunnable = new Runnable() 
    {
        @Override
        public void run() 
        {
            long millis = System.currentTimeMillis() - timeElapsed;
            int seconds = (int) (millis / 1000);
            
            // Check if 1 second elapsed
            if(seconds >= 1)
            {
            	timeElapsed = System.currentTimeMillis();
            	
            	// Count seconds
            	durationSeconds ++;

            	// Check if 1 minute elapsed
            	if(durationSeconds >= 60)
            	{
            		durationSeconds -= 60;
            		durationMinutes ++;
            	}
            	
            	// Check if 1 hour elapsed
            	if(durationMinutes >= 60)
            	{
            		durationMinutes -= 60;
            		durationHours ++;
            	}
            	
            	// Format the time to be 00:00:00
            	String secondsStr;
            	String minutesStr;
            	String hoursStr;
            	
            	if(durationSeconds < 10)
            		secondsStr = "0" + durationSeconds;
            	else
            		secondsStr = "" + durationSeconds;
            	
            	if(durationMinutes < 10)
            		minutesStr = "0" + durationMinutes;
            	else
            		minutesStr = "" + durationMinutes;
            	
            	if(durationHours < 10)
            		hoursStr = "0" + durationHours;
            	else
            		hoursStr = "" + durationHours;
            		
            	// Update duration
            	durationText.setText("" + hoursStr + ":" + minutesStr + ":" + secondsStr);
            }
                
            timerHandler.postDelayed(this, 500);
        }
    };
    
    /**
     * Releases media recorder once it's not needed.
     */
    private void releaseMediaRecorder() 
    {
        if (this.mediaRecorder != null) 
        {
            this.mediaRecorder.reset();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
    }
}