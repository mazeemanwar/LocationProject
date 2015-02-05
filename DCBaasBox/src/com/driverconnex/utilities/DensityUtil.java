package com.driverconnex.utilities;

import android.content.Context;

/**
 * Utility functions to deal with density.
 * @author Yin Lee
 * 
 * NOTE:
 * They are never used.
 * Comment by Adrian Klimczak.
 *
 */

public class DensityUtil 
{
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
