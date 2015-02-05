package com.driverconnex.basicmodules;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.driverconnex.app.R;
import com.parse.ParseUser;

/**
 * Activity for changing password in My Account module
 * 
 * NOTE: This activity is not used, because Parse doesn't give the option to get the user's password from the database
 * instead it will display the dialogue, which gives the option to send the reset password email. 
 * @author Adrian Klimczak
 *
 */

public class ChangePasswordActivity extends Activity
{
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		// Get reference to edit texts from the layout
		oldPassword = (EditText) findViewById(R.id.change_password_old_password);
		newPassword = (EditText) findViewById(R.id.change_password_new_password);
		confirmPassword = (EditText) findViewById(R.id.change_password_confirm_password);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.change_password_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Check if back arrow in the action bar is selected
	    if (item.getItemId() == android.R.id.home) 
	    {
	    	// Go to previous activity
	        finish();
	        overridePendingTransition(R.anim.null_anim, R.anim.slide_out);   
	        return true;
	    }
	    // Check if user wants to save a new password
	    else if(item.getItemId() == R.id.action_save_password)
	    {
	    	ParseUser user = ParseUser.getCurrentUser();
	    	
	    	Log.d("TEST",user.getString("password"));
	    	
	    	if(oldPassword.getText().toString().equals(user.getString("password")))
	    	{
	    		Log.d("TEST", "password matches");
	    	}
	    	else
	    	{
	    		Log.d("TEST", "password doesn't match");
	    	}
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
}
