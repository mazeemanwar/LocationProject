package com.driverconnex.journeys;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.R;
import com.driverconnex.expenses.AddFuelActivity;
import com.driverconnex.singletons.DCScoreSingleton;
import com.driverconnex.utilities.LocationUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.VehiclesListActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Activity for displaying details of the journey.
 * 
 * @author Yin Lee(SGI)
 * @author Adrian Klimczak
 * 
 */

public class JourneyDetailsActivity extends Activity {
	private int[] txtIds = { R.id.startTimeTxt, R.id.endTimeTxt,
			R.id.durationTxt, R.id.distanceTxt, R.id.expenseTxt,
			R.id.startAddrTxt, R.id.endAddrTxt, R.id.avgSpeedTxt,
			R.id.emissionTxt };

	private EditText descEdit;
	private TextView regText;
	private ImageButton delete, save;
	private Button businessBtn;
	private Button personalBtn;
	private Button behaviourBtn;
	private final int REQUEST_VEHICLE_CODE = 300;

	private DCJourney journey;
	private ArrayList<DCJourneyPoint> points;

	private boolean isModify = false;
	private boolean isBusiness = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journey_details);

		delete = (ImageButton) findViewById(R.id.deleteBtn);
		save = (ImageButton) findViewById(R.id.saveBtn);
		descEdit = (EditText) findViewById(R.id.descEdit);
		regText = (TextView) findViewById(R.id.regTxt);
		businessBtn = (Button) findViewById(R.id.businessBtn);
		personalBtn = (Button) findViewById(R.id.personalBtn);
		behaviourBtn = (Button) findViewById(R.id.behaviourBtn);

		descEdit.setOnClickListener(onClickListener);
		regText.setOnClickListener(onClickListener);
		delete.setOnClickListener(onClickListener);
		save.setOnClickListener(onClickListener);
		businessBtn.setOnClickListener(onClickListener);
		personalBtn.setOnClickListener(onClickListener);
		behaviourBtn.setOnClickListener(onClickListener);

		descEdit.setOnEditorActionListener(onEditorActionListener);

		behaviourBtn.setEnabled(false);

		init();
	}

	private void init() {
		if (getIntent().getExtras() != null) {
			setRegVehilce();
			journey = getIntent().getExtras().getParcelable("journey");
			isModify = getIntent().getExtras().getBoolean("modify", false);

			getActionBar().setDisplayHomeAsUpEnabled(isModify);
			getActionBar().setHomeButtonEnabled(isModify);

			// Gets journey points
			JourneyDataSource dataSource = new JourneyDataSource(
					JourneyDetailsActivity.this);
			dataSource.open();
			points = dataSource.getJourneyPoints(journey.getId());
			dataSource.close();

			// Check if user is modifying existing journey
			if (isModify) {
				if (journey.getDescription() != null)
					descEdit.setText(journey.getDescription().toString());

				isBusiness = journey.isBusiness();

				// Initialise business buttons
				if (isBusiness) {
					// Deselect personal option
					personalBtn.setBackgroundDrawable(null);
					personalBtn.setTextColor(getResources().getColor(
							R.color.main_interface));

					// Select business option
					businessBtn.setBackgroundResource(R.color.main_interface);
					businessBtn.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					// Deselect business option
					businessBtn.setBackgroundDrawable(null);
					businessBtn.setTextColor(getResources().getColor(
							R.color.main_interface));

					// Select personal option
					personalBtn.setBackgroundResource(R.color.main_interface);
					personalBtn.setTextColor(getResources().getColor(
							R.color.white));
				}
			}

			for (int i = 0; i < txtIds.length; i++) {
				TextView txt = (TextView) findViewById(txtIds[i]);

				if (txtIds[i] == R.id.endAddrTxt)
					new GetAddressTask().execute(points);
				if (journey.getValuesByOrder(i) != null)
					txt.setText(journey.getValuesByOrder(i).toString());
			}

			// If score has not been calculate it will calculate it
			if (!journey.isScoreAdded())
				new calculateBehaviourTask().execute();

			// If emissions have not been yet calculated it will calculate them.
			if (journey.getEmissions() <= 0) {
				// Get default vehicle of the user
				ParseObject vehicle = ParseUser.getCurrentUser()
						.getParseObject("userDefaultVehicle");

				// Fetch vehicle attributes
				vehicle.fetchInBackground(new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject object,
							com.parse.ParseException e) {
						if (e == null) {
							double emissionDistance = Double.valueOf(journey
									.getDistance());

							// Work out the journey emissions
							// Get the emissions per kilometer for the user's
							// current vehicle
							int gkm = (Integer) object
									.getNumber("vehicleEmissions");
							// Convert the km to miles
							float gm = gkm * 0.621f;
							// Multiply this by the number of miles travelled
							float netEmissions = (float) (gm * emissionDistance);
							// Add 15% to get the final reading
							float grossEmissions = netEmissions
									+ ((netEmissions / 100) * 15);

							// Set journey's emissions
							journey.setEmissions(grossEmissions);

							for (int i = 0; i < txtIds.length; i++) {
								TextView txt = (TextView) findViewById(txtIds[i]);

								if (txtIds[i] == R.id.emissionTxt)
									txt.setText(""
											+ journey.getRoundedEmissions()
											+ " grams");
							}
						} else
							Log.e("fetch default vehicle: ", e.getMessage());
					}
				});
			} else {
				for (int i = 0; i < txtIds.length; i++) {
					TextView txt = (TextView) findViewById(txtIds[i]);

					if (txtIds[i] == R.id.emissionTxt)
						txt.setText("" + journey.getRoundedEmissions()
								+ " grams");
				}
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Check if user touched business button
			if (v == businessBtn || v == personalBtn) {
				selectBusinessButton(v);
			} else if (v == delete) {
				new AlertDialog.Builder(JourneyDetailsActivity.this)
						.setTitle("Delete Journey")
						.setMessage(
								getResources().getString(
										R.string.journey_delete_warning2))
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										long journeyId = journey.getId();

										// Deletes journey
										JourneyDataSource dataSource = new JourneyDataSource(
												JourneyDetailsActivity.this);

										dataSource.open();
										dataSource.deleteJourney(journey);
										dataSource.close();

										// Journey is deleted, now delete in
										// background all journey points and
										// behaviour points associated with
										// deleted journey
										new deleteJourneyTask(
												JourneyDetailsActivity.this)
												.execute(journeyId);

										// Finish this activity
										Intent returnIntent = new Intent();
										returnIntent.putExtra("finish", true);
										setResult(RESULT_OK, returnIntent);

										finish();
										overridePendingTransition(
												R.anim.null_anim,
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
			} else if (v == save) {
				JourneyDataSource dataSource = new JourneyDataSource(
						JourneyDetailsActivity.this);

				journey.setDescription(descEdit.getText().toString());
				journey.setBusiness(isBusiness);

				// Journey has been already created in the tracking activity, so
				// it will always only update
				dataSource.open();
				dataSource.updateJourney(journey);
				dataSource.close();

				// Check if user came from ReviewActivity, if so then user
				// should go back there
				if (isModify) {
					finish();
					overridePendingTransition(R.anim.null_anim,
							R.anim.slide_out);
				}
				// Otherwise, user came from TrackActivity, he added a journey
				// so he should go back to home activity
				// rather than track journey activity.
				else {
					Intent intent = new Intent(JourneyDetailsActivity.this,
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_right_main,
							R.anim.slide_right_sub);
				}
			} else if (v == behaviourBtn) {
				Intent intent = new Intent(JourneyDetailsActivity.this,
						BehaviourScoreActivity.class);
				intent.putExtra("score", journey.getBehaviourScore());
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in, R.anim.null_anim);
			} else if (v == regText) {
				Intent intent = new Intent(JourneyDetailsActivity.this,
						VehiclesListActivity.class);

				intent.putExtra("key", "journeyActivity");
				startActivity(intent);
				JourneyDetailsActivity.this.overridePendingTransition(
						R.anim.slide_in, R.anim.null_anim);
			} else if (v == descEdit)
				startEditingText(v, true);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Check if user came from ReviewActivity, if so then user should go
			// back there
			if (isModify)
				finish();
			// Otherwise, user came from TrackActivity, he added a journey so he
			// should go back to home activity
			// rather than track journey activity.
			else {
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
			}

			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);

			return true;
		} else if (item.getItemId() == R.id.action_map) {
			Intent intent = new Intent(JourneyDetailsActivity.this,
					RouteActivity.class);

			if (!points.isEmpty())
				intent.putExtra("points", points);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.journey_details, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * Overrides back soft key. If user came from TrackingActivity, then back
	 * button shouldn't take him to previous screen. If he came from
	 * ReviewActivity then back button should take him back to that screen.
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// Check if
		if (isModify) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
		}
	}

	/**
	 * Gets start and end address of the journey in the background
	 * 
	 */
	private class GetAddressTask extends
			AsyncTask<ArrayList<DCJourneyPoint>, Void, String[]> {
		@Override
		protected String[] doInBackground(ArrayList<DCJourneyPoint>... points) {
			String[] results = new String[2];
			results[0] = " ";
			results[1] = " ";

			if (points[0].size() == 0) {
				results[0] = "-";
				results[1] = "-";
			} else {
				// Get address from first point, which is where user started
				// tracking
				results[0] = LocationUtilities.getAddressFromLatlng(
						JourneyDetailsActivity.this, points[0].get(0).getLat(),
						points[0].get(0).getLng());
				// Get address from the last point, which is where user stopped
				// tracking
				results[1] = LocationUtilities.getAddressFromLatlng(
						JourneyDetailsActivity.this,
						points[0].get(points[0].size() - 1).getLat(), points[0]
								.get(points[0].size() - 1).getLng());
			}

			return results;
		}

		@Override
		protected void onPostExecute(String[] results) {
			super.onPostExecute(results);
			TextView startTxt = (TextView) findViewById(R.id.startAddrTxt);
			TextView endTxt = (TextView) findViewById(R.id.endAddrTxt);
			journey.setStartAddr(results[0]);
			journey.setEndAddr(results[1]);
			startTxt.setText(results[0]);
			endTxt.setText(results[1]);
		}
	}

	/**
	 * Selects business or personal button.
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

	private void startEditingText(View v, boolean editing) {
		if (v == descEdit) {
			// Starts editing
			if (editing)
				descEdit.setCursorVisible(true);
			// Stops editing
			else
				descEdit.setCursorVisible(false);
		}
	}

	/**
	 * Calculates behaviour score in the background
	 * 
	 */
	private class calculateBehaviourTask extends
			AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... keywords) {
			DCScoreSingleton.calculateJourneyScore(JourneyDetailsActivity.this,
					journey);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean results) {
			behaviourBtn.setEnabled(journey.isValidBehaviour());
			super.onPostExecute(results);
		}
	}

	/**
	 * Deletes behaviour and journey points associated with given journey in the
	 * background.
	 * 
	 */
	private class deleteJourneyTask extends AsyncTask<Long, Void, Long> {
		private Context context;

		public deleteJourneyTask(Context context) {
			this.context = context;
		}

		@Override
		protected Long doInBackground(Long... params) {
			JourneyDataSource dataSource = new JourneyDataSource(context);
			dataSource.open();

			// Deletes all behaviour points and journey points associated with
			// the journey
			dataSource.deleteBehaviourPoints(params[0]);
			dataSource.deleteJourneyPoints(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Long journeyId) {
			super.onPostExecute(journeyId);
		}
	}

	private void setRegVehilce() {
		ParseUser user = ParseUser.getCurrentUser();
		ParseObject vehicleQuery = user.getParseObject("userDefaultVehicle");

		vehicleQuery.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					regText.setText((object.getString("vehicleRegistration")));
				} else
					Log.e("get default vehicle", e.getMessage());
			}

		});

	}
}
