package com.driverconnex.journeys;

import android.app.IntentService;
import android.content.Intent;

import com.driverconnex.singletons.TrackJourneySingleton;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Activity 
 * @author Wizard
 *
 */

public class ActivityRecognitionIntentService extends IntentService
{
    public ActivityRecognitionIntentService() 
    {
        super("ActivityRecognitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) 
    {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        DetectedActivity activity = result.getMostProbableActivity();
        int type = activity.getType();
		TrackJourneySingleton.setActivity(getNameFromType(type));
    }

    private String getNameFromType(int activityType) 
    {
        switch(activityType) 
        {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "Walking";
            case DetectedActivity.STILL:
                return "Stationary";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
}
