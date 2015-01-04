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
import com.driverconnex.vehicles.Service;

/**
 * Reads service_providers_config.xml file and parsers information to ArrayList of service objects
 * @author wizard
 *
 */

public class XMLServiceProviderParser 
{
	/**
	 * Gets services from XML file
	 * @param context
	 * @return
	 */
	public static ArrayList<Service> getServicesFromXML(Context context)
	{
		ArrayList<Service> services = new ArrayList<Service>();
		
		XmlPullParserFactory factory;
		
		try 
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);		
			XmlPullParser parser = factory.newPullParser();
			
			// Open file
			InputStream inputStream = context.getAssets().open("service_providers_config.xml");	 
			
			// Set the input for the parser using an InputStreamReader	
			parser.setInput(new InputStreamReader(inputStream));
			
			// Read nodes
			readFeed(parser, services);
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return services;
	}
		
	/**
	 * Reads nodes from XML file.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeed(XmlPullParser parser, ArrayList<Service> services) throws XmlPullParserException, IOException
	{
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) 
			{
				// Get name of the tag
				String nodeName = parser.getName();
				
				// Read main entry
				if(nodeName.contentEquals("service")) 
				{
					Service service = new Service(parser.getAttributeValue(0));
					services.add(service);
					readServiceEntry(parser, nodeName, service);
				}
			} 
			
			eventType = parser.next();
		}
	}
	
	/**
	 * Reads service node
	 * @param parser
	 * @param subtag
	 * @param service
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readServiceEntry(XmlPullParser parser, String subtag, Service service)
			throws XmlPullParserException, IOException 
	{
		parser.require(XmlPullParser.START_TAG, null, subtag);
		
		// Start reading values of main entry
		while (parser.next() != XmlPullParser.END_TAG) 
		{	
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// I have not found a reason yet why this has to be in order to work
			parser.next();
			
			String nodeName = parser.getName();

			// Look for item entry
			if (nodeName.contentEquals("item")) 
				service.addNewProvider(parser.getAttributeValue(0),parser.getAttributeValue(1));
			else 
				XMLUtilities.skip(parser);
		}
	}
}
