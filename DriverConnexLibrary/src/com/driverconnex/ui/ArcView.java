package com.driverconnex.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * ArcView used to display rings on the DriverConnex dashboard. 
 * 
 * ArcView has following attributes that can be changed:
 * 
 *  x - X position of the arc
 *  y - Y position of the arc
 *  radius - radius of the arc
 *  angle - angle is how much is drawn of the arc (e.g 180 is arc that covers half of the circle)
 * 
 * @author Adrian Klimczak
 *
 */
public class ArcView extends View
{
	public Paint paint = new Paint();
	
	public float x = 0;
	public float y = 0;
	public float radius;
    public int angle = 0;

	final private RectF oval = new RectF();	
	
	public ArcView(Context context) 
	{
		 super(context);
		 paint.setStyle(Paint.Style.STROKE);
	}
 
	@Override
	protected void onDraw(Canvas canvas) 
	{
		 super.onDraw(canvas);
		 
		 oval.set(x - radius, y - radius, x + radius, y + radius);
         canvas.drawArc(oval, -90, angle, false, paint);
	}
}
