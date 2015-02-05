package com.driverconnex.singletons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.driverconnex.app.AppConfig;
import com.driverconnex.data.MenuListItem;
import com.driverconnex.data.MenuListItems;
import com.driverconnex.data.XMLMenuConfigParser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

public class DCModuleSingleton {

	// Static member holds only one instance of the

	// SingletonExample class

	private static DCModuleSingleton singletonInstance;
	private static ArrayList<String> moduleFromServer = new ArrayList<String>();
	private static ArrayList<MenuListItems> moduleMenuList = new ArrayList<MenuListItems>();

	// SingletonExample prevents any other class from instantiating

	private DCModuleSingleton() {

	}

	// Providing Global point of access

	public static DCModuleSingleton getDCModuleSingleton(final Context context) {

		if (null == singletonInstance) {

			singletonInstance = new DCModuleSingleton();
			System.out.println("with out creating object");
			int size = moduleMenuList.size();
			System.out.println(size);
			if (moduleMenuList.size() < 1) {
				getMenuFromServer(context);

			}
		}

		return singletonInstance;

	}

	public ArrayList<MenuListItems> getServerModule() {

		System.out.println("with out creating object");
		return moduleMenuList;
	}

	final static HashMap<Integer, String> moduleMap = new HashMap<Integer, String>();

	// FOR TESTING ONLY THIS FUNCTION SHOULD BE FOR MENU DISPLAYING AND TABBAR
	private static void getMenuFromServer(final Context context) {
		ParseObject userOrganisation = null;
		ParseUser user = ParseUser.getCurrentUser();
		userOrganisation = user.getParseObject("userOrganisation");
		// check if online menu required
		if (AppConfig.getIsOnlineModuleRequired().equals("yes")) {

			// make a query on role table
			ParseQuery<ParseRole> query = ParseRole.getQuery();
			query.whereExists("roleModule");
			query.include("roleModule");

			ParseObject organisation = ParseObject
					.createWithoutData("DCOrganisation", userOrganisation
							.getObjectId().toString());

			query.whereEqualTo("roleOrganisation",

			organisation);
			query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

			query.findInBackground(new FindCallback<ParseRole>() {

				@Override
				public void done(List<ParseRole> results, ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						for (int i = 0; i < results.size(); i++) {
							String moduleNameFromParse = results.get(i)
									.getParseObject("roleModule")
									.getString("moduleName");
							Integer modulePriorityFromParse = results.get(i)
									.getParseObject("roleModule")
									.getInt("modulePriority");
							moduleMap.put(modulePriorityFromParse,
									moduleNameFromParse);

						}
						sortedModuleList(context);
					} else {
						System.out.println("something going wrong");
					}

				}
			});

		}
	}

	private static void sortedModuleList(Context context) {
		moduleFromServer.clear();

		Map<Integer, String> map = new TreeMap<Integer, String>(moduleMap);
		Set set2 = map.entrySet();
		Iterator iterator2 = set2.iterator();
		while (iterator2.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator2.next();

			moduleFromServer.add(me2.getValue().toString());
		}

		for (int i = 0; i < moduleFromServer.size(); i++) {
		}
		for (int i = 0; i < moduleFromServer.size(); i++) {
			ArrayList<MenuListItem> itemList = new ArrayList<MenuListItem>();
			MenuListItems items = new MenuListItems();
			String moduleName = moduleFromServer.get(i);
			ArrayList<MenuListItems> tempList = null;
			String path = "";
			if (moduleName.equals("DC Mileage")) {
				path = "dc_mileage.xml";
			} else if (moduleName.equals("DC Parking")) {
				path = "dc_parking.xml";
			} else if (moduleName.equals("DC Expenses")) {

				path = "dc_expense.xml";

			} else if (moduleName.equals("DC Claims User")) {
				path = "dc_claims.xml";
			} else if (moduleName.equals("DC Incidents User")) {

				path = "dc_incident.xml";

			} else if (moduleName.equals("DC Policy")) {

				path = "dc_policy.xml";

			}
			if (!path.equals("")) {
				tempList = XMLMenuConfigParser.getBasicMenuItemsFromXML(
						context, path);
				// items = new MenuListItems();
				for (int j = 0; j < tempList.size(); j++) {
					String name = tempList.get(j).getName();
					items.setName(name);
					items.setEnabled(tempList.get(j).isEnabled());
					Boolean bool = tempList.get(j).isEnabled();
					System.out.println(bool);
					ArrayList<MenuListItem> temp = tempList.get(j)
							.getSubitems();
					for (int k = 0; k < temp.size(); k++) {
						MenuListItem item = new MenuListItem();
						item.setName(temp.get(k).getName());
						item.setEnabled(temp.get(k).isEnabled());
						if ((temp.get(k).getIcon() != null)
								|| (!temp.get(k).getIcon().equals(""))) {
							item.setIcon(temp.get(k).getIcon());
						}
						if ((temp.get(k).getUrl() != null)) {
							item.setUrl(temp.get(k).getUrl());
						}
						item.setClassName(temp.get(k).getClassName());
						itemList.add(item);
					}
				}

				items.setSubitems(itemList);

			}
			System.out.println();
			if (!path.equals("")) {
				path = "";

				moduleMenuList.add(items);
			}

		}

		// disPlayMenu();
	}

}
