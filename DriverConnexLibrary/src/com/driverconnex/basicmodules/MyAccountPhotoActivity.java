package com.driverconnex.basicmodules;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.driverconnex.app.R;
import com.driverconnex.utilities.AssetsUtilities;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Activity for taking a photo by camera or from an album and passing it to MyAccountActivity. 
 * @author Adrian Klimczak
 *
 */

public class MyAccountPhotoActivity extends Activity 
{
	private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final int GET_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	
	private ImageView photoImg;
	private RelativeLayout bottomActionbar;
	private ImageButton cameraBtn;
	private ImageButton albumsBtn;
	private RelativeLayout loading;
	
	private Uri fileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account_photo);

		photoImg = (ImageView) findViewById(R.id.photoEdit);
		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		albumsBtn = (ImageButton) findViewById(R.id.albumsBtn);
		bottomActionbar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		loading = (RelativeLayout) findViewById(R.id.loadSpinner);
		
		// Check if device has got a camera
		//---------------------------------
		PackageManager pm = this.getPackageManager();
		
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
			cameraBtn.setOnClickListener(onClickListener);
		else
			cameraBtn.setVisibility(View.INVISIBLE);
		//---------------------------------
		
		albumsBtn.setOnClickListener(onClickListener);
		
		// Checks if we got photo from previous screen, if so then display it
		if (getIntent().getExtras() != null)
		{
			byte[] data = getIntent().getExtras().getByteArray("photo");
			
			if(data != null)
			{
				Bitmap bmp = AssetsUtilities.readBitmap(data);
				photoImg.setImageBitmap(bmp);
			}
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

		// Animates bottom action bar
		bottomActionbar.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(this,	R.anim.bottom_actionbar_show);
		bottomActionbar.startAnimation(anim);
	}
	
	private OnClickListener onClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			if (v == cameraBtn) 
			{
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			if (v == albumsBtn) 
			{
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, GET_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 
			    || requestCode == GET_IMAGE_ACTIVITY_REQUEST_CODE) 
		{
			if (resultCode != RESULT_OK)
				return;

			// Check if we got any data
			if (data != null) 
			{
				fileUri = data.getData();
				int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
				
				Bitmap bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmapFromUriInPortraitMode(
						MyAccountPhotoActivity.this,fileUri, true),screenHeight);
				
				if(bmp != null)
					photoImg.setImageBitmap(bmp);	
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		return super.onCreateOptionsMenu(menu);
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
		else if (item.getItemId() == R.id.action_save) 
		{	
			Bitmap bmp = null;
			
			if(((BitmapDrawable)photoImg.getDrawable()) != null)
				bmp = ((BitmapDrawable)photoImg.getDrawable()).getBitmap();
			
			// Check if user took any photo
			if(bmp != null)
			{
				// If so, save this photo in the Parse
				ParseUser user = ParseUser.getCurrentUser();
				
				ParseFile photoFile = new ParseFile(AssetsUtilities.getBytesFromBitmap(bmp), "user.png");
				user.put("userProfilePhoto", photoFile);
				
				loading.setVisibility(View.VISIBLE);
				
				user.saveInBackground(new SaveCallback()
				{
					@Override
					public void done(ParseException e) 
					{
						if(e == null)
						{
							// Once done, it returns to previous activity with the uri of the photo, so that image is updated
							Intent returnIntent = new Intent();
							returnIntent.setData(fileUri);

							setResult(RESULT_OK, returnIntent);
							finish();
							overridePendingTransition(R.anim.slide_right_main,R.anim.slide_right_sub);	
							loading.setVisibility(View.INVISIBLE);
						}
						else 
							Log.e("save user.png", e.getMessage());
					}	
				});
			}	
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
