package com.driverconnex.expenses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ThemeUtilities;

/**
 * 
 * @author Yin Lee (SGI)
 *
 *	NOTE:
 *	This activity is used for KPMG. I didn't do any changes here, so I can't say anything about what is happening here.
 *	Comment by Adrian Klimczak
 */

public class AddTravelSubsistenceActivity extends Activity 
{
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 90;
	private LinearLayout photoEdit;
	private EditText dateEdit, spendEdit, curEdit, hoursEdit, claimsEdit;
	private EditText descriptionEdit;
	//private SharedPreferences prefs;
	private AlertDialog.Builder builder;
	private ImageView photo;
	private DatePickerDialog datePickerDialog;
//	private Bitmap bmp;
	private boolean isModify = false;
	private long id;
	private String pic;

	private String intToTime(int mInt) {
		String mTime;
		if (mInt <= 9) {
			mTime = "0" + mInt;
		} else
			mTime = String.valueOf(mInt);
		return mTime;
	}

	public static String getMonthFromInt(int monthNumber) {
		String monthName = "";

		if (monthNumber >= 0 && monthNumber < 12)
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, monthNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
				simpleDateFormat.setCalendar(calendar);
				monthName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		return monthName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_travel_subsistence);

		// Get views
		descriptionEdit = (EditText) findViewById(R.id.descEdit);
		photo = (ImageView) findViewById(R.id.photo);
		dateEdit = (EditText) findViewById(R.id.dateEdit);
		spendEdit = (EditText) findViewById(R.id.spendEdit);
		curEdit = (EditText) findViewById(R.id.currencyEdit);
		hoursEdit = (EditText) findViewById(R.id.hoursEdit);
		claimsEdit = (EditText) findViewById(R.id.claimedEdit);
		photoEdit = (LinearLayout) findViewById(R.id.photoEdit);

		photoEdit.setOnClickListener(onClickListener);
		dateEdit.setOnClickListener(onClickListener);
		curEdit.setOnClickListener(onClickListener);
		hoursEdit.setOnClickListener(onClickListener);
		claimsEdit.setOnClickListener(onClickListener);

		spendEdit.setOnFocusChangeListener(onFocusChangeListener);
		dateEdit.setOnFocusChangeListener(onFocusChangeListener);
		curEdit.setOnFocusChangeListener(onFocusChangeListener);
		hoursEdit.setOnFocusChangeListener(onFocusChangeListener);
		claimsEdit.setOnFocusChangeListener(onFocusChangeListener);

		descriptionEdit.requestFocus();
		
		init();
	}

	private void init() 
	{
		if (getIntent().getExtras() != null) 
		{
			isModify = true;
			Bundle extras = getIntent().getExtras();
			// typeEdit.setText(extras.getString("type"));
			id = extras.getLong("id");
			descriptionEdit.setText(extras.getString("description"));
			dateEdit.setText(extras.getString("date"));
			spendEdit.setText(String.valueOf(extras.getFloat("spend")));
			curEdit.setText(extras.getString("currency"));
			//String is_vat = extras.getBoolean("is_vat") ? "Yes" : "No";
			//String is_business = extras.getBoolean("is_business") ? "Yes"
				//	: "No";
			// VEdit.setText(is_vat);
			// busiEdit.setText(is_business);
			hoursEdit.setText(extras.getString("hours"));
			claimsEdit.setText(extras.getString("claimed"));
			pic = extras.getString("pic_path");
			if (pic != null) {
				Bitmap bmp = null;
				try {
					File file = AssetsUtilities.getOutMediaFile(AssetsUtilities.EXPENSE_PIC_PATH, pic);
					if (file != null) {
						bmp = BitmapFactory.decodeStream(new FileInputStream(file));
						photo.setImageBitmap(bmp);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			dateEdit.setText((String) DateFormat.format("dd-MM-yyyy", Calendar.getInstance().getTime()));
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		dateEdit.setInputType(InputType.TYPE_NULL);
		curEdit.setInputType(InputType.TYPE_NULL);
		hoursEdit.setInputType(InputType.TYPE_NULL);
		claimsEdit.setInputType(InputType.TYPE_NULL);
	}

	// Initiate edit dialog
	private void initAlertDialog(int title, final int items, final View view) 
	{
		hideIM(view);
		
		builder = new AlertDialog.Builder(AddTravelSubsistenceActivity.this);
		builder.setTitle(title).setItems(items,
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				// The 'which' argument contains the index position
				// of the selected item
				((EditText) view).setText(getResources().getStringArray(items)[which]);
			}
		});

		builder.create();
	}

	private void initDatePickerDialog() {
		Calendar c = Calendar.getInstance();
		// datePickerDialog = new DatePickerDialog(this,
		// R.style.CustomDatePickerDialog, mDateSetListener,
		// c.get(Calendar.YEAR), c.get(Calendar.MONTH),
		// c.get(Calendar.DAY_OF_MONTH));
		datePickerDialog = new DatePickerDialog(this, mDateSetListener,
				c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() 
	{
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			dateEdit.setText(new StringBuilder().append(intToTime(day)).append("-")
					.append(intToTime(month + 1)).append("-")
					.append(year));
		}
	};

	private void changeAlertDialogTheme(AlertDialog dialog) {
		try {
			Resources resources = dialog.getContext().getResources();
			int color = ThemeUtilities.getMainInterfaceColor(this);

			int alertTitleId = resources.getIdentifier("alertTitle", "id",
					"android");
			TextView alertTitle = (TextView) dialog.getWindow().getDecorView()
					.findViewById(alertTitleId);
			alertTitle.setTextColor(color); // change title text color

			int titleDividerId = resources.getIdentifier("titleDivider", "id",
					"android");
			View titleDivider = dialog.getWindow().getDecorView()
					.findViewById(titleDividerId);
			titleDivider.setBackgroundColor(color); // change divider color
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// private void changeDatePickerDialogTheme(DatePickerDialog dialog) {
	// try {
	// Resources resources = dialog.getContext().getResources();
	// int color = new ThemeUtilities(this).getMainInterfaceColor();
	//
	// int alertTitleId = resources.getIdentifier("alertTitle", "id",
	// "android");
	// TextView alertTitle = (TextView) dialog.getWindow().getDecorView()
	// .findViewById(alertTitleId);
	// alertTitle.setTextColor(color); // change title text color
	//
	// int titleDividerId = resources.getIdentifier("titleDivider", "id",
	// "android");
	// View titleDivider = dialog.getWindow().getDecorView()
	// .findViewById(titleDividerId);
	// titleDivider.setBackgroundColor(color); // change divider color
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	// Hide soft keyboard
	private void hideIM(View v) {
		try {
			InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			IBinder windowToken = v.getWindowToken();

			if (windowToken != null) {
				im.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			hideIM(v);
			
			if (v == hoursEdit) {
				initAlertDialog(R.string.expense_details_title_hours,
						R.array.kpmg_details_hours, v);
				changeAlertDialogTheme(builder.show());
			}
			if (v == claimsEdit) {
				initAlertDialog(R.string.expense_details_title_claimed,
						R.array.kpmg_details_claimed, v);
				changeAlertDialogTheme(builder.show());
			}
			if (v == dateEdit) {
				initDatePickerDialog();
				// changeDatePickerDialogTheme(datePickerDialog);
				datePickerDialog.show();
			}
			if (v == curEdit) 
			{
				initAlertDialog(R.string.expense_details_title_currency,
						R.array.expense_details_currency, v);
				changeAlertDialogTheme(builder.show());
			}
			
			
			// if (v == VEdit) {
			// initAlertDialog(R.string.expense_details_title_vat,
			// R.array.expense_details_yesno, v);
			// changeAlertDialogTheme(builder.show());
			// }
			// if (v == busiEdit) {
			// initAlertDialog(R.string.expense_details_title_business,
			// R.array.expense_details_yesno, v);
			// changeAlertDialogTheme(builder.show());
			// }
			if (v == photoEdit) {
				Intent intent = new Intent(AddTravelSubsistenceActivity.this,
						AddExpensePhotoActivity.class);
				if (isModify) {
					Bundle extras = new Bundle();
					extras.putString("pic_path", pic);
					intent.putExtras(extras);
				}
				startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Bundle extras = data.getExtras();
				// byte[] byteArray = extras.getByteArray("temp_photo");
				//
				// Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0,
				// byteArray.length);
				// photo.setImageBitmap(bmp);
				// bmp.recycle();

				Bitmap bmp = null;
				File tempFile = AssetsUtilities.getOutTempMediaFile();
				try {
					if (tempFile != null) {
						bmp = BitmapFactory.decodeFile(tempFile
								.getAbsolutePath());
						photo.setImageBitmap(bmp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
	}

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() 
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			hideIM(v);
			if (v == hoursEdit) 
			{
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_hours,
							R.array.kpmg_details_hours, v);
					changeAlertDialogTheme(builder.show());
				}
			}
			if (v == claimsEdit) 
			{
				if (hasFocus) 
				{
					initAlertDialog(R.string.expense_details_title_claimed,
							R.array.kpmg_details_claimed, v);
					changeAlertDialogTheme(builder.show());
				}
			}
			if (v == dateEdit) {
				if (hasFocus) {
					initDatePickerDialog();
					// changeDatePickerDialogTheme(datePickerDialog);
					datePickerDialog.show();
				}
			}
			if (v == curEdit) {
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_currency,
							R.array.expense_details_currency, v);
					changeAlertDialogTheme(builder.show());
				}
			}
			else if (v == spendEdit) 
			{
				if (!hasFocus) 
				{
					// Check if text contains symbol of the pound
					/*if(spendEdit.getText().toString().contains("£"))
					{
						// Remove this symbol from the text
						String newText = spendEdit.getText().toString().replace("£", "");
						spendEdit.setText(newText);
					}
					
					if(spendEdit.getText().toString().isEmpty())
						spendEdit.setText("0");
					
					// Put a pound symbol before number
					spendEdit.setText("£" + spendEdit.getText().toString());*/
					
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			if (isModify) {
				overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);
			} else {
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			}
			return true;
		}
		if (item.getItemId() == R.id.action_save) 
		{
			if (dateEdit.getText().length() == 0
					|| spendEdit.getText().length() == 0
					|| hoursEdit.getText().length() == 0
					|| claimsEdit.getText().length() == 0) {
				new AlertDialog.Builder(AddTravelSubsistenceActivity.this)
						.setTitle("Error")
						.setMessage(
								getResources().getString(
										R.string.expense_error_info))
						.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with
										// delete
									}
								}).show();
			} else {
				// Save picture from temp folder
				String picPath = AssetsUtilities.relocateTempPhotoForExpense();
				
				ExpensesDataSource dataSource = new ExpensesDataSource(
						AddTravelSubsistenceActivity.this);
				dataSource.open();
				
				if (isModify) {
					DCExpense expense = new DCExpense();
					expense.setDate(dateEdit.getText().toString());
					expense.setCurrency(curEdit.getText().toString());
					expense.setSpend(Float.parseFloat(spendEdit.getText().toString()));
					expense.setDescription(descriptionEdit.getText().toString());
					expense.setPicPath(picPath);
					expense.setKpmg_hours(hoursEdit.getText().toString());
					expense.setKpmg_claimed(claimsEdit.getText().toString());
					dataSource.updateExpense(id, expense);
				} 
				else 
				{
					dataSource.createKPMG(Float.parseFloat(spendEdit.getText().toString()),
							descriptionEdit.getText().toString(),
							dateEdit.getText().toString(),
							curEdit.getText().toString(), picPath, hoursEdit
									.getText().toString(), claimsEdit.getText()
									.toString());		
				}
				dataSource.close();
				finish();
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		AssetsUtilities.bmpRecycle(bmp);
//		super.onPause();
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		AssetsUtilities.bmpRecycle(bmp);
		AssetsUtilities.cleanTempMediaFile();
		super.onDestroy();
	}
}
