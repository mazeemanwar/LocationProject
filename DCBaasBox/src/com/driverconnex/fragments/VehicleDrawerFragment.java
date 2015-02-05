package com.driverconnex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.DataStreamHandler;
import com.driverconnex.app.AppConfig;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.basicmodules.ServiceViewActivity;
import com.driverconnex.singletons.DCVehilceDataSingleton;
import com.driverconnex.ui.RoundedImageView;
import com.driverconnex.utilities.AssetsUtilities;
import com.driverconnex.vehicles.DCVehicle;
import com.driverconnex.vehicles.VehicleDetailsActivity;
import com.driverconnex.vehicles.VehiclesListActivity;

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

	private LinearLayout retailersLayout;
	private LinearLayout driverLineLayout;
	private LinearLayout bookDemoLayout;

	private ScrollView mDrawerListView;
	private View mFragmentContainerView;

	private TextView vehicleText;
	private TextView regText;
	private TextView alertsTex;

	private TextView retailersText;
	private TextView driverLineText;
	private TextView bookDemoTex;

	private RoundedImageView photoEdit;
	BaasFile imagefile = null;
	private DCVehicle vehicle;
	private ArrayList<DCVehicle> vehiclesList = new ArrayList<DCVehicle>();

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
		// Layout view
		vehicleLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.vehicleLayout);
		alertsLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.alertsLayout);
		serviceLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.serviceLayout);
		retailersLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.retailersLayout);
		driverLineLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.driverLineLayout);
		bookDemoLayout = (LinearLayout) mDrawerListView
				.findViewById(R.id.bookDemoLayout);
		// TextBox view
		vehicleText = (TextView) mDrawerListView
				.findViewById(R.id.modelTextView);
		regText = (TextView) mDrawerListView
				.findViewById(R.id.regNumberTextView);
		alertsTex = (TextView) mDrawerListView
				.findViewById(R.id.alertsTextView);
		photoEdit = (RoundedImageView) mDrawerListView
				.findViewById(R.id.photoEdit);
		//
		retailersText = (TextView) mDrawerListView
				.findViewById(R.id.retailersTextView);
		driverLineText = (TextView) mDrawerListView
				.findViewById(R.id.driverLineTextView);
		bookDemoTex = (TextView) mDrawerListView
				.findViewById(R.id.bookDemoTextView);
		// layout Listener
		vehicleLayout.setOnClickListener(onClickListener);
		alertsLayout.setOnClickListener(onClickListener);
		serviceLayout.setOnClickListener(onClickListener);
		retailersLayout.setOnClickListener(onClickListener);
		driverLineLayout.setOnClickListener(onClickListener);
		bookDemoLayout.setOnClickListener(onClickListener);

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
		vehiclesList = DCVehilceDataSingleton.getDCModuleSingleton(
				getActivity()).getVehilesList();

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
				if (extras != null)
					intent.putExtras(extras);

				if (intent != null) {
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_in,
							R.anim.null_anim);
				}
			} else if (v == alertsLayout) {
				extras = new Bundle();
				intent = new Intent(getActivity(), VehicleDetailsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				extras.putSerializable("vehicle", vehicle);
				extras.putString("vehicleId", vehicle.getId());
			} else if (v == serviceLayout) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.skoda.co.uk/"));
				startActivity(browserIntent);
			} else if (v == retailersLayout) {

				intent = new Intent(getActivity(), ServiceViewActivity.class);
				// startActivity(intent);

			} else if (v == driverLineLayout) {
				// reading from configuration file
				Intent callIntent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel:" + AppConfig.getDRIVERLINE()));
				startActivity(callIntent);

				// intent = new Intent(getActivity(),
				// ServiceListActivity.class);
			} else if (v == bookDemoLayout) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(AppConfig.getBOOK_DEMO()));
				startActivity(browserIntent);

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
		// BaasFile.stream("75ebf7b1-54bb-4d98-927c-ad1f24563343",
		// new DataStreamHandler<R>() {
		//
		// @Override
		// public void startData(String arg0, long contentLength,
		// String contentType) throws Exception {
		// // TODO Auto-generated method stub
		//
		// System.out.println(contentLength);
		//
		// }
		//
		// @Override
		// public void onData(byte[] arg0, int arg1) throws Exception {
		// // TODO Auto-generated method stub
		//
		// System.out.println();
		// }
		//
		// @Override
		// public void finishStream(String arg0) {
		// // TODO Auto-generated method stub
		// System.out.println();
		// }
		//
		// @Override
		// public R endData(String arg0, long arg1, String arg2)
		// throws Exception {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// }, new BaasHandler<R>() {
		//
		// @Override
		// public void handle(BaasResult<R> arg0) {
		// // TODO Auto-generated method stub
		// System.out.println();
		// }
		//
		//
		//
		//
		// });
		// BaasFile.fetchStream("75ebf7b1-54bb-4d98-927c-ad1f24563343",
		// new BaasHandler<BaasFile>() {
		//
		// @Override
		// public void handle(BaasResult<BaasFile> arg0) {
		// // TODO Auto-generated method stub
		// try {
		// String n = arg0.get().getAuthor();
		// } catch (BaasException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String m = arg0.value().getAuthor();
		// System.out.println(m);
		// byte[] data = arg0.value().getData();
		// try {
		// byte[] datas = arg0.get().getData();
		// } catch (BaasException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(data);
		// }
		//
		// });

		// new AsyncListViewLoader().execute("ss");

		// JsonObject attachedData = new JsonObject().putString("id",
		// "a4fdf598-c2d1-45f3-9d9d-66a28e27cc19");
		// BaasFile file = new BaasFile(attachedData);
		//
		// file.stream("a4fdf598-c2d1-45f3-9d9d-66a28e27cc19", new
		// StreamHandler()new BaasHandler<BaasFile>() {
		// @Override
		// public void handle(BaasResult<BaasFile> res) {
		// if (res.isSuccess()) {
		// byte[] data = res.value().getData();
		// Log.d("LOG", "File received" + data);
		// } else {
		// Log.e("LOG", "Error while streaming", res.error());
		// }
		// }
		// });

		vehiclesList = DCVehilceDataSingleton.getDCModuleSingleton(
				getActivity()).getVehilesList();

		if (vehiclesList != null && vehiclesList.size() > 0) {

			for (int i = 0; i < vehiclesList.size(); i++) {
				String defaultVehilceId = DriverConnexApp.getUserPref()
						.getDefaultVehicleReg();

				if (defaultVehilceId != null && !defaultVehilceId.equals("")) {
					if (defaultVehilceId.equals(vehiclesList.get(i)
							.getRegistration())) {
						vehicle = vehiclesList.get(i);
						int alert = vehiclesList.get(i).getAlertsCount();
						System.out.println();
					} else {
						vehicle = vehiclesList.get(0);
					}
				} else {

					vehicle = vehiclesList.get(0);
				}
			}

			alertsLayout.setVisibility(View.VISIBLE);
			serviceLayout.setVisibility(View.VISIBLE);

			// Set texts
			vehicleText.setText(vehicle.getMake() + " " + vehicle.getModel());
			regText.setText(vehicle.getRegistration());
			alertsTex
					.setText("" + vehicle.getAlertsCount() + " Vehicle Alerts");

			// final String regId = DriverConnexApp.getUserPref()
			// .getDefaultVehicleReg();
			//
			// final BaasQuery PREPARED_QUERY = BaasQuery.builder()
			// .collection("BAAVehicle").build();
			// // then
			// PREPARED_QUERY.query(new BaasHandler<List<JsonObject>>() {
			//
			// @Override
			// public void handle(BaasResult<List<JsonObject>> res) {
			// // TODO Auto-generated method stub
			// if (res != null && res.value().size() > 0) {
			//
			// for (int i = 0; i < res.value().size(); i++) {
			// if (res.value().get(i).getString("vehicleRegistration")
			// .equals(regId)) {
			// System.out.println();
			// vehicle = ParseUtilities.convertVehicleObject(res
			// .value().get(0));
			// } else {
			// vehicle = ParseUtilities.convertVehicleObject(res
			// .value().get(0));
			//
			// }
			// String imageId = res.value().get(0)
			// .getString("vehicleImageID");
			// // readImage(imageId);
			// alertsLayout.setVisibility(View.VISIBLE);
			// serviceLayout.setVisibility(View.VISIBLE);
			//
			// // Set texts
			// vehicleText.setText(res.value().get(i)
			// .getString("vehicleMake")
			// + " "
			// + res.value().get(i).getString("vehicleModel"));
			// // regText.setText(regId);
			// // alertsTex.setText("" + vehicle.getAlertsCount()
			// // + " Vehicle Alerts");
			//
			// // Get photo from the parse
			// // ParseFile photo = (ParseFile) object
			// // .get("vehiclePhoto");
			byte[] data = null;
			//
			// if (res.value().get(i).getBinary("vehiclePhoto") ==
			// null) {
			// System.out.println();
			// return;
			//
			// }
			// data = res.value().get(i).getBinary("vehiclePhoto");
			// System.out.println();
			//
			if (data == null) {
				vehicle.setPhotoSrc(null);
				vehicle.setPhoto(false);
			} else {
				vehicle.setPhotoSrc(data);
				vehicle.setPhoto(true);
				Bitmap bmp = AssetsUtilities.readBitmapVehicle(vehicle
						.getPhotoSrc());
				photoEdit.setImageBitmap(bmp);
				// }
				// return;
				// }
			}
		} else {
			vehicleText.setText(" ");
			regText.setText("No default vehicle");
			alertsLayout.setVisibility(View.INVISIBLE);
			serviceLayout.setVisibility(View.INVISIBLE);
			retailersLayout.setVisibility(View.GONE);
			driverLineLayout.setVisibility(View.GONE);
			bookDemoLayout.setVisibility(View.GONE);

		}
		// }

		// });

		// // ParseUser user = ParseUser.getCurrentUser();
		// //
		// // if (user != null) {
		// //
		// // ParseObject vehicleParse = null;
		// // vehicleParse = user.getParseObject("userDefaultVehicle");
		// //
		// // vehicleParse.fetchInBackground(new GetCallback<ParseObject>() {
		// // @Override
		// // public void done(ParseObject object, ParseException e) {
		// // if (e == null) {
		// // vehicle = ParseUtilities.convertVehicle(object);
		// //
		// // alertsLayout.setVisibility(View.VISIBLE);
		// // serviceLayout.setVisibility(View.VISIBLE);
		// //
		// // // Set texts
		// // vehicleText.setText(vehicle.getMake() + " "
		// // + vehicle.getModel());
		// // regText.setText(vehicle.getRegistration());
		// // alertsTex.setText("" + vehicle.getAlertsCount()
		// // + " Vehicle Alerts");
		// //
		// // // Get photo from the parse
		// // ParseFile photo = (ParseFile) object
		// // .get("vehiclePhoto");
		// // byte[] data = null;
		// //
		// // try {
		// // if (photo != null)
		// // data = photo.getData();
		// // } catch (ParseException e1) {
		// // e1.printStackTrace();
		// // }
		// //
		// // if (data == null) {
		// // vehicle.setPhotoSrc(null);
		// // vehicle.setPhoto(false);
		// // } else {
		// // vehicle.setPhotoSrc(data);
		// // vehicle.setPhoto(true);
		// // Bitmap bmp = AssetsUtilities
		// // .readBitmapVehicle(vehicle.getPhotoSrc());
		// // photoEdit.setImageBitmap(bmp);
		// // }
		// // } else {
		// // vehicleText.setText(" ");
		// // regText.setText("No default vehicle");
		// // alertsLayout.setVisibility(View.INVISIBLE);
		// // serviceLayout.setVisibility(View.INVISIBLE);
		// // }
		// // }
		// // });
		// }

		getActivity().invalidateOptionsMenu();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface VehicleDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onVehicleDrawerItemSelected(int position);
	}//

	private void readImage(String fileId) {
		// ParseFile photo = (ParseFile) object
		// .get("vehiclePhoto");

		BaasFile.fetch("75ebf7b1-54bb-4d98-927c-ad1f24563343",
				new BaasHandler<BaasFile>() {
					@Override
					public void handle(BaasResult<BaasFile> res) {
						if (res.isSuccess()) {
							Log.d("LOG", "Your file details" + res);
							byte[] data = null;

							try {

								final BaasFile files = res.get();

								Thread thread = new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											files.stream(new BaasHandler<BaasFile>() {

												@Override
												public void handle(
														BaasResult<BaasFile> arg0) {
													System.out.println(arg0);
													try {
														byte[] data = arg0
																.get()
																.getData();
														System.out
																.println(data);
													} catch (BaasException e) {
														// TODO Auto-generated
														// catch
														// block
														e.printStackTrace();
													}

												}
											});

											// Your code goes here
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});

								thread.start();

								if (data == null) {
									vehicle.setPhotoSrc(null);
									vehicle.setPhoto(false);
								} else {
									vehicle.setPhotoSrc(data);
									vehicle.setPhoto(true);
									Bitmap bmp = AssetsUtilities
											.readBitmapVehicle(vehicle
													.getPhotoSrc());
									photoEdit.setImageBitmap(bmp);
								}

							} catch (BaasException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							Log.e("LOG", "Deal with eror", res.error());
						}
					}
				});

	}

	private class AsyncListViewLoader extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// readImage("");
			BaasFile.fetch("a4fdf598-c2d1-45f3-9d9d-66a28e27cc19",
					new BaasHandler<BaasFile>() {
						@Override
						public void handle(BaasResult<BaasFile> res) {
							if (res.isSuccess()) {
								try {
									// byte[] data = res.value().
									// System.out.println(data);

									BaasResult<BaasFile> dlds = BaasFile
											.fetchStream(
													"a4fdf598-c2d1-45f3-9d9d-66a28e27cc19",
													BaasHandler.NOOP).await();
									BaasFile download = dlds.get();
									// assertEquals(id,download.getId());
									// assertNotNull(download.getData());
									byte[] data = download.getData();
									System.out.println(data);
									// String s = res.value().getAuthor();
									// String auth =
									// BaasUser.current().getName();
									// BaasFile imagefiled = res.value();
									//
									// BaasFile imagefile = res.get().asFile();
									// String names = imagefile.getAuthor();
									//
									// System.out.println(imagefile);
									//
									// imagefile
									// .stream(new BaasHandler<BaasFile>() {
									// @Override
									// public void handle(
									// BaasResult<BaasFile> res) {
									// if (res.isSuccess()) {
									// byte[] data = res
									// .value()
									// .getData();
									// Log.d("LOG",
									// "File received");
									// } else {
									// Log.e("LOG",
									// "Error while streaming",
									// res.error());
									// }
									// }
									// });
								} catch (BaasException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								Log.d("LOG", "Your file details" + res);
							} else {
								Log.e("LOG", "Deal with eror", res.error());
							}
						}
					});

			return null;
		}

	}
}
