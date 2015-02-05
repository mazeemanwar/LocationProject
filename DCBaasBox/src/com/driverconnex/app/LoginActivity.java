package com.driverconnex.app;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.driverconnex.data.DatabaseHelper;
import com.driverconnex.singletons.DCMessageSingleton;
import com.driverconnex.vehicles.AddVehicleActivity;

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
		// String s = AppConfig.get
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Checks if user is logged in
		if (DriverConnexApp.getUserPref().isLogin()) {
			// Hide everything else than load spinner
			registerBtn.setVisibility(View.INVISIBLE);
			loginBtn.setVisibility(View.INVISIBLE);
			logo.setVisibility(View.INVISIBLE);
			facebookBar.setVisibility(View.INVISIBLE);
			loadSpinner.setVisibility(View.VISIBLE);
			DCMessageSingleton.getDCModuleSingleton(LoginActivity.this)
					.getSerMessage();
			// Runs it with a bit of delay so that splash screen can be
			// displayed
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Checks if user doesn't have any vehicle

					BaasDocument.fetchAll("BAAVehicle",
							new BaasHandler<List<BaasDocument>>() {
								@Override
								public void handle(
										BaasResult<List<BaasDocument>> res) {

									if (res.isSuccess()) {
										int s = res.value().size();
										setProperActivity(s);

										for (BaasDocument doc : res.value()) {
											Log.d("LOG", "Doc: " + doc);
										}
									} else {
										registerBtn.setVisibility(View.VISIBLE);
										loginBtn.setVisibility(View.VISIBLE);
										logo.setVisibility(View.VISIBLE);
										facebookBar.setVisibility(View.VISIBLE);
										loadSpinner.setVisibility(View.GONE);
									}
								}
							});

					// Checks if user doesn't have any vehicle
				}
			};

			new Handler().postDelayed(runnable, 3000);
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

	private void setProperActivity(int nVehicles) {
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
			DatabaseHelper.setDatabaseName(BaasUser.current().getName());

			Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_sub,
					R.anim.slide_left_main);
		}

	}
}
