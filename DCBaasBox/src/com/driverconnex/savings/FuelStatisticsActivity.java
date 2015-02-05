package com.driverconnex.savings;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.singletons.DCSavingsSingleton;

/**
 * Activity for displaying fuel statistics. Statistics are taken from savings dictionary (savingsDict) from DCSavingsSingleton. Savings are calculated in the background.
 * @author Adrian Klimczak
 *
 */

public class FuelStatisticsActivity extends Activity 
{	
	private TextView currentMonthSpent;
	private TextView currentMonthLitres;
	private TextView currentMonthCostPerLitre;
	
	private TextView lastMonthSpent;
	private TextView lastMonthLitres;
	private TextView lastMonthCostPerLitre;
	
	private TextView averagesDay;
	private TextView averagesWeek;
	private TextView averagesMonth;
	private TextView averagesYear;
	private TextView averagesPerLitre;
	
	private TextView budgetDay;
	private TextView budgetWeek;
	private TextView budgetMonth;
	private TextView budgetYear;
	
	private TextView totalsYear;
	private TextView totalsLitres;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savings_statistics_fuel);
		
		currentMonthSpent = (TextView) findViewById(R.id.currentSpentText);
		currentMonthLitres = (TextView) findViewById(R.id.currentLitreText);
		currentMonthCostPerLitre = (TextView) findViewById(R.id.currentCostText);
		
		lastMonthSpent = (TextView) findViewById(R.id.lastSpentText);
		lastMonthLitres = (TextView) findViewById(R.id.lastLitreText);
		lastMonthCostPerLitre = (TextView) findViewById(R.id.lastCostText);
		
		averagesDay = (TextView) findViewById(R.id.averageDayText);
		averagesWeek = (TextView) findViewById(R.id.averageWeekText);
		averagesMonth = (TextView) findViewById(R.id.averageMonthText);
		averagesYear = (TextView) findViewById(R.id.averageYearText);
		averagesPerLitre = (TextView) findViewById(R.id.averageLitreText);
		
		budgetDay = (TextView) findViewById(R.id.budgetDayText);
		budgetWeek = (TextView) findViewById(R.id.budgetWeekText);
		budgetMonth = (TextView) findViewById(R.id.budgetMonthText);
		budgetYear = (TextView) findViewById(R.id.budgetYearText);
		
		totalsYear = (TextView) findViewById(R.id.totalYearText);
		totalsLitres = (TextView) findViewById(R.id.totalLitreText);
		
		currentMonthSpent.setText("£0.00");
		currentMonthLitres.setText("£0.00");
		currentMonthCostPerLitre.setText("£0.00");
		
		lastMonthSpent.setText("£0.00");
		lastMonthLitres.setText("£0.00");
		lastMonthCostPerLitre.setText("£0.00");
		
		averagesDay.setText("£0.00");
		averagesWeek.setText("£0.00");
		averagesMonth.setText("£0.00");
		averagesYear.setText("£0.00");
		averagesPerLitre.setText("£0.00");
		
		budgetDay.setText("£0.00");
		budgetWeek.setText("£0.00");
		budgetMonth.setText("£0.00");
		budgetYear.setText("£0.00");
		
		totalsYear.setText("£0.00");
		totalsLitres.setText("£0.00");
		
		init();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    if (item.getItemId() == android.R.id.home) 
	    {
	        finish();
	        overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);   
	        return true;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
	private void init()
	{
		if(DCSavingsSingleton.getSavingsDict() == null)
			return;
		
		ArrayList<Map<String, Number>> monthArray;
		monthArray = (ArrayList<Map<String, Number>>) DCSavingsSingleton.getSavingsDict().get("Months");
	 
		if(monthArray == null)
			return;
		
	    if (monthArray.size() > 0) 
	    {
	    	Map<String, Number> currentMonthDict = monthArray.get(monthArray.size()-1);
	        
			currentMonthSpent.setText(String.format("£%.2f", currentMonthDict.get("Fuel Spend")));
			currentMonthLitres.setText(currentMonthDict.get("Fuel Litres") + " L");
			currentMonthCostPerLitre.setText(String.format("£%.2f", currentMonthDict.get("Average PPL")));
	    	
			totalsYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Fuel Total")));
			
			int annualFuelLitres = (Integer) DCSavingsSingleton.getSavingsDict().get("Annual Fuel Litres");
			
			totalsLitres.setText((int)annualFuelLitres + " L");
			
			averagesDay.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Daily Average Fuel")));
			averagesWeek.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Weekly Average Fuel")));
			averagesMonth.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Average Fuel")));
			averagesYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Average Fuel")));
			averagesPerLitre.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Average Fuel PPL")));
			
			budgetDay.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Daily Fuel Budget")));
			budgetWeek.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Weekly Fuel Budget")));
			budgetMonth.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Fuel Budget")));
			budgetYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Fuel Budget")));
	    }
	    
	    if (monthArray.size() > 1) 
	    {    
	    	Map<String, Number> lastMonthDict = monthArray.get(monthArray.size()-2);
	        
			lastMonthSpent.setText(String.format("£%.2f", lastMonthDict.get("Fuel Spend")));
			lastMonthLitres.setText(lastMonthDict.get("Fuel Litres") + " L");
			lastMonthCostPerLitre.setText(String.format("£%.2f", lastMonthDict.get("Average PPL")));			        
		}		
	}
}
