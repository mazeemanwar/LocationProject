package com.driverconnex.basicmodules;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Activity for displaying an alert. 
 * 
 * @author Adrian Klimczak
 * 
 */

public class VehicleAlertActivity extends Activity 
{
	private TextView descriptionText;
	private TextView vehicleModelText;
	private TextView vehicleRegNumberText;
	private ImageView vehicleImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);

		descriptionText = (TextView) findViewById(R.id.descriptionText);
		vehicleModelText = (TextView) findViewById(R.id.modelTextView);
		vehicleRegNumberText = (TextView) findViewById(R.id.regNumberText);
		vehicleImage = (ImageView) findViewById(R.id.photoEdit);
		
		if(getIntent().getExtras() != null)
		{
			Bundle extras = getIntent().getExtras();
			
			String descriptionStr = extras.getString("description");
			String vehicleId = extras.getString("vehicleId");
			String modelStr = extras.getString("model");
			String regNumberStr = extras.getString("regNumber");
			
			if(descriptionStr != null)
				descriptionText.setText(descriptionStr);
			if(modelStr != null)
				vehicleModelText.setText(modelStr);
			if(regNumberStr != null)
				vehicleRegNumberText.setText(regNumberStr);				
			
			// Get Image of the vehicle
			ParseQuery<ParseObject> query = ParseQuery.getQuery("DCVehicle");
			query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			query.getInBackground(vehicleId, new GetCallback<ParseObject>() 
			{
				public void done(ParseObject vehicle, ParseException e) 
				{
					if (e == null) 
					{	
						// Get photo from the parse
						ParseFile photo = (ParseFile) vehicle.get("vehiclePhoto");
						byte[] data = null;
						
						try 
						{
							if (photo != null) 
								data = photo.getData();
						} 
						catch (ParseException e1) {
							e1.printStackTrace();
						}
						
						if (data != null) 
						{
							Bitmap bmp = AssetsUtilities.readBitmapVehicle(data);
							vehicleImage.setImageBitmap(bmp);
						} 
					}
					else
						Log.e("get vehicle", e.getMessage());
				}
			});
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main, R.anim.slide_right_sub);

			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
