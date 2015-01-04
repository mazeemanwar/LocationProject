package com.driverconnex.basicmodules;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.driverconnex.app.R;

/**
 * Not yet implemented.
 * @author Adrian Klimczak
 *
 */

public class TermsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms);
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
