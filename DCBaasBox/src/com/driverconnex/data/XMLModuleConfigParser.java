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
	private static String path = "modules_config.xml";

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

	public static ArrayList<Tab> getTabsFromXML(Context context, String filePath) {

		ArrayList<Tab> tabs = new ArrayList<Tab>();

		XmlPullParserFactory factory;

		// if (!filePath.equals("")) {
		path = filePath;
		// }
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
		path = "modules_config.xml";
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

				tab.setIcon(parser.getAttributeValue(1));
				tab.setPriority(Integer.parseInt(parser.getAttributeValue(2)));

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

	/**
	 * Reads help nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */

	public static ArrayList<HelpListItems> getHelpItemsFromXML(Context context,
			String filePath) {
		ArrayList<HelpListItems> helpItems = new ArrayList<HelpListItems>();
		// getMenuFromServer();
		XmlPullParserFactory factory;
		path = filePath;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			// Open file
			InputStream inputStream = context.getAssets().open(path);

			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			// Read nodes
			readFeedHelp(parser, helpItems);

			path = "modules_config.xml";
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return helpItems;
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeedHelp(XmlPullParser parser,
			ArrayList<HelpListItems> helpItems) throws XmlPullParserException,
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
					readHelpEntry(parser, nodeName, helpItems);
				}
			}

			eventType = parser.next();
		}
	}

	private static void readHelpEntry(XmlPullParser parser, String subtag,
			ArrayList<HelpListItems> helpItems) throws XmlPullParserException,
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
				readQuestionEntry(parser, nodeName, helpItems);
			} else
				XMLUtilities.skip(parser);
		}
	}

	private static void readQuestionEntry(XmlPullParser parser, String subtag,
			ArrayList<HelpListItems> helpItems) throws XmlPullParserException,
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
					HelpListItems items = new HelpListItems();
					items.setName(parser.getAttributeValue(0));
					items.setEnabled(true);
					String s = parser.getAttributeName(0);
					String sd = items.getName();
					System.out.println();
			
					ArrayList<HelpListItem> itemList = new ArrayList<HelpListItem>();

					// Read the content of the menu section
					readAnswerSectionEntry(parser, nodeName, itemList);

					items.setSubitems(itemList);
					helpItems.add(items);
				}
			} else
				XMLUtilities.skip(parser);
		}
	}

	private static void readAnswerSectionEntry(XmlPullParser parser,
			String subtag, ArrayList<HelpListItem> itemList)
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
				// if (XMLUtilities.isEnabled(parser)) {
				HelpListItem item = new HelpListItem();
				item.setName(parser.getAttributeValue(0));
				String s = parser.getAttributeName(0);
				System.out.println();
				// item.setAnswer(parser.getAttributeName(1));
				
				if (XMLUtilities.getValue(parser, "question") != null)
					item.setQuestion(XMLUtilities.getValue(parser, "question"));
				item.setQuestion(true);
				if (XMLUtilities.getValue(parser, "answer") != null)
					item.setAnswer(XMLUtilities.getValue(parser, "answer"));
				item.setAnswer(true);
				//
				// item.setClassName(parser.getAttributeValue(0));

				itemList.add(item);
				// }
			} else
				XMLUtilities.skip(parser);
		}
	}

	// servies
	public static ArrayList<ServiceListItems> getServiceItemsFromXML(
			Context context, String filePath) {
		ArrayList<ServiceListItems> serviceItems = new ArrayList<ServiceListItems>();
		// getMenuFromServer();
		XmlPullParserFactory factory;
		path = filePath;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			// Open file
			InputStream inputStream = context.getAssets().open(path);

			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			// Read nodes
			readFeedService(parser, serviceItems);

			path = "modules_config.xml";
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return serviceItems;
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeedService(XmlPullParser parser,
			ArrayList<ServiceListItems> serviceItems)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();

				// Read Module
				if (nodeName.contentEquals("module")) {
					// Read the content of the module node
					readServiceEntry(parser, nodeName, serviceItems);
				}
			}

			eventType = parser.next();
		}
	}

	private static void readServiceEntry(XmlPullParser parser, String subtag,
			ArrayList<ServiceListItems> serviceItems)
			throws XmlPullParserException, IOException {
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
				readPhoneEntry(parser, nodeName, serviceItems);
			} else
				XMLUtilities.skip(parser);
		}
	}

	private static void readPhoneEntry(XmlPullParser parser, String subtag,
			ArrayList<ServiceListItems> serviceItems)
			throws XmlPullParserException, IOException {
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
					ServiceListItems items = new ServiceListItems();
					items.setName(parser.getAttributeValue(0));
					items.setEnabled(true);

					ArrayList<ServiceListItem> itemList = new ArrayList<ServiceListItem>();

					// Read the content of the menu section
					readEmailEntry(parser, nodeName, itemList);

					items.setSubitems(itemList);
					serviceItems.add(items);
				}
			} else
				XMLUtilities.skip(parser);
		}
	}

	private static void readEmailEntry(XmlPullParser parser, String subtag,
			ArrayList<ServiceListItem> itemList) throws XmlPullParserException,
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

			// Read menu entry
			if (nodeName.contentEquals("item")) {
				// Check if item is enabled
				// if (XMLUtilities.isEnabled(parser)) {
				ServiceListItem item = new ServiceListItem();
				item.setName(parser.getAttributeValue(0));
				// item.setAnswer(parser.getAttributeName(1));

				if (XMLUtilities.getValue(parser, "phone") != null)
					item.setPhone(XMLUtilities.getValue(parser, "phone"));
				// item.setQuestion(true);
				if (XMLUtilities.getValue(parser, "phonename") != null)
					item.setPhonename(XMLUtilities
							.getValue(parser, "phonename"));
				if (XMLUtilities.getValue(parser, "email") != null)
					item.setEmail(XMLUtilities.getValue(parser, "email"));
				// item.setQuestion(true);
				if (XMLUtilities.getValue(parser, "emailname") != null)
					item.setEmailname(XMLUtilities
							.getValue(parser, "emailname"));

				// item.setAnswer(true);
				//
				// item.setClassName(parser.getAttributeValue(0));

				itemList.add(item);
				// }
			} else
				XMLUtilities.skip(parser);
		}
	}

}
