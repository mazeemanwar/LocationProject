package com.driverconnex.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.driverconnex.app.AppConfig;
import com.driverconnex.utilities.XMLUtilities;

/**
 * This class reads the app_config file and initialises AppConfig.
 * 
 * @author Adrian Klimczak
 * @author Muhammad Azeem Anwar
 */

public class XMLAppConfigParser {
	/**
	 * Reads app_config.xml file and initialises AppConfig.
	 * 
	 * @param context
	 */
	public static void parseAppConfigFromXML(Context context) {
		XmlPullParserFactory factory;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			// Open file
			InputStream inputStream = context.getAssets()
					.open("app_config.xml");

			// Set the input for the parser using an InputStreamReader
			parser.setInput(new InputStreamReader(inputStream));

			// Read nodes
			readFeed(parser);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads nodes from XML file.
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) {
				// Get name of the tag
				String nodeName = parser.getName();

				// Read main entry
				if (nodeName.contentEquals("main"))
					readMainEntry(parser, nodeName);
				if (nodeName.contentEquals("appearance"))
					readAppearanceEntry(parser, nodeName);
				if (nodeName.contentEquals("homepage"))
					readCustomHomePage(parser, nodeName);
				else if (nodeName.contentEquals("isorganizationrequired")) {
					AppConfig.setIsOrganizationRequried(parser
							.getAttributeValue(0));
				} else if (nodeName.contentEquals("isverificationrequired")) {
					AppConfig.setIsVerificationRequired(parser
							.getAttributeValue(0));
				} else if (nodeName.contentEquals("isonlinemodules")) {
					AppConfig.setIsOnlineModuleRequired(parser
							.getAttributeValue(0));
				} else if (nodeName.contentEquals("isfinancedisable")) {
					AppConfig.setisfinanceDisable(readBool(parser
							.getAttributeValue(0)));

				} else if (nodeName.contentEquals("issharingdisable")) {
					AppConfig.setIsSharingDisable(readBool(parser
							.getAttributeValue(0)));
				} else if (nodeName.contentEquals("driverline")) {
					AppConfig.setDRIVERLINE(parser.getAttributeValue(0));
				} else if (nodeName.contentEquals("bookservice")) {
					AppConfig.setBOOK_A_SERVICE(parser.getAttributeValue(0));
				} else if (nodeName.contentEquals("bookdemo")) {
					AppConfig.setBOOK_DEMO(parser.getAttributeValue(0));

				}
			}

			eventType = parser.next();
		}
	}

	/**
	 * Reads main entry node.
	 * 
	 * @param parser
	 * @param subtag
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readMainEntry(XmlPullParser parser, String subtag)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String nodeName = parser.getName();

			if (nodeName.contentEquals("appid"))
				AppConfig.setAppID(parser.getAttributeValue(0));
			if (nodeName.contentEquals("clientkey"))
				AppConfig.setClientKey(parser.getAttributeValue(0));
			else
				XMLUtilities.skip(parser);
		}
	}

	/**
	 * Reads appearance node.
	 * 
	 * @param parser
	 * @param subtag
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readAppearanceEntry(XmlPullParser parser, String subtag)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of appearance entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String nodeName = parser.getName();

			if (nodeName.contentEquals("dashboard")) {
				AppConfig.setDashboard(parser.getAttributeValue(0));
			} else
				XMLUtilities.skip(parser);
		}
	}

	/**
	 * Reads appearance node.
	 * 
	 * @param parser
	 * @param subtag
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readCustomHomePage(XmlPullParser parser, String subtag)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, subtag);

		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String nodeName = parser.getName();

			if (nodeName.contentEquals("pagename"))
				AppConfig.setHOMEPAGENAME(parser.getAttributeValue(0));
			if (nodeName.contentEquals("isenable"))
				AppConfig.setISHOMEPAGEENABLE(readBool(parser
						.getAttributeValue(0)));
			if (nodeName.contentEquals("image"))
				AppConfig.setHOMEPAGEICON(parser.getAttributeValue(0));

			if (nodeName.contentEquals("url"))
				AppConfig.setHOMEPAGEURL(parser.getAttributeValue(0));

			else
				XMLUtilities.skip(parser);
		}
	}

	private static boolean readBool(String parseValue) {

		if (parseValue.equals("true"))
			return true;

		else

			return false;
	}
}
