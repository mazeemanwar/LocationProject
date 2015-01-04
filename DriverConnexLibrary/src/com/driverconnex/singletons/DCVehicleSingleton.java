package com.driverconnex.singletons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.driverconnex.app.DriverConnexApp;
import com.parse.ParseObject;

/**
 * Singleton for things related with the vehicle
 * @author Adrian Klimczak
 *
 */

public class DCVehicleSingleton 
{
	/**
	 * Gets ArrayList of allerts for the given vehicle
	 * @param vehicle
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> getAlertsForVehicle(ParseObject vehicle)
	{    
		ArrayList<HashMap<String, Object>> alertArray = new ArrayList<HashMap<String, Object>>();
	    
		if(vehicle != null)
		{
		    // Check the vehicle's MOT
		    if (vehicle.getDate("vehicleMOTDate") == null) 
		    {    
		        HashMap<String, Object> alertDict = new HashMap<String, Object>();
		        alertDict.put("alertVehicle", vehicle);
		        alertDict.put("alertSubject", "MOT Alert");
		        alertDict.put("alertBody","Please check this vehicles MOT expiry date.");
		        
		        alertArray.add(alertDict);    
		    }
		    
		    if (vehicle.getDate("vehicleTaxDate") == null)
		    {
		        HashMap<String, Object> alertDict = new HashMap<String, Object>();
		        alertDict.put("alertVehicle", vehicle);
		        alertDict.put("alertSubject", "Tax Alert");
		        alertDict.put("alertBody","Please check this vehicles Tax expiry date.");
		        
		        alertArray.add(alertDict);   
		    }
		    
		    if (vehicle.getDate("vehicleOdometer") == null)
		    {
		        HashMap<String, Object> alertDict = new HashMap<String, Object>();
		        alertDict.put("alertVehicle", vehicle);
		        alertDict.put("alertSubject", "Mileage");
		        alertDict.put("alertBody","Please check this vehicles mileage is correct.");
		        
		        alertArray.add(alertDict);   
		    }
		    
		    if (vehicle.getDate("vehicleCover") == null)
		    {
		        HashMap<String, Object> alertDict = new HashMap<String, Object>();
		        alertDict.put("alertVehicle", vehicle);
		        alertDict.put("alertSubject", "Insurance Alert");
		        alertDict.put("alertBody","Please check this vehicles insurance cover is correct.");
		        
		        alertArray.add(alertDict);   
		    }
		    
		    // Check for outstanding checks	   
		    if(getOutstandingChecks(vehicle.getObjectId()) != 0)
		    {
		    	HashMap<String, Object> alertDict = new HashMap<String, Object>();
		        alertDict.put("alertVehicle", vehicle);
		        alertDict.put("alertSubject", "Vehicle Check Alert");
		        alertDict.put("alertBody","You have an expired vehicle check on this vehicle.");
		        
		        alertArray.add(alertDict);
		    }
		}
	    
	    return alertArray;
	}
	
	/**
	 * Gets number of outstanding checks
	 * @param vehicleId
	 * @return
	 */
	public static int getOutstandingChecks(String vehicleId)
	{
		int count = 0;
		
		if(isMonthlyCheckExired(vehicleId))
			count ++;
		
		return count;
	}
	
	/**
	 * Checks if vehicle has monthly check expired
	 * @param vehicleId
	 * @return
	 */
	public static boolean isMonthlyCheckExired(String vehicleId)
	{
		if(!DriverConnexApp.getUserPref().getMonthlyVehicleCheckExpiryDate(vehicleId).isEmpty())
		{
			Calendar c = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			
			try {
				c.setTime(format.parse(DriverConnexApp.getUserPref().getMonthlyVehicleCheckExpiryDate(vehicleId)));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			
			// Check if vehicle check expired
			if(Calendar.getInstance().after(c))
				return true;
			else
				return false;	
		}
		
		return true;
	}
}
