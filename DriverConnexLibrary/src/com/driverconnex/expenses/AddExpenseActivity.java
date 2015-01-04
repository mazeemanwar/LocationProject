package com.driverconnex.expenses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import android.widget.Toast;

import com.driverconnex.adapter.CustomAlertAdapter;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.Organisation;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ThemeUtilities;
import com.driverconnex.utilities.Utilities;

/**
 * Activity for adding an expense and reviewing existing expense.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class AddExpenseActivity extends Activity {
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 90;

	private LinearLayout photoEdit;
	private ImageView photo;

	private TextView typeEdit;
	private TextView dateText;
	private TextView currencyText;
	private TextView vatText;

	private EditText spendEdit;
	private EditText descriptionEdit;

	private Button businessBtn;
	private Button personalBtn;

	private AlertDialog.Builder builder;

	private DatePickerDialog datePickerDialog;

	private boolean isModify = false;
	private boolean isBusiness = true;

	private DCExpense expense;
	private String pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense_add);

		// Get views
		descriptionEdit = (EditText) findViewById(R.id.descEdit);
		spendEdit = (EditText) findViewById(R.id.spentEdit);
		photo = (ImageView) findViewById(R.id.photo);
		typeEdit = (TextView) findViewById(R.id.expense_type);
		dateText = (TextView) findViewById(R.id.dateEdit);
		currencyText = (TextView) findViewById(R.id.currencyEdit);
		vatText = (TextView) findViewById(R.id.VATEdit);
		photoEdit = (LinearLayout) findViewById(R.id.photoEdit);
		businessBtn = (Button) findViewById(R.id.businessBtn);
		personalBtn = (Button) findViewById(R.id.personalBtn);

		photoEdit.setOnClickListener(onClickListener);
		typeEdit.setOnClickListener(onClickListener);
		dateText.setOnClickListener(onClickListener);
		currencyText.setOnClickListener(onClickListener);
		vatText.setOnClickListener(onClickListener);
		businessBtn.setOnClickListener(onClickListener);
		personalBtn.setOnClickListener(onClickListener);
		spendEdit.setOnClickListener(onClickListener);

		typeEdit.setOnFocusChangeListener(onFocusChangeListener);
		dateText.setOnFocusChangeListener(onFocusChangeListener);
		currencyText.setOnFocusChangeListener(onFocusChangeListener);
		vatText.setOnFocusChangeListener(onFocusChangeListener);
		spendEdit.setOnFocusChangeListener(onFocusChangeListener);

		spendEdit.setOnEditorActionListener(onEditorActionListener);
		// we are setting defalt currency if there is in OrganisationConfig
		if (!getDefualtCurrency().equals("") && getDefualtCurrency() != null) {
			currencyText.setText(getDefualtCurrency());
		} else {
			currencyText.setText(expense.getCurrency());
		}
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
			typeEdit.setText(expense.getType());
			dateText.setText(expense.getDateString());
			spendEdit.setText(String.valueOf(expense.getSpend()));
			// check if there is any default currency set
			if (!getDefualtCurrency().equals("")
					&& getDefualtCurrency() != null) {
				currencyText.setText(getDefualtCurrency());
			} else {
				currencyText.setText(expense.getCurrency());
			}
			String is_vat = expense.isVat() ? "Yes" : "No";
			vatText.setText(is_vat);

			isBusiness = expense.isBusiness();

			pic = expense.getPicPath();

			startEditingText(spendEdit, false);

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

			// Check if business option is chosen
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
		} else {
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
					|| spendEdit.getText().length() == 0) {
				new AlertDialog.Builder(AddExpenseActivity.this)
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

				if (expense == null)
					expense = new DCExpense();

				expense.setDate(dateText.getText().toString());
				expense.setType(typeEdit.getText().toString());
				expense.setCurrency(currencyText.getText().toString());
				expense.setSpend(Float.parseFloat(spent));
				expense.setDescription(descriptionEdit.getText().toString());
				expense.setBusiness(isBusiness);
				expense.setVat(isVat);
				expense.setPicPath(picPath);

				ExpensesDataSource dataSource = new ExpensesDataSource(
						AddExpenseActivity.this);
				dataSource.open();

				if (isModify)
					dataSource.updateExpense(expense);
				else
					dataSource.createOtherExpense(expense);

				dataSource.close();

				finish();
				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
				return true;
			}
		} else if (item.getItemId() == R.id.action_delete) {
			new AlertDialog.Builder(AddExpenseActivity.this)
					.setTitle("Delete Expense")
					.setMessage(
							getResources().getString(R.string.expense_warning))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ExpensesDataSource dataSource = new ExpensesDataSource(
											AddExpenseActivity.this);
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
			if (v == businessBtn || v == personalBtn)
				selectBusinessButton(v);
			else if (v == typeEdit) {
				String[] expenseArray = getResources().getStringArray(
						R.array.expense_details_type);
				List<String> list = new ArrayList<String>();
				list = Arrays.asList(expenseArray);
				ArrayList<String> arrayList = new ArrayList<String>(list);
				if (getExpenseTypeFromDefault().size() > 0) {
					for (int i = 0; i < getExpenseTypeFromDefault().size(); i++) {
						arrayList.add(getExpenseTypeFromDefault().get(i));
					}
					expenseArray = arrayList.toArray(new String[list.size()]);
				}

				typeinitAlertDialog(R.string.expense_details_title_type,
						arrayList, v);
				ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
						builder.show());
			} else if (v == dateText) {
				initDatePickerDialog();
				datePickerDialog.show();
				ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
						datePickerDialog);
			} else if (v == currencyText) {
				String s = getDefualtCurrency();
				System.out.println(s);
				if (getDefualtCurrency().equals("€ EUR")) {

					initAlertDialog(R.string.expense_details_title_currency,
							R.array.expense_details_currency_euro, v);
				} else {
					initAlertDialog(R.string.expense_details_title_currency,
							R.array.expense_details_currency, v);
				}
				ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
						builder.show());
			} else if (v == vatText) {
				initAlertDialog(R.string.expense_details_title_vat,
						R.array.details_yesno, v);
				ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
						builder.show());
			} else if (v == photoEdit) {
				Intent intent = new Intent(AddExpenseActivity.this,
						AddExpensePhotoActivity.class);

				if (isModify) {
					Bundle extras = new Bundle();
					extras.putString("pic_path", pic);
					intent.putExtras(extras);
				}

				startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			} else if (v == spendEdit)
				startEditingText((EditText) v, true);
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// Hides keyboard
			Utilities.hideIM(AddExpenseActivity.this, v);

			if (v == typeEdit) {
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_type,
							R.array.expense_details_type, v);
					ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
							datePickerDialog);
				}
			} else if (v == dateText) {
				if (hasFocus) {
					initDatePickerDialog();
					datePickerDialog.show();
					ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
							datePickerDialog);
				}
			} else if (v == currencyText) {
				if (hasFocus) {
					String s = getDefualtCurrency();
					System.out.println(s);
					if (getDefualtCurrency().equals("€ EUR")) {

						initAlertDialog(
								R.string.expense_details_title_currency,
								R.array.expense_details_currency_euro, v);
					} else {
						initAlertDialog(
								R.string.expense_details_title_currency,
								R.array.expense_details_currency, v);
					}
					ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
							builder.show());
				}
			} else if (v == vatText) {
				if (hasFocus) {
					initAlertDialog(R.string.expense_details_title_vat,
							R.array.details_yesno, v);
					ThemeUtilities.changeDialogTheme(AddExpenseActivity.this,
							builder.show());
				}
			} else if (v == spendEdit) {
				if (hasFocus)
					startEditingText((EditText) v, true);
				else
					startEditingText((EditText) v, false);
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

	/**
	 * Selects business or personal button
	 * 
	 * @param v
	 */
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
			}
		}
	}

	/**
	 * Initialises date picker dialog.
	 */
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

	/**
	 * Initialises alert dialog
	 * 
	 * @param title
	 * @param items
	 * @param view
	 */
	private void initAlertDialog(int title, final int items, final View view) {
		builder = new AlertDialog.Builder(AddExpenseActivity.this);
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

	// only for expence type list. because some time we have to add some item to
	// this list
	private void typeinitAlertDialog(int title, final ArrayList<String> list,
			final View view) {
		builder = new AlertDialog.Builder(AddExpenseActivity.this);
		builder.setTitle(title);
		builder.setAdapter(new CustomAlertAdapter(AddExpenseActivity.this,
				R.layout.custom_list_dialog, list),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						typeEdit.setText(list.get(item));
						dialog.dismiss();
					}
				});

		builder.create();
	}

	private ArrayList<String> getExpenseTypeFromDefault() {
		ArrayList<String> expenseTypeList = new ArrayList<String>();
		Organisation organisationConfig = DriverConnexApp.getUserPref()
				.getOrganizationConfig();
		ArrayList<String> listOfExpense = organisationConfig.getExpenseType();
		for (int a = 0; a < listOfExpense.size(); a++) {
			System.out.println("expense value" + listOfExpense.get(a));
			expenseTypeList.add(listOfExpense.get(a));
		}
		return expenseTypeList;
	}

	private String getDefualtCurrency() {

		Organisation organisationConfig = DriverConnexApp.getUserPref()
				.getOrganizationConfig();
		String defualtCurrency = organisationConfig.getCurrency();
		return defualtCurrency;
	}
}
