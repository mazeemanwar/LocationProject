package com.driverconnex.fragments;

import java.util.HashMap;
import java.util.List;

import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.basicmodules.BadgeActivity;
import com.driverconnex.basicmodules.LeaderboardActivity;
import com.driverconnex.ui.ArcView;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * A DC Rating section fragment for the DriverConnex dashboard.
 * 
 * @author Adrian Klimczak
 */
public class RatingSectionFragment extends Fragment 
{
	private final long ANIMATION_DURATION = 1000;
	
	// Badge circle
	private ImageView badgeImage[] = new ImageView[3];
	private TextView badgeTitle[] = new TextView[3];
	private LinearLayout badgeLayout[] = new LinearLayout[3];
	
	// Main board
	private RelativeLayout mainBoardLayout;
	private ImageView background;
	private ImageView dot;
	private TextView ratingTextView;
	private ArcView arc = null;
	
	private String[][] badge;
	private Bitmap[] badgeIcons;
	
	private float DCRating = 0;
	private boolean isDoingInBackground = false;      // It's used to avoid doing multiple tasks that are the same in the background
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_dcrating, container, false);
        
        // Get views
        //-----------------------------
        mainBoardLayout = (RelativeLayout) rootView.findViewById(R.id.scoreLayout);
        background = (ImageView) rootView.findViewById(R.id.backgroundImage);
        dot = (ImageView) rootView.findViewById(R.id.scoreDot);
        ratingTextView = (TextView) rootView.findViewById(R.id.ratingTextView);
                
        badgeImage[0] = (ImageView) rootView.findViewById(R.id.badgeImage1);
        badgeImage[1] = (ImageView) rootView.findViewById(R.id.badgeImage2);
        badgeImage[2] = (ImageView) rootView.findViewById(R.id.badgeImage3);
        
        badgeTitle[0] = (TextView) rootView.findViewById(R.id.badgeTitle1);
        badgeTitle[1] = (TextView) rootView.findViewById(R.id.badgeTitle2);
        badgeTitle[2] = (TextView) rootView.findViewById(R.id.badgeTitle3);
        
        badgeLayout[0] = (LinearLayout) rootView.findViewById(R.id.badgeLayout1);
        badgeLayout[1] = (LinearLayout) rootView.findViewById(R.id.badgeLayout2);
        badgeLayout[2] = (LinearLayout) rootView.findViewById(R.id.badgeLayout3);
        //-----------------------------
        
        for(int i=0; i<badgeImage.length; i++)
        {
        	badgeLayout[i].setVisibility(View.INVISIBLE);
        	badgeImage[i].setOnClickListener(onClickListener);
        }
        
        mainBoardLayout.setOnClickListener(onClickListener);
        
        // This page is the first page that user sees
        pageViewed();
        
		// Check if user has a dc rating
		if(ParseUser.getCurrentUser().getNumber("userDCRating") != null)
        {
        	// Get a DC Rating from the Parse
        	final float rating = Float.parseFloat(ParseUser.getCurrentUser().getNumber("userDCRating").toString());
        	
        	ratingTextView.setText(""+(int)rating);
        	
        	// Make sure background image view is put on the layout, before we try to do anything with it
	        background.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
	        {
	        	public void onGlobalLayout() 
	        	{
	        		background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	        	}
	        });
        }		
        
        return rootView;
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// Checks if this is the page that is currently being viewed by the user
    	if(DriverConnexFragment.getSelectedPageIndex() == 0)
    		pageViewed();
    }
    
    @Override
    public void onDestroyView()
    {
    	super.onDestroyView();
    	mainBoardLayout.removeView(arc);
    }
    
    /**
     * This function is called when this page is currently being viewed
     */
    public void pageViewed()
    {
    	if(!isDoingInBackground)
    		new GetRatingTask().execute();
    }
    
    private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			for(int i=0; i<badgeImage.length; i++)
			{
				if(v == badgeImage[i])
				{
					Intent intent = new Intent(getActivity(), BadgeActivity.class);
					intent.putExtra("badge", badge[i]);
					
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_in, R.anim.null_anim);	
					
					break;
				}
			}
			
			if(v == mainBoardLayout)
			{
				Intent intent = new Intent(getActivity(), LeaderboardActivity.class);			
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.null_anim);	
			}
		}
	};
	
	/**
	 * Calculates DCRating and sets the results.
	 */
	private void setDCRating()
	{		        
        // Calculate DC Rating
		HashMap<String, Object> params = new HashMap<String, Object>();
		ParseCloud.callFunctionInBackground("calculateDCRating", params,
				new FunctionCallback <Number>() 
		{
			@Override
			public void done(Number dcRating, ParseException e) 
			{
				if(e == null)
				{
					final int rating = (int)Float.parseFloat(dcRating.toString());
					
					// Check if new rating is different from current one
					if(rating != DCRating)
					{
						ratingTextView.setText(""+rating);
						
				        background.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
				        {
				        	public void onGlobalLayout() 
				        	{
				        		background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				        		setRingValue(DCRating, rating);
								DCRating = rating;
				        	}
				        });
					}
				}
				else
					Log.e("Get DCRating", e.getMessage());
			}
		});
	}
	
	/**
	 * Gets three badges to display
	 */
	private void getBadges()
	{
		if(ParseUser.getCurrentUser() == null)
			return;
		
		ParseQuery<ParseObject> query = ParseUser.getCurrentUser().getRelation("userBadges").getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.setLimit(3);
		
		try {
			List<ParseObject> objects = query.find();
			
			badge = new String[objects.size()][2];
			badgeIcons = new Bitmap[objects.size()];
			
			for(int i=0; i<objects.size(); i++)
			{
				badge[i][0] = objects.get(i).getString("badgeTitle");
				badge[i][1] = objects.get(i).getString("badgeDescription");
				
				// Get photo from the parse
				ParseFile photo = (ParseFile) objects.get(i).get("badgeImage");
				byte[] data = null;
				
				try 
				{
					if (photo != null) 
						data = photo.getData();
				} 
				catch (ParseException e1) {
					e1.printStackTrace();
				}

				Bitmap bmp = AssetsUtilities.readBitmap(data);
				badgeIcons[i] = bmp;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates savings in the background.
	 *
	 */
	private class GetRatingTask extends AsyncTask<String, Void, Boolean> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			isDoingInBackground = true;
		}

		@Override
		protected Boolean doInBackground(String... keywords) 
		{
	        setDCRating();
	        getBadges();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{	        
			super.onPostExecute(result);
			
			for(int i=0; i< badgeIcons.length; i++)
			{
				badgeTitle[i].setText(badge[i][0]);
				badgeImage[i].setImageBitmap(badgeIcons[i]);
				badgeLayout[i].setVisibility(View.VISIBLE);
			}
			
			isDoingInBackground = false;	
		}
	}
	
	/**
	 * Animates ring
	 * @param fromValue
	 * @param toValue
	 */
	private void setRingValue(float fromValue, float toValue)
	{    
		long delay = 1000;
		
		// Dot
		//--------------------------
		float rotationDegreesTo;
		float rotationDegreesFrom;
		
	    //if (fromValue > toValue) 
	    //{    
	    	double rotationRadiansTo = (((Math.PI * 2.0)/100) * toValue);
	    	rotationDegreesTo = (float) Math.toDegrees(rotationRadiansTo);
	    	
	    	double rotationRadiansFrom = (((Math.PI * 2.0)/100) * fromValue);
	    	rotationDegreesFrom = (float) Math.toDegrees(rotationRadiansFrom);
	    //}
	    /*else
	    {    
	    	double rotationRadiansTo = (float) (((Math.PI * 2.0)/100) * toValue);
	    	rotationDegreesTo = (float) Math.toDegrees(rotationRadiansTo);
	    	
	    	double rotationRadiansFrom = (float) (((-Math.PI * 2.0)/100) * fromValue);
	    	rotationDegreesFrom = (float) Math.toDegrees(rotationRadiansFrom);
	    }*/
	    
		RotateAnimation dotAnim = new RotateAnimation(rotationDegreesFrom, rotationDegreesTo, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
		dotAnim.setInterpolator(new LinearInterpolator());
		dotAnim.setRepeatCount(0);
		dotAnim.setStartOffset(delay);
		dotAnim.setFillAfter(true);
		dotAnim.setDuration(ANIMATION_DURATION);
	    
		// Arc
		//--------------------------    
		if(arc == null)
		{
			arc = new ArcView(getActivity());
			
			float height = background.getHeight();
			float totalHeight = height/2;
			float percentage = (height * 13.5f) / 100;
			
			arc.radius = totalHeight - percentage;
			arc.paint.setColor(Color.rgb(242, 101, 34));		    
			arc.paint.setStrokeWidth(5);
			arc.x = background.getWidth()/2;
			arc.y = background.getHeight()/2;
		}
		else
		{
			mainBoardLayout.removeView(arc);
		}

		// Animate arc
		ValueAnimator arcAnimator = ValueAnimator.ofFloat(rotationDegreesFrom, rotationDegreesTo);
		arcAnimator.setDuration(ANIMATION_DURATION);
		arcAnimator.setInterpolator(new LinearInterpolator());
		arcAnimator.setStartDelay(delay);
		arcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation) 
			{
				float value = ((Float) (animation.getAnimatedValue())).floatValue();
				arc.angle = (int) value;
				arc.invalidate();
			}
		});
		
		LayoutParams params = new LayoutParams(0);
		mainBoardLayout.addView(arc, params);
				
		dot.startAnimation(dotAnim);
		dot.bringToFront();
		arcAnimator.start();
	}
}
