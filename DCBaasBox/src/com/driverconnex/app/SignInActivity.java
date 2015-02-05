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

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.driverconnex.utilities.Utilities;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Activity for Sign in to the app.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */
public class SignInActivity extends Activity {
	private EditText emailEdit, pswEdit;
	private RelativeLayout loading;
	private String email;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		emailEdit = (EditText) findViewById(R.id.firEdit);
		pswEdit = (EditText) findViewById(R.id.secEdit);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		pswEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {

					// check if location services active?
					if (locationInit()) {
						loading.setVisibility(View.VISIBLE);
						email = emailEdit.getText().toString();
						password = pswEdit.getText().toString();
						if (AppConfig.getIsOnlineModuleRequired().endsWith(
								"yes")) {
							parseSignInProcess();
						} else {
							BaasUser user = BaasUser.withUserName(email);
							user.setPassword(password);
							user.login(onComplete);
							DriverConnexApp.getUserPref().setLogin(true);
							DriverConnexApp.getUserPref()
									.updateSharedPreferences();

						}
					}
				}
				return false;
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
		AlertDialog.Builder builder = new Builder(SignInActivity.this);

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

	private void parseSignInProcess() {

		ParseUser.logInInBackground(email, password, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					// Sets shared preferences for a new
					// user
					DriverConnexApp.getUserPref().setUserID(user.getObjectId());
					DriverConnexApp.getUserPref().setUserName(
							user.getUsername());
					DriverConnexApp.getUserPref().setLogin(true);
					DriverConnexApp.getUserPref().updateSharedPreferences();

					loading.setVisibility(View.GONE);

					Intent intent = new Intent(SignInActivity.this,
							LoginActivity.class);
					SignInActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.slide_right_main,
							R.anim.slide_right_sub);
				} else {
					// Signup failed. Look at the
					// ParseException to see what
					// happened.
					new AlertDialog.Builder(SignInActivity.this)
							.setTitle("Error")
							.setMessage("Username or Password not recognized.")
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();

					loading.setVisibility(View.GONE);
					Log.e("login", e.getMessage());
				}
			}
		});

	}

	private final BaasHandler<BaasUser> onComplete = new BaasHandler<BaasUser>() {
		@Override
		public void handle(BaasResult<BaasUser> user) {

			if (user.isFailed()) {
				Log.d("ERROR", "ERROR", user.error());
			}
			try {
				String name = user.get().getName();
				System.out.println("email is " + name);
				// DriverConnexApp.getUserPref().setUserID(user.getObjectId());
				DriverConnexApp.getUserPref().setUserName(user.get().getName());
				DriverConnexApp.getUserPref().setLogin(true);
				DriverConnexApp.getUserPref().updateSharedPreferences();
				boolean test = DriverConnexApp.getUserPref().isLogin();
				System.out.println(test);
				Intent intent = new Intent(SignInActivity.this,
						LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			} catch (BaasException e) {
				// Signup failed. Look at the
				// ParseException to see what
				// happened.
				new AlertDialog.Builder(SignInActivity.this)
						.setTitle("Error")
						.setMessage("Username or Password not recognized.")
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();

				loading.setVisibility(View.GONE);
				Log.e("login", e.getMessage());
			}

			// completeLogin(result.isSuccess());
		}
	};

}
