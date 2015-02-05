package com.driverconnex.vehicles;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.driverconnex.app.AppConfig;
import com.driverconnex.app.R;
import com.driverconnex.singletons.DCVehicleSingleton;
import com.driverconnex.singletons.DCVehilceDataSingleton;
import com.driverconnex.utilities.AssetsUtilities;

/**
 * Activity for displaying information about given vehicle.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class VehicleDetailsActivity extends Activity {
	private RelativeLayout vehicleLayout;
	private LinearLayout motLayout;
	private LinearLayout taxLayout;
	private LinearLayout insuranceLayout;
	private LinearLayout serviceLayout;
	private LinearLayout mileageLayout;
	private LinearLayout economyLayout;
	private LinearLayout financeLayout;
	private LinearLayout vehicleChecksLayout;
	private LinearLayout serviceTabLayout;
	private LinearLayout financeHistoryLayout;

	private RelativeLayout loading;

	private TextView modelTextView;
	private TextView derivativeTextView;
	private TextView mileageTextView;
	private TextView emissionTextView;
	private TextView fuelTextView;
	// private TextView alertsTextView;
	private TextView motTextView;
	private TextView taxTextView;
	private TextView serviceTextTab;
	private TextView insuranceTextView;
	// this is book service text view
	private TextView serviceTextView;
	private TextView financeTextView;
	private TextView economyTextView;
	private TextView outstandingChecksTextView;

	private ImageView photoEdit;

	private DCVehicle vehicle;

	private boolean vehicleAdded = false; // Indicates if user came from
											// AddVehicleConfirmActivity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_details);

		vehicleLayout = (RelativeLayout) findViewById(R.id.mainVehicleLayout);
		motLayout = (LinearLayout) findViewById(R.id.motLayout);
		taxLayout = (LinearLayout) findViewById(R.id.taxLayout);
		serviceTabLayout = (LinearLayout) findViewById(R.id.serviceTabLayout);
		insuranceLayout = (LinearLayout) findViewById(R.id.insuranceLayout);
		serviceLayout = (LinearLayout) findViewById(R.id.serviceLayout);
		mileageLayout = (LinearLayout) findViewById(R.id.mileageLayout);
		economyLayout = (LinearLayout) findViewById(R.id.economyLayout);
		financeLayout = (LinearLayout) findViewById(R.id.financeLayout);
		financeHistoryLayout = (LinearLayout) findViewById(R.id.finance_History_layout);
		if (AppConfig.isfinanceDisable()) {
			financeHistoryLayout.setVisibility(View.GONE);

		}

		vehicleChecksLayout = (LinearLayout) findViewById(R.id.vehicleChecksLayout);

		modelTextView = (TextView) findViewById(R.id.modelTextView);
		derivativeTextView = (TextView) findViewById(R.id.derivativeTextView);
		mileageTextView = (TextView) findViewById(R.id.mileageTextView);
		emissionTextView = (TextView) findViewById(R.id.emissionTextView);
		fuelTextView = (TextView) findViewById(R.id.fuelTextView);
		// alertsTextView = (TextView) findViewById(R.id.alertsTextView);
		motTextView = (TextView) findViewById(R.id.motTextView);
		taxTextView = (TextView) findViewById(R.id.taxTextView);
		serviceTextTab = (TextView) findViewById(R.id.serviceTextTab);

		insuranceTextView = (TextView) findViewById(R.id.insuranceTextView);
		serviceTextView = (TextView) findViewById(R.id.serviceTextView);
		economyTextView = (TextView) findViewById(R.id.economyTextView);
		financeTextView = (TextView) findViewById(R.id.financeTextView);
		// financeLayout.setVisibility(View.GONE);
		outstandingChecksTextView = (TextView) findViewById(R.id.outstandingChecksText);

		photoEdit = (ImageView) findViewById(R.id.pic);

		vehicleLayout.setOnClickListener(onClickListener);
		motLayout.setOnClickListener(onClickListener);
		// serviceTabLayout.setOnClickListener(onClickListener);
		taxLayout.setOnClickListener(onClickListener);
		insuranceLayout.setOnClickListener(onClickListener);
		serviceLayout.setOnClickListener(onClickListener);
		mileageLayout.setOnClickListener(onClickListener);
		economyLayout.setOnClickListener(onClickListener);
		financeLayout.setOnClickListener(onClickListener);
		vehicleChecksLayout.setOnClickListener(onClickListener);

		loading = (RelativeLayout) findViewById(R.id.loadSpinner);

		// Get extras from the previous activity
		Bundle extras = getIntent().getExtras();

		// If there are any extras
		if (extras != null) {
			// Get vehicle
			vehicle = (DCVehicle) extras.getSerializable("vehicle");
			vehicleAdded = extras.getBoolean("vehicleAdded");
			String id = extras.getString("vehicleId");
			vehicle.setId(id);
			if (vehicle != null) {
				modelTextView.setText("" + vehicle.getMake() + " "
						+ vehicle.getModel());
				derivativeTextView.setText(vehicle.getDerivative());
				emissionTextView.setText("" + vehicle.getCo2() + " g/km");
				fuelTextView.setText("" + vehicle.getFuel());

				getActionBar().setTitle("" + vehicle.getRegistration());// vehicle.getMake()
																		// + " "
																		// +
																		// vehicle.getModel());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// String id = vehicle.getId();
		// System.out.println(id);
		if (vehicle.getId() == null) {
			setSelectedId();
		}
		// System.out.println(vehicle.getId());
		outstandingChecksTextView.setText(""
				+ DCVehicleSingleton.getOutstandingChecks(vehicle.getId())
				+ " outstanding checks");

		if (vehicle != null) {
			int alerts = 0;

			financeTextView.setText(String.format("£%.2f",
					vehicle.getMonthlyFinance()));
			economyTextView.setText((int) vehicle.getKnownMPG() + " MPG");

			// Check if mileage is set
			if (vehicle.getCurrentMileage() > 0) {
				mileageTextView.setText("" + vehicle.getCurrentMileage()
						+ " Miles");
				mileageTextView.setTextColor(getResources().getColor(
						R.color.common_signin_btn_text_light));
			} else {
				mileageTextView.setText(R.string.vehicle_no_mileage);
				mileageTextView.setTextColor(getResources().getColor(
						android.R.color.holo_red_light));
				alerts++;
			}

			// Check if MOT is set
			if (vehicle.getDateMOT() != null) {
				motTextView.setText(vehicle.getDateMOT());
				motTextView.setTextColor(getResources().getColor(
						R.color.common_signin_btn_text_light));
			} else
				alerts++;

			// Check if road tax is set
			if (vehicle.getRoadtax() != null) {
				taxTextView.setText(vehicle.getRoadtax());
				taxTextView.setTextColor(getResources().getColor(
						R.color.common_signin_btn_text_light));
			} else
				alerts++;

			// check if service reminder set
			// Check if road tax is set
			if (vehicle.getServiceDate() != null) {
				serviceTextTab.setText(vehicle.getService());
				serviceTextTab.setTextColor(getResources().getColor(
						R.color.common_signin_btn_text_light));
			} else
				alerts++;
			// Check if vehicle has insurance
			if (vehicle.getInsurance() != null) {
				insuranceTextView.setText(vehicle.getInsurance());
				insuranceTextView.setTextColor(getResources().getColor(
						R.color.common_signin_btn_text_light));
			} else
				alerts++;

			vehicle.setAlertsCount(alerts);
			getActionBar().setSubtitle("" + alerts + " Alerts");
			// alertsTextView.setText("" + alerts + " Alerts");
			serviceTextView
					.setText("" + vehicle.getServiceHistory() + " items");

			if (vehicle.getPhotoSrc() != null) {
				Bitmap bmp = AssetsUtilities.readBitmapVehicle(vehicle
						.getPhotoSrc());
				photoEdit.setImageBitmap(bmp);
			}
		}

	}

	/**
	 * Handles what happens when specific view of the item on the list is
	 * clicked/touched
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			Bundle extras = new Bundle();
			extras.putSerializable("vehicle", vehicle);

			// Check which layout was touched
			if (v == vehicleLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						AddVehiclePhotoActivity.class);
				extras.putBoolean("is_edit", true);
			} else if (v == economyLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						EconomyActivity.class);
			} else if (v == mileageLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						SingleUpdateActivity.class);
				extras.putInt("update", SingleUpdateActivity.UPDATE_MILEAGE);
			} else if (v == financeLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						SingleUpdateActivity.class);
				extras.putInt("update", SingleUpdateActivity.UPDATE_FINANCE);
			} else if (v == insuranceLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						DCCoverListActivity.class);
			} else if (v == serviceLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						ServiceHistoryListActivity.class);
				extras.putSerializable("vehicle", vehicle);
			} else if (v == vehicleChecksLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						VehicleChecksListActivity.class);
				extras.putString("vehicleID", vehicle.getId());
			} else if (v == motLayout || v == taxLayout
					|| v == serviceTabLayout) {
				intent = new Intent(VehicleDetailsActivity.this,
						UpdateDocumentationActivity.class);

				if (v == taxLayout)
					extras.putInt("index", 0);
				else if (v == motLayout)
					extras.putInt("index", 1);
				else if (v == serviceTabLayout)
					extras.putInt("index", 2);
			}

			if (intent != null) {
				intent.putExtras(extras);
				startActivityForResult(intent, 100);
				overridePendingTransition(R.anim.slide_left_sub,
						R.anim.slide_left_main);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				vehicle = (DCVehicle) extras.getSerializable("vehicle");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.vehicle_details, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (!vehicleAdded) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				intent.putExtra("vehicle", vehicle);
				finish();

				overridePendingTransition(R.anim.null_anim, R.anim.slide_out);
			} else {
				Intent intent = new Intent(VehicleDetailsActivity.this,
						VehiclesListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_TASK_ON_HOME
						| Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_main,
						R.anim.slide_right_sub);
			}

			return true;
		} else if (item.getItemId() == R.id.action_delete) {
			new AlertDialog.Builder(VehicleDetailsActivity.this)
					.setTitle("Delete Vehicle")
					.setMessage("Are you sure to delete this vehicle?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									deleteVehicle();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}

		DCVehilceDataSingleton
				.getDCModuleSingleton(VehicleDetailsActivity.this)
				.getVehilesList();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Deletes vehicle from the Parse database
	 */
	private void deleteVehicle() {
		loading.setVisibility(View.VISIBLE);

		if (!isConnected(VehicleDetailsActivity.this)) {
			new AlertDialog.Builder(VehicleDetailsActivity.this)

					.setTitle("Error")
					.setMessage(
							getResources().getString(
									R.string.connection_not_found))
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();

			return;

		}

		String s = vehicle.getId();
		// System.out.println(s);
		if (s != null) {

			System.out.println(vehicle.getId());
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BaasDocument.deleteSync("BAAVehicle", vehicle.getId());

						// Your code goes here
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			thread.start();
		}
		if (!vehicleAdded) {
			// DC Service History item is deleted, go back
			// to previous screen
			loading.setVisibility(View.INVISIBLE);
			finish();
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
		}
		// Otherwise he came from an adding vehicle screen
		else {
			Intent intent = new Intent(VehicleDetailsActivity.this,
					VehiclesListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_TASK_ON_HOME
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);
			overridePendingTransition(R.anim.slide_right_main,
					R.anim.slide_right_sub);
		}

		// ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
		// query.getInBackground(vehicle.getId(), new GetCallback<ParseObject>()
		// {
		// public void done(ParseObject object, ParseException e) {
		// if (e == null) {
		// object.deleteInBackground(new DeleteCallback() {
		// @Override
		// public void done(ParseException e) {
		// // Check if user came from Vehicle list activity
		// }
		// });
		// }
		// }
		// });
	}

	private void setSelectedId() {
		final BaasQuery PREPARED_QUERY = BaasQuery.builder()
				.collection("BAAVehicle").build();
		// then
		PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {

			@Override
			public void handle(BaasResult<List<JsonObject>> res) {
				// TODO Auto-generated method stub
				for (int i = 0; i < res.value().size(); i++) {
					if (res.value().get(i).getString("vehicleRegistration")
							.equals(vehicle.getRegistration())) {

						vehicle.setId(res.value().get(i).getString("id"));
					}
				}
			}
		});

	}

	private static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}

}
