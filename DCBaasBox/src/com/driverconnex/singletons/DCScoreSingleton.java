package com.driverconnex.singletons;

import java.util.ArrayList;

import android.content.Context;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.data.DatabaseHelper;
import com.driverconnex.journeys.DCBehaviourPoint;
import com.driverconnex.journeys.DCJourney;
import com.driverconnex.journeys.JourneyDataSource;
import com.parse.ParseObject;

/**
 * Singleton for calculating driving score.
 * @author Adrian Klimczak
 *
 */

public class DCScoreSingleton 
{
	/**
	 * Calculates journey score.
	 * @param context
	 * @param journey
	 */
	public static void calculateJourneyScore (Context context, DCJourney journey)
	{
		boolean validBehaviour = true;
		
	    // Load the behaviour averages
		float averageCornering = DriverConnexApp.getUserPref().getAverageCorneringTotal();
	    float averageBraking = DriverConnexApp.getUserPref().getAverageBrakingTotal();
	    float averageAcceleration =  DriverConnexApp.getUserPref().getAverageAccelerationTotal();
		
	    // Load the array of behaviour points from the journey
	    JourneyDataSource dataSource = new JourneyDataSource(context);
	    dataSource.open();
	    ArrayList<DCBehaviourPoint> behaviourPoints = dataSource.getBehaviourPoints(journey.getId());
	    dataSource.close();
	    
	    // Loop through the recorded points
	    float acceleration = 0;
	    float braking = 0;
	    float steeringLeft = 0;
	    float steeringRight = 0;
	    float steeringTotal = 0;
	    
	    int invalidPoints = 0;
	    int numberOfPoints = 0;

	    // Loops through all behaviour points
	    for (int i=0; i<behaviourPoints.size(); i++) 
	    {    
	    	DCBehaviourPoint currentPoint = behaviourPoints.get(i);
	    	
	    	if(currentPoint.getActivity() != null)
	    	{
		        // Check that the speed is above 5 m/s.   
		        // Also check that activity ISN'T walking
		        if (currentPoint.getSpeed() > 5 || !currentPoint.getActivity().equals("Walking")
		        		|| !currentPoint.getActivity().equals("Stationary"))
		        {
		            if ((!currentPoint.isDeviceFlat() && !currentPoint.isDeviceLandscape()
		            		&& !currentPoint.isDevicePortrait()))
		            {   
		                invalidPoints++;    
		            } 
		            else 
		            {
		            	// Increase the total number of used points.
		            	numberOfPoints++;
		            	
		            	// Check the orientation of the device to get the correct axis values
		            	// If the device is laying flat, acceleration braking values are taken from the Y axis.
		            	if (currentPoint.isDeviceFlat()) 
		            	{
		            		if (currentPoint.getAccelerationY() > 0) 
		            		{ 
		            			acceleration = (float) (acceleration + currentPoint.getAccelerationY());
		            		} 
		            		else 
		            		{
		            			braking = (float) (braking + currentPoint.getAccelerationY());
		            		} 
		            	} 
		            	else 
		            	{
		            		// If the phone isn't flat, acceleration and braking is taken from the Z axis
		            		if (currentPoint.getAccelerationZ() > 0)   
		            			acceleration = (float) (acceleration + currentPoint.getAccelerationZ());
		            		else 
		            			braking = (float) (braking + currentPoint.getAccelerationZ());   
		            	}
		            
			            // Check if the phone is in portrait or landscape.  
			            // If the phone is in landscape, take the values for cornering from the Y axis.	            
			            // If the device is in portrait, or lying flat, take the values from the X axis.	            
			            
			            if (currentPoint.isDeviceLandscape()) 
			            {    
			                if (currentPoint.getAccelerationY() > 0) 
			                    steeringLeft = (float) (steeringLeft + currentPoint.getAccelerationY());
			                else 
			                    steeringRight = (float) (steeringRight + currentPoint.getAccelerationY());
			            } 
			            else 
			            {    
			                if (currentPoint.getAccelerationX() > 0)   
			                    steeringLeft = (float) (steeringLeft + currentPoint.getAccelerationX());
			                else 
			                    steeringRight = (float) (steeringRight + currentPoint.getAccelerationX());  
			            }
		            }
		        }	
	    	}
	    }
	        
	    // If enough points are invalid, invalidate the whole journey     	    
	    float percentageOfTotalInvalid = ((float)invalidPoints/(float)behaviourPoints.size()) * 100;
	 	    
	 	if (percentageOfTotalInvalid > 30) 
	 		validBehaviour = false;	        
	 	
	    // If there aren't enough points, invalidate the behaviour  
	    if (numberOfPoints < 100) 
	        validBehaviour = false;   
	    
	    // Checks if score is valid
	    if(validBehaviour)
	    {
		    // Scale the numbers to make them easier to work with
		    int scaleFactor = 2000;
		    
		    // Get the absolute values of the totals.
		    acceleration = Math.abs((acceleration/numberOfPoints) * scaleFactor);
		    braking = Math.abs((braking/numberOfPoints) * scaleFactor);
		    steeringLeft = Math.abs((steeringLeft/numberOfPoints) * scaleFactor);
		    steeringRight = Math.abs((steeringRight/numberOfPoints) * scaleFactor);
		    
		    // Divide the steering value since it uses double the amount of points as the others
		    steeringTotal = (steeringLeft + steeringRight)/2;
		    
		    // Update the behaviour stats table
			// ParseObject behaviourStat = new ParseObject("DCBehaviourStat");
			// behaviourStat.put("behaviourCornering", steeringTotal);
			// behaviourStat.put("behaviourAcceleration", acceleration);
			// behaviourStat.put("behaviourBraking", braking);
			//
			// behaviourStat.getACL().setPublicReadAccess(true);
			// behaviourStat.saveInBackground();

		    float bottomEndAccel = averageAcceleration - 25;
		    float topEndAccel = averageAcceleration + 25;
		    float bottomEndBraking = averageBraking - 25;
		    float topEndBraking = averageBraking + 25;
		    float bottomEndCornering = averageCornering - 25;
		    float topEndCornering = averageCornering + 25;
		    
		    // Work out the star value
		    float accelerationScore = ((acceleration - bottomEndAccel)/(topEndAccel-bottomEndAccel)) * 100;
		    
		    if (accelerationScore > 100) {
		        accelerationScore = 100;
		    }
		    if (accelerationScore < 0) {
		        accelerationScore = 0;
		    }
		    
		    float brakingScore = ((braking - bottomEndBraking)/(topEndBraking-bottomEndBraking)) * 100;
		    
		    if (brakingScore > 100)
		        brakingScore = 100;
		    
		    if (brakingScore < 0)
		        brakingScore = 0;

		    float steeringScore = ((steeringTotal - bottomEndCornering)/(topEndCornering-bottomEndCornering)) * 100;

		    if (steeringScore > 100)
		        steeringScore = 100;
		    
		    if (steeringScore < 0)
		        steeringScore = 0;
		    
		    int ratingScore = (int) ((steeringScore + brakingScore + accelerationScore)/3);
		    
		    String descriptionString = "";
		    
		    if (ratingScore < 25)
		        descriptionString = "Poor";
		    else if (ratingScore >=25 && ratingScore < 50)
		        descriptionString = "Below Average";
		    else if (ratingScore >= 50 && ratingScore < 75)    
		        descriptionString = "Good";  
		    else if (ratingScore >= 75)
		        descriptionString = "Excellent";
		    
		    int points = (int) ((ratingScore * 10) * (1 + journey.getDistance()));
		    
		    journey.setValidBehaviour(validBehaviour);
		    
		    String behaviourScore = "Acceleration Score:" + (int) accelerationScore + ","
		    		+ "Braking Score:" + (int)brakingScore + ","
		    		+ "Steering Score:" + (int)steeringScore + ","
		    		+ "Description:" + descriptionString + ","
		    		+ "Points Awarded:" + points;
		    		
		    journey.setBehaviourScore(behaviourScore);
		    
		    dataSource.open();
		    dataSource.updateJourney(journey, new String[]{DatabaseHelper.JOURNEY_SCORE});
		    dataSource.close();
	    }
	    
	    journey.setScoreAdded(true);
	    
	    // Save if journey is valid, and save that score was added
	    dataSource.open();
	    dataSource.updateJourney(journey, new String[]{DatabaseHelper.JOURNEY_VALID_BEHAVIOUR,
	    		DatabaseHelper.JOURNEY_SCORE_ADDED});
	    dataSource.close();
	}
}