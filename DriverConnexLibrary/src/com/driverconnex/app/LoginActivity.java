package com.driverconnex.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.driverconnex.basicmodules.InvitesActivity;
import com.driverconnex.data.DatabaseHelper;
import com.driverconnex.data.Organisation;
import com.driverconnex.vehicles.AddVehicleActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Login activity for the app. When user doesn't have any vehicle it will take
 * them to the activity for adding a vehicle. Otherwise user will be taken to
 * the home screen.
 * 
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class LoginActivity extends Activity {
	private Button registerBtn, loginBtn;
	private ImageView logo;
	private LinearLayout loadSpinner;
	private RelativeLayout facebookBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		registerBtn = (Button) findViewById(R.id.businessBtn);
		loginBtn = (Button) findViewById(R.id.personalBtn);
		loadSpinner = (LinearLayout) findViewById(R.id.loadSpinner);
		logo = (ImageView) findViewById(R.id.logo);
		facebookBar = (RelativeLayout) findViewById(R.id.facebookBar);

		registerBtn.setOnClickListener(onClickListener);
		loginBtn.setOnClickListener(onClickListener);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Checks if user is logged in
		if (DriverConnexApp.getUserPref().isLogin()) {
			appLoginProces();
		} else {
			// We are back to Login screen so make these things visible and hide
			// spinner
			registerBtn.setVisibility(View.VISIBLE);
			loginBtn.setVisibility(View.VISIBLE);
			logo.setVisibility(View.VISIBLE);
			facebookBar.setVisibility(View.VISIBLE);
			loadSpinner.setVisibility(View.GONE);
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;

			if (v == registerBtn)
				intent = new Intent(LoginActivity.this, RegisterActivity.class);

			else if (v == loginBtn)
				intent = new Intent(LoginActivity.this, SignInActivity.class);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
		}
	};

	// Initial check for application . (business or personal)
	public void appLoginProces() {
		setProgressBar();
		ParseUser user = ParseUser.getCurrentUser();
		ParseObject userOrganisation = user.getParseObject("userOrganisation");
		ParseObject userGroup = user.getParseObject("userGroup");
		Boolean isEmailVerified = user.getBoolean("emailVerified");
		// Declare Dialog for reusable
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		// check if it is a business app and need an organisation
		if (AppConfig.getIsOrganizationRequried().equals("yes")) {
			// check if email verification required and is email verified
			if ((isEmailVerified)
					&& (AppConfig.getIsVerificationRequired().equals("yes"))) {
				// check if user have organisation and group coming from parse
				if (((userOrganisation != null && !"".equals(userOrganisation)) && ((userGroup != null && !""
						.equals(userGroup))))) {

					DriverConnexApp.getUserPref().setLogin(true);
					DriverConnexApp.getUserPref().updateSharedPreferences();

					loadSpinner.setVisibility(View.INVISIBLE);

					Intent intent = new Intent(LoginActivity.this,
							HomeActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_right_main,
							R.anim.slide_right_sub);

				} else {
					// No organisation found. Invite user to join an
					// organisation
					DriverConnexApp.getUserPref().setLogin(true);
					DriverConnexApp.getUserPref().updateSharedPreferences();

					loadSpinner.setVisibility(View.INVISIBLE);

					Intent intent = new Intent(LoginActivity.this,
							InvitesActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_right_main,
							R.anim.slide_right_sub);

				}

				// addFriend(position);

			} else {
				// oops email is not verified.
				builder.setMessage(R.string.error_email_verified);
				builder.setTitle("Error");
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								loadSpinner.setVisibility(View.INVISIBLE);
								DriverConnexApp.getUserPref().setLogin(false);
								Intent intent = new Intent(LoginActivity.this,
										SignInActivity.class);
								startActivity(intent);
								overridePendingTransition(
										R.anim.slide_right_main,
										R.anim.slide_right_sub);

							}
						});

				builder.show();

			}
		} else {
			setProgressBar();
			// Runs it with a bit of delay so that splash screen can be
			// displayed

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Query for the number of vehicles that belong to user
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("DCVehicle");
					query.whereEqualTo("vehiclePrivateOwner",
							ParseUser.getCurrentUser());

					int nVehicles = 0;

					try {
						nVehicles = query.count();
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// Checks if user doesn't have any vehicle
					if (nVehicles == 0) {
						// If so, then let him add the vehicle first before
						// entering the app
						Intent intent = new Intent(LoginActivity.this,
								AddVehicleActivity.class);
						intent.putExtra("isFirstVehicle", true);

						startActivity(intent);
						overridePendingTransition(R.anim.slide_left_sub,
								R.anim.slide_left_main);
					} else {
						// Sets the correct database for the user
						DatabaseHelper.setDatabaseName(ParseUser
								.getCurrentUser().getUsername());

						Intent intent = new Intent(LoginActivity.this,
								HomeActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_left_sub,
								R.anim.slide_left_main);
					}
				}
			};

			new Handler().postDelayed(runnable, 3000);

		}
		// set organisation details in local preference
		getOrganisationConfig();
		// push notification
		// ParseInstallation installation = ParseInstallation
		// .getCurrentInstallation();
		// installation.put("user", ParseUser.getCurrentUser());
		// PushService.setDefaultPushCallback(this, SignInActivity.class);
		// installation.saveInBackground();
	}

	// getting organisation configuration from parse and set to local
	// sharedPrefrence.
	private void getOrganisationConfig() {
		// here check should be added for null pointer organisation
		ParseObject organisation = ParseUser.getCurrentUser().getParseObject(
				"userOrganisation");
		if (organisation != null) {

			ParseQuery<ParseObject> query = ParseQuery
					.getQuery("DCOrganisation");
			query.getInBackground(organisation.getObjectId().toString(),
					new GetCallback<ParseObject>() {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						public void done(ParseObject object, ParseException e) {
							if (e == null) {
								Organisation organisation = new Organisation();

								Map myArray = (Map) object
										.get("organisationConfiguration");
								// Default Currency

								Iterator entries = myArray.entrySet()
										.iterator();
								while (entries.hasNext()) {

									Map.Entry entry = (Map.Entry) entries
											.next();
									String key = (String) entry.getKey();
									if (key.equals("Default PPM")) {
										Integer ppm = (Integer) entry
												.getValue();
										organisation.setPPM(ppm);

									} else if (key.equals("Default Currency")) {
										String currency = (String) entry
												.getValue();

										organisation.setCurrency(currency);
									}

									else if (key.equals("Accident Numbers")) {

										ArrayList<ArrayList<String>> myList = (ArrayList<ArrayList<String>>) entry
												.getValue();
										// Integer value =
										// (Integer)entry.getValue();
										organisation.setAccidentNumber(myList);
										for (int i = 0; i < myList.size(); i++) {
											List<String> st = myList.get(i);
											for (int j = 0; j < st.size(); j++) {

											}
										}

									} else if (key.equals("Breakdown Numbers")) {
										ArrayList<ArrayList<String>> myList = (ArrayList<ArrayList<String>>) entry
												.getValue();
										organisation.setBreakDown(myList);

										for (int i = 0; i < myList.size(); i++) {
											List<String> st = myList.get(i);
											for (int j = 0; j < st.size(); j++) {

											}
										}

									} else if (key.equals("Expense Types")) {

										ArrayList<String> listOfExpense = (ArrayList<String>) entry
												.getValue();
										organisation
												.setExpenseType(listOfExpense);
										for (int a = 0; a < listOfExpense
												.size(); a++) {

											// }
										}

									}
								}
								DriverConnexApp.getUserPref()
										.setOrganisationConfig(organisation);

							} else {
								// something went wrong
							}
						}
					});
		}
	}

	private void setProgressBar() {
		registerBtn.setVisibility(View.INVISIBLE);
		loginBtn.setVisibility(View.INVISIBLE);
		logo.setVisibility(View.INVISIBLE);
		facebookBar.setVisibility(View.INVISIBLE);
		loadSpinner.setVisibility(View.VISIBLE);

	}

}
