package com.driverconnex.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.driverconnex.utilities.AssetsUtilities;

/**
 * @author Yin Lee (SGI)
 *
 *	NOTE:
 *	This is replaced by XMLBasicConfigParser
 *	comment by Adrian Klimczak
 */

public class BasicConfigParser {
	
	private static final String path = "BasicConfig.json";
	private Context context;
	
	public BasicConfigParser(Context context) {
		super();
		this.context = context;
	}

	public ArrayList<MenuListItems> menuParser() throws Exception 
	{
		try {
			JSONObject basicConfig = new JSONObject(AssetsUtilities.readAssetFile(context, path));
			ArrayList<MenuListItems> menuItems = new ArrayList<MenuListItems>();
			JSONObject menuObj = basicConfig.getJSONObject("Menu Items");
			//	Tablet or phone
			JSONArray itemArr = menuObj.getJSONArray("phone");
			for (int i = 0; i < itemArr.length(); i++) {
				MenuListItems items = new MenuListItems();
				ArrayList<MenuListItem> itemList = new ArrayList<MenuListItem>();
				JSONObject itemsObj = itemArr.getJSONObject(i);
				items.setName(itemsObj.getString("Section Name"));
				boolean isSectionEnabled = false;
				if (itemsObj.has("Enabled")) {
					items.setEnabled(itemsObj.getBoolean("Enabled"));
					isSectionEnabled = itemsObj.getBoolean("Enabled");
				}
				if (isSectionEnabled) {
					JSONArray arr = itemsObj.getJSONArray("Sub Items");
					for (int j = 0; j < arr.length(); j++) {
						MenuListItem item = new MenuListItem();
						JSONObject itemObj = arr.getJSONObject(j);
						item.setName(itemObj.getString("Item Name"));
						boolean isItemEnabled = false;
						if (itemsObj.has("Enabled")) {
							item.setEnabled(itemObj.getBoolean("Enabled"));
							isItemEnabled = itemObj.getBoolean("Enabled");	
						}
						if (!isItemEnabled) {
							continue;
						}	
						if (itemObj.has("Email")) {
							item.setEmail(itemObj.getString("Email"));
						}
						if (itemObj.has("Image Name")) {
							item.setIcon(itemObj.getString("Image Name"));
						}
						if (itemObj.has("Storyboard ID")) {
							item.setStoryboardId(itemObj.getString("Storyboard ID"));
						}
						if (itemObj.has("Storyboard Name")) {
							item.setStoryboardName(itemObj.getString("Storyboard Name"));
						}
						if (itemObj.has("URL")) {
							item.setUrl(itemObj.getString("URL"));
						}
						item.setHeader(false);
						item.setClassName(itemObj.getString("Item Name"));
				
						itemList.add(item);
					}
					items.setSubitems(itemList);
				} else {
					continue;
				}
				menuItems.add(items);
			}
				
			return menuItems;
		} catch (JSONException e) {
			// TODO: handle exception
			throw new Exception("Failed to parse the basic config file.");
		}
	}
}
