package com.driverconnex.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.driverconnex.app.AppConfig;
import com.driverconnex.utilities.XMLUtilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

/**
 * Parser for basic configuration of the app. It parsers information from
 * basic_config.xml file.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class XMLBasicConfigParser {
	private static final String path = "basic_config.xml";
	private static	ArrayList<String> moduleFromServer = new ArrayList<String>();
	/**
	 * Gets basic menu items from XML file
	 * 
	 * @return
	 */
	
	public static ArrayList<MenuListItems> getBasicMenuItemsFromXML(
			Context context) {
		ArrayList<MenuListItems> menuItems = new ArrayList<MenuListItems>();

		XmlPullParserFactory factory;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			// Open file
			InputStream inputStream = context.getAssets().open(path);

			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			// Read nodes
			readFeed(parser, menuItems);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return menuItems;
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeed(XmlPullParser parser,
			ArrayList<MenuListItems> menuItems) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();

				// Read menu entry
				if (nodeName.contentEquals("menu")) {
					// Read the content of the menu
					readMenuEntry(parser, nodeName, menuItems);
				}
			}

			eventType = parser.next();
		}
	}

	/**
	 * Reads menu node.
	 * 
	 * @param parser
	 * @param subtag
	 * @param menuItems
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readMenuEntry(XmlPullParser parser, String subtag,
			ArrayList<MenuListItems> menuItems) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String nodeName = parser.getName();

			// Look for item entry
			if (nodeName.contentEquals("section")) {
				if (XMLUtilities.isEnabled(parser)) {
					MenuListItems items = new MenuListItems();
					items.setName(parser.getAttributeValue(0));
					items.setEnabled(true);

					ArrayList<MenuListItem> itemList = new ArrayList<MenuListItem>();

					// Read the content of the menu section
					readMenuSectionEntry(parser, nodeName, itemList);

					items.setSubitems(itemList);
					menuItems.add(items);
				}
			} else
				XMLUtilities.skip(parser);
		}
	}

	/**
	 * Reads section node.
	 * 
	 * @param parser
	 * @param subtag
	 * @param itemList
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readMenuSectionEntry(XmlPullParser parser,
			String subtag, ArrayList<MenuListItem> itemList)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// I have not found a reason yet why this has to be in order to work
			parser.next();

			String nodeName = parser.getName();

			// Read item of the menu section
			if (nodeName.contentEquals("item")) {
				// Check if item is enabled
				if (XMLUtilities.isEnabled(parser)) {
					// Create an item
					MenuListItem item = new MenuListItem();
					item.setName(parser.getAttributeValue(0));
					item.setEnabled(true);

					if (XMLUtilities.getValue(parser, "image") != null)
						item.setIcon(XMLUtilities.getValue(parser, "image"));
					if (XMLUtilities.getValue(parser, "URL") != null)
						item.setUrl(XMLUtilities.getValue(parser, "URL"));

					item.setClassName(parser.getAttributeValue(0));

					// Add item to the list of section items
					itemList.add(item);
				}
			} else
				XMLUtilities.skip(parser);
		}
	}
	// module from server
	final static HashMap<Integer, String> moduleMap = new HashMap<Integer, String>();

	// FOR TESTING ONLY THIS FUNCTION SHOULD BE FOR MENU DISPLAYING AND TABBAR
	private static void getMenuFromServer() {
		ParseObject userOrganisation = null;
		ParseUser user = ParseUser.getCurrentUser();
		userOrganisation = user.getParseObject("userOrganisation");
		// check if online menu required
		if (AppConfig.getIsOnlineModuleRequired().equals("yes")) {

			// make a query on role table
			ParseQuery<ParseRole> query1 = ParseRole.getQuery();
			query1.whereExists("roleModule");
			query1.include("roleModule");

			ParseObject organisation = ParseObject
					.createWithoutData("DCOrganisation", userOrganisation
							.getObjectId().toString());

			query1.whereEqualTo("roleOrganisation",

			organisation);
			query1.findInBackground(new FindCallback<ParseRole>() {

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
						sortedModuleList();
					} else {
						System.out.println("something going wrong");
					}

				}
			});

		}
	}

	// make sorted menu list

	private static void sortedModuleList() {

		Map<Integer, String> map = new TreeMap<Integer, String>(moduleMap);
		Set set2 = map.entrySet();
		Iterator iterator2 = set2.iterator();
		while (iterator2.hasNext()) {
			Map.Entry me2 = (Map.Entry) iterator2.next();
			// System.out.print(me2.getKey() + ": ");
			// System.out.println(me2.getValue());
			moduleFromServer.add(me2.getValue().toString());

		}
		for (int i = 0; i < moduleFromServer.size(); i++) {
			System.out.println("SORTED MODULE LIST = "+ i+" " + moduleFromServer.get(i));
String moduleName = moduleFromServer.get(i);

if (moduleName.equals("DC Mileage")) {
	
}
		}
System.out.println("");
	}

}
