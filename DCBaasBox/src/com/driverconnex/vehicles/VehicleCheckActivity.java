package com.driverconnex.vehicles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.driverconnex.app.R;

/**
 * Activity for displaying a item of the check: name and text. User selects if this check failed or passed. Result is passed back to previous activity.
 * @author Adrian Klimczak
 *
 */
public class VehicleCheckActivity extends Activity 
{
	private TextView title;
	private TextView description;
	private TextView failed;
	private TextView passed;
	
	private String[] item;
	private int checkPosition;       // Position of the item check that was selected
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_check);

		title = (TextView) findViewById(R.id.titleText);
		description = (TextView) findViewById(R.id.descriptionText);
		failed = (TextView) findViewById(R.id.failedText);
		passed = (TextView) findViewById(R.id.passedText);
		
		failed.setOnClickListener(onClickListener);
		passed.setOnClickListener(onClickListener);
		
		if(getIntent().getExtras() != null)
		{
			// Gets item
			item = (String[]) getIntent().getSerializableExtra("item");
			// Gets the position of the item on the list
			checkPosition = getIntent().getIntExtra("checkIndex", -1);
			
			// Sets title of the check
			title.setText(item[0]);
			
			// XML doesn't handle new lines very well, so we need to do it manually
			item[1] = item[1].replace("-", "\n\n-");
			
			// Sets description of the check
			description.setText(item[1]);
		}
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
	
	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			Intent returnIntent = new Intent();
			
			if(v == failed)
			{
				returnIntent.putExtra("passedCheck", false);
			}
			else if(v == passed)
			{
				returnIntent.putExtra("passedCheck", true);
			}
			
			// Inform previous activity which item check was checked
			returnIntent.putExtra("checkIndex", checkPosition);
			setResult(RESULT_OK, returnIntent);
			finish();
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
		}
	};
}
