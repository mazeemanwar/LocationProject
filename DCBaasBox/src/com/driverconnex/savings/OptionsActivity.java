package com.driverconnex.savings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;

/**
 * Activity for choosing a savings scale. User can choose from following options:
 * - Daily savings
 * - Weekly savings
 * - Monthly savings
 * - Annual savings
 * - Savings per mile (But I'm not sure about this one)
 * @author Adrian Klimczak
 *
 */

public class OptionsActivity extends Activity
{
	private Button optionBtn[] = new Button[5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savings_options);
		
		optionBtn[0] = (Button) findViewById(R.id.dayBtn);
		optionBtn[1] = (Button) findViewById(R.id.weekBtn);
		optionBtn[2] = (Button) findViewById(R.id.monthBtn);
		optionBtn[3] = (Button) findViewById(R.id.yearBtn);
		optionBtn[4] = (Button) findViewById(R.id.mileBtn);	
		
		for(int i=0; i<optionBtn.length; i++)
			optionBtn[i].setOnClickListener(onClickListener);
		
		if(DriverConnexApp.getUserPref().getSavingsScale().equals("Daily"))
			setOptionButton(0);
		else if(DriverConnexApp.getUserPref().getSavingsScale().equals("Weekly"))
			setOptionButton(1);
		else if(DriverConnexApp.getUserPref().getSavingsScale().equals("Monthly"))
			setOptionButton(2);
		else if(DriverConnexApp.getUserPref().getSavingsScale().equals("Annual"))
			setOptionButton(3);
		else if(DriverConnexApp.getUserPref().getSavingsScale().equals("Mile"))
			setOptionButton(4);
		
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
	
	private void setOptionButton(int position)
	{
		for(int i=0; i<optionBtn.length; i++)
		{
			if(i == position)
			{
				// Deselects all buttons
				for(int j=0; j<optionBtn.length; j++)
				{
					optionBtn[j].setTextColor(getResources().getColor(R.color.main_interface));
					optionBtn[j].setBackgroundDrawable(null);
				}
				
				// Selects selected button
				optionBtn[i].setTextColor(getResources().getColor(R.color.white));
				optionBtn[i].setBackgroundResource(R.color.main_interface);
			}
		}
	}
	
	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			for(int i=0; i<optionBtn.length; i++)
			{
				if(v == optionBtn[i])
				{
					setOptionButton(i);
				}
			}
			
			// Sets savings scale in the user shared preferences
			if(v == optionBtn[0])
				DriverConnexApp.getUserPref().setSavingsScale("Daily");		
			else if(v == optionBtn[1])
				DriverConnexApp.getUserPref().setSavingsScale("Weekly");	
			else if(v == optionBtn[2])
				DriverConnexApp.getUserPref().setSavingsScale("Monthly");	
			else if(v == optionBtn[3])
				DriverConnexApp.getUserPref().setSavingsScale("Annual");	
			else if(v == optionBtn[4])
				DriverConnexApp.getUserPref().setSavingsScale("Mile");	
		}
	};
}
