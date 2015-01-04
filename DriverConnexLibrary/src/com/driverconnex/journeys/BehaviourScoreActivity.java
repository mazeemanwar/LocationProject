package com.driverconnex.journeys;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.ui.ArcView;

/**
 * Activity for reporting an incident.
 * 
 * @author Adrian Klimczak
 * 
 */

public class BehaviourScoreActivity extends Activity 
{
	private final long ANIMATION_DURATION = 3500;
	
	private TextView scoreText;
	private TextView descriptionText;
	private ImageView background;
	private ImageView greenDot;
	private ImageView blueDot;
	private ImageView orangeDot;
	
	private RelativeLayout layout;

	private int accelerationScore;
	private int brakingScore;
	private int steeringScore;
	private int points;
	private String description;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journey_behaviour_score);

		greenDot = (ImageView) findViewById(R.id.dotGreenImage);	
		blueDot = (ImageView) findViewById(R.id.dotBlueImage);
		orangeDot = (ImageView) findViewById(R.id.dotOrangeImage);
		background = (ImageView) findViewById(R.id.backgroundImage);
		scoreText = (TextView) findViewById(R.id.scoreTextView);
		descriptionText = (TextView) findViewById(R.id.descriptionTextView);
		layout = (RelativeLayout) findViewById(R.id.relativeLayout1);
		
		if(getIntent().getExtras() != null)
		{
			String score = getIntent().getStringExtra("score");
			Log.d("Behaviour Score", ""+score);
			
			decodeScore(score);
		}
		
		// Wait for views to be put on the layout first
		background.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
		{
		    public void onGlobalLayout() 
		    {
		    	background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			    
		    	descriptionText.setText(description);
			    setRingValue(accelerationScore, "green_ring");
				setRingValue(brakingScore, "blue_ring");
				setRingValue(steeringScore, "orange_ring");
				setScoreValue(points);
		    }
		});
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
	
	void setRingValue(int value, String tag)
	{	
		// Dot
		//--------------------------
		double rotationRadians = ((Math.PI * 2.0)/100) * value;
		float rotationDegrees = (float) Math.toDegrees(rotationRadians);
		
		RotateAnimation rotationAnim = new RotateAnimation(0f, rotationDegrees, Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
		rotationAnim.setInterpolator(new LinearInterpolator());
		rotationAnim.setRepeatCount(0);
		rotationAnim.setFillAfter(true);
		rotationAnim.setDuration(ANIMATION_DURATION);
		
		// Arc
		//--------------------------     
	    final ArcView arc = new ArcView(this);
		
	    // Set up the shape of the arc 
	    if(tag.equals("green_ring"))
	    {
	    	arc.radius = 228f;
	    	arc.paint.setColor(Color.rgb(144, 180, 92));
	    }
	    if(tag.equals("blue_ring"))
	    {
	    	arc.radius = 247f;
	        arc.paint.setColor(Color.rgb(38, 158, 208));
	    }
	    else if(tag.equals("orange_ring"))  
	    {
	    	arc.radius = 268;
	    	arc.paint.setColor(Color.rgb(210, 133, 45));
	    }
	    
	    arc.paint.setStrokeWidth(5); 
	    
	    LayoutParams params = new LayoutParams(0);
	    
	    int[] locations = new int[2];
        background.getLocationOnScreen(locations);
        
        arc.x = background.getWidth()/2;
        arc.y = background.getHeight()/2;
        
        // Animate arc
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, rotationDegrees-2);
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) 
            {
                float value = ((Float) (animation.getAnimatedValue())).floatValue();
                arc.angle = (int) value;
                arc.invalidate();
            }
        });
                        
        valueAnimator.start();
        
        layout.addView(arc, params);
        
		// Start animations
		if(tag.equals("green_ring"))
		{
			greenDot.startAnimation(rotationAnim);
			greenDot.bringToFront();
		}
		else if(tag.equals("blue_ring"))
		{
			blueDot.startAnimation(rotationAnim);
			blueDot.bringToFront();
		}
		else if(tag.equals("orange_ring"))
		{
			orangeDot.startAnimation(rotationAnim);
			orangeDot.bringToFront();
		}
	}
	
	private void setScoreValue(int score)
	{
		ValueAnimator scoreAnimator = ValueAnimator.ofInt(0, score);
        scoreAnimator.setDuration(ANIMATION_DURATION);
        scoreAnimator.setInterpolator(new LinearInterpolator());
        scoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) 
            {
                int value = ((Integer) (animation.getAnimatedValue())).intValue();
                scoreText.setText(""+value);
            }
        });
        scoreAnimator.addListener(new AnimatorListener()
        {
			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) 
			{
				Animation anim = new AlphaAnimation((float) 0.0f, 1.0f);
				anim.setDuration(900);
				anim.setRepeatMode(0);
				anim.setFillAfter(true);
							
				descriptionText.setVisibility(View.VISIBLE);
				descriptionText.startAnimation(anim);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
        	
        });
        scoreAnimator.start();
	}
	
	private void decodeScore(String score)
	{
		if(score != null)
		{
			/*
			 * Score string is in this format:
			 * 
				Acceleration Score:20,
		    	Braking Score:30,
		    	Steering Score:40,
		    	Description:Great,
		    	Points Awarded:500
			 */
			
			String[] scores = score.split(",");
			
			ArrayList<String> scoresValue = new ArrayList<String>();
			
			for(int i=0; i<scores.length; i++)
			{
				scoresValue.add(scores[i].split(":")[1]);
			}
			
			accelerationScore = Integer.parseInt(scoresValue.get(0));
			brakingScore = Integer.parseInt(scoresValue.get(1));
			steeringScore = Integer.parseInt(scoresValue.get(2));
			description = scoresValue.get(3);
			points = Integer.parseInt(scoresValue.get(4));
		}
	}
}
