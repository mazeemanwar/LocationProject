package com.driverconnex.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.driverconnex.utilities.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity for registering a new user.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class RegisterActivity extends Activity {
	private EditText firstNameEdit;
	private EditText lastNameEdit;
	private EditText emailEdit;
	private EditText passwordEdit;
	private EditText confirmEdit;

	private RelativeLayout loading;
	// BaasBox login token
	private RequestToken mSignupOrLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regsiter);

		firstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
		lastNameEdit = (EditText) findViewById(R.id.lastNameEdit);
		emailEdit = (EditText) findViewById(R.id.emailEdit);
		passwordEdit = (EditText) findViewById(R.id.passwordEdit);
		confirmEdit = (EditText) findViewById(R.id.confirmEdit);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		confirmEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_GO)) {
					// Checks if user didn't input his first name
					if (firstNameEdit.getText().length() == 0) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(R.string.register_first_name_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Checks if user didn't input his last name
					else if (lastNameEdit.getText().length() == 0) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(R.string.register_last_name_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Checks if user didn't input his email
					else if (emailEdit.getText().length() == 0) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(R.string.register_email_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Checks if user didn't input his password
					else if (passwordEdit.getText().length() == 0) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(R.string.register_password_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Checks if user didn't input his password again
					else if (confirmEdit.getText().length() == 0) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(
										R.string.register_confirm_password_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Checks if inputed passwords match together
					else if (!passwordEdit.getText().toString()
							.equals(confirmEdit.getText().toString())) {
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Error")
								.setMessage(
										R.string.register_password_match_error)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
					// Otherwise registers new user
					else {
						// check if location services active ?
						// if (locationInit()) {
						if (AppConfig.getIsOnlineModuleRequired().equals("yes")) {
							parseRegisterProcess();
						} else {
							BaasUser user = BaasUser.withUserName(emailEdit
									.getText().toString());
							user.setPassword(passwordEdit.getText().toString());
							mSignupOrLogin = user.signup(onComplete);

						}

						// }
					}
				}

				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// check location services
	private Boolean locationInit() {
		AlertDialog.Builder builder = new Builder(RegisterActivity.this);

		if (!Utilities.checkNetworkEnabled(this)
				&& !Utilities.checkGPSEnabled(this)) {

			builder.setMessage("Please turn on Location Services in your System Settings.");
			builder.setTitle("Notification");
			builder.setPositiveButton("Settings",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
							loading.setVisibility(View.GONE);
						}
					});

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							loading.setVisibility(View.GONE);
							dialog.dismiss();
						}
					});

			builder.setCancelable(false);
			builder.create().show();
			return false;
		}
		return true;
	}

	// PARSE REGISTER PROCESS
	private void parseRegisterProcess() {

		final ParseUser user = new ParseUser();
		user.setUsername(emailEdit.getText().toString());
		user.setPassword(passwordEdit.getText().toString());
		user.setEmail(emailEdit.getText().toString());

		user.put("userFirstName", firstNameEdit.getText().toString());
		user.put("userSurname", lastNameEdit.getText().toString());

		loading.setVisibility(View.VISIBLE);

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// Sets shared preferences for a new
					// user
					DriverConnexApp.getUserPref().setUserID(user.getObjectId());
					DriverConnexApp.getUserPref().setUserName(
							user.getUsername());
					DriverConnexApp.getUserPref().setLogin(true);
					DriverConnexApp.getUserPref().updateSharedPreferences();

					Intent intent = new Intent(RegisterActivity.this,
							LoginActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_right_main,
							R.anim.slide_right_sub);
				} else {
					Log.e("Parse Sign up", e.getMessage());

					new AlertDialog.Builder(RegisterActivity.this)
							.setTitle("Error")
							.setMessage(
									R.string.register_error + " "
											+ emailEdit.getText().toString())
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				}

				loading.setVisibility(View.INVISIBLE);
			}
		});

	}

	private final BaasHandler<BaasUser> onComplete = new BaasHandler<BaasUser>() {
		@Override
		public void handle(BaasResult<BaasUser> user) {

			mSignupOrLogin = null;
			if (user.isFailed()) {
				Log.d("ERROR", "ERROR", user.error());
			}
			try {
				String name = user.get().getName();

				setWelcomeMsg();
				// DriverConnexApp.getUserPref().setUserID(user.getObjectId());
				DriverConnexApp.getUserPref().setUserName(user.get().getName());
				DriverConnexApp.getUserPref().setLogin(true);
				DriverConnexApp.getUserPref().updateSharedPreferences();
				boolean test = DriverConnexApp.getUserPref().isLogin();
				System.out.println(test);
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			} catch (BaasException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// completeLogin(result.isSuccess());
		}
	};

	private void setWelcomeMsg() {
		BaasBox box = BaasBox.getDefault();

		box.rest(HttpRequest.POST, "plugin/dc.welcomeMessage",
				new JsonObject(), true, new BaasHandler<JsonObject>() {
					@Override
					public void handle(BaasResult<JsonObject> res) {
						System.out.println(res);
					}
				});

	}
}
