package com.driverconnex.vehicles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ThemeUtilities;
import com.driverconnex.utilities.Utilities;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for adding a new DCCover. 
 * @author Adrian Klimczak
 *
 */

public class AddNewDCCoverActivity extends Activity 
{
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 90;
	
	private EditText policyProviderEdit;
	private EditText policyNumberEdit;
	private EditText policyContactNumberEdit;
	private TextView expiryDateEdit;
	private EditText annualCostEdit;
	
	private RelativeLayout photoLayout;
	private ImageView photoEdit;
	private Switch breakdownSwitch;
	private RelativeLayout loading;
	
	private DatePickerDialog datePickerDialog;
	
	private DCCover insuranceCover;
	
	private boolean isModify = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_add_new_cover);

		// Get views
		policyProviderEdit = (EditText) findViewById(R.id.policEditText);
		policyNumberEdit = (EditText) findViewById(R.id.policyNumberEdit);
		policyContactNumberEdit = (EditText) findViewById(R.id.policyContactNumber);
		annualCostEdit = (EditText) findViewById(R.id.costEditText);
		expiryDateEdit = (TextView) findViewById(R.id.dateEdit);
		
		photoLayout = (RelativeLayout) findViewById(R.id.photoLayout);
		photoEdit = (ImageView) findViewById(R.id.photoEdit);
		breakdownSwitch = (Switch) findViewById(R.id.breakdownCoverSwitch);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		photoLayout.setOnClickListener(onClickListener);
		expiryDateEdit.setOnClickListener(onClickListener);
		
		expiryDateEdit.setOnFocusChangeListener(onFocusChangeListener);
		
		breakdownSwitch.setChecked(true);
		
		init();
		
		policyProviderEdit.requestFocus();
	}
	
	@Override
	protected void onDestroy() 
	{
		AssetsUtilities.cleanTempMediaFile();
		super.onDestroy();
	}
	
	/*
	 * Gets result from the previous intent that was launched from this activity
	 *
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) 
		{
			if (resultCode == RESULT_OK) 
			{
				Bitmap bmp = null;
				int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
				bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmapFromUriInPortraitMode(AddNewDCCoverActivity.this,data.getData(),true), screenHeight);
				
				if(bmp != null)
					photoEdit.setImageBitmap(bmp);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		
		if(isModify)
			inflater.inflate(R.menu.add_new_service_history, menu);
		else
			inflater.inflate(R.menu.action_save, menu);
		
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
			// User can't save without completing all fields
			if (expiryDateEdit.getText().length() == 0 || annualCostEdit.getText().length() == 0
					|| policyProviderEdit.getText().length() == 0) 
			{
				new AlertDialog.Builder(AddNewDCCoverActivity.this)
						.setTitle("Error")
						.setMessage(getResources().getString(R.string.vehicle_insurance_error))
						.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog,	int which) 
							{
							}
							
						}).show();
			} 
			else
			{
				 new UpdateTask().execute();
			}
		}
		else if (item.getItemId() == R.id.action_delete) 
		{
			new AlertDialog.Builder(AddNewDCCoverActivity.this)
			.setTitle("Delete Item")
			.setMessage("Are you sure to delete this item?")
			.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,	int which) 
				{
					loading.setVisibility(View.VISIBLE);
					
					ParseQuery<ParseObject> query = ParseQuery.getQuery("DCCover");
					query.getInBackground(insuranceCover.getId(), new GetCallback<ParseObject>() 
					{
						public void done(ParseObject object, ParseException e) 
						{
							if (e == null) 
							{		
								object.deleteInBackground(new DeleteCallback()
								{
									@Override
									public void done(ParseException e) 
									{
										//  DC Service History item is deleted, go back to previous screen
										loading.setVisibility(View.INVISIBLE);
										finish();
										overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
									}	
								});
							}
						}
					});
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			}).show();	
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() 
	{
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) 
		{
			insuranceCover = (DCCover)extras.getSerializable("cover");
			
			// Check if insurance cover was passed here, which means user wants to edit it
			if(insuranceCover != null)
			{
				policyProviderEdit.setText(insuranceCover.getPolicyProvider());
				annualCostEdit.setText(""+insuranceCover.getAnualCost());
				expiryDateEdit.setText(insuranceCover.getExpiryDate());	
				breakdownSwitch.setChecked(insuranceCover.isCoverBreakdown());
				
				isModify = true;
				
				Bitmap bmp = null;
				try 
				{
					byte[] bytes = insuranceCover.getPhotoSrc();

					if (bytes != null) 
					{
						InputStream is = new ByteArrayInputStream(bytes);
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Config.RGB_565;
						
						int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
						bmp = AssetsUtilities.bmpResize(BitmapFactory.decodeStream(is, null, options), screenHeight);
						
						is.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				photoEdit.setImageBitmap(bmp);
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			Utilities.hideIM(AddNewDCCoverActivity.this, v);

			if (v == expiryDateEdit) 
			{
				initDatePickerDialog();
				datePickerDialog.show();
				ThemeUtilities.changeDialogTheme(AddNewDCCoverActivity.this, datePickerDialog);
			}
			else if (v == photoLayout) 
			{
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,CAPTURE_IMAGE_REQUEST_CODE);
			}
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() 
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			Utilities.hideIM(AddNewDCCoverActivity.this, v);

			if (v == expiryDateEdit) 
			{
				if (hasFocus) 
				{
					initDatePickerDialog();
					datePickerDialog.show();
					ThemeUtilities.changeDialogTheme(AddNewDCCoverActivity.this, datePickerDialog);
				}
			}
		}
	};
	
	/**
	 * Passes data about vehicle to Parse database
	 *
	 */
	private class UpdateTask extends AsyncTask<String, Void, String> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) 
		{
			// Check if user wants to modify existing cover or create new one
			if(isModify)
				updateCover();
			else
				addNewCover();
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			loading.setVisibility(View.INVISIBLE);
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
		}
	}

	private void addNewCover()
	{
		Date date = null;
		// Convert date into format that Parse will read
		final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			date = format.parse(expiryDateEdit.getText().toString());
		} catch (java.text.ParseException e1) {
			e1.printStackTrace();
		}
		
		// Create a new DC Cover object
		final ParseObject cover = new ParseObject("DCCover");
		
		// Put data into Parse database
		cover.put("coverAnnualCost", Integer.parseInt(annualCostEdit.getText().toString()));
		cover.put("coverBreakdown", breakdownSwitch.isChecked());
		cover.put("coverExpiryDate", date);
		cover.put("coverProvider", policyProviderEdit.getText().toString());
		cover.put("coverContactNumber", policyContactNumberEdit.getText().toString());
		cover.put("coverPolicyNumber", policyNumberEdit.getText().toString());
		
		Bitmap bitmap = ((BitmapDrawable)photoEdit.getDrawable()).getBitmap();
		
		// Check if there is a photo 
		if(bitmap != null)
		{
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, temp);
			byte[] data = temp.toByteArray();
			
			// Create ParseFile for the photo
			ParseFile file = new ParseFile("document.png", data);
			cover.put("coverDocumentPhoto", file);
			
			try 
			{
				file.save();
				temp.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		// Save the object and put it into Parse database
		try 
		{
			cover.save();
			ParseUser.getCurrentUser().save();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}
	
	private void updateCover()
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCCover");
		 
		// Retrieve the object by id
		query.getInBackground(insuranceCover.getId(), new GetCallback<ParseObject>() 
		{
		  public void done(ParseObject DCCover, ParseException e) 
		  {
		    if (e == null) 
		    {
				Date date = null;
				
				// Convert date into format that Parse will read
				final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				
				try {
					date = format.parse(expiryDateEdit.getText().toString());
				} catch (java.text.ParseException e1) {
					e1.printStackTrace();
				}
		    	
		    	// Update cover in Parse database
		    	DCCover.put("coverAnnualCost", Integer.parseInt(annualCostEdit.getText().toString()));
				DCCover.put("coverBreakdown", breakdownSwitch.isChecked());
				DCCover.put("coverExpiryDate", date);
				DCCover.put("coverProvider", policyProviderEdit.getText().toString());
				
				Bitmap bitmap = ((BitmapDrawable)photoEdit.getDrawable()).getBitmap();
				
				// Check if there is a photo 
				if(bitmap != null)
				{
					ByteArrayOutputStream temp = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 50, temp);
					byte[] data = temp.toByteArray();
					
					// Create ParseFile for the photo
					ParseFile file = new ParseFile("document", data);
					DCCover.put("coverDocumentPhoto", file);
					
					try 
					{
						file.save();
						temp.close();
						// Create parse file object		
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				// Save the object and put it into Parse database
				try 
				{
					DCCover.save();
					ParseUser.getCurrentUser().save();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
		    }
		  }
		});
	}
	
	private void initDatePickerDialog() 
	{
		Calendar c = Calendar.getInstance();
		datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
		{

			@Override
			public void onDateSet(DatePicker view, int year, int month,int day) 
			{
				expiryDateEdit.setText(new StringBuilder().append(Utilities.intToTime(day)).append("-")
						.append(Utilities.intToTime(month + 1)).append("-")
						.append(year));
			}
			
		},
		c.get(Calendar.YEAR),
		c.get(Calendar.MONTH),
		c.get(Calendar.DAY_OF_MONTH));
	}
}
