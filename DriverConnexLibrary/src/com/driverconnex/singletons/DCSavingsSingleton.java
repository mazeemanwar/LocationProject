package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.database.SQLException;

import com.driverconnex.expenses.DCExpense;
import com.driverconnex.expenses.ExpensesDataSource;
import com.driverconnex.fragments.SavingsSectionFragment;
import com.driverconnex.journeys.DCJourney;
import com.driverconnex.journeys.JourneyDataSource;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

/**
 * Singleton class for calculating savings in the background.
 * @author Adrian Klimczak
 *
 */

public class DCSavingsSingleton 
{
	private static Map<String, Object> savingsDict = new HashMap<String, Object>();      // The actual savings dictionary
	private static Map<String, Object> tempDict = new HashMap<String, Object>();         // Temporal dictionary
	private static boolean initialCalculationPerformed = false;          // Indicates if first calculation was performed 
	private static boolean isCalculating = false;                        // Indicates if currently is calculating savings
	
	/**
	 * Calculates savings
	 */
	public static void calculateSavings(SavingsSectionFragment savingsFragment)
	{
		// Makes sure it will not perform calculations twice at the same time
		if(!isCalculating)
		{
			isCalculating = true;
			
			// Gets default vehicle of the user
			ParseObject vehicle = ParseUser.getCurrentUser().getParseObject("userDefaultVehicle");
			
			if(vehicle != null)
			{
				try {
					vehicle.fetchIfNeeded();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// Checks to see the vehicle has an annual mileage set
				if(vehicle.getNumber("vehicleAnnualMileage") == null)
				{
					// If it doesn't then set it
					vehicle.put("vehicleAnnualMileage", 10000);
					
					try {
						vehicle.save();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				calculateAllMonthsFuel(savingsFragment.getActivity());
		        calculateFuelActuals(savingsFragment.getActivity());
		        calculateFuelBudget(vehicle);
		        calculateStandingCosts(vehicle);
		        calculateMaintenaceBudget(vehicle);
		        calculateMaintenanceActuals(vehicle);
			}	
			
	        isCalculating = false;
	        initialCalculationPerformed = true;
		}		
	}
	
	
	private static void calculateFuelActuals(Context context) 
	{	
	    // Calculate the month averages   
		ArrayList<Map<String, Number>> months;
	    months = (ArrayList<Map<String, Number>>) tempDict.get("Months");
		
	    float averageMonthlySpend = 0;
	    int averageMonthlyLitres = 0;
	    float averagePPL = 0;
	    
	    float totalYearlySpend = 0;
	    int totalLifetimeLitres = 0;
	    
	    if(months != null)
	    {
	    	for (int i=0; i<months.size(); i++) 
		    {    
		        averageMonthlySpend = averageMonthlySpend + Float.parseFloat(months.get(i).get("Fuel Spend").toString());
		        averageMonthlyLitres = (int) Float.parseFloat(averageMonthlyLitres +  months.get(i).get("Fuel Litres").toString());
		        averagePPL = averagePPL + Float.parseFloat(months.get(i).get("Average PPL").toString());
		        totalYearlySpend = totalYearlySpend + Float.parseFloat(months.get(i).get("Fuel Spend").toString());
		        totalLifetimeLitres = totalLifetimeLitres + (int) Float.parseFloat(months.get(i).get("Fuel Litres").toString());
		    }
	    	
	    	if(!months.isEmpty())
	    	{
			    averageMonthlySpend =  averageMonthlySpend/months.size();
			    averageMonthlyLitres = averageMonthlyLitres/months.size();
			    averagePPL = averagePPL/months.size();
	    	}
	    }

	    double milesTracked = getTotalMilesTracked(context);
	    
	    if (milesTracked == 0) 
	    {    
	        // If no miles have been tracked yet default it to 4000 miles.   
	        milesTracked = 4000;
	    }
	    
	    float averagePPM = 0;
	    float averagePPD = 0;
	    float averagePerYear = 0;
	    float averagePPW = 0;
	    
	    if(months != null)
	    {
	    	if(!months.isEmpty())
	    	{
	    		averagePPM = (float) (averageMonthlySpend/milesTracked);
	    	    averagePPD = (averageMonthlySpend * 12) / 365;
	    	    averagePerYear = averageMonthlySpend * 12;
	    	    averagePPW = averagePerYear/52;
	    	}
	    }
	    
	    tempDict.put("Annual Fuel Total", totalYearlySpend);
	    tempDict.put("Annual Fuel Litres", totalLifetimeLitres);
	    tempDict.put("Annual Average Fuel", averagePerYear);
	    tempDict.put("Mile Average Fuel", averagePPM);
	    tempDict.put("Daily Average Fuel", averagePPD);
	    tempDict.put("Weekly Average Fuel", averagePPW);
	    tempDict.put("Monthly Average Fuel", averageMonthlySpend);
	    tempDict.put("Monthly Average Fuel Litres", averageMonthlyLitres);
	    tempDict.put("Average Fuel PPL", averagePPL);
	}
	
	
	private static void calculateStandingCosts(ParseObject vehicle)
	{    
		// Vehicle is needed for further calculations
		if(vehicle == null)
			return;
		
		// Annual Tax is needed for further calculations
		if(vehicle.getNumber("vehicleAnnualTax") == null)
			return;
		
	    final float annualTax = Float.parseFloat(vehicle.getNumber("vehicleAnnualTax").toString());
	    
	    // vehicleMonthlyFinance is needed for further calculations
	    if(vehicle.getNumber("vehicleMonthlyFinance") == null)
	    	return;
	    
	    final float monthlyFinance = Float.parseFloat(vehicle.getNumber("vehicleMonthlyFinance").toString());
	    
	    ParseObject insurancePolicy = vehicle.getParseObject("vehicleCover");

	    if(insurancePolicy != null)
	    {
	    	try {
				insurancePolicy.fetchIfNeeded();
			} catch (ParseException e) {
				e.printStackTrace();
			}
	    	
			float insuranceCost = Float.parseFloat(insurancePolicy.getNumber("coverAnnualCost").toString());
		     
			// Calculate yearly standing cost
			float annualStandingCost = annualTax + (monthlyFinance * 12) + insuranceCost;
			int annualMileage = Integer.parseInt(vehicle.getNumber("vehicleAnnualMileage").toString());
			
			tempDict.put("Annual Tax Cost", annualTax);
			tempDict.put("Monthly Finance Cost", monthlyFinance);
			tempDict.put("Annual Insurance Cost", insuranceCost);
			tempDict.put("Annual Standing Cost", annualStandingCost);
			tempDict.put("Monthly Standing Cost", annualStandingCost/12);
			tempDict.put("Weekly Standing Cost",annualStandingCost/52);
			tempDict.put("Daily Standing Cost",annualStandingCost/365);
			tempDict.put("Mile Standing Cost",annualStandingCost/annualMileage);
	    }
	}
	
	/**
	 * Calculates fuel expenses for each month.
	 * @param context
	 */
	private static void calculateAllMonthsFuel(Context context)
	{   
		ArrayList<DCExpense> fuelExpenseArray = null;
		
		// Gets all fuel expenses
		try
		{
			ExpensesDataSource expensesDataSource = new ExpensesDataSource(context);
			expensesDataSource.open();
			fuelExpenseArray = expensesDataSource.getAllExpenses("Fuel");
			expensesDataSource.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		if(fuelExpenseArray == null)
			return;
		
		// Dictionary containing months, which consist of expenses that are of the same month
		Map<Date, ArrayList<DCExpense>> months = new HashMap<Date, ArrayList<DCExpense>>();	    
		
		// Loop trough all fuel expenses
	    for (int i=0; i<fuelExpenseArray.size(); i++)
	    {
	    	Date dateRepresentingThisMonth = dateAtBeginningOfMonthForDate(fuelExpenseArray.get(i).getDate());
	        
	        // If we don't yet have an array to hold the events for this day, create one
	        ArrayList<DCExpense> expensesOnThisMonth = months.get(dateRepresentingThisMonth);
	       
	        if (expensesOnThisMonth == null) 
	        {
	            expensesOnThisMonth = new ArrayList<DCExpense>();
	            
	            // Use the reduced date as dictionary key to later retrieve the event list this day
	            months.put(dateRepresentingThisMonth, expensesOnThisMonth);
	        }
	        
	        // Add the event to the list for this day
	        expensesOnThisMonth.add(fuelExpenseArray.get(i));
	    }
	    
	    ArrayList<ArrayList<DCExpense>> unsortedMonths = new ArrayList<ArrayList<DCExpense>>();
	    
	    Iterator<ArrayList<DCExpense>> iterator = months.values().iterator();
	    
	    // Copy all months from dictionary to the array list
	    while(iterator.hasNext())
	    {
	    	unsortedMonths.add(iterator.next());
	    }	    
	    
	    // Sort these months
	    ArrayList<ArrayList<DCExpense>> sortedMonths = sortMonths(unsortedMonths);
	    
	    ArrayList<Map<String, Number>> monthlyCosts = new ArrayList<Map<String, Number>>();
	    
	    // Loop through all sorted months
	    for (int i=0; i<sortedMonths.size(); i++) 
	    {	        
	    	// Calculates fuel expenses for the given month
	    	Map<String, Number> monthlyFuelDict = calculateFuelForMonth(sortedMonths.get(i));        
	        monthlyCosts.add(monthlyFuelDict);
	    }

	    // Adds monthly costs to the dictionary
	    tempDict.put("Months", monthlyCosts);
	}
	
	/**
	 * Calculates fuel budget
	 */
	private static void calculateFuelBudget(ParseObject currentVehicle)
	{       
	    if(currentVehicle != null)
	    {
	    	// Get the estimated annual mileage	 
		    int annualMileage = Integer.parseInt(currentVehicle.getNumber("vehicleAnnualMileage").toString());
		    int vehicleMPG = 0;
		    
		    // Get the vehicles MPG
		    if(currentVehicle.getNumber("vehicleQuotedMPG") != null)
		    	vehicleMPG = Integer.parseInt(currentVehicle.getNumber("vehicleQuotedMPG").toString());
		    
		    // If there's a calculated MPG, use this
		    if (currentVehicle.getNumber("vehicleCalculatedMPG") != null)    
		        vehicleMPG = Integer.parseInt(currentVehicle.getNumber("vehicleCalculatedMPG").toString());  
		        
		    // If there is a known MPG use this
		    if (currentVehicle.getNumber("vehicleKnownMPG") != null)
		    	vehicleMPG = Integer.parseInt(currentVehicle.getNumber("vehicleKnownMPG").toString());
		    
		    // MPG is needed for further calculations.
		    if(vehicleMPG == 0)
		    	return;
		    
		    // Get the average pence per litre  
		    float averagePPL = 0 ;
		    
		    if(savingsDict.containsKey("Average PPL"))
		    	averagePPL = (Float) savingsDict.get("Average PPL");
		    
		    // If the average PPL hasn't been set yet, load a default value	    
		    if (averagePPL == 0) {
		        averagePPL = 1.30f; 
		    }
		    
		    float annualFuelBudget = (((annualMileage/vehicleMPG) * 4.54f) * averagePPL);
		    float monthlyFuelBudget = annualFuelBudget/12;
		    float weeklyFuelBudget = annualFuelBudget/52;
		    float dailyFuelBudget = annualFuelBudget/365;
		    float mileFuelBudget = annualFuelBudget/annualMileage;
		    
		    tempDict.put("Annual Fuel Budget", annualFuelBudget);
		    tempDict.put("Monthly Fuel Budget", monthlyFuelBudget);
		    tempDict.put("Weekly Fuel Budget", weeklyFuelBudget);
		    tempDict.put("Daily Fuel Budget", dailyFuelBudget);
		    tempDict.put("Mile Fuel Budget", mileFuelBudget);	
	    }
	}
		
	/**
	 * Calculates maintenance budget
	 * @param vehicle
	 */
	private static void calculateMaintenaceBudget(ParseObject vehicle)
	{    
	    if(vehicle != null)
	    {
		    float annualServiceCost = 250;
		    float annualMileage = Float.parseFloat(vehicle.getNumber("vehicleAnnualMileage").toString());
		    float annualTyreCost = 400/(20000/annualMileage);
		    float repairsAllowance = 200;
		    
		    tempDict.put("Annual Service Cost", annualServiceCost);		    
		    tempDict.put("Annual Tyre Cost", annualTyreCost);
		    tempDict.put("Annual Repairs Allowance", repairsAllowance);
		    
		    // Checks if vehicleMOTCost exists in the Parse
	    	if(vehicle.getNumber("vehicleMOTCost") != null)
	    	{
	    		float vehicleMOTCost = Float.parseFloat(vehicle.getNumber("vehicleMOTCost").toString());
	    		float annualMaintenaceBudget = vehicleMOTCost + annualServiceCost + annualTyreCost + repairsAllowance;
	    		
	    		tempDict.put("Annual MOT Cost", vehicleMOTCost);
			    tempDict.put("Annual Maintenance Budget", annualMaintenaceBudget);
			    tempDict.put("Monthly Maintenance Budget", annualMaintenaceBudget/12);
			    tempDict.put("Weekly Maintenance Budget", annualMaintenaceBudget/52);
			    tempDict.put("Daily Maintenance Budget", annualMaintenaceBudget/365);
			    tempDict.put("Mile Maintenance Budget", annualMaintenaceBudget/annualMileage);
	    	}
	    }
	}
	
	/**
	 * Calculates maintenance actuals
	 * @param savingsFragment
	 * @param vehicle
	 */
	private static void calculateMaintenanceActuals(ParseObject vehicle)
	{    
		if(vehicle != null)
		{    
		    // Get the date one year ago
		    Calendar c = Calendar.getInstance();
		    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
		    Date targetDate = c.getTime();
		    
		    ParseQuery<ParseObject> relationQuery = vehicle.getRelation("vehicleServiceHistory").getQuery();
		    relationQuery.whereGreaterThan("serviceDate", targetDate);
		    relationQuery.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
	
		    try {
		    	 List<ParseObject> objects = relationQuery.find();
					
		    	 float annualMileage = Float.parseFloat(vehicle.getNumber("vehicleAnnualMileage").toString());
		    	 float annualMaintenaceCost = 0;
	            
		    	 for (int i=0; i<objects.size(); i++)
		    	 {    
		    		 annualMaintenaceCost = Float.parseFloat(objects.get(i).getNumber("serviceCost").toString()) + annualMaintenaceCost;                
		    	 }

	            tempDict.put("Annual Maintenance Average", annualMaintenaceCost);
	            tempDict.put("Monthly Maintenance Average", annualMaintenaceCost/12);
	            tempDict.put("Weekly Maintenance Average", annualMaintenaceCost/52);
	            tempDict.put("Daily Maintenance Average", annualMaintenaceCost/365);
	            tempDict.put("Mile Maintenance Average", annualMaintenaceCost/annualMileage);
	            
			} catch (ParseException e) {
				e.printStackTrace();
			}
  
	        createDict();
		}
	}
	
	/**
	 * Creates savings dictionary.
	 */
	private static void createDict() 
	{ 
		savingsDict.putAll(tempDict);
	}

	/**
	 * Calculates fuel for a given month.
	 * @param fuelExpenseArray - array of expenses for the same month
	 * @return
	 */
	private static Map<String, Number> calculateFuelForMonth(ArrayList<DCExpense> fuelExpenseArray)
	{    
	    float litres = 0;
	    float spent = 0;
	    
	    ArrayList<Float> pplArray = new ArrayList<Float>();
	    
	    // Loop through all fuel expenses
	    for (int i=0; i<fuelExpenseArray.size(); i++) 
	    {    
	        litres = (litres + fuelExpenseArray.get(i).getVolume());
	        spent = spent + fuelExpenseArray.get(i).getSpend();
	        
	        float pencePerLitre = spent/litres;
	        
	        pplArray.add(pencePerLitre);
	    }
	    
	    // Work out the average PPL
	    float total = 0;
	    
	    for (int i=0; i<pplArray.size(); i++) 
	    {    
	        float ppl = pplArray.get(i);
	        total = total + ppl;
	    }
	    
	    total = total / pplArray.size();
	    
	    Map<String, Number> costDict = new HashMap<String, Number>();
	    costDict.put("Average PPL", total);
	    costDict.put("Fuel Spend",spent);
	    costDict.put("Fuel Litres", litres);

	    return costDict;   
	}
	
	/**
	 * Returns a date at the beginning of the month of given date e.g. (input - 13.02.2014, output - 01.02.2014)
	 * @param inputDate
	 * @return
	 */
	static private Date dateAtBeginningOfMonthForDate(Date inputDate)
	{	    
	    // Use the user's current calendar and time zone
	    Calendar calendar = Calendar.getInstance();
	    
	    TimeZone timezone = TimeZone.getDefault();
	    calendar.setTimeZone(timezone);
	    		
	    // Selectively convert the date components (year, month, day) of the input date
	    calendar.setTime(inputDate);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);	    
	    
	    // Convert back
	    Date beginningOfMonth = calendar.getTime();	    
	    
	    return beginningOfMonth;
	}
	
	/**
	 * Sorts months from January to December.
	 * @param unsortedMonths
	 * @return
	 */
	private static ArrayList<ArrayList<DCExpense>> sortMonths(ArrayList<ArrayList<DCExpense>> unsortedMonths)
	{
		ArrayList<ArrayList<DCExpense>> sortedMonths = new ArrayList<ArrayList<DCExpense>>();
		
		Calendar c = Calendar.getInstance();
		
		int month = Calendar.JANUARY;
		
		do
		{
			for(int i=0; i<unsortedMonths.size(); i++)
			{
				c.setTime(unsortedMonths.get(i).get(0).getDate()); 
				
				if(c.get(Calendar.MONTH) == month)
					sortedMonths.add(unsortedMonths.get(i));
			}
			
			month ++;
			
		}while(month <= Calendar.DECEMBER);
		
		return sortedMonths;	
	}
	
	/**
	 * Returns total tracked mileage.s
	 * @param context
	 * @return
	 */
	private static double getTotalMilesTracked(Context context) 
	{    
		JourneyDataSource dataSource = new JourneyDataSource(context);
	    dataSource.open();
	    
	    ArrayList<DCJourney> journeys;
	    journeys = dataSource.getAllJourneys();
	    dataSource.close();
	    
	    if(journeys != null)
	    {
		    double totalMiles = 0;
		    
		    for (int i=0; i<journeys.size(); i++) { 
		        totalMiles = totalMiles + journeys.get(i).getDistance(); 
		    }
		    
		    tempDict.put("Total Miles Tracked", totalMiles);
		    
		    return totalMiles;
	    }
	    
	    return 0;
	}
	
	/**
	 * Gets savings dictionary.
	 * @return
	 */
	public static Map<String, Object> getSavingsDict() {
		return savingsDict;
	}

	/**
	 * Checks if initial calculation has been performed.
	 * @return true if it has been performed
	 */
	public static boolean IsInitialCalculationPerformed() {
		return initialCalculationPerformed;
	}
	
}
