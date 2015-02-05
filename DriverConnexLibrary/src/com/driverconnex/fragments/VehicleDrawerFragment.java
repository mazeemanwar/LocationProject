package com.driverconnex.fragments;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.ui.RoundedImageView;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.utilities.ParseUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.ServiceListActivity;
import com.driverconnex.vehicles.VehicleDetailsActivity;
import com.driverconnex.vehicles.VehiclesListActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Vehicle drawer fragment. It displays a default vehicle menu. It's the menu
 * displayed from the right side of the screen.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */
public class VehicleDrawerFragment extends Fragment {
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private VehicleDrawerCallbacks mCallbacks;

	private int mCurrentSelectedPosition = -1;

	private DrawerLayout mDrawerLayout;

	private LinearLayout vehicleLayout;
	private LinearLayout alertsLayout;
	private LinearLayout serviceLayout;

	private ScrollView mDrawerListView;
	private View mFragmentContainerView;

	private TextView vehicleText;
	private TextView regText;
	private TextView alertsTex;

	private RoundedImageView photoEdit;

	private DCVehicle vehicle;

	public VehicleDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ScrollView) inflater.inflate(
				R.layout.fragment_vehicle_drawer, container, false);

		// Get views from the layout
		vehicleLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.vehicleLayout);
		alertsLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.alertsLayout);
		serviceLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.serviceLayout);

		vehicleText = (TextView) mDrawerListView
				.findViewById(R.id.modelTextView);
		regText = (TextView) mDrawerListView
				.findViewById(R.id.regNumberTextView);
		alertsTex = (TextView) mDrawerListView
				.findViewById(R.id.alertsTextView);

		photoEdit = (RoundedImageView) mDrawerListView
				.findViewById(R.id.photoEdit);

		vehicleLayout.setOnClickListener(onClickListener);
		alertsLayout.setOnClickListener(onClickListener);
		serviceLayout.setOnClickListener(onClickListener);

		return mDrawerListView;
	}

	/**
	 * Checks if vehicle drawer is currently opened.
	 * 
	 * @return
	 */
	public boolean isDrawerOpened() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Disable swipe gesture to open the drawer
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (VehicleDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Handles what happens when specific view is clicked
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			Bundle extras = null;

			// Check which layout was touched
			if (v == vehicleLayout) {
				intent = new Intent(getActivity(), VehiclesListActivity.class);
			} else if (v == alertsLayout) {
				extras = new Bundle();
				intent = new Intent(getActivity(), VehicleDetailsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				extras.putSerializable("vehicle", vehicle);
				extras.putString("vehicleId", vehicle.getId());
			} else if (v == serviceLayout) {
				intent = new Intent(getActivity(), ServiceListActivity.class);
			}

			if (extras != null)
				intent.putExtras(extras);

			if (intent != null) {
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in,
						R.anim.null_anim);
			}
		}
	};

	public void openDrawer() {
		refresh();
		mDrawerLayout.openDrawer(mFragmentContainerView);
		getActivity().invalidateOptionsMenu();
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(mFragmentContainerView);
		getActivity().invalidateOptionsMenu();
	}

	/**
	 * Refreshes vehicle drawer fragment.
	 */
	public void refresh() {
		ParseUser user = ParseUser.getCurrentUser();

		if (user != null) {
			ParseObject vehicleParse = null;
			vehicleParse = user.getParseObject("userDefaultVehicle");
			// vehicleParse.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

			vehicleParse.fetchInBackground(new GetCallback<ParseObject>() {
				@Override
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						// ParseObject userDefaultVehileObjt = new ParseObject(
						// "userDefaultVehileObjt");
						// userDefaultVehileObjt = object;
						// object.pinInBackground();

						vehicle = ParseUtilities.convertVehicle(object);

						alertsLayout.setVisibility(View.VISIBLE);
						serviceLayout.setVisibility(View.VISIBLE);

						// Set texts
						vehicleText.setText(vehicle.getMake() + " "
								+ vehicle.getModel());
						regText.setText(vehicle.getRegistration());
						alertsTex.setText("" + vehicle.getAlertsCount()
								+ " Vehicle Alerts");

						// Get photo from the parse
						ParseFile photo = (ParseFile) object
								.get("vehiclePhoto");
						byte[] data = null;

						try {
							if (photo != null)
								data = photo.getData();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}

						if (data == null) {
							vehicle.setPhotoSrc(null);
							vehicle.setPhoto(false);
						} else {
							vehicle.setPhotoSrc(data);
							vehicle.setPhoto(true);
							Bitmap bmp = AssetsUtilities
									.readBitmapVehicle(vehicle.getPhotoSrc());
							photoEdit.setImageBitmap(bmp);
						}
					} else {
						// refreshOffline();
						vehicleText.setText(" ");
						regText.setText("No default vehicle");
						alertsLayout.setVisibility(View.INVISIBLE);
						serviceLayout.setVisibility(View.INVISIBLE);
					}
				}
			});
		}

		getActivity().invalidateOptionsMenu();
	}

	// get offline
	// public void refreshOffline() {
	// ParseQuery<ParseObject> query = ParseQuery
	// .getQuery("userDefaultVehileObjt");
	// query.fromLocalDatastore();
	// query.findInBackground(new FindCallback<ParseObject>() {
	// public void done(List<ParseObject> msglist, ParseException e) {
	// if (e == null) {
	// for (int i = 0; i < msglist.size(); i++) {
	// ParseObject object = msglist.get(i);
	// vehicle = ParseUtilities.convertVehicle(object);
	//
	// alertsLayout.setVisibility(View.VISIBLE);
	// serviceLayout.setVisibility(View.VISIBLE);
	//
	// // Set texts
	// vehicleText.setText(vehicle.getMake() + " "
	// + vehicle.getModel());
	// regText.setText(vehicle.getRegistration());
	// alertsTex.setText("" + vehicle.getAlertsCount()
	// + " Vehicle Alerts");
	//
	// // Get photo from the parse
	// ParseFile photo = (ParseFile) object
	// .get("vehiclePhoto");
	// byte[] data = null;
	//
	// try {
	// if (photo != null)
	// data = photo.getData();
	// } catch (ParseException e1) {
	// e1.printStackTrace();
	// }
	//
	// if (data == null) {
	// vehicle.setPhotoSrc(null);
	// vehicle.setPhoto(false);
	// } else {
	// vehicle.setPhotoSrc(data);
	// vehicle.setPhoto(true);
	// Bitmap bmp = AssetsUtilities
	// .readBitmapVehicle(vehicle.getPhotoSrc());
	// photoEdit.setImageBitmap(bmp);
	// }
	//
	// }
	// // object will be your game score
	// } else {
	// vehicleText.setText(" ");
	// regText.setText("No default vehicle");
	// alertsLayout.setVisibility(View.INVISIBLE);
	// serviceLayout.setVisibility(View.INVISIBLE);
	//
	// // something went wrong
	// }
	// }
	// });
	//
	// }

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface VehicleDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onVehicleDrawerItemSelected(int position);
	}
}
