package com.driverconnex.app;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driverconnex.data.HelpListItems;
import com.driverconnex.data.Tab;
import com.driverconnex.data.XMLModuleConfigParser;
import com.driverconnex.fragments.DriverConnexFragment;
import com.driverconnex.fragments.KPMGFragment;
import com.driverconnex.fragments.NavigationDrawerFragment;
import com.driverconnex.fragments.VehicleDrawerFragment;
import com.driverconnex.singletons.DCVehilceDataSingleton;
import com.driverconnex.utilities.ModulesUtilities;
import com.driverconnex.vehicles.AlertsActivity;

/**
 * Home activity of the app.
 * 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class HomeActivity extends FragmentActivity implements
		VehicleDrawerFragment.VehicleDrawerCallbacks,
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	// Fragment managing the behaviors, interactions and presentation of the
	// navigation drawer.
	private NavigationDrawerFragment navigationDrawerFragment;

	// Vehicle drawer
	private VehicleDrawerFragment vehicleDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// Selects Dashboard according to the app
		// ----------------------------------
		if ((AppConfig.dashboard == AppConfig.Dashboard.KPMG)
				|| (AppConfig.dashboard == AppConfig.Dashboard.LOGICAL)) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.container, new KPMGFragment());
			ft.commit();
		} else if (AppConfig.dashboard == AppConfig.Dashboard.DRIVER_CONNEX) {
			android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.container, new DriverConnexFragment());
			ft.commit();
		}
		// ----------------------------------

		// Sets up the drawers
		// ----------------------------------
		navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		navigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		vehicleDrawerFragment = (VehicleDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.vehicle_drawer);
		vehicleDrawerFragment.setUp(R.id.vehicle_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		// ----------------------------------

		// Hide application title from the action bar
		getActionBar().setTitle(" ");
	}

	@Override
	public void onResume() {
		super.onResume();
		vehicleDrawerFragment.refresh();
		DCVehilceDataSingleton.getDCModuleSingleton(HomeActivity.this)
				.getVehilesList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Checks if DC Vehicles module is enabled, if it is then it will
		// display a vehicle icon on the top action bar that
		// when clicked it will open vehicle drawer
		if (XMLModuleConfigParser.isModuleEnabled(HomeActivity.this,
				"DC Vehicles")) {
			getMenuInflater().inflate(R.menu.home, menu);
			return true;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_vehicle) {
			// Checks if vehicle drawer is opened
			if (!vehicleDrawerFragment.isDrawerOpened()) {
				// Checks if navigation drawer is opened, if so close it
				if (navigationDrawerFragment.isDrawerOpen())
					navigationDrawerFragment.closeDrawer();

				// Opens vehicle drawer
				vehicleDrawerFragment.openDrawer();
			} else
				vehicleDrawerFragment.closeDrawer();
		}
		// When home button is pressed navigation drawer is displayed
		else if (item.getItemId() == android.R.id.home) {
			// Check if vehicle drawer is opened
			if (vehicleDrawerFragment.isDrawerOpened()) {
				// Close it, so that it doesn't overlap with navigation drawer
				vehicleDrawerFragment.closeDrawer();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return false;
	}

	@Override
	public void onVehicleDrawerItemSelected(int position) {
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
	}

	/**
	 * Creates a tab bar. Call this function from main Fragment e.g
	 * DriverConnexFragment whenever you want to create a tab bar for the home
	 * screen. It takes a LinearLayout as a parameter which is a layout for the
	 * tab bar in the layout.xml (look at fragment_driverconnex).
	 */
	public static void createTabBar(final Context context, LinearLayout tabBar,
			String path) {
		// Get tabs from XML file
		final ArrayList<Tab> tabs = XMLModuleConfigParser.getTabsFromXML(
				context, path);

		int piority = 0;
		int tabIndex = 0;
		boolean added[] = new boolean[tabs.size()];

		// Loops through all tabs we got
		for (int i = 0; i < tabs.size(); i++) {
			// Gets priority of the tab
			piority = tabs.get(i).getPriority();

			// Gets the index of tab with the highest priority
			tabIndex = i;

			// Loops again through all tabs
			for (int j = 0; j < tabs.size(); j++) {
				// Compares priorities and makes sure tab has not been yet added
				System.out.println("getpriority =  "
						+ tabs.get(j).getPriority() + "added? = " + added[j]);
				if (piority > tabs.get(j).getPriority() && !added[j]) {
					// This tab has higher priority, so it should be displayed
					// before the previous one
					piority = tabs.get(j).getPriority();
					tabIndex = j;
				}
			}

			added[tabIndex] = true;

			// Creates layout for the tab
			LinearLayout tab = new LinearLayout(context);

			// Creates icon for the tab
			LinearLayout imageLayout = new LinearLayout(context);
			imageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			ImageView icon = new ImageView(context);

			icon.setImageResource(context.getResources().getIdentifier(
					tabs.get(tabIndex).getIcon(), "drawable",
					context.getPackageName()));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// lp.setMargins(0, 10, 0, 0);
			// // for small resolutino
			lp.setMargins(0, 5, 0, 0);

			icon.setLayoutParams(lp);
			imageLayout.addView(icon);

			LinearLayout textLayout = new LinearLayout(context);
			textLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			TextView title = new TextView(context);
			String titleText = tabs.get(tabIndex).getName();
			if (titleText.equals("Track Journey")) {
				titleText = "Track";
			} else if (titleText.equals("Park Vehicle")) {
				titleText = "Park";

			} else if (titleText.equals("Add Expense")) {
				titleText = "Expense";

			} else if (titleText.equals("Alerts")) {
				titleText = "Alerts";

			}
			title.setText(titleText);

			System.out.println("title is " + tabs.get(tabIndex).getName());
			// Determines screen size and sets size of the title to match the
			// screen
			if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
				title.setTextSize(18);
			} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				title.setTextSize(11);
			} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
				title.setTextSize(9);
			} else {
				title.setTextSize(11);
			}
			title.setTextColor(context.getResources().getColor(R.color.white));

			textLayout.addView(title);

			// Creates tab
			LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.60f);
			LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.30f);
			titleParams.setMargins(0, 4, 0, 5);

			tab.setOrientation(LinearLayout.VERTICAL);
			tab.addView(imageLayout, iconParams);
			tab.addView(textLayout, titleParams);

			final int tabIndexFinal = tabIndex;

			// Sets listener for the tab. When it's clicked it will open
			// assigned to the tab activity
			tab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ModulesUtilities moduleUtil = new ModulesUtilities(context);

					// Testing. not implement yet . So should remove.
					ArrayList<HelpListItems> moduleMenuList = new ArrayList<HelpListItems>();

					String s = tabs.get(tabIndexFinal).getName();
					System.out.println(s);
					if (tabs.get(tabIndexFinal).getName().equals("Alerts")) {
						// moduleMenuList = XMLModuleConfigParser
						// .getHelpItemsFromXML(context, "help.xml");
						// System.out.println("size of help list is "
						// + moduleMenuList.size());
						// System.out.println();
						Intent alertActiviy = new Intent(context,
								AlertsActivity.class);
						alertActiviy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(alertActiviy);
						((Activity) context).overridePendingTransition(
								R.anim.slide_in, R.anim.null_anim);

						return;
					} else {
						// Gets the tab's activity
						Intent intent = new Intent(context, moduleUtil
								.getModuleClass(tabs.get(tabIndexFinal)
										.getName()));

						// Starts the activity
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
						((Activity) context).overridePendingTransition(
								R.anim.slide_in, R.anim.null_anim);
					}
				}
			});
			// set for equal space
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.MATCH_PARENT, 1);
			// Add tabs to the tab bar
			tabBar.addView(tab, param);

		}
	}
}
