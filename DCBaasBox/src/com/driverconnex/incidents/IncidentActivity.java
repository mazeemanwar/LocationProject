package com.driverconnex.incidents;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.Organisation;
import com.driverconnex.utilities.Utilities;

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
					+ "<br><br>• Remain calm"
					+ "<br><br>• Assess the scene and the seriousness of the incident"
					+ "<br><br>• If there are injured persons, or the scene presents a risk to other road users"
					+ ", phone the emergency services immediately"
					+ "<br><br>• If you have reflective jackets in the vehicle wear them"
					+ "<br><br>• If it is safe and you have one, put a warning triangle "
					+ "on the road at least 45 metres behind your vehicle"
					+ "<br><br>• Do not stand between your vehicle and oncoming traffic"
					+ "<br><br>• Ask drivers to switch off their engines and stop smoking"
					+ "<br><br>• Do not move injured people from their vehicles unless "
					+ "they are in immediate danger from fire or explosion"
					+ "<br><br>• Do not remove a motorcyclst's helmet unless it is essential to do so"
					+ "<br><br>• Be prepared to give first aid"
					+ "<br><br>• Stay at the scene until emergency services arrive"
					+ "<br><br>• Do not admit fault, apportioning blame after a car accident is for others to decide"
					+ "<br><br>• Record details of the incident to enable you to inform your "
					+ "insurer (note - you may wish to use the Accident Report feature of this app)"
					+ "<br><br>• Tell your insurer as soon as possible after the incident <br><br>";
		} else if (incidentActivity == INCIDENT_BREAKDOWN) {
			guidance = "Please note this information is provided for guidance only."
					+ "<br><br><b>On a motorway:</b>"
					+ "<br><br>• Pull onto the hard shoulder and stop as far to the left as possible"
					+ "<br><br>• Put on your hazard warning lights and, if it is dark or visibility "
					+ "is poor, your sidelights"
					+ "<br><br>• If you have reflective jackets in the vehicle wear them"
					+ "<br><br>• Do not use a warning triangle on the hard shoulder"
					+ "<br><br>• Make sure that you and all occupants leave the vehicle by the left hand doors"
					+ "<br><br>• Move to a safe area away from the carriageway"
					+ "<br><br>• Leave any animals in the vehicle"
					+ "<br><br>• Keep children under control"
					+ "<br><br>• Do not attempt even simple repairs"
					+ "<br><br><b>On other roads:</b>"
					+ "<br><br>• Put on your hazard warning lights and, if it is "
					+ "dark or visibility is poor, your sidelights"
					+ "<br><br>• If you have reflective jackets in the vehicle wear them"
					+ "<br><br>• If it is safe and you have one, put a warning triangle on "
					+ "the road at least 45 metres behind your vehicle"
					+ "<br><br>• If you have any fear that your vehicle may be struck "
					+ "by other traffic make, move to a safe area away from the road"
					+ "<br><br>• Do not stand between your vehicle and oncoming traffic"
					+ "<br><br>• Leave any animals in the vehicle"
					+ "<br><br>• Keep children under control" + "<br><br>";

		}

		text.setText(Html.fromHtml(guidance));
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

				// final ParseUser user = ParseUser.getCurrentUser();
				//
				// // Get user's default vehicle
				// ParseObject defaultVehicle = user
				// .getParseObject("userDefaultVehicle");
				// defaultVehicle
				// .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
				// @Override
				// public void done(ParseObject vehicle,
				// ParseException e) {
				// if (e == null) {
				// // Get vehicle's cover
				// ParseObject cover = vehicle
				// .getParseObject("vehicleCover");
				// cover.fetchIfNeededInBackground(new
				// GetCallback<ParseObject>() {
				// @Override
				// public void done(ParseObject cover,
				// ParseException e) {
				// Intent i = new Intent(
				// Intent.ACTION_SEND);
				//
				// // Get the user name
				// String username = user
				// .getString("userFirstName")
				// + " "
				// + user.getString("userSurname");
				//
				// String policyNumber = "(null)";
				// String provider = "(null)";
				// String contactNumber = "(null)";
				//
				// if (e == null) {
				// policyNumber = cover
				// .getString("coverPolicyNumber");
				// provider = cover
				// .getString("coverProvider");
				// contactNumber = cover
				// .getString("coverContactNumber");
				// } else
				// Log.e("Get cover",
				// e.getMessage());
				//
				// // Body of the email
				// String emailBody = "" + username
				// + "\nPolicy Number: "
				// + policyNumber
				// + "\nProvider: " + provider
				// + "\nContact Number: "
				// + contactNumber;
				//
				// // Prepare intent
				// i.setType("message/rfc822");
				// i.putExtra(
				// Intent.EXTRA_EMAIL,
				// new String[] { "recipient@example.com" });
				// i.putExtra(Intent.EXTRA_SUBJECT,
				// "Insurance Details for "
				// + username);
				// i.putExtra(Intent.EXTRA_TEXT,
				// emailBody);
				//
				// try {
				// startActivity(Intent
				// .createChooser(i,
				// "Send mail..."));
				// } catch (android.content.ActivityNotFoundException ex) {
				// Toast.makeText(
				// IncidentActivity.this,
				// "There are no email clients installed.",
				// Toast.LENGTH_SHORT)
				// .show();
				// }
				// }
				// });
				// } else
				// Log.e("Get default vehicle", e.getMessage());
				// }
				// });
				//
			} else if (v == callEmergencyBtn) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						IncidentActivity.this);
				Organisation organisationConfig = new Organisation();
				final String phoneNumber[] = new String[4];
				final CharSequence[] items = new CharSequence[4];

				if (incidentActivity == INCIDENT_ACCIDENT) {
					phoneNumber[0] = "0300 100 8889";
					phoneNumber[1] = "999";
					phoneNumber[2] = "101";
					phoneNumber[3] = "112";

					items[0] = "Skoda Driverline " + phoneNumber[0];
					items[1] = "Emergency services " + phoneNumber[1];
					items[2] = "Police (non emergency) " + phoneNumber[2];
					items[3] = "EU emergency service " + phoneNumber[3];

				} else if (incidentActivity == INCIDENT_BREAKDOWN) {

					phoneNumber[0] = "0300 100 8889";
					phoneNumber[1] = "999";
					phoneNumber[2] = "101";
					phoneNumber[3] = "112";

					items[0] = "Skoda Driverline " + phoneNumber[0];
					items[1] = "Emergency services " + phoneNumber[1];
					items[2] = "Police (non emergency) " + phoneNumber[2];
					items[3] = "EU emergency service " + phoneNumber[3];

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
