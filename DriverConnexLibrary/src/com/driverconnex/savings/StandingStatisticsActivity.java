package com.driverconnex.savings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.singletons.DCSavingsSingleton;

/**
 * Activity for displaying standing costs statistics.
 * @author Adrian Klimczak
 *
 */

public class StandingStatisticsActivity extends Activity 
{	
	private TextView annualTax;
	private TextView finance;
	private TextView insurance;
	
	private TextView budgetDay;
	private TextView budgetWeek;
	private TextView budgetMonth;
	private TextView budgetYear;
	private TextView budgetMile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savings_statistics_standing_costs);
		
		annualTax = (TextView) findViewById(R.id.annualTaxText);
		finance = (TextView) findViewById(R.id.financeText);
		insurance = (TextView) findViewById(R.id.insuranceText);	
	
		budgetDay = (TextView) findViewById(R.id.budgetDayText);
		budgetWeek = (TextView) findViewById(R.id.budgetWeekText);
		budgetMonth = (TextView) findViewById(R.id.budgetMonthText);
		budgetYear = (TextView) findViewById(R.id.budgetYearText);
		budgetMile = (TextView) findViewById(R.id.budgetMileText);
		
		annualTax.setText("£0.00");
		finance.setText("£0.00");
		insurance.setText("£0.00");	
	
		budgetDay.setText("£0.00");
		budgetWeek.setText("£0.00");
		budgetMonth.setText("£0.00");
		budgetYear.setText("£0.00");
		budgetMile.setText("£0.00");
		
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
		
		if(DCSavingsSingleton.getSavingsDict().get("Annual MOT Cost") != null)
			annualTax.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual MOT Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Monthly Finance Cost") != null)
			finance.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Finance Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Insurance Cost") != null)
			insurance.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Insurance Cost")));  	
			
		if(DCSavingsSingleton.getSavingsDict().get("Daily Standing Cost") != null)
			budgetDay.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Daily Standing Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Weekly Standing Cost") != null)
			budgetWeek.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Weekly Standing Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Monthly Standing Cost") != null)
			budgetMonth.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Standing Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Standing Cost") != null)
			budgetYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Standing Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Mile Standing Cost") != null)
			budgetMile.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Mile Standing Cost")));
	}
}
