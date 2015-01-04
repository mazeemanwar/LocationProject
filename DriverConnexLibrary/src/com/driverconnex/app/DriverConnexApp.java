package com.driverconnex.app;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.driverconnex.data.XMLAppConfigParser;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.PushService;

/**
 * Main Class for the DriverConnex app
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */
public class DriverConnexApp extends Application {
	private static Preferences userPref;

	@Override
	public void onCreate() {
		super.onCreate();

		// Parse XML app_config file to the app
		// It will initialise values in AppConfig
		XMLAppConfigParser.parseAppConfigFromXML(this);

		// Initialise Parse database
		Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
		// Enable Crash Reporting

//		ParseCrashReporting.enable(this);

		Parse.initialize(this, AppConfig.getAppID(), AppConfig.getClientKey());
	
		

		// Set ACL for security purposes
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);

		// Set Parse notification system

		PushService.setDefaultPushCallback(this, HomeActivity.class);

		// Create user preferences
		userPref = new Preferences(this);

		// Get averages from the Parse
		// --------------------------
		// Function doesn't take any params but it needs this variable passed,
		// otherwise it won't work
		HashMap<String, Object> params = new HashMap<String, Object>();

		ParseCloud.callFunctionInBackground("calculateAverageBehaviour",
				params, new FunctionCallback<Map<String, Object>>() {
					@Override
					public void done(Map<String, Object> map, ParseException e) {
						if (e == null) {
							String strCornering = map.get("corneringTotal")
									.toString();
							String strAcceleration = map.get(
									"accelerationTotal").toString();
							String strBraking = map.get("brakingTotal")
									.toString();

							float cornering = 0;
							float acceleration = 0;
							float braking = 0;

							if (!strCornering.equals("null"))
								cornering = Float.parseFloat(strCornering);

							if (!strAcceleration.equals("null"))
								acceleration = Float
										.parseFloat(strAcceleration);

							if (!strBraking.equals("null"))
								braking = Float.parseFloat(strBraking);

							userPref.setAveragesTotal(cornering, acceleration,
									braking);
						} else
							Log.e("Get averages", e.getMessage());
					}
				});
		// --------------------------
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Preferences getUserPref() {
		return userPref;
	}
}