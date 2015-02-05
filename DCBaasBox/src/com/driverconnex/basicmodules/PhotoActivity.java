package com.driverconnex.basicmodules;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;

/**
 * Activity for displaying a photo. It expects to receive URI object from which it will extract bitmap.
 * @author Adrian Klimczak
 *
 */

public class PhotoActivity extends Activity 
{
	private ImageView photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		photo = (ImageView) findViewById(R.id.photoImage);
	
		if(getIntent().getExtras() != null)
		{
			Uri imageUri = (Uri) getIntent().getExtras().get("photo");
			boolean isDownloaded = getIntent().getBooleanExtra("imageDownloaded", false);
			
			if(imageUri != null)
			{
				Bitmap bmp = null;
				
				// If image wasn't downloaded then it has to be optimized to avoid OutOfMemoryError.
				if(!isDownloaded)
					bmp = AssetsUtilities.readBitmapFromUriInPortraitMode(this,imageUri, true);
				// Otherwise downloaded image was already optimized, so don't resize it, because it will produce a tiny image
				else 
					bmp = AssetsUtilities.readBitmapFromUriInPortraitMode(this,imageUri);
				
				photo.setImageBitmap(bmp);
			}
		}

		getActionBar().setTitle("Back");
	}
	
	/**
	 * Items on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    if (item.getItemId() == android.R.id.home) 
	    {
	    	// Go to parent activity
	        finish();
	        overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub); 
	        return true;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
}
