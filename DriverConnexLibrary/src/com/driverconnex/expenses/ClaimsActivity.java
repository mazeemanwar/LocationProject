package com.driverconnex.expenses;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.driverconnex.app.R;
/**

 * @author MUhammad Azeem Anwar
 */
public class ClaimsActivity  extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_claims
			);
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
