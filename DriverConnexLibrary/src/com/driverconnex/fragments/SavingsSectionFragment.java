package com.driverconnex.fragments;

import java.util.Calendar;

import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.savings.SavingsMonitorActivity;
import com.driverconnex.singletons.DCSavingsSingleton;
import com.driverconnex.ui.ArcView;

/**
 * A savings section fragment for the DriverConnex dashboard.
 * 
 * @author Adrian Klimczak
 */
public class SavingsSectionFragment extends Fragment 
{
	private final long ANIMATION_DURATION = 2500;
	
	// Expense small circle
	private LinearLayout circleLayout[] = new LinearLayout[3];
	private ImageView circleImage[] = new ImageView[3];
	private boolean circleSelected[] = new boolean[3]; 
	private TextView circleText[] = new TextView[3];
	private TextView circleValue[] = new TextView[3];
	
	// Expense main board
	private RelativeLayout mainBoardLayout;
	
	private ImageView background;
	private ImageView blueDot;
	private ImageView redDot;
	
	private TextView expenseTextView;
	private TextView unitTextView;
	
	private RotateAnimation blueDotAnim;
	private RotateAnimation redDotAnim;
	private ValueAnimator blueArcAnimator;
	private ValueAnimator redArcAnimator;
	private ArcView blueArc;
	private ArcView redArc;
	
	private String unitString;

	private float averageCost = -1;
	private boolean isDoingInBackground = false;      // It's used to avoid doing multiple tasks that are the same in the background
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_savings, container, false);
        
        // Get views
        //-----------------------------
        mainBoardLayout = (RelativeLayout) rootView.findViewById(R.id.expenseScoreLayout);
        background = (ImageView) rootView.findViewById(R.id.backgroundImage);
        blueDot = (ImageView) rootView.findViewById(R.id.outerDot);
        redDot = (ImageView) rootView.findViewById(R.id.innerDot);
        expenseTextView = (TextView) rootView.findViewById(R.id.expenseTextView);
        unitTextView = (TextView) rootView.findViewById(R.id.unitTextView);
        
        circleLayout[0] = (LinearLayout) rootView.findViewById(R.id.expenseLayout1);
        circleLayout[1] = (LinearLayout) rootView.findViewById(R.id.expenseLayout2);
        circleLayout[2] = (LinearLayout) rootView.findViewById(R.id.expenseLayout3);
        
        circleImage[0] = (ImageView) rootView.findViewById(R.id.expenseImage1);
        circleImage[1] = (ImageView) rootView.findViewById(R.id.expenseImage2);
        circleImage[2] = (ImageView) rootView.findViewById(R.id.expenseImage3);
        
        circleText[0] = (TextView) rootView.findViewById(R.id.expenseTitle1);
        circleText[1] = (TextView) rootView.findViewById(R.id.expenseTitle2);
        circleText[2] = (TextView) rootView.findViewById(R.id.expenseTitle3);
        
        circleValue[0] = (TextView) rootView.findViewById(R.id.valueTextView1);
        circleValue[1] = (TextView) rootView.findViewById(R.id.valueTextView2);
        circleValue[2] = (TextView) rootView.findViewById(R.id.valueTextView3);
        //-----------------------------
        
        // Get which circles are selected
        circleSelected[0] = DriverConnexApp.getUserPref().isFuelCostsIncluded();
        circleSelected[1] = DriverConnexApp.getUserPref().isStandingCostsIncluded();
        circleSelected[2] = DriverConnexApp.getUserPref().isMaintenanceCostsIncluded();
        
        // Set listener and select the circles that are selected and deselect the ones that are deselected
        for(int i=0; i<circleLayout.length; i++)
        {
        	circleLayout[i].setOnClickListener(onClickListener);
        	
    		if(circleSelected[i])
    		{
    			circleImage[i].setImageDrawable(getActivity()
    					.getResources().getDrawable(R.drawable.icon_background));
    			circleText[i].setTextColor(getActivity()
    					.getResources().getColor(R.color.white));
    		}
    		else
    		{
    			circleImage[i].setImageDrawable(getActivity()
    					.getResources().getDrawable(R.drawable.icon_disabled));
    			circleText[i].setTextColor(getActivity()
    					.getResources().getColor(R.color.tran_grey));
    		}
        }
        
        mainBoardLayout.setOnClickListener(onClickListener);
        
        initUnitString();

        return rootView;
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// Checks if this is the page that is currently being viewed by the user
    	if(DriverConnexFragment.getSelectedPageIndex() == 1)
    		pageViewed();
    }
    
    /**
     * Handles what happens when page is being viewed.
     */
    public void pageViewed()
    {	
        // Checks if calculations are currently being performed, so that it will only do it once at a time
    	if(!isDoingInBackground)
    		calculateSavings();
    	
    	initUnitString();
    	
    	// Checks if calculations has been done first
		if(DCSavingsSingleton.IsInitialCalculationPerformed())
			displayCosts();
    }
    
    /**
     * Calculates savings. It can be called from outside.
     */
    public void calculateSavings()
    {
    	new savingsTask().execute();
    }
    
    private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if(v.getId() == R.id.expenseLayout1)
			{
				selectExpense(0);
				displayCosts();
			}
			else if(v.getId() == R.id.expenseLayout2)
			{
				selectExpense(1);
				displayCosts();
			}
			else if(v.getId() == R.id.expenseLayout3)
			{
				selectExpense(2);
				displayCosts();
			}
			else if(v.getId() == R.id.expenseScoreLayout)
			{
				Intent intent = new Intent(getActivity(), SavingsMonitorActivity.class);			
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.null_anim);	
			}
		}
	};
	
	/**
	 * Selects expense circle by giving it index of the circle
	 * @param index
	 */
	private void selectExpense(int index)
	{		
		if(circleSelected[index])
		{
			circleImage[index].setImageDrawable(getActivity()
					.getResources().getDrawable(R.drawable.icon_disabled));
			circleText[index].setTextColor(getActivity()
					.getResources().getColor(R.color.tran_grey));
			
			circleSelected[index] = false;
			
			switch(index)
			{
			case 0:
				DriverConnexApp.getUserPref().setFuelCostsIncluded(false);
				break;
			case 1:
				DriverConnexApp.getUserPref().setStandingCostsIncluded(false);
				break;
			case 2:
				DriverConnexApp.getUserPref().setMaintenanceCostsIncluded(false);
			}
		}
		else
		{
			circleImage[index].setImageDrawable(getActivity()
					.getResources().getDrawable(R.drawable.icon_background));
			circleText[index].setTextColor(getActivity()
					.getResources().getColor(R.color.white));
			
			circleSelected[index] = true;
			
			switch(index)
			{
			case 0:
				DriverConnexApp.getUserPref().setFuelCostsIncluded(true);
				break;
			case 1:
				DriverConnexApp.getUserPref().setStandingCostsIncluded(true);
				break;
			case 2:
				DriverConnexApp.getUserPref().setMaintenanceCostsIncluded(true);
			}
		}
	}
	
	/**
	 * Displays costs 
	 */
	public void displayCosts()
	{
	    // Set the inner bar percentage
	    int outerBarPercentage = 0;
	   
	    if (unitString.equals("Daily"))
        	outerBarPercentage = getPercentageOfDay();
        else if (unitString.equals("Weekly"))
        	outerBarPercentage = getPercentageOfWeek();
        else if (unitString.equals("Monthly"))
        	outerBarPercentage = getPercentageOfMonth();
        else if (unitString.equals("Annual"))
        	outerBarPercentage = getPercentageOfYear();
        else if (unitString.equals("Mile"))
        	outerBarPercentage = 0;

	    // Declare the total variables for displaying costs
	    float budget = 0;
	    float actualAverage = 0;
	    float fuelAverage = 0;
	    float standingAverage = 0;
	    float maintenanceAverage = 0;
	    
	    // Makes sure that any calculation with fuel budget have been done before
	    if(DCSavingsSingleton.getSavingsDict().containsKey(unitString + " Fuel Budget"))
	    {
	    	fuelAverage = Float.parseFloat(DCSavingsSingleton.getSavingsDict().get
	    		(unitString + " Fuel Budget").toString());
	    	
		    // If display fuel is turned on, then add in the fuel totals    
		    if (DriverConnexApp.getUserPref().isFuelCostsIncluded())
		    {    
		        float monthlyFuelBudget = (Float) DCSavingsSingleton.getSavingsDict().get(unitString + " Fuel Budget");
		        
		        budget = budget + monthlyFuelBudget;
		        actualAverage = actualAverage + fuelAverage;
		    }
	    }
	    
	    // Makes sure that any calculation with standing costs have been done before
	    if(DCSavingsSingleton.getSavingsDict().containsKey(unitString + " Standing Cost"))
	    {
	    	standingAverage = Float.parseFloat(DCSavingsSingleton.getSavingsDict().get
	    		(unitString + " Standing Cost").toString());
	    	
		    // If display other is turned on, add in the standing cost    
		    if (DriverConnexApp.getUserPref().isStandingCostsIncluded())
		    {
		        float monthlyStandingBudget = (Float) DCSavingsSingleton.getSavingsDict().get(
		        		unitString + " Standing Cost");

		        budget = budget + monthlyStandingBudget;
		        actualAverage = actualAverage + standingAverage;
		    }
	    }
	    
	    // Makes sure that any calculation with maintenance costs have been done before
	    if(DCSavingsSingleton.getSavingsDict().containsKey(unitString + " Maintenance Average"))
	    {
	    	maintenanceAverage = Float.parseFloat(DCSavingsSingleton.getSavingsDict().get
	    		(unitString + " Maintenance Average").toString());
	    	
		    // If include maintenance is turned on, add in the maintenance costs.	    
		    if (DriverConnexApp.getUserPref().isMaintenanceCostsIncluded())
		    {    
		    	if(DCSavingsSingleton.getSavingsDict().containsKey(unitString + " Maintenance Budget"))
		    	{
			        float monthlyMaintenanceBudget = (Float) DCSavingsSingleton.getSavingsDict().get(
			        		unitString + " Maintenance Budget");

			        budget = budget + monthlyMaintenanceBudget;
		    	}
		    	
		        actualAverage = actualAverage + maintenanceAverage;  
		    }
	    }
	    
	    // Check if there are any changes in figures
	    if(actualAverage != averageCost)
	    {
	    	averageCost = actualAverage;
	    	
		    // Calculate the percentage of budget that has been spent and set the inner circle   
		    float percentageOfBudget = (actualAverage/budget) * 100;
		    
		    setRingValue(outerBarPercentage, (int)percentageOfBudget);
		    
		    // If the monthly actual average isn't infinite, display it in the main circle
		    if (Float.isNaN(actualAverage)) 
		    	expenseTextView.setText("-");
		    else
		    	expenseTextView.setText("£"+ (int)actualAverage);
		    
		    if (Float.isNaN(fuelAverage))
	            circleValue[0].setText("-");    
	        else
	           // circleValue[0].setText(String.format("£%.2f", fuelAverage));
	        	circleValue[0].setText("£" + (int)fuelAverage);
		    
		    if (Float.isNaN(standingAverage))
	            circleValue[1].setText("-");    
	        else
	            //circleValue[1].setText(String.format("£%.2f", standingAverage));
	        	circleValue[1].setText("£" + (int)standingAverage);
		    
		    if (Float.isNaN(maintenanceAverage))
	            circleValue[2].setText("-");    
	        else
	            //circleValue[2].setText(String.format("£%.2f", maintenanceAverage));
	        	circleValue[2].setText("£" + (int)maintenanceAverage);
	    }
	}
		
	/**
	 * Sets the value of the rings and animates them.
	 * @param toValue
	 * @param secondToValue
	 */
	private void setRingValue(int toValue, int secondToValue)
	{    	
		long delay = 1000;
		
	    if (toValue > 200) {
	        toValue = 200;
	    }

	    if (secondToValue > 200) {
	        secondToValue = 200;
	    }
	    
		// Blue Dot
		//--------------------------
		double rotationRadians = ((Math.PI * 2.0)/100) * (toValue/2);
		float rotationDegrees = (float) Math.toDegrees(rotationRadians);
		
		blueDotAnim = new RotateAnimation(0f, rotationDegrees, Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
		blueDotAnim.setInterpolator(new LinearInterpolator());
		blueDotAnim.setRepeatCount(0);
		blueDotAnim.setStartOffset(delay);
		blueDotAnim.setFillAfter(true);
		blueDotAnim.setDuration(ANIMATION_DURATION);
	  
		LayoutParams params = new LayoutParams(0);
		
		// If for some reason background is null it shouldn't go any further
		if(background == null)
			return;
		
		float height = background.getHeight();
		float totalWidth = height/2;
		
		// Blue Arc
		//--------------------------     
		if(blueArc == null)
		{
			blueArc = new ArcView(getActivity());
		
			float percentage = (height * 13.5f) / 100;
			
			blueArc.radius = totalWidth - percentage;
			blueArc.paint.setColor(Color.rgb(38, 158, 208));		    
			blueArc.paint.setStrokeWidth(5); 
			blueArc.x = background.getWidth()/2;
			blueArc.y = background.getHeight()/2;
		}
		else
			mainBoardLayout.removeView(blueArc);
		
		if(blueArcAnimator != null)
			blueArcAnimator.end();
		
		// Animate arc
		blueArcAnimator = ValueAnimator.ofFloat(0, rotationDegrees);
		blueArcAnimator.setDuration(ANIMATION_DURATION);
		blueArcAnimator.setInterpolator(new LinearInterpolator());
		blueArcAnimator.setStartDelay(delay);
		blueArcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation) 
			{
				float value = ((Float) (animation.getAnimatedValue())).floatValue();
				blueArc.angle = (int) value;
				blueArc.invalidate();
			}
		});
				                        
		// Red dot
		//--------------------------
		rotationRadians = ((Math.PI * 2.0)/100) * (secondToValue/2);
		rotationDegrees = (float) Math.toDegrees(rotationRadians);
		
		redDotAnim = new RotateAnimation(0f, rotationDegrees, Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
		redDotAnim.setInterpolator(new LinearInterpolator());
		redDotAnim.setRepeatCount(0);
		redDotAnim.setFillAfter(true);
		redDotAnim.setStartOffset(delay);
		redDotAnim.setDuration(ANIMATION_DURATION);
		
		// Red Arc
		//--------------------------     
		if(redArc == null)
		{
			redArc = new ArcView(getActivity());
			
			float percentage = (height * 15.7f) / 100;
			
			redArc.radius = totalWidth - percentage;
			redArc.paint.setColor(Color.rgb(237, 28, 36));		    
			redArc.paint.setStrokeWidth(5);
			redArc.x = background.getWidth()/2;
			redArc.y = background.getHeight()/2;
		}
		else
			mainBoardLayout.removeView(redArc);
		
		if(redArcAnimator != null)
			redArcAnimator.end();
		
		// Animate arc
		redArcAnimator = ValueAnimator.ofFloat(0, rotationDegrees);
		redArcAnimator.setDuration(ANIMATION_DURATION);
		redArcAnimator.setInterpolator(new LinearInterpolator());
		redArcAnimator.setStartDelay(delay);
		redArcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation) 
			{
				float value = ((Float) (animation.getAnimatedValue())).floatValue();
				redArc.angle = (int) value;
				redArc.invalidate();
			}
		});
		
		mainBoardLayout.addView(blueArc, params);
		mainBoardLayout.addView(redArc, params);
		
	    blueDot.startAnimation(blueDotAnim);
		blueDot.bringToFront();
		blueArcAnimator.start();  
		
		redDot.startAnimation(redDotAnim);
		redDot.bringToFront();
		redArcAnimator.start();
	}
	
	/**
	 * Calculates savings in the background.
	 *
	 */
	private class savingsTask extends AsyncTask<String, Void, Boolean> 
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
			DCSavingsSingleton.calculateSavings(SavingsSectionFragment.this);		
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{	        
			super.onPostExecute(result);
			isDoingInBackground = false;	
		}
	}
	
	/**
	 * Initialises unitString and unitTextView.
	 */
    private void initUnitString()
    {
    	unitString = DriverConnexApp.getUserPref().getSavingsScale();
    	
    	if(unitTextView != null)
    	{
            if (unitString.equals("Daily"))
                unitTextView.setText("Average Per Day");
            else if (unitString.equals("Weekly"))
            	unitTextView.setText("Average Per Week");
            else if (unitString.equals("Monthly"))
                unitTextView.setText("Average Per Month");
            else if (unitString.equals("Annual"))
                unitTextView.setText("Average Per Year");
            else if (unitString.equals("Mile"))
                unitTextView.setText("Average Per Mile");
    	}
    }
	
	/**
	 * Gets percentage of the current hour out of 24 hours
	 * @return
	 */
	int getPercentageOfDay()
	{    
	    Calendar c = Calendar.getInstance();
	    
	    float currentHour = c.get(Calendar.HOUR_OF_DAY);
	    int percentage = (int) ((currentHour/24) * 100);
	    
	    return percentage; 
	}
	
	/**
	 * Gets percentage of the current day out of 7 days of the week
	 * @return
	 */
	int getPercentageOfWeek()
	{    
	    Calendar c = Calendar.getInstance();
	    
	    float currentDay = 0;
	    
	    switch(c.get(Calendar.DAY_OF_WEEK))
	    {
	    case Calendar.MONDAY:
	    	currentDay = 1;
	    	break;
	    case Calendar.TUESDAY:
	    	currentDay = 2;
	    	break;
	    case Calendar.WEDNESDAY:
	    	currentDay = 3;
	    	break;
	    case Calendar.THURSDAY:
	    	currentDay = 4;
	    	break;
	    case Calendar.FRIDAY:
	    	currentDay = 5;
	    	break;
	    case Calendar.SATURDAY:
	    	currentDay = 6;
	    	break;
	    case Calendar.SUNDAY:
	    	currentDay = 7;
	    	break;
	    }
 
	    int percentage = (int) ((currentDay/7) * 100);

	    return percentage; 
	}
	
	/**
	 * Gets percentage of the current day out of total days of the month
	 * @return
	 */
	int getPercentageOfMonth()
	{    
	    Calendar c = Calendar.getInstance();
	    
	    float currentDay = c.get(Calendar.DAY_OF_MONTH);
	    float dayCount = c.getActualMaximum(Calendar.DAY_OF_MONTH);
	    int percentage = (int) ((currentDay/dayCount) * 100);

	    return percentage; 
	}
	
	/**
	 * Gets percentage of the current day of the year out of total days of the year
	 * @return
	 */
	int getPercentageOfYear()
	{    
	    Calendar c = Calendar.getInstance();
	    
	    float currentDay = c.get(Calendar.DAY_OF_YEAR);
	    float dayCount = c.getActualMaximum(Calendar.DAY_OF_YEAR);
	   
	    int percentage = (int) ((currentDay/dayCount) * 100);

	    return percentage; 
	}
}
