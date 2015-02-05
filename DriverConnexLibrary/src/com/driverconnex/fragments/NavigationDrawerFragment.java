package com.driverconnex.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.driverconnex.adapter.NavigationDrawerAdapter;
import com.driverconnex.app.AppConfig;
import com.driverconnex.app.DriverConnexApp;
import com.driverconnex.app.R;
import com.driverconnex.data.MenuListItem;
import com.driverconnex.data.MenuListItems;
import com.driverconnex.data.XMLBasicConfigParser;
import com.driverconnex.data.XMLMenuConfigParser;
import com.driverconnex.data.XMLModuleConfigParser;
import com.driverconnex.incidents.IncidentActivity;
import com.driverconnex.singletons.DCModuleSingleton;
import com.driverconnex.singletons.TrackJourneySingleton;
import com.driverconnex.utilities.ModulesUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 *         NOTE: This is a navigation drawer which acts as the main menu on the
 *         left side of the screen. Comment by Adrian Klimczak
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = -1;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	private ArrayList<MenuListItem> menuOptions;
	private static ArrayList<String> moduleFromServer = new ArrayList<String>();
	ArrayList<MenuListItems> basicMenuList = new ArrayList<MenuListItems>();
	ArrayList<MenuListItems> moduleMenuList = new ArrayList<MenuListItems>();

	public NavigationDrawerFragment() {

		System.out.println("CONSTRUCTOR");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		System.out.println(savedInstanceState);
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}

				});

		// getMenuFromServer() is used in logical(business) app for menu fetch
		// from server

		// ArrayList<MenuListItems> moduleMenuList = null;
		menuOptions = new ArrayList<MenuListItem>();

		try {
			// Module Menu List
			// ---------------------------------------
			if (AppConfig.getIsOnlineModuleRequired() != null
					&& AppConfig.getIsOnlineModuleRequired().equals("yes")) {
				DCModuleSingleton.getDCModuleSingleton(getActivity())
						.getServerModule();
				disPlayMenu();
			} else {
				moduleMenuList = XMLModuleConfigParser
						.getMenuItemsFromXML(getActivity());
				disPlayMenu();
			}

		} catch (Exception e) {
			getActivity().finish();
		}

		// mDrawerListView.setAdapter(new NavigationDrawerAdapter(getActivity(),
		// menuOptions));

		return mDrawerListView;
	}

	public boolean isDrawerOpen() {
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

		// set up the drawer's list view with items and click listener
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();

				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * Handles what happens when the item from the menu is selected
	 * 
	 * @param position
	 */
	private void selectItem(int position) {
		MenuListItem item = new MenuListItem();
		item = menuOptions.get(position);
		if (!item.isHeader()) {
			if (item.getUrl() != null) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(item.getUrl()));
				startActivity(browserIntent);
			}
			// Check if the item touched is Log Out
			else if (item.getName().equals("Log Out")) {
				DriverConnexApp.getUserPref().setLogin(false);
				ParseUser.logOut();

				// Reset tracker
				TrackJourneySingleton.stopTracking();
				TrackJourneySingleton.reset();
				getActivity().finish();
				getActivity().overridePendingTransition(
						R.anim.slide_right_main, R.anim.slide_right_sub);
			} else if (item.getEmail() != null) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { item.getEmail() });
				startActivity(Intent.createChooser(intent, "Report via Email"));
			}
			// Check if any other item from the menu is touched
			else {
				ModulesUtilities moduleUtil = new ModulesUtilities(
						getActivity());

				if (moduleUtil.getModuleClass(item.getClassName()) != null) {
					// Get the item's activity
					Intent intent = new Intent(getActivity(),
							moduleUtil.getModuleClass(item.getName()));

					// Check which item is selected from the menu
					if (item.getName().equals("Tracker")) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					}
					// Accidents and Breakdown share the same activity
					// So we need to know which one was touched
					else if (item.getName().equals("Accidents")) {
						intent.putExtra("incidentActivity",
								IncidentActivity.INCIDENT_ACCIDENT);
					} else if (item.getName().equals("Breakdown")) {
						intent.putExtra("incidentActivity",
								IncidentActivity.INCIDENT_BREAKDOWN);
					}

					// Start the activity
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_in,
							R.anim.null_anim);
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			System.out.println("ONAATACH");

			mCallbacks = (NavigationDrawerCallbacks) activity;
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
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	public void openDrawer() {
		mDrawerLayout.openDrawer(mFragmentContainerView);
		getActivity().invalidateOptionsMenu();
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(mFragmentContainerView);
		getActivity().invalidateOptionsMenu();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}

	private void disPlayMenu() {
		menuOptions = new ArrayList<MenuListItem>();
		moduleMenuList = DCModuleSingleton.getDCModuleSingleton(getActivity())
				.getServerModule();

		try {

			// Module Menu List
			// ---------------------------------------
			// moduleMenuList = XMLModuleConfigParser
			// .getMenuItemsFromXML(getActivity());
			//
			// // Convert both header and items into MenuItem for menu list
			for (int i = 0; i < moduleMenuList.size(); i++) {

				MenuListItem item = new MenuListItem();
				item.setHeader(true);
				item.setName(moduleMenuList.get(i).getName());
				item.setEnabled(moduleMenuList.get(i).isEnabled());
				menuOptions.add(item);

				for (int j = 0; j < moduleMenuList.get(i).getSubitems().size(); j++)
					menuOptions.add(moduleMenuList.get(i).getSubitems().get(j));
			}
			// ---------------------------------------

			// Basic Menu List
			// ---------------------------------------
			basicMenuList = XMLBasicConfigParser
					.getBasicMenuItemsFromXML(getActivity());

			System.out
					.println("size fo menu list is = " + basicMenuList.size());
			// Convert both header and items into MenuItem for menu list
			for (int i = 0; i < basicMenuList.size(); i++) {
				MenuListItem item = new MenuListItem();
				item.setHeader(true);
				item.setName(basicMenuList.get(i).getName());
				item.setEnabled(basicMenuList.get(i).isEnabled());
				menuOptions.add(item);
				// In menu list we have to add blank heading for better
				// readability.So name is empty NO need to add sub item
				if (!basicMenuList.get(i).getName().equals("")) {

					if (AppConfig.getISHOMEPAGEENABLE()) {
						System.out.println("menuoption size = "
								+ basicMenuList.get(i).getSubitems().get(0)
										.getClassName());
						basicMenuList.get(i).getSubitems().remove(0);

						basicMenuList.get(i).getSubitems()
								.add(0, getCustomeHomePage());

					}
					for (int j = 0; j < basicMenuList.get(i).getSubitems()
							.size(); j++) {

						Log.d("BASICMENU", basicMenuList.get(i).getSubitems()
								.get(j).getClassName()
								+ "index of subitems is  " + j);

						menuOptions.add(basicMenuList.get(i).getSubitems()
								.get(j));

					}

				}
			}
			// ---------------------------------------

		} catch (Exception e) {
			getActivity().finish();
		}
		mDrawerListView.setAdapter(new NavigationDrawerAdapter(getActivity(),
				menuOptions));

	}

	//
	private MenuListItem getCustomeHomePage() {
		MenuListItem item = new MenuListItem();
		item.setName(AppConfig.getHOMEPAGENAME());
		item.setUrl(AppConfig.getHOMEPAGEURL());
		item.setIcon(AppConfig.getHOMEPAGEICON());
		item.setClassName(AppConfig.getHOMEPAGENAME());
		return item;
	}
}
