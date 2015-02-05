package com.driverconnex.incidents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.driverconnex.app.R;

/**
 * Activity for creating a witness of the incident and reviewing existing witness.
 * 
 * @author Adrian Klimczak
 * 
 */

public class AddWitnessActivity extends Activity 
{
	private EditText nameEdit;
	private EditText numberEdit;
	private EditText descEdit;
	private EditText emailEdit;
	
	private String[] witness = new String[4];
	
	private boolean review = false;   // Indicates if this activity is used to review witness
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident_add_witness);

		// Get views
		nameEdit = (EditText) findViewById(R.id.nameEdit);
		numberEdit = (EditText) findViewById(R.id.numberEdit);
		descEdit = (EditText) findViewById(R.id.descEdit);
		emailEdit = (EditText) findViewById(R.id.emailEdit);
		
		if(getIntent().getExtras() != null)
		{
			witness = getIntent().getExtras().getStringArray("witness");
			review = getIntent().getExtras().getBoolean("review");
			
			if(witness != null)
			{
				nameEdit.setText(witness[0]);
				numberEdit.setText(witness[1]);
				emailEdit.setText(witness[2]);
				descEdit.setText(witness[3]);
			}
		}
		
		// Check if witness is being reviewed or being created
		if(!review)
			nameEdit.requestFocus();
		else
		{
			nameEdit.setInputType(InputType.TYPE_NULL);
			numberEdit.setInputType(InputType.TYPE_NULL);
			emailEdit.setInputType(InputType.TYPE_NULL);
			descEdit.setInputType(InputType.TYPE_NULL);
			
			TextView info = (TextView) findViewById(R.id.infoText);
			info.setVisibility(View.INVISIBLE);
		}
		
		getActionBar().setTitle(R.string.title_activity_back);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		
		if(!review)
			inflater.inflate(R.menu.action_save, menu);
		else
			inflater.inflate(R.menu.none, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_save) 
		{			
			witness = new String[4];
			
			witness[0] = (nameEdit.getText().toString());
			witness[1] = (numberEdit.getText().toString());
			witness[2] = (emailEdit.getText().toString());
			witness[3] = (descEdit.getText().toString());
			
			Intent returnIntent = new Intent();
			returnIntent.putExtra("witness", witness);
			setResult(RESULT_OK, returnIntent);
			finish();
			
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
				
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
