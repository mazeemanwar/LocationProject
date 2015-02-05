package com.driverconnex.vehicles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.driverconnex.app.R;
import com.driverconnex.utilities.Utilities;

/**
 * Activity for displaying MPG and updating known MPG.
 * 
 * @author Adrian Klimczak
 * 
 */

public class EconomyActivity extends Activity {
	private TextView quotedMPGText;
	private TextView knownMPGText;
	private EditText knownMPGEdit;

	private DCVehicle vehicle;
	private int mpg = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_economy);

		quotedMPGText = (TextView) findViewById(R.id.quotedText);
		knownMPGText = (TextView) findViewById(R.id.knownText);
		knownMPGEdit = (EditText) findViewById(R.id.mpgEdit);

		knownMPGEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// Check if "Done" key is pressed
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					mpg = Integer.parseInt(knownMPGEdit.getText().toString());
					knownMPGText.setText(knownMPGEdit.getText() + " MPG");
					knownMPGEdit.clearFocus();
					knownMPGEdit.getText().clear();

					Utilities.hideIM(EconomyActivity.this, v);
				}

				return false;
			}
		});

		if (getIntent().getExtras() != null) {
			// Gets vehicle from previous activity
			vehicle = (DCVehicle) getIntent().getExtras().getSerializable(
					"vehicle");

			if (vehicle != null) {
				quotedMPGText.setText("" + vehicle.getQuotedMPG() + " MPG");
				knownMPGText.setText("" + vehicle.getKnownMPG() + " MPG");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// Checks if mpg was set by the user
			if (mpg != 0) {
				vehicle.setKnownMPG(mpg);

				System.out.println(vehicle.getId());
				BaasDocument.fetch("BAAVehicle", vehicle.getId(),
						new BaasHandler<BaasDocument>() {

							@Override
							public void handle(BaasResult<BaasDocument> res) {
								// TODO Auto-generated method stub
								if (res.isSuccess()) {
									BaasDocument doc = res.value();
									doc.put("vehicleKnownMPG",
											vehicle.getKnownMPG());

									doc.save(new BaasHandler<BaasDocument>() {

										@Override
										public void handle(
												BaasResult<BaasDocument> arg0) {
											// TODO Auto-generated
											// method stub
											// loading.setVisibility(View.INVISIBLE);

											// Finishes this activity
											Intent intent = new Intent();
											setResult(RESULT_OK, intent);
											intent.putExtra("vehicle", vehicle);

											finish();
											overridePendingTransition(
													R.anim.slide_right_main,
													R.anim.slide_right_sub);
										}
									});
									Log.d("LOG", "Document: " + doc);
								} else {
									Log.e("LOG", "error", res.error());
								}
							}
						});

				// Updates MPG field for the DCVehicle in the Parse
				// ParseQuery<ParseObject> query =
				// ParseQuery.getQuery("DCVehicle");
				// query.getInBackground(vehicle.getId(), new
				// GetCallback<ParseObject>()
				// {
				// @Override
				// public void done(ParseObject object, com.parse.ParseException
				// e)
				// {
				// if (e == null)
				// {
				// object.put("vehicleKnownMPG", vehicle.getKnownMPG());
				// object.saveInBackground();
				// }
				// else
				// Log.e("get vehicle", e.getMessage());
				// }
				// });
			}

			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			intent.putExtra("vehicle", vehicle);

			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
