package com.driverconnex.basicmodules;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.driverconnex.app.R;

/**
 * Activity for previewing a badge.
 * 
 * @author Adrian Klimczak
 * 
 */

public class BadgeActivity extends Activity 
{
	private TextView description;
	private TextView title;
	private String badge[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_badge);

		description = (TextView) findViewById(R.id.descriptionText);
		title = (TextView) findViewById(R.id.titleText);
		
		if(getIntent().getExtras() != null)
		{
			badge = getIntent().getExtras().getStringArray("badge");
			
			if(badge != null)
			{
				title.setText(badge[0]);
				description.setText(badge[1]);
			}
		}
		
		getActionBar().setTitle("Close");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
