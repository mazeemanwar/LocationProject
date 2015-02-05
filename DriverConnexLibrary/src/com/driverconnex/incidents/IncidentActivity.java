package com.driverconnex.incidents;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.Organisation;
import com.driverconnex.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Main activity for Incidents Module. It displays Accidents or Breakdown.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class IncidentActivity extends Activity {
	public static final int INCIDENT_ACCIDENT = 0;
	public static final int INCIDENT_BREAKDOWN = 1;

	private Button reportBtn;
	private ImageButton emailBtn;
	private ImageButton callEmergencyBtn;
	private TextView text;

	private int incidentActivity = INCIDENT_ACCIDENT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident);

		emailBtn = (ImageButton) findViewById(R.id.emailBtn);
		callEmergencyBtn = (ImageButton) findViewById(R.id.callBtn);
		text = (TextView) findViewById(R.id.textView);
		reportBtn = (Button) findViewById(R.id.reportButton);

		reportBtn.setOnClickListener(onClickListener);
		emailBtn.setOnClickListener(onClickListener);
		callEmergencyBtn.setOnClickListener(onClickListener);

		if (getIntent().getExtras() != null)
			incidentActivity = getIntent().getExtras().getInt(
					"incidentActivity");

		// Initialise
		init();

		if (incidentActivity == INCIDENT_ACCIDENT) {
			getActionBar().setTitle("Accident Guidance");
			emailBtn.setVisibility(View.INVISIBLE);
		} else if (incidentActivity == INCIDENT_BREAKDOWN) {
			getActionBar().setTitle("Breakdown Guidance");
			emailBtn.setVisibility(View.INVISIBLE);
			reportBtn.setVisibility(View.INVISIBLE);
		}

		getActionBar().setSubtitle("Please follow the instructions below");
	}

	private void init() {
		String guidance = "";

		if (incidentActivity == INCIDENT_ACCIDENT) {
			guidance = "Please note this information is provided for guidance only."
					+ "\n\n• Remain calm"
					+ "\n\n• Assess the scene and the seriousness of the incident"
					+ "\n\n• If there are injured persons, or the scene presents a risk to other road users"
					+ ", phone the emergency services immediately"
					+ "\n\n• If you have reflective jackets in the vehicle wear them"
					+ "\n\n• If it is safe and you have one, put a warning triangle "
					+ "on the road at least 45 metres behind your vehicle"
					+ "\n\n• Do not stand between your vehicle and oncoming traffic"
					+ "\n\n• Ask drivers to switch off their engines and stop smoking"
					+ "\n\n• Do not move injured people from their vehicles unless "
					+ "they are in immediate danger from fire or explosion"
					+ "\n\n• Do not remove a motorcyclst's helmet unless it is essential to do so"
					+ "\n\n• Be prepared to give first aid"
					+ "\n\n• Stay at the scene until emergency services arrive"
					+ "\n\n• Do not admit fault, apportioning blame after a car accident is for others to decide"
					+ "\n\n• Record details of the incident to enable you to inform your "
					+ "insurer (note - you may wish to use the â€˜Accident Reportâ€™ feature of this app)"
					+ "\n\n• Tell your insurer as soon as possible after the incident";
		} else if (incidentActivity == INCIDENT_BREAKDOWN) {
			guidance = "Please note this information is provided for guidance only."
					+ "\n\n\nOn a motorway:"
					+ "\n\n• Pull onto the hard shoulder and stop as far to the left as possible"
					+ "\n\n• Put on your hazard warning lights and, if it is dark or visibility "
					+ "is poor, your sidelights"
					+ "\n\n• If you have reflective jackets in the vehicle wear them"
					+ "\n\n• Do not use a warning triangle on the hard shoulder"
					+ "\n\n• Make sure that you and all occupants leave the vehicle by the left hand doors"
					+ "\n\n• Move to a safe area away from the carriageway"
					+ "\n\n• Leave any animals in the vehicle"
					+ "\n\n• Keep children under control"
					+ "\n\n• Do not attempt even simple repairs"
					+ "\n\n\nOn other roads:"
					+ "\n\n• Put on your hazard warning lights and, if it is "
					+ "dark or visibility is poor, your sidelights"
					+ "\n\n• If you have reflective jackets in the vehicle wear them"
					+ "\n\n• If it is safe and you have one, put a warning triangle on "
					+ "the road at least 45 metres behind your vehicle"
					+ "\n\n• If you have any fear that your vehicle may be struck "
					+ "by other traffic make, move to a safe area away from the road"
					+ "\n\n• Do not stand between your vehicle and oncoming traffic"
					+ "\n\n• Leave any animals in the vehicle"
					+ "\n\n• Keep children under control";

		}

		text.setText(guidance);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.incidents_menu, menu);
		return true;
	}

	/**
	 * Items on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Go to parent activity
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		} else if (item.getItemId() == R.id.locate_incident) {
			Intent intent = new Intent(this, IncidentLocationActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
		}

		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == reportBtn) {
				Intent intent = new Intent(IncidentActivity.this,
						ReportActivity.class);

				intent.putExtra("incidentActivity", incidentActivity);
				intent.putExtra("tutorial", true);

				startActivity(intent);
				overridePendingTransition(R.anim.slide_in, R.anim.null_anim);
			} else if (v == emailBtn) {
				final ParseUser user = ParseUser.getCurrentUser();

				// Get user's default vehicle
				ParseObject defaultVehicle = user
						.getParseObject("userDefaultVehicle");
				defaultVehicle
						.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
							@Override
							public void done(ParseObject vehicle,
									ParseException e) {
								if (e == null) {
									// Get vehicle's cover
									ParseObject cover = vehicle
											.getParseObject("vehicleCover");
									cover.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
										@Override
										public void done(ParseObject cover,
												ParseException e) {
											Intent i = new Intent(
													Intent.ACTION_SEND);

											// Get the user name
											String username = user
													.getString("userFirstName")
													+ " "
													+ user.getString("userSurname");

											String policyNumber = "(null)";
											String provider = "(null)";
											String contactNumber = "(null)";

											if (e == null) {
												policyNumber = cover
														.getString("coverPolicyNumber");
												provider = cover
														.getString("coverProvider");
												contactNumber = cover
														.getString("coverContactNumber");
											} else
												Log.e("Get cover",
														e.getMessage());

											// Body of the email
											String emailBody = "" + username
													+ "\nPolicy Number: "
													+ policyNumber
													+ "\nProvider: " + provider
													+ "\nContact Number: "
													+ contactNumber;

											// Prepare intent
											i.setType("message/rfc822");
											i.putExtra(
													Intent.EXTRA_EMAIL,
													new String[] { "recipient@example.com" });
											i.putExtra(Intent.EXTRA_SUBJECT,
													"Insurance Details for "
															+ username);
											i.putExtra(Intent.EXTRA_TEXT,
													emailBody);

											try {
												startActivity(Intent
														.createChooser(i,
																"Send mail..."));
											} catch (android.content.ActivityNotFoundException ex) {
												Toast.makeText(
														IncidentActivity.this,
														"There are no email clients installed.",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									});
								} else
									Log.e("Get default vehicle", e.getMessage());
							}
						});
			} else if (v == callEmergencyBtn) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						IncidentActivity.this);
				Organisation organisationConfig = new Organisation();
				final String phoneNumber[] = new String[2];
				final CharSequence[] items = new CharSequence[2];

				if (incidentActivity == INCIDENT_ACCIDENT) {
					phoneNumber[0] = "999";
					phoneNumber[1] = "112";
					items[0] = "Emergency Services " + phoneNumber[0];
					items[1] = "EU Emergency Services " + phoneNumber[1];
				} else if (incidentActivity == INCIDENT_BREAKDOWN) {
					if (getPhoneNumber().size() > 0) {
						phoneNumber[0] = getPhoneNumber().get(0);
						phoneNumber[1] = getPhoneNumber().get(1);

					} else {
						phoneNumber[0] = "0333 2000 999";
						phoneNumber[1] = "0800 88 77 66";

					}
					items[0] = "RAC " + phoneNumber[0];
					items[1] = "AA " + phoneNumber[1];
				}

				builder.setTitle("Emergency Numbers");

				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (Utilities.isMobilePhone(IncidentActivity.this)) {
							Intent callIntent = null;

							switch (which) {
							case 0:
								callIntent = new Intent(Intent.ACTION_DIAL, Uri
										.parse("tel:" + phoneNumber[0]));
								break;
							case 1:
								callIntent = new Intent(Intent.ACTION_DIAL, Uri
										.parse("tel:" + phoneNumber[1]));
							}

							if (callIntent != null)
								startActivity(callIntent);
						}
					}
				});

				// Set the cancel button
				builder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// It doesn't do anything than closing the
								// dialog
								dialog.dismiss();
							}
						});

				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	};

	private ArrayList<String> getPhoneNumber() {
		ArrayList<String> breakDownPhone = new ArrayList<String>();
		Organisation organisationConfig = DriverConnexApp.getUserPref()
				.getOrganizationConfig();

		ArrayList<ArrayList<String>> myList = organisationConfig.getBreakDown();
		for (int i = 0; i < myList.size(); i++) {
			List<String> st = myList.get(i);
			for (int j = 0; j < st.size(); j++) {
				System.out.println("breakdown values" + st.get(j));
				breakDownPhone.add(st.get(j));
			}
		}

		return breakDownPhone;

	}
}
