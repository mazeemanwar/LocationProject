package com.driverconnex.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.driverconnex.utilities.AssetsUtilities;

import android.content.Context;
import android.util.Log;


/**
 * @author Yin Lee (SGI)
 *
 *	NOTE:
 *	This is replaced by XMLModuleConfigParser
 *	comment by Adrian Klimczak
 */
public class ModuleConfigParser {

	private static final String path = "ModulesConfig.json";
	private Context context;

	public ModuleConfigParser(Context context) {
		super();
		this.context = context;
	}

	
	/**
	 * Reads the modules config file and Returns ArrayList of MenuListItems 
	 * @return ArrayList<MenuListItems>
	 * @throws Exception
	 */
	public ArrayList<MenuListItems> menuParser() throws Exception {
		try {
			JSONObject basicConfig = new JSONObject(AssetsUtilities.readAssetFile(context,path));
			ArrayList<MenuListItems> menuItems = new ArrayList<MenuListItems>();
			JSONArray modules = basicConfig.getJSONArray("Modules");
			for (int m = 0; m < modules.length(); m++) {
				JSONObject menuObj = null;
				if (modules.getJSONObject(m).has("Menu Items")) {
					menuObj = modules.getJSONObject(m).getJSONObject("Menu Items");
				} else {
					continue;
				}
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
			}
			Log.i("Module Parser", "Successfully parsed!");
			return menuItems;
			
		} catch (JSONException e) {
			Log.i("Module Parser", "Failed to parse the module config file.");
			throw new Exception("Failed to parse the module config file.");
		}
		
	}
	
	/**
	 * Checks if given module is enabled
	 * @param moduleName - Name of the module
	 * @return true - enabled or false - disabled
	 * @throws Exception
	 */
	public boolean isModuleEnabled(String moduleName) //throws Exception
	{
		JSONObject modulesConfig;
		
		try {
			// Read the file
			modulesConfig = new JSONObject(AssetsUtilities.readAssetFile(context,path));
			JSONArray modules = modulesConfig.getJSONArray("Modules");
			
			JSONObject obj;
			
			// Loop through all modules
			for(int i=0; i<modules.length(); i++)
			{
				// Get module
				obj = modules.getJSONObject(i);
				
				// Check if this is the module we are looking for
				if(obj.getString("Module Name").contains(moduleName))
				{
					// Check if this module is enabled
					if(obj.getBoolean("Enabled"))
						return true;
					else
						return false;
				}
			}
			
		} catch (JSONException e) {
			Log.i("Module Parser", "Failed to read ModulesConfig.json file.");
		}
		
		return false;
	}
}
