package com.driverconnex.utilities;

import java.util.HashMap;

import android.content.Context;

import com.driverconnex.basicmodules.HelpActivity;
import com.driverconnex.basicmodules.MessageActivity;
import com.driverconnex.basicmodules.MyAccountActivity;
import com.driverconnex.basicmodules.NotificationsActivity;
import com.driverconnex.basicmodules.PolicyActivity;
import com.driverconnex.basicmodules.SettingsActivity;
import com.driverconnex.basicmodules.TermsActivity;
import com.driverconnex.community.FriendsListActivity;
import com.driverconnex.community.FriendsRequestActivity;
import com.driverconnex.community.ShareAppActivity;
import com.driverconnex.expenses.AddExpenseActivity;
import com.driverconnex.expenses.AddFuelActivity;
import com.driverconnex.expenses.AddTravelSubsistenceActivity;
import com.driverconnex.expenses.ClaimsActivity;
import com.driverconnex.journeys.AddJourneyActivity;
import com.driverconnex.journeys.TrackJourneyActivity;
import com.driverconnex.vehicles.VehiclesListActivity;

/**
 * Utility class for modules. Modules are read from XML file and each item of the module that should appear on the menu is registered here.
 * 
 * example:
 * modulesMap.put(NameOfItem, ActivityClass);
 * 
 * NameOfItem - is an exact name of the item defined in XML file. If you will put a different name it will not work, since this is how they are linked together.
 * ActivityClass - is the activity that will be opened when item on the menu is clicked. 
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */ 

public class ModulesUtilities 
{	
	private Context context;
	private HashMap<String, Class<?>> modulesMap = new HashMap<String, Class<?>>();
	
	public ModulesUtilities(Context context) 
	{ 
		super();
		this.context = context;
		
		// Items for DC Expenses Module
		modulesMap.put("Add Expense", AddExpenseActivity.class);
		modulesMap.put("Add Fuel", AddFuelActivity.class);
		modulesMap.put("Review", com.driverconnex.expenses.ReviewExpensesActivity.class);
		
		modulesMap.put("Claims", ClaimsActivity.class);
		
		// Items for Basic Module
		modulesMap.put("My Account", MyAccountActivity.class);
		modulesMap.put("Inbox", MessageActivity.class);
		modulesMap.put("Terms", TermsActivity.class);
		modulesMap.put("Help", HelpActivity.class);
		modulesMap.put("Settings",SettingsActivity.class);
		modulesMap.put("Notifications", NotificationsActivity.class);
		modulesMap.put("Policy", PolicyActivity.class);
		
		
		
		// Items for DC Mileage Module
		modulesMap.put("Vehicles List", VehiclesListActivity.class);
		modulesMap.put("Track Journey", TrackJourneyActivity.class);
		modulesMap.put("Review Journeys", com.driverconnex.journeys.ReviewJourneysActivity.class);
		modulesMap.put("Add Manually", AddJourneyActivity.class);
		
		// Items for DC Parking Module
		modulesMap.put("Park Vehicle", com.driverconnex.parking.AddParkingLocationActivity.class);
		modulesMap.put("Locate Vehicle", com.driverconnex.parking.LocateParkingActivity.class);
		
		// Items for DC Incidents
		modulesMap.put("Accidents", com.driverconnex.incidents.IncidentActivity.class);
		modulesMap.put("Breakdown", com.driverconnex.incidents.IncidentActivity.class);
		modulesMap.put("Incident Reports", com.driverconnex.incidents.ReportListActivity.class);
		
		// Items for KPMG T&S Module
		modulesMap.put("Add T&S", AddTravelSubsistenceActivity.class);
		
		// Items for DC Community
		modulesMap.put("My Community", FriendsListActivity.class);
		modulesMap.put("Friend Requests", FriendsRequestActivity.class);
		modulesMap.put("Share App", ShareAppActivity.class);
	}
	
	/**
	 * Gets an activity class assigned to the item.
	 * @param module
	 * @return
	 */
	public Class<?> getModuleClass(String module) {
		return this.modulesMap.get(module);
	}
}
