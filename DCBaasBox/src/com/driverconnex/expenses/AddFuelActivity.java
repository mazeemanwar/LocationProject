package com.driverconnex.expenses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ThemeUtilities;
import com.driverconnex.utilities.Utilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.VehiclesListActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Activity for adding fuel expense and reviewing existing fuel expense.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class AddFuelActivity extends Activity {
	private final int CAPTURE_IMAGE_REQUEST_CODE = 90;
	private final int REQUEST_VEHICLE_CODE = 300;

	private LinearLayout photoEdit;

	private EditText descriptionEdit;
	private EditText spendEdit;
	private EditText volumeEdit;
	private EditText mileageEdit;

	private TextView currencyText;
	private TextView vatText;
	private TextView vehicleText;
	private TextView dateText;

	private Button businessBtn;
	private Button personalBtn;

	private AlertDialog.Builder builder;
	private DatePickerDialog datePickerDialog;

	private ImageView photo;

	private boolean isBusiness = true;
	private boolean isModify = false;

	private DCExpense expense;
	private String pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense_add_fuel);

		// Get views
		descriptionEdit = (EditText) findViewById(R.id.descEdit);
		volumeEdit = (EditText) findViewById(R.id.volumeEdit);
		mileageEdit = (EditText) findViewById(R.id.mileageEdit);
		spendEdit = (EditText) findViewById(R.id.spendEdit);

		dateText = (TextView) findViewById(R.id.dateEdit);
		currencyText = (TextView) findViewById(R.id.currencyEdit);
		vatText = (TextView) findViewById(R.id.VATEdit);
		vehicleText = (TextView) findViewById(R.id.vehicleText);

		photo = (ImageView) findViewById(R.id.fuelPhoto);
		photoEdit = (LinearLayout) findViewById(R.id.photoEdit);

		businessBtn = (Button) findViewById(R.id.businessBtn);
		personalBtn = (Button) findViewById(R.id.personalBtn);

		// Set listeners
		photoEdit.setOnClickListener(onClickListener);
		dateText.setOnClickListener(onClickListener);
		currencyText.setOnClickListener(onClickListener);
		vatText.setOnClickListener(onClickListener);
		volumeEdit.setOnClickListener(onClickListener);
		mileageEdit.setOnClickListener(onClickListener);
		businessBtn.setOnClickListener(onClickListener);
		personalBtn.setOnClickListener(onClickListener);
		spendEdit.setOnClickListener(onClickListener);
		vehicleText.setOnClickListener(onClickListener);

		dateText.setOnFocusChangeListener(onFocusChangeListener);
		currencyText.setOnFocusChangeListener(onFocusChangeListener);
		vatText.setOnFocusChangeListener(onFocusChangeListener);
		mileageEdit.setOnFocusChangeListener(onFocusChangeListener);
		volumeEdit.setOnFocusChangeListener(onFocusChangeListener);
		spendEdit.setOnFocusChangeListener(onFocusChangeListener);
		vehicleText.setOnFocusChangeListener(onFocusChangeListener);

		spendEdit.setOnEditorActionListener(onEditorActionListener);
		volumeEdit.setOnEditorActionListener(onEditorActionListener);
		mileageEdit.setOnEditorActionListener(onEditorActionListener);

		// Initialise data
		init();
	}

	private void init() {
		// Check if there are any extras from the intent
		if (getIntent().getExtras() != null) {
			// We are modifying information
			isModify = true;

			// Get extras
			expense = getIntent().getExtras().getParcelable("expense");

			descriptionEdit.setText(expense.getDescription());
			dateText.setText(expense.getDateString());
			spendEdit.setText(String.valueOf(expense.getSpend()));
			currencyText.setText(expense.getCurrency());
			volumeEdit.setText(String.valueOf(expense.getVolume()));
			mileageEdit.setText(String.valueOf(expense.getMileage()));
			vehicleText.setText(expense.getVehicle());

			isBusiness = expense.isBusiness();

			String is_vat = expense.isVat() ? "Yes" : "No";
			vatText.setText(is_vat);

			pic = expense.getPicPath();

			startEditingText(spendEdit, false);
			startEditingText(volumeEdit, false);
			startEditingText(mileageEdit, false);

			// Check if picture path is not empty
			if (pic != null) {
				// Set picture
				Bitmap bmp = null;

				try {
					File file = AssetsUtilities.getOutMediaFile(
							AssetsUtilities.EXPENSE_PIC_PATH, pic);

					if (file != null) {
						bmp = BitmapFactory.decodeStream(new FileInputStream(
								file));

						if (bmp != null)
							photo.setImageBitmap(bmp);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			if (isBusiness) {
				// Deselect personal option
				personalBtn.setBackgroundDrawable(null);
				personalBtn.setTextColor(getResources().getColor(
						R.color.main_interface));

				// Select business option
				businessBtn.setBackgroundResource(R.color.main_interface);
				businessBtn
						.setTextColor(getResources().getColor(R.color.white));
			} else {
				// Deselect business option
				businessBtn.setBackgroundDrawable(null);
				businessBtn.setTextColor(getResources().getColor(
						R.color.main_interface));

				// Select personal option
				personalBtn.setBackgroundResource(R.color.main_interface);
				personalBtn
						.setTextColor(getResources().getColor(R.color.white));
			}
		}
		// User didn't come from ReviewActivity he came from AddFuelActivity
		else {
			// Set default date
			dateText.setText((String) DateFormat.format("dd-MM-yyyy", Calendar
					.getInstance().getTime()));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bitmap bmp = null;
				File tempFile = AssetsUtilities.getOutTempMediaFile();
				try {
					if (tempFile != null) {
						bmp = BitmapFactory.decodeFile(tempFile
								.getAbsolutePath());
						photo.setImageBitmap(bmp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == REQUEST_VEHICLE_CODE) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				DCVehicle vehicle = (DCVehicle) data.getExtras()
						.getSerializable("vehicle");

				if (vehicle != null)
					vehicleText.setText(vehicle.getRegistration());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		if (isModify)
			inflater.inflate(R.menu.add_expense, menu);
		else
			inflater.inflate(R.menu.action_save, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();

			if (isModify)
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			else
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);

			return true;
		} else if (item.getItemId() == R.id.action_save) {
			if (dateText.getText().length() == 0
					|| spendEdit.getText().length() == 0
					|| volumeEdit.getText().length() == 0
					|| mileageEdit.getText().length() == 0) {
				new AlertDialog.Builder(AddFuelActivity.this)
						.setTitle("Error")
						.setMessage(
								getResources().getString(
										R.string.expense_error_info))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				// Save picture from temp folder
				String picPath = AssetsUtilities.relocateTempPhotoForExpense();

				// Check if any new picture was saved
				// If it wasn't then use the path of the picture that is already
				// set. If there is
				// no picture at all, then picPath will be null.
				if (picPath == null)
					picPath = pic;

				boolean isVat = vatText.getText().toString().equals("Yes") ? true
						: false;

				String spent = Utilities
						.getFirstNumberDecimalFromText(spendEdit.getText()
								.toString());
				String volume = Utilities
						.getFirstNumberDecimalFromText(volumeEdit.getText()
								.toString());
				String mileage = Utilities.getFirstNumberFromText(mileageEdit
						.getText().toString());

				if (expense == null)
					expense = new DCExpense();

				expense.setDate(dateText.getText().toString());
				expense.setCurrency(currencyText.getText().toString());
				expense.setSpend(Float.parseFloat(spent));
				expense.setDescription(descriptionEdit.getText().toString());
				expense.setBusiness(isBusiness);
				expense.setVat(isVat);
				expense.setPicPath(picPath);
				expense.setVolume(Float.parseFloat(volume));
				expense.setMileage(Long.parseLong(mileage));

				if (vehicleText.getText().toString()
						.contains("default vehicle")) {
					ParseUser user = ParseUser.getCurrentUser();
					ParseObject vehicle = user
							.getParseObject("userDefaultVehicle");

					vehicle.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
						@Override
						public void done(ParseObject object, ParseException e) {
							if (e == null) {
								expense.setVehicle(object
										.getString("vehicleRegistration"));
							} else
								Log.e("get default vehicle", e.getMessage());
						}

					});
				} else
					expense.setVehicle(vehicleText.getText().toString());

				ExpensesDataSource dataSource = new ExpensesDataSource(
						AddFuelActivity.this);
				dataSource.open();

				if (isModify)
					dataSource.updateExpense(expense);
				else
					dataSource.createFuel(expense);

				dataSource.close();

				finish();
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);

				return true;
			}
		} else if (item.getItemId() == R.id.action_delete) {
			new AlertDialog.Builder(AddFuelActivity.this)
					.setTitle("Delete Expense")
					.setMessage(
							getResources().getString(R.string.expense_warning))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ExpensesDataSource dataSource = new ExpensesDataSource(
											AddFuelActivity.this);
									dataSource.open();
									dataSource.deleteExpense(expense.getId());
									dataSource.close();

									finish();
									overridePendingTransition(R.anim.null_anim,
											R.anim.slide_out);
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		AssetsUtilities.cleanTempMediaFile();
		super.onDestroy();
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// hideIM(v);

			// Check if user touched business button
			if (v == businessBtn || v == personalBtn)
				selectBusinessButton(v);
			// Check if user touched date field
			else if (v == dateText) {
				initDatePickerDialog();
				datePickerDialog.show();
				ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
						datePickerDialog);
			}
			// Check if user touched currency field
			else if (v == currencyText) {
				initAlertDialog(R.string.expense_details_title_currency,
						R.array.expense_details_currency, v);
				ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
						builder.show());
			} else if (v == vatText) {
				initAlertDialog(R.string.expense_details_title_vat,
						R.array.details_yesno, v);
				ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
						builder.show());
			}
			// Check if user touched photo field
			else if (v == photoEdit) {
				Intent intent = new Intent(AddFuelActivity.this,
						AddExpensePhotoActivity.class);

				if (isModify) {
					Bundle extras = new Bundle();
					extras.putString("pic_path", pic);
					intent.putExtras(extras);
				}

				startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			} else if (v == spendEdit || v == mileageEdit || v == volumeEdit)
				startEditingText((EditText) v, true);
			else if (v == vehicleText) {
				Bundle extras = new Bundle();
				extras.putBoolean("vehiclePicker", true);

				Intent intent = new Intent(AddFuelActivity.this,
						VehiclesListActivity.class);
				intent.putExtras(extras);

				startActivityForResult(intent, REQUEST_VEHICLE_CODE);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			Utilities.hideIM(AddFuelActivity.this, v);

			if (v == dateText) {
				if (hasFocus) {
					initDatePickerDialog();
					datePickerDialog.show();
					ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
							datePickerDialog);
				}
			} else if (v == currencyText) {
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_currency,
							R.array.expense_details_currency, v);
					ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
							builder.show());
				}
			} else if (v == vatText) {
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_vat,
							R.array.details_yesno, v);
					ThemeUtilities.changeDialogTheme(AddFuelActivity.this,
							builder.show());
				}
			} else if (v == spendEdit || v == mileageEdit || v == volumeEdit) {
				if (hasFocus)
					startEditingText((EditText) v, true);
				else
					startEditingText((EditText) v, false);
			} else if (v == vehicleText) {
				if (hasFocus) {
					Bundle extras = new Bundle();
					extras.putBoolean("vehiclePicker", true);

					Intent intent = new Intent(AddFuelActivity.this,
							VehiclesListActivity.class);
					intent.putExtras(extras);

					startActivityForResult(intent, REQUEST_VEHICLE_CODE);
					overridePendingTransition(R.anim.slide_left_sub,
							R.anim.slide_left_main);
				}
			}
		}
	};

	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
					|| (actionId == EditorInfo.IME_ACTION_DONE)) {
				startEditingText((EditText) v, false);
			}
			return false;
		}
	};

	private void selectBusinessButton(View v) {
		if (v == businessBtn) {
			if (!isBusiness) {
				// Deselect personal option
				personalBtn.setBackgroundDrawable(null);
				personalBtn.setTextColor(getResources().getColor(
						R.color.main_interface));

				// Select business option
				businessBtn.setBackgroundResource(R.color.main_interface);
				businessBtn
						.setTextColor(getResources().getColor(R.color.white));

				// Choose business option
				isBusiness = true;
			}
		}
		// Check if user touched personal button
		else if (v == personalBtn) {
			if (isBusiness) {
				// Deselect business option
				businessBtn.setBackgroundDrawable(null);
				businessBtn.setTextColor(getResources().getColor(
						R.color.main_interface));

				// Select personal option
				personalBtn.setBackgroundResource(R.color.main_interface);
				personalBtn
						.setTextColor(getResources().getColor(R.color.white));

				// Choose personal option
				isBusiness = false;
			}
		}
	}

	/**
	 * Handles what happens when text is being edited or if it stopped being
	 * edited.
	 * 
	 * @param editText
	 * @param editing
	 */
	private void startEditingText(EditText editText, boolean editing) {
		// Starts editing
		if (editing) {
			editText.setCursorVisible(true);

			if (editText == spendEdit) {
				if (spendEdit.getText().toString().contains("€")
						|| spendEdit.getText().toString().contains("£")) {
					if (spendEdit.getText().length() > 2)
						spendEdit.getText().delete(0, 2);
				}
			} else if (editText == volumeEdit) {
				if (volumeEdit.getText().toString().contains("Litres")) {
					// " Litres" has 7 characters, including space
					volumeEdit.getText().delete(
							volumeEdit.getText().length() - 7,
							volumeEdit.getText().length());
				}
			} else if (editText == mileageEdit) {
				if (mileageEdit.getText().toString().contains("Miles")) {
					// " Miles" has 6 characters, including space
					mileageEdit.getText().delete(
							mileageEdit.getText().length() - 6,
							mileageEdit.getText().length());
				}

			}
		}
		// Stops editing
		else {
			editText.setCursorVisible(false);

			if (editText == spendEdit) {
				if (currencyText.getText().toString().contains("€")) {
					if (!spendEdit.getText().toString().contains("€")
							&& !spendEdit.getText().toString().isEmpty())
						spendEdit.setText("€ " + spendEdit.getText());
				} else if (currencyText.getText().toString().contains("£")) {
					if (!spendEdit.getText().toString().contains("£")
							&& !spendEdit.getText().toString().isEmpty())
						spendEdit.setText("£ " + spendEdit.getText());
				}
			} else if (editText == volumeEdit) {
				if (!volumeEdit.getText().toString().contains("Litres")
						&& !volumeEdit.getText().toString().isEmpty())
					volumeEdit.setText(volumeEdit.getText() + " Litres");
			} else if (editText == mileageEdit) {
				if (!mileageEdit.getText().toString().contains("Miles")
						&& !mileageEdit.getText().toString().isEmpty())
					mileageEdit.setText(mileageEdit.getText() + " Miles");
			}
		}
	}

	private void initDatePickerDialog() {
		Calendar c = Calendar.getInstance();
		datePickerDialog = new DatePickerDialog(this, mDateSetListener,
				c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			dateText.setText(new StringBuilder()
					.append(Utilities.intToTime(day)).append("-")
					.append(Utilities.intToTime(month + 1)).append("-")
					.append(year));
		}
	};

	private void initAlertDialog(int title, final int items, final View view) {
		builder = new AlertDialog.Builder(AddFuelActivity.this);
		builder.setTitle(title).setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						((TextView) view).setText(getResources()
								.getStringArray(items)[which]);

						if (view == currencyText) {
							if (spendEdit.getText().toString().contains("€")
									|| spendEdit.getText().toString()
											.contains("£")) {
								if (spendEdit.getText().length() > 2)
									spendEdit.getText().delete(0, 2);

								if (currencyText.getText().toString()
										.contains("€")) {
									if (!spendEdit.getText().toString()
											.contains("€")
											&& !spendEdit.getText().toString()
													.isEmpty())
										spendEdit.setText("€ "
												+ spendEdit.getText());
								} else if (currencyText.getText().toString()
										.contains("£")) {
									if (!spendEdit.getText().toString()
											.contains("£")
											&& !spendEdit.getText().toString()
													.isEmpty())
										spendEdit.setText("£ "
												+ spendEdit.getText());
								}
							}
						}
					}
				});

		builder.create();
	}
}