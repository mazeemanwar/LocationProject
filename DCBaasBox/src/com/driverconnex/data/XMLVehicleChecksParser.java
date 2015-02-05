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
import com.driverconnex.vehicles.VehicleCheck;

/**
 * Reads vehicle_checks.xml file and parsers information to ArrayList of VehicleCheck objects
 * @author Adrian Klimczak
 *
 */

public class XMLVehicleChecksParser 
{
	private static final String path = "vehicle_checks.xml";
	
	/**
	 * Gets vehicle checks from XML file.
	 * @return
	 */
	public static ArrayList<VehicleCheck> getVehicleChecksFromXML(Context context)
	{
		ArrayList<VehicleCheck> vehicleChecks = new ArrayList<VehicleCheck>();
		
		XmlPullParserFactory factory;
		
		try 
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);		
			XmlPullParser parser = factory.newPullParser();
			
			// Open file
			InputStream inputStream = context.getAssets().open(path);	 
			
			// Set the input for the parser using an InputStreamReader	
			parser.setInput(new InputStreamReader(inputStream));
			
			// Read nodes
			readFeed(parser, vehicleChecks);
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return vehicleChecks;
	}
		
	/**
	 * Reads nodes from XML file.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readFeed(XmlPullParser parser, ArrayList<VehicleCheck> vehicleChecks)
			throws XmlPullParserException, IOException
	{
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			// Check if we found a tag
			if (eventType == XmlPullParser.START_TAG) 
			{
				// Get name of the tag
				String nodeName = parser.getName();
				
				// Read Module
				if(nodeName.contentEquals("check")) 
				{
					VehicleCheck check = new VehicleCheck();
					check.setName(parser.getAttributeValue(0));
					check.setMonths(Integer.parseInt(parser.getAttributeValue(1)));
					check.setDescription(parser.getAttributeValue(2));
					
					vehicleChecks.add(check);
					
					readCheckEntry(parser, nodeName, check);
				}
			} 
			
			eventType = parser.next();
		}
	}
	
	/**
	 * Reads check node
	 * @param parser
	 * @param subtag
	 * @param vehicleCheck
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void readCheckEntry(XmlPullParser parser, String subtag, VehicleCheck vehicleCheck)
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

			// Read menu entry
			if (nodeName.contentEquals("item")) 
			{
				String[] item = new String[2];
				item[0] = parser.getAttributeValue(0);
				item[1] = parser.getAttributeValue(1);
				
				vehicleCheck.getItems().add(item);
			}
			else 
				XMLUtilities.skip(parser);
		}
	}
}
