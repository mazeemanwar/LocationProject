package com.driverconnex.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.driverconnex.utilities.XMLUtilities;

/**
 * Reads modules_config.xml file and parsers information to ArrayList of service
 * objects
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 * 
 */

public class XMLModuleConfigParser {
	private static final String path = "modules_config.xml";

	/**
	 * Gets menu items from XML file.
	 * 
	 * @return
	 */
	public static ArrayList<MenuListItems> getMenuItemsFromXML(Context context) {
		ArrayList<MenuListItems> menuItems = new ArrayList<MenuListItems>();
		// getMenuFromServer();
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
			readFeedModule(parser, menuItems);

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
	private static void readFeedModule(XmlPullParser parser,
			ArrayList<MenuListItems> menuItems) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();

				// Read Module
				if (nodeName.contentEquals("module")) {
					// Read the content of the module node
					readModuleEntry(parser, nodeName, menuItems);
				}
			}

			eventType = parser.next();
		}
	}

	private static void readModuleEntry(XmlPullParser parser, String subtag,
			ArrayList<MenuListItems> menuItems) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// I have not found a reason yet why this has to be in order to work
			// parser.next();

			String nodeName = parser.getName();

			// Read menu entry
			if (nodeName.contentEquals("menu")) {
				// Read the content of the menu
				readMenuEntry(parser, nodeName, menuItems);
			} else
				XMLUtilities.skip(parser);
		}
	}

	private static void readMenuEntry(XmlPullParser parser, String subtag,
			ArrayList<MenuListItems> menuItems) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// I have not found a reason yet why this has to be in order to work
			// parser.next();

			String nodeName = parser.getName();

			// Look for item entry
			if (nodeName.contentEquals("section")) {
				// Check if item is enabled
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

			// Read menu entry
			if (nodeName.contentEquals("item")) {
				// Check if item is enabled
				if (XMLUtilities.isEnabled(parser)) {
					MenuListItem item = new MenuListItem();
					item.setName(parser.getAttributeValue(0));
					item.setEnabled(true);

					if (XMLUtilities.getValue(parser, "image") != null)
						item.setIcon(XMLUtilities.getValue(parser, "image"));
					if (XMLUtilities.getValue(parser, "URL") != null)
						item.setUrl(XMLUtilities.getValue(parser, "URL"));

					item.setClassName(parser.getAttributeValue(0));

					itemList.add(item);
				}
			} else
				XMLUtilities.skip(parser);
		}
	}

	public static ArrayList<Tab> getTabsFromXML(Context context) {
		ArrayList<Tab> tabs = new ArrayList<Tab>();

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
			readFeedTabBar(parser, tabs);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tabs;
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeedTabBar(XmlPullParser parser, ArrayList<Tab> tabs)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();

				// Read entry
				if (nodeName.contentEquals("tabBar")) {
					readTabBarItemEntry(parser, nodeName, tabs);
				}
			}

			eventType = parser.next();
		}
	}

	private static void readTabBarItemEntry(XmlPullParser parser,
			String subtag, ArrayList<Tab> tabs) throws XmlPullParserException,
			IOException {

		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// I have not found a reason yet why this has to be in order to work
			parser.next();

			String nodeName = parser.getName();

			// Look for item entry
			if (nodeName.contentEquals("item")) {
				Tab tab = new Tab();
				tab.setName(parser.getAttributeValue(0));
				tab.setIcon(parser.getAttributeValue(2));

				tab.setPriority(Integer.parseInt(parser.getAttributeValue(1)));

				tabs.add(tab);
			} else
				XMLUtilities.skip(parser);
		}
	}

	/**
	 * Checks if given module is enabled
	 * 
	 * @param context
	 * @param moduleName
	 *            - Name of the module
	 * @return true - enabled or false - disabled
	 */
	public static boolean isModuleEnabled(Context context, String moduleName) {
		XmlPullParserFactory factory;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			// Open file
			InputStream inputStream = context.getAssets().open(path);

			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			int eventType = parser.getEventType();

			// Start reading the file
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Check if we found a tag
				if (eventType == XmlPullParser.START_TAG) {
					// Get name of the tag
					String nodeName = parser.getName();

					// Check if it's module node
					if (nodeName.contentEquals("module")) {
						// Check if name of the module matches with given name
						if (parser.getAttributeValue(0).equals(moduleName)) {
							// Check if module is enabled
							if (XMLUtilities.isEnabled(parser))
								return true;
							else
								return false;
						}
					}
				}

				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

}
