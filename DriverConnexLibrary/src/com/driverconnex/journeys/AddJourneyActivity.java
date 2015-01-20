package com.driverconnex.journeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;

import com.driverconnex.adapter.ListAdapter;
import com.driverconnex.adapter.ListAdapterItem;
import com.driverconnex.app.R;
import com.driverconnex.utilities.LocationUtilities;
import com.driverconnex.utilities.Utilities;
import com.driverconnex.vehicles.VehiclesListActivity;

/**
 * Activity used to add a DCJourney object to the database.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class AddJourneyActivity extends Activity {
	private EditText descEdit;
	private static TextView startTimeEdit;
	
	// private static TextView startDateEdit;
	// private static TextView endDateEdit;
	private static TextView endTimeEdit;
	private EditText distanceEdit;
	private TextView vehilceEdit;

	private ListView locationsBtn;
	private ArrayList<ListAdapterItem> items = new ArrayList<ListAdapterItem>();
	private ListAdapter adapter;
	private ArrayList<DCJourneyPoint> points;

	private Button businessBtn;
	private Button personalBtn;

	private boolean isBusiness = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journey_add);

		// Get views
		descEdit = (EditText) findViewById(R.id.descEdit);
		// startDateEdit = (TextView) findViewById(R.id.startDateTxt);
		startTimeEdit = (TextView) findViewById(R.id.startTimeTxt);
		// endDateEdit = (TextView) findViewById(R.id.endDateTxt);
		endTimeEdit = (TextView) findViewById(R.id.endTimeTxt);
		distanceEdit = (EditText) findViewById(R.id.distanceTxt);

		locationsBtn = (ListView) findViewById(R.id.locaBtn);
		vehilceEdit = (TextView) findViewById(R.id.vechileSelectEdit);
		businessBtn = (Button) findViewById(R.id.businessBtn);
		personalBtn = (Button) findViewById(R.id.personalBtn);

		// Set listener for the views
		descEdit.setOnClickListener(onClickListener);
		startTimeEdit.setOnClickListener(onClickListener);
		endTimeEdit.setOnClickListener(onClickListener);
		// startDateEdit.setOnClickListener(onClickListener);
		// endDateEdit.setOnClickListener(onClickListener);
		businessBtn.setOnClickListener(onClickListener);
		personalBtn.setOnClickListener(onClickListener);
		distanceEdit.setOnClickListener(onClickListener);
		vehilceEdit.setOnClickListener(onClickListener);

		descEdit.setOnFocusChangeListener(onFocusChangeListener);
		startTimeEdit.setOnFocusChangeListener(onFocusChangeListener);
		endTimeEdit.setOnFocusChangeListener(onFocusChangeListener);
		// startDateEdit.setOnFocusChangeListener(onFocusChangeListener);
		// endDateEdit.setOnFocusChangeListener(onFocusChangeListener);
		distanceEdit.setOnFocusChangeListener(onFocusChangeListener);

		descEdit.setOnEditorActionListener(onEditorActionListener);
		distanceEdit.setOnEditorActionListener(onEditorActionListener);

		ListAdapterItem item = new ListAdapterItem();
		item.title = "0 Locations selected.";
		item.subtitle = "Estimated mileage: ";

		items.add(item);

		points = new ArrayList<DCJourneyPoint>();
		adapter = new ListAdapter(this, items);
		locationsBtn.setAdapter(adapter);
		locationsBtn.setOnItemClickListener(onItemClickListener);

		// startDateEdit.setText((String) DateFormat.format("dd-MM-yyyy",
		// Calendar.getInstance().getTime()));
		// endDateEdit.setHint((String) DateFormat.format("dd-MM-yyyy",
		// Calendar.getInstance().getTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.action_save) {
			if (checkCompleted()) {
				// Check if user selected two location points
				if (points.size() > 1) {
					DCJourney journey = prepare4Database();

					JourneyDataSource dataSource = new JourneyDataSource(
							AddJourneyActivity.this);
					dataSource.open();
					dataSource.createJourney(journey, points);
					dataSource.close();

					finish();
					overridePendingTransition(R.anim.null_anim,
							R.anim.slide_out);
				}
				// Otherwise display warning
				else {
					new AlertDialog.Builder(AddJourneyActivity.this)
							.setTitle("Error")
							.setMessage(
									getResources().getString(
											R.string.journey_error_info))
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}

									}).show();
				}
			} else {
				new AlertDialog.Builder(AddJourneyActivity.this)
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
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				Intent intent = new Intent(AddJourneyActivity.this,
						AddLocationActivity.class);

				if (points.size() > 0)
					intent.putExtra("points", points);

				startActivityForResult(intent, 199);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == 199) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				points = data.getParcelableArrayListExtra("points");

				if (points.size() == 1)
					items.get(0).title = points.size() + " Location selected.";
				else
					items.get(0).title = points.size() + " Locations selected.";

				if (points.size() >= 2) {
					// Connect with Google maps to get the distance from the
					// points
					new calculateDistanceTask().execute();
				}

				adapter.notifyDataSetChanged();
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Utilities.hideIM(AddJourneyActivity.this, v);

			// Check if user touched business button
			if (v == businessBtn || v == personalBtn)
				selectBusinessButton(v);
			// else if (v == startDateEdit)
			// {
			// DialogFragment newFragment = new DatePickerFragment();
			// newFragment.show(getFragmentManager(), "startDatePicker");
			// }
			// else if (v == endDateEdit)
			// {
			// DialogFragment newFragment = new DatePickerFragment();
			// newFragment.show(getFragmentManager(), "endDatePicker");
			// }
			else if (v == startTimeEdit) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "startTimePicker");
			} else if (v == endTimeEdit) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "endTimePicker");
			} else if (v == distanceEdit || v == descEdit) {
				startEditingText(v, true);
			} else if (v == vehilceEdit) {
				Intent intent = new Intent(AddJourneyActivity.this,
						VehiclesListActivity.class);

				intent.putExtra("fromActivity", true);
				startActivity(intent);
				AddJourneyActivity.this.overridePendingTransition(
						R.anim.slide_in, R.anim.null_anim);

			}
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			Utilities.hideIM(AddJourneyActivity.this, v);

			// if (v == startDateEdit)
			// {
			// if (hasFocus)
			// {
			// DialogFragment newFragment = new DatePickerFragment();
			// newFragment.show(getFragmentManager(), "startDatePicker");
			// }
			// }
			// else if (v == endDateEdit)
			// {
			// if (hasFocus)
			// {
			// DialogFragment newFragment = new DatePickerFragment();
			// newFragment.show(getFragmentManager(), "endDatePicker");
			// }
			// }
			// else
			if (v == startTimeEdit) {
				if (hasFocus) {
					DialogFragment newFragment = new TimePickerFragment();
					newFragment.show(getFragmentManager(), "startTimePicker");
				}
			} else if (v == endTimeEdit) {
				if (hasFocus) {
					DialogFragment newFragment = new TimePickerFragment();
					newFragment.show(getFragmentManager(), "endTimePicker");
				}
			} else if (v == distanceEdit || v == descEdit) {
				if (hasFocus)
					startEditingText(v, true);
				else
					startEditingText(v, false);
			}
		}
	};

	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
					|| (actionId == EditorInfo.IME_ACTION_DONE)) {
				startEditingText(v, false);
			}
			return false;
		}
	};

	private DCJourney prepare4Database() {
		DCJourney journey = new DCJourney();

		String distanceStr = Utilities
				.getFirstNumberDecimalFromText(distanceEdit.getText()
						.toString());

		journey.setDescription(descEdit.getText().toString());
		journey.setStartTime("", startTimeEdit.getText().toString());
		journey.setEndTime("", endTimeEdit.getText().toString());
		//
		// // Convert date to a format used in calculateDuration()
		// String startDate =
		// Utilities.convertDateFormat(startTimeEdit.getText()
		// .toString(), "dd-MM-yyyy", "yyyy-MM-dd");
		// String endDate = Utilities.convertDateFormat(endTimeEdit.getText()
		// .toString(), "dd-MM-yyyy", "yyyy-MM-dd");
		String startTime = Utilities.getTimeFromDate(startTimeEdit.getText()
				.toString());
		String endTime = Utilities.getTimeFromDate(endTimeEdit.getText()
				.toString());
		//
		long diff = Utilities.calculateTimeDuration(startTime, endTime);
		journey.setDuration(Utilities.calculateTimeDuration(startTime, endTime));

		journey.setDistance(Double.valueOf(distanceStr));
		journey.setBusiness(isBusiness);

		double averageSpeed = journey.getDistance() / journey.getDuration()
				* 60;
		journey.setAvgSpeed((float) averageSpeed);

		if (points.size() > 0) {
			String startAddr = LocationUtilities.getAddressFromLatlng(this,
					points.get(0).getLat(), points.get(0).getLng());
			String endAddr = LocationUtilities.getAddressFromLatlng(this,
					points.get(points.size() - 1).getLat(),
					points.get(points.size() - 1).getLng());

			if (startAddr != null)
				journey.setStartAddr(startAddr);
			if (endAddr != null)
				journey.setEndAddr(endAddr);
		}

		return journey;
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			if (getTag() == "startDatePicker") {
				// startDateEdit.setText(new
				// StringBuilder().append(Utilities.intToTime(day)).append("-")
				// .append(Utilities.intToTime(month + 1)).append("-")
				// .append(year));

			} else if (getTag() == "endDatePicker") {
				// endDateEdit.setText(new
				// StringBuilder().append(Utilities.intToTime(day)).append("-")
				// .append(Utilities.intToTime(month + 1)).append("-")
				// .append(year));
			}
		}
	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			if (getTag() == "startTimePicker") {
				startTimeEdit.setText(new StringBuilder()
						.append(Utilities.intToTime(hourOfDay)).append(":")
						.append(Utilities.intToTime(minute)));
			}
			if (getTag() == "endTimePicker") {
				endTimeEdit.setText(new StringBuilder()
						.append(Utilities.intToTime(hourOfDay)).append(":")
						.append(Utilities.intToTime(minute)));
			}
		}
	}

	private class calculateDistanceTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			double mileage = 0;

			// Loop trough all location points
			for (int i = 0; i < points.size() - 1; i++) {
				// Get the distance between two points
				String distanceInKilometers = (LocationUtilities
						.getDistFromGoogleMaps(points.get(i).getLat(), points
								.get(i).getLng(), points.get(i + 1).getLat(),
								points.get(i + 1).getLng()));

				// Make sure string contains only a number
				distanceInKilometers = Utilities
						.getFirstNumberDecimalFromText(distanceInKilometers);

				double distanceInMiles = 0;

				if (!distanceInKilometers.isEmpty())
					// Convert string into double
					distanceInMiles = Double.parseDouble(distanceInKilometers) * 0.6;

				mileage += distanceInMiles;
			}

			items.get(0).subtitle = "Estimated mileage: "
					+ String.format("%.2f", mileage) + " Miles";

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
		}
	}

	private void startEditingText(View v, boolean editing) {
		if (v == distanceEdit) {
			if (distanceEdit.getText().toString().contains("Miles")) {
				// " Miles" has 6 characters, including space
				distanceEdit.getText().delete(
						distanceEdit.getText().length() - 6,
						distanceEdit.getText().length());
			}

			// Starts editing
			if (editing) {
				distanceEdit.setCursorVisible(true);
			}
			// Stops editing
			else {
				distanceEdit.setCursorVisible(false);
				setText(v);
			}
		} else if (v == descEdit) {
			// Starts editing
			if (editing) {
				descEdit.setCursorVisible(true);
			}
			// Stops editing
			else {
				descEdit.setCursorVisible(false);
			}
		}
	}

	private void setText(View v) {
		if (v == distanceEdit) {
			distanceEdit.setText(distanceEdit.getText() + " Miles");
		}
	}

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

	private boolean checkCompleted() {
		if (descEdit.getText().length() == 0
				|| startTimeEdit.getText().length() == 0
				|| endTimeEdit.getText().length() == 0
				// || startDateEdit.getText().length() == 0
				// || endDateEdit.getText().length() == 0
				|| distanceEdit.getText().length() == 0) {
			return false;
		}

		return true;
	}

}
