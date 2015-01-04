package com.driverconnex.utilities;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Utility functions to deal with XML files.
 * @author Adrian Klimczak
 *
 */

public class XMLUtilities 
{
	/**
	 * Skips an entry.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException 
	{
		if (parser.getEventType() != XmlPullParser.START_TAG) 
			throw new IllegalStateException();
		
		int depth = 1;
		while (depth != 0) 
		{
			switch (parser.next()) 
			{
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
	
	/**
	 * Checks if given attribute exists.
	 * @param parser
	 * @param attribute
	 * @return
	 */
	public static boolean has(XmlPullParser parser, String attribute)
	{
		for(int i=0; i<parser.getAttributeCount(); i++)
		{
			if(parser.getAttributeName(i).equals(attribute))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the item is enabled
	 * @param parser
	 * @return
	 */
	public static boolean isEnabled(XmlPullParser parser)
	{
		for(int i=0; i<parser.getAttributeCount(); i++)
		{
			if(parser.getAttributeName(i).equals("enabled"))
			{
				String enabled = parser.getAttributeValue(i);
				enabled = enabled.toLowerCase();
				
				if(enabled.equals("true"))
					return true;
				else 
					return false;
			}
		}

		return false;
	}
	
	/**
	 * Gets the position of the attribute in the node.
	 * @param parser
	 * @param attribute
	 * @return
	 */
	public static int getIndex(XmlPullParser parser, String attribute)
	{
		for(int i=0; i<parser.getAttributeCount(); i++)
		{
			if(parser.getAttributeName(i).equals(attribute))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Gets value from the attribute.
	 * @param parser
	 * @return
	 */
	public static String getValue(XmlPullParser parser, String attribute)
	{
		if(getIndex(parser,attribute) >= 0)
			return parser.getAttributeValue(getIndex(parser,attribute));
		
		return null;
	}
}
