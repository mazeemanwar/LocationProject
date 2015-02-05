package com.driverconnex.basicmodules;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Activity for displaying user account information.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 *         NOTE: - userSurname from Parse for some reason started to return
 *         "null". It may be due to changes that Greg did recently in the Parse.
 *         - I haven't figure it out yet why scrolling in this activity
 *         "is cutting" in another words not smooth.
 */

public class MyAccountActivity extends Activity {
	private final int REQUEST_PHOTO_CODE = 100;

	private TextView emailText;
	private TextView usernameText;
	private TextView joinedText;
	private TextView appVersionText;
	private TextView membershipText;
	private TextView organisationText;
	private TextView groupText;
	private TextView appNameText;

	private LinearLayout pictureLayout;
	private ImageView userPhoto;
	private byte[] userPhotoByte;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		setOrganisaiton();
		setGroup();
		// Get text views from the layout
		emailText = (TextView) findViewById(R.id.emailText);
		usernameText = (TextView) findViewById(R.id.usernameText);
		joinedText = (TextView) findViewById(R.id.joinedText);
		appVersionText = (TextView) findViewById(R.id.appVersionText);
		// membershipText = (TextView) findViewById(R.id.membershipText);
		appNameText = (TextView) findViewById(R.id.applicationNameTxt);
		organisationText = (TextView) findViewById(R.id.organisationText);
		groupText = (TextView) findViewById(R.id.groupText);

		pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
		userPhoto = (ImageView) findViewById(R.id.photoEdit);
		// we have to display appName name to lower case. 
		appNameText.setText(appNameText.getText().toString().toLowerCase());
		pictureLayout.setOnClickListener(onClickListener);

		// Get user that is currently using this app
		ParseUser user = ParseUser.getCurrentUser();

		// Get user's profile photo
		ParseFile userPhotoFile = user.getParseFile("userProfilePhoto");

		if (userPhotoFile != null) {
			userPhotoFile.getDataInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					if (e == null) {
						userPhotoByte = data;

						Bitmap bmp = AssetsUtilities.readBitmap(data);

						if (bmp != null)
							userPhoto.setImageBitmap(bmp);
					} else
						Log.e("get user profile photo", e.getMessage());
				}
			});
		}

		// Get his name
		String username = user.getString("userFirstName") + " "
				+ user.getString("userSurname");

		// Fill details with the data
		emailText.setText(user.getString("email"));
		usernameText.setText(username);
		joinedText.setText((String) DateFormat.format("dd MMM yyyy",
				user.getCreatedAt()));
		// ParseObject userOrganisation =
		// user.getParseObject("userOrganisation");
		// ParseObject userGroup = user.getParseObject("userGroup");
		// ParseObject organisationObject = ParseObject
		// .createWithoutData("DCOrganisation", userOrganisation
		// .getObjectId().toString());
		// System.out.println(userOrganisation
		// .getObjectId().toString());
		// String organisation =
		// organisationObject.getString("organisationName");
		// String group = userGroup.getString("groupName");
		//
		// organisationText.setText(organisation);
		// groupText.setText(group);
		// App version name
		try {
			appVersionText
					.setText(getBaseContext().getPackageManager()
							.getPackageInfo(getBaseContext().getPackageName(),
									0).versionName);
		} catch (NameNotFoundException e) {
			// In case it fails to get version name of the app
			appVersionText.setText("1.0");
			e.printStackTrace();
		}

		// At the moment hard coded membership
		// membershipText.setText("Free");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PHOTO_CODE) {
			if (resultCode != RESULT_OK)
				return;

			if (data.getData() != null) {
				int screenHeight = getWindowManager().getDefaultDisplay()
						.getHeight();
				Bitmap bmp = AssetsUtilities.bmpResize(AssetsUtilities
						.readBitmapFromUriInPortraitMode(
								MyAccountActivity.this, data.getData(), true),
						screenHeight);

				if (bmp != null)
					userPhoto.setImageBitmap(bmp);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_account_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			return true;
		}
		// Check if password icon on action bar is selected
		else if (item.getItemId() == R.id.action_change_password) {
			// It meant to take user to a new activity where they can change the
			// password
			// but since Parse doesn't give the option to get the password of
			// the user
			// from database, we need to do it in a different way
			// -------------------
			/*
			 * Intent intent = new Intent(this, ChangePasswordActivity.class);
			 * this.startActivity(intent);
			 * 
			 * // Add animation for changing activity
			 * this.overridePendingTransition(R.anim.slide_in,
			 * R.anim.null_anim);
			 */
			// --------------------

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Send password reset email to inbox?")
					.setTitle("Password Reset")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									try {
										ParseUser
												.requestPasswordReset(ParseUser
														.getCurrentUser()
														.getEmail());
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});

			AlertDialog dialog = builder.create();
			dialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == pictureLayout) {
				Intent intent = new Intent(MyAccountActivity.this,
						MyAccountPhotoActivity.class);
				intent.putExtra("photo", userPhotoByte);

				startActivityForResult(intent, REQUEST_PHOTO_CODE);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	private void setOrganisaiton() {
		ParseObject userOrganisationObject = ParseUser.getCurrentUser()
				.getParseObject("userOrganisation");
		// should be find a direct way to updated organisation and group
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCOrganisation");
		query.getInBackground(userOrganisationObject.getObjectId().toString(),
				new GetCallback<ParseObject>() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							organisationText.setText(object
									.getString("organisationName"));
						} else {
							// something went wrong
						}
					}
				});

	}

	private void setGroup() {
		ParseObject userGroupObject = ParseUser.getCurrentUser()
				.getParseObject("userGroup");
		// should be find a direct way to updated organisation and group
		ParseQuery<ParseObject> query = ParseQuery.getQuery("DCGroup");
		query.getInBackground(userGroupObject.getObjectId().toString(),
				new GetCallback<ParseObject>() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							groupText.setText(object.getString("groupName"));
						} else {
							// something went wrong
						}
					}
				});

	}
}
