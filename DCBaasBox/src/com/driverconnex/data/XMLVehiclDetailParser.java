package com.driverconnex.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.driverconnex.utilities.XMLUtilities;
import com.driverconnex.vehicles.DCVehicle;

/**
 * Parser for individual menu configuration of the app. It parsers information
 * from basic_config.xml file.
 * 
 * @author MUHAMMAD AZEEM ANWAR
 * 
 */

public class XMLVehiclDetailParser {
	DCVehicle mVehicle = new DCVehicle();
	private static final String path = "basic_config.xml";
	private ArrayList<String> moduleFromServer = new ArrayList<String>();

	/**
	 * Gets basic menu items from XML file
	 * 
	 * @return
	 */

	public static DCVehicle getBasicMenuItemsFromXML(Context context,
			String filePath, String xmlString) {
		ArrayList<MenuListItems> menuItems = new ArrayList<MenuListItems>();
		DCVehicle mVehicle = new DCVehicle();

		// homeAcitivyContex = context;
		XmlPullParserFactory factory;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			// Open file
			InputStream inputStream = new ByteArrayInputStream(
					xmlString.getBytes("UTF-8"));
			// inputStream = context.getAssets().open(filePath);
			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			// Read nodes
			readFeed(parser, menuItems, mVehicle);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println();
		return mVehicle;
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeed(XmlPullParser parser,
			ArrayList<MenuListItems> menuItems, DCVehicle mVehicle)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();
				// Read menu entry
				if (nodeName.contentEquals("MAKE")) {
					// Read the content of the menu

					readSubEntry(parser, nodeName, mVehicle);
				} else if (nodeName.contentEquals("MODEL")) {
					readSubEntry(parser, nodeName, mVehicle);

				} else if (nodeName.contentEquals("REG_DATE")) {
					System.out.println();
					String s = parser.nextText();
					System.out.println(s);

				} else if (nodeName.contentEquals("VIN_ENDING")) {
					String s = parser.nextText();
					System.out.println(s);

				} else if (nodeName.contentEquals("COLOUR")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setColour(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("BODY")) {
					readSubEntry(parser, nodeName, mVehicle);

				} else if (nodeName.contentEquals("BODY_CLASS")) {
					System.out.println();
					String s = parser.nextText();
					System.out.println(s);

				}

				else if (nodeName.contentEquals("SEATING_CAPACITY")) {
					System.out.println();
					String s = parser.nextText();
					System.out.println(s);

				} else if (nodeName.contentEquals("WHEELPLAN")) {
					System.out.println();
					String s = parser.nextText();
					System.out.println(s);

				} else if (nodeName.contentEquals("CO2")) {
					System.out.println();
					String s = parser.nextText();
					// int f = Integer.valueOf(s);
					mVehicle.setCo2Value(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("CC")) {

					String s = parser.nextText();
					mVehicle.setCC(s);

				} else if (nodeName.contentEquals("FUEL")) {
					readSubEntry(parser, nodeName, mVehicle);
				} else if (nodeName.contentEquals("RATE_6_MONTH")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setSixMonthRFL(s);

				} else if (nodeName.contentEquals("RATE_12_MONTH")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setYearRFL(s);

				} else if (nodeName.contentEquals("ENGINE_SIZE")) {
					System.out.println();
					String s = parser.nextText();
					Float d = Float.valueOf(s);
					mVehicle.setEngineSize(d);
					// System.out.println(d);

				} else if (nodeName.contentEquals("MODEL_VARIANT_NAME")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setDerivative(s);
					System.out.println(s);

				}

				else if (nodeName.contentEquals("DOOR_COUNT")) {
					System.out.println();
					String s = parser.nextText();
					// mVehicle.set
					int f = Integer.valueOf(s);
					mVehicle.setDoorCount(f);
					System.out.println(s);

				} else if (nodeName.contentEquals("BODY_DESC")) {
					System.out.println();
					String s = parser.nextText();

					mVehicle.setBody(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("MODEL_VARIANT_NAME")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setModel(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("GEARBOX_TYPE")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setGearBox(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("NUMBER_OF_AXLES")) {
					System.out.println();
					String s = parser.nextText();
					System.out.println(s);

				} else if (nodeName.contentEquals("EXTRA_URBAN_MPG")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setExturbMPG(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("COMBINED_MPG")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setComMPG(s);
					System.out.println(s);

				} else if (nodeName.contentEquals("BHP_COUNT")) {
					System.out.println();
					String s = parser.nextText();
					mVehicle.setBhp(s);
					System.out.println(s);

				}

			}

			eventType = parser.next();
		}
		System.out.println(parser);
		// XMLUtilities.skip(parser);

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
	private static void readSubEntry(XmlPullParser parser, String subtag,
			DCVehicle mVehicle) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String nodeName = parser.getName();

			// Look for item entry
			if (nodeName.contentEquals("DESC")) {
				// if (XMLUtilities.isEnabled(parser)) {
				if (subtag.equals("MAKE")) {
					String s = parser.nextText();

					mVehicle.setMake(s);
				} else if (subtag.equals("MODEL")) {
					String s = parser.nextText();
					mVehicle.setModel(s);
					System.out.println(s);

				} else if (subtag.equals("BODY")) {
					String s = parser.nextText();
					mVehicle.setBody(s);
					System.out.println(s);

				} else if (subtag.equals("FUEL")) {
					String s = parser.nextText();
					mVehicle.setFuel(s);
				}

				// }
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

}
