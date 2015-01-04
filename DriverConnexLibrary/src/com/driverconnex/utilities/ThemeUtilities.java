package com.driverconnex.utilities;

import com.driverconnex.app.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.TextView;

/**
 * Utility functions to deal with theme. 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 */

public class ThemeUtilities 
{
	/**
	 * Gets main interface colour of the app.
	 * @param context
	 * @return
	 */
	public static int getMainInterfaceColor(Context context) 
	{
		int[] attrs = new int[]{R.attr.main_interface};
		TypedArray typedArray = context.obtainStyledAttributes(attrs);
		
		return typedArray.getInt(0, 0);
	}
	
	/**
	 * Gets resource of the main and default text colour. You need to use getResources().getColor().
	 * @return
	 */
	public static int getMainTextColor() {
		return android.R.color.primary_text_light;
	}
	
	/**
	 * Gets resource of red text colour. You need to use getResources().getColor().
	 * @return
	 */
	public static int getRedTextColor() {
		return android.R.color.holo_red_light;
	}
	
	/**
	 * Gets a red colour from the resource.
	 * @param context
	 * @return
	 */
	public static int getRedTextColor(Context context) {
		return context.getResources().getColor(android.R.color.holo_red_light);
	}
	
	/**
	 * Gets resource of green text colour. You need to use getResources().getColor().
	 * @return
	 */
	public static int getGreenTextColor() {
		return android.R.color.holo_green_light;
	}
	
	/**
	 * Changes alert dialog theme to match the theme of the app.
	 * @param context
	 * @param dialog
	 */
	public static void changeDialogTheme(Context context, Dialog dialog) 
	{
		try {
			Resources resources = dialog.getContext().getResources();
			int color = ThemeUtilities.getMainInterfaceColor(context);

			int alertTitleId = resources.getIdentifier("alertTitle", "id","android");
			TextView alertTitle = (TextView) dialog.getWindow().getDecorView().findViewById(alertTitleId);
			alertTitle.setTextColor(color);

			int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
			View titleDivider = dialog.getWindow().getDecorView().findViewById(titleDividerId);
			titleDivider.setBackgroundColor(color);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
