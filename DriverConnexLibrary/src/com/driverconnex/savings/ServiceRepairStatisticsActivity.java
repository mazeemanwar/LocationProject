package com.driverconnex.savings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.singletons.DCSavingsSingleton;

/**
 * Activity for displaying service and repair statistics. 
 * @author Adrian Klimczak
 *
 */

public class ServiceRepairStatisticsActivity extends Activity 
{	
	private TextView motCost;
	private TextView serviceCost;
	private TextView tyreCost;
	private TextView repairs;
	private TextView total;
	
	private TextView budgetDay;
	private TextView budgetWeek;
	private TextView budgetMonth;
	private TextView budgetYear;
	private TextView budgetMile;
	
	private TextView actualsDay;
	private TextView actualsWeek;
	private TextView actualsMonth;
	private TextView actualsYear;
	private TextView actualsMile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savings_statistics_maintenance);
		
		motCost = (TextView) findViewById(R.id.motCostText);
		serviceCost = (TextView) findViewById(R.id.serviceCostText);
		tyreCost = (TextView) findViewById(R.id.tyreCostText);	
		repairs = (TextView) findViewById(R.id.repairsText);
		total = (TextView) findViewById(R.id.totalText);
	
		budgetDay = (TextView) findViewById(R.id.budgetDayText);
		budgetWeek = (TextView) findViewById(R.id.budgetWeekText);
		budgetMonth = (TextView) findViewById(R.id.budgetMonthText);
		budgetYear = (TextView) findViewById(R.id.budgetYearText);
		budgetMile = (TextView) findViewById(R.id.budgetMileText);
		
		actualsDay = (TextView) findViewById(R.id.actualsDayText);
		actualsWeek = (TextView) findViewById(R.id.actualsWeekText);
		actualsMonth = (TextView) findViewById(R.id.actualsMonthText);
		actualsYear = (TextView) findViewById(R.id.actualsYearText);
		actualsMile = (TextView) findViewById(R.id.actualsMileText);
		
		motCost.setText("£0.00");
		serviceCost.setText("£0.00");
		tyreCost.setText("£0.00");
		repairs.setText("£0.00");
		total.setText("£0.00");
	
		budgetDay.setText("£0.00");
		budgetWeek.setText("£0.00");
		budgetMonth.setText("£0.00");
		budgetYear.setText("£0.00");
		budgetMile.setText("£0.00");
		
		actualsDay.setText("£0.00");
		actualsWeek.setText("£0.00");
		actualsMonth.setText("£0.00");
		actualsYear.setText("£0.00");
		actualsMile.setText("£0.00");
		
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
			motCost.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual MOT Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Service Cost") != null)
			serviceCost.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Service Cost")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Tyre Cost") != null)
			tyreCost.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Tyre Cost")));  	
		if(DCSavingsSingleton.getSavingsDict().get("Annual Repairs Allowance") != null)
			repairs.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Repairs Allowance")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Budget") != null)
			total.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Budget")));
			
		if(DCSavingsSingleton.getSavingsDict().get("Daily Maintenance Budget") != null)
			budgetDay.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Daily Maintenance Budget")));
		if(DCSavingsSingleton.getSavingsDict().get("Weekly Maintenance Budget") != null)
			budgetWeek.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Weekly Maintenance Budget")));
		if(DCSavingsSingleton.getSavingsDict().get("Monthly Maintenance Budget") != null)
			budgetMonth.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Maintenance Budget")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Budget") != null)
			budgetYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Budget")));
		if(DCSavingsSingleton.getSavingsDict().get("Mile Maintenance Budget") != null)
			budgetMile.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Mile Maintenance Budget")));
		
		if(DCSavingsSingleton.getSavingsDict().get("Daily Maintenance Average") != null)
			actualsDay.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Daily Maintenance Average")));
		if(DCSavingsSingleton.getSavingsDict().get("Weekly Maintenance Average") != null)
			actualsWeek.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Weekly Maintenance Average")));
		if(DCSavingsSingleton.getSavingsDict().get("Monthly Maintenance Average") != null)
			actualsMonth.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Monthly Maintenance Average")));
		if(DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Average") != null)
			actualsYear.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Annual Maintenance Average")));
		if(DCSavingsSingleton.getSavingsDict().get("Mile Maintenance Average") != null)
			actualsMile.setText(String.format("£%.2f", DCSavingsSingleton.getSavingsDict().get("Mile Maintenance Average")));
	}
}
