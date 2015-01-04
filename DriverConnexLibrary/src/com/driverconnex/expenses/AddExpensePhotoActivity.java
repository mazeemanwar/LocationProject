package com.driverconnex.expenses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

/**
 * Activity for adding a photo for the expense.
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 */
public class AddExpensePhotoActivity extends Activity 
{
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int GET_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	
	private ImageView photoImg;
	private RelativeLayout bottomActionbar;
	private ImageButton cameraBtn, albumsBtn;

	private MenuItem saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo);

		photoImg = (ImageView) findViewById(R.id.photo);
		bottomActionbar = (RelativeLayout) findViewById(R.id.bottomActionBar);
		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		albumsBtn = (ImageButton) findViewById(R.id.albumsBtn);

		// Check if device has got a camera
		//---------------------------------
		PackageManager pm = this.getPackageManager();
		
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
			cameraBtn.setOnClickListener(onClickListener);
		else
			cameraBtn.setVisibility(View.INVISIBLE);
		//---------------------------------
		
		albumsBtn.setOnClickListener(onClickListener);

		Bitmap bmp = null;
		
		try 
		{
			File tempFile = AssetsUtilities.getOutTempMediaFile();
			
			// Check if there is a photo already
			if (tempFile != null) 
			{
				bmp = BitmapFactory.decodeStream(new FileInputStream(tempFile));
				photoImg.setImageBitmap(bmp);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		init();
	}

	private void init() 
	{
		if (getIntent().getExtras() != null)
		{
			Bundle extras = getIntent().getExtras();
			String pic = extras.getString("pic_path");
			
			if(pic != null) 
			{
				Bitmap bmp = null;
				
				try 
				{
					File file = AssetsUtilities.getOutMediaFile(AssetsUtilities.EXPENSE_PIC_PATH, pic);
					
					if (file != null) 
					{
						int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
						bmp = AssetsUtilities.bmpResize(BitmapFactory.decodeStream(new FileInputStream(file)), screenHeight);
						photoImg.setImageBitmap(bmp);
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

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
				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				// start the image capture Intent
				startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			else if (v == albumsBtn) 
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
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == GET_IMAGE_ACTIVITY_REQUEST_CODE) 
		{
			if (resultCode != RESULT_OK)
				return;

			Log.e("Capture Photo", "Captured Photo...");
						
			if (data != null) 
			{					
				int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
				Bitmap bmp = AssetsUtilities.bmpResize(AssetsUtilities.readBitmapFromUriInPortraitMode(AddExpensePhotoActivity.this, data.getData(), true), screenHeight);

				if(bmp != null)
					photoImg.setImageBitmap(bmp);					
				
				saveButton.setVisible(true);
			}
			else 
			{
				saveButton.setVisible(false);
				Log.e("Capture Photo", "Failure!");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_save, menu);
		
		// Get a reference to save button from the menu
		saveButton = menu.getItem(0);
		
		// If there is no photo already set, hide save button
		// to prevent user from saving a photo that he has not chosen
		if(photoImg.getDrawable() == null)
			saveButton.setVisible(false);
					
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == android.R.id.home) 
		{
			finish();
			overridePendingTransition(R.anim.slide_right_main,	R.anim.slide_right_sub);
			return true;
		}
		else if (item.getItemId() == R.id.action_save) 
		{
			// Check if user did any photo
			Bitmap bmp = null;
			
			if((BitmapDrawable)photoImg.getDrawable() != null)
				bmp = ((BitmapDrawable)photoImg.getDrawable()).getBitmap();
			
			if(bmp != null)
			{		
				File file = AssetsUtilities.getOutputMediaFile(AssetsUtilities.MEDIA_TYPE_IMAGE);

				// Encode the file as a JPEG image.
				FileOutputStream outStream;
				try {
					outStream = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
					/* 100 to keep full quality of the image */
					outStream.flush();
					outStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(R.anim.slide_right_main,R.anim.slide_right_sub);		
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
