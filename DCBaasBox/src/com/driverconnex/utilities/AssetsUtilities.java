package com.driverconnex.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Utility functions for assets. This class contains functions to read bitmap from byte[] and URI, and some other functions that help to deal with assets. 
 * @author Yin Lee (SGI)
 * @author Adrian Klimczak
 *
 * NOTE:
 * Some functions were created by Yin Lee, he didn't comment them and I haven't used them so I wasn't able to give my comments.
 * Functions that were created by me are commented.
 * Comment by Adrian Klimczak
 */

public class AssetsUtilities 
{
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	public static final String TEMP_PIC_PATH = "driverConnex/PhotoTemp";
	public static final String EXPENSE_PIC_PATH = "driverConnex/ExpensePhoto";
	public static final String VEHICLE_PIC_PATH = "driverConnex/VehiclePhoto";
	public static final String TEMP_PATH = "/driverConnex/temp";
	public static final String DRIVERCONNEX_PATH = "/driverConnex";
	
	private static final int IMAGE_MAX_SIZE = 500000; // 0.5MP
	
	/**
	 * Reads asset file from given name of the file
	 * @param fileName
	 * @return
	 */
	public static String readAssetFile(Context context, String fileName) 
	{
		InputStream file;

		try {
			file = context.getAssets().open(fileName);
			byte[] data = new byte[file.available()];
			file.read(data);
			file.close();
			return new String(data);
		} catch (Exception e) {
		}

		return "";
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type) 
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TEMP_PIC_PATH);
		
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory no matter it exists or not
		if (!mediaStorageDir.exists()) 
		{
			if (!mediaStorageDir.mkdirs()) 
			{
				Log.d("DriverConnex", "Failed to create directory!");
				return null;
			}
		}

		// Create a media file name
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		// .format(new Date());
		
		File mediaFile = null;
		
		if (type == MEDIA_TYPE_IMAGE) 
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_TEMP.jpg");
		} 
		else if (type == MEDIA_TYPE_VIDEO) 
		{
			// mediaFile = new File(mediaStorageDir.getPath() + File.separator
			// + "VID_" + timeStamp + ".mp4");
		} 
		else 
			return null;
		
		
		return mediaFile;
	}

	// Get temp media file if exists
	public static File getOutTempMediaFile() 
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TEMP_PIC_PATH);

		File tempMediaFile = null;
		if (mediaStorageDir.exists()) 
		{
			tempMediaFile = new File(mediaStorageDir.getPath() + File.separator	+ "IMG_TEMP.jpg");
		}

		return tempMediaFile;
	}

	/**
	 *  Cleans temp media folder and its content
	 */
	public static void cleanTempMediaFile() 
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TEMP_PIC_PATH);
		deleteRecursive(mediaStorageDir);
	}

	private static void deleteRecursive(File dir) 
	{
		Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
		
		if (dir.isDirectory()) 
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) 
			{
				File temp = new File(dir, children[i]);
				if (temp.isDirectory()) 
				{
					Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
					deleteRecursive(temp);
				} 
				else 
				{
					Log.d("DeleteRecursive", "Delete File" + temp.getPath());
					boolean b = temp.delete();
					if (b == false) 
					{
						Log.d("DeleteRecursive", "Delete Fail");
					}
				}
			}
		}
		dir.delete();
	}

	public static String relocateTempPhotoForExpense() 
	{
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		File fromDir = new File(Environment.getExternalStorageDirectory(), TEMP_PIC_PATH);
		File toDir = new File(Environment.getExternalStorageDirectory(), EXPENSE_PIC_PATH);

		File from = null;
		File to = null;
		if (fromDir.exists()) {
			from = new File(fromDir.getPath() + File.separator + "IMG_TEMP.jpg");
		} else {
			return null;
		}
 
		if (!toDir.exists()) {
			if (!toDir.mkdirs()) {
				Log.d("DriverConnex", "Failed to create directory!");
				return null;
			}
		}

		to = new File(toDir.getPath() + File.separator + "IMG_" + timeStamp
				+ ".jpg");

		from.renameTo(to);

		return "IMG_" + timeStamp + ".jpg";
	}

	public static boolean savePhotoForVehicle(File photo, String id) 
	{
		File toDir = new File(Environment.getExternalStorageDirectory(),VEHICLE_PIC_PATH);
		File to = null;
		
		if (!toDir.exists()) {
			if (!toDir.mkdirs()) {
				Log.d("DriverConnex", "Failed to create directory!");
				return false;
			}
		}

		to = new File(toDir.getPath() + File.separator + "IMG_" + id + ".png");
		photo.renameTo(to);
		return true;
	}

	/**
	 *  Gets media file using file path and file name if exists
	 * @param filepath
	 * @param filename
	 * @return
	 */
	public static File getOutMediaFile(String filepath, String filename) 
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), filepath);
		File mediaFile = null;
		
		if (mediaStorageDir.exists()) 
			mediaFile = new File(mediaStorageDir.getPath() + File.separator	+ filename);

		return mediaFile;
	}

	/**
	 *  Gets media file from Uri.
	 * @param uri
	 * @return
	 */
	public static File getOutMediaFile(Context context, Uri uri) 
	{
		File mediaFile = null;
		mediaFile = new File(getRealPathFromUri(context, uri));

		return mediaFile;
	}

	/**
	 * Gets bytes of image from Uri
	 * @param uri
	 * @return
	 */
	public static byte[] getBytesFromImage(Context context, Uri uri) 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
		// Get bitmap
		Bitmap bmp = readBitmapFromUriInPortraitMode(context, uri);
		bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        
		// Turn it into bytes
		byte[] bytes = stream.toByteArray(); 
       
		return bytes;
	}
	
	/**
	 * Gets bytes from Bitmap
	 * @param Bitmap bmp
	 * @return
	 */
	public static byte[] getBytesFromBitmap(Bitmap bmp) 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
		byte[] bytes = stream.toByteArray(); 
		
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	/**
	 * Takes Uri of a video and returns video in form of bytes
	 * @param uri
	 * @return
	 */
	public static byte[] getBytesFromVideo(Context context, Uri uri) 
	{
		InputStream inputStream = null;
		
		try {
			inputStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	    int bufferSize = 1024;
	    byte[] buffer = new byte[bufferSize];

	    int len = 0;
	    try 
	    {
			while ((len = inputStream.read(buffer)) != -1) 
			    bytes.write(buffer, 0, len);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		return bytes.toByteArray();
	}
	
	public static Bitmap getScaledBitmap(InputStream is) 
	{
		InputStream in = null;
		try 
		{
			in = is;
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}

			Bitmap b = null;
			in = is;
			if (scale > 1) 
			{
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} 
			else 
			{
				b = BitmapFactory.decodeStream(in);
			}
			in.close();
			return b;
		} catch (IOException e) {
			return null;
		}
	}

	public static void bmpRecycle(Bitmap bmp) 
	{
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
		System.gc();
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) 
	{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * Resizes bitmap
	 * @param bmp
	 * @param x
	 * @return
	 */
	public static Bitmap bmpResize(Bitmap bmp, int x) 
	{
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		
		if (w > x) 
		{
			float sx = (float) x / w;
			Matrix matrix = new Matrix();
			matrix.postScale(sx, sx);
			Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
			return resizeBmp;
		} 
		else 
		{
			return bmp;
		}
	}
	
	/**
	 * Reads bitmap from given Uri. It fixes orientation of the image so that it always is displayed 
	 * in a portrait orientation. As sometimes camera may return rotated image by 90 degrees, this function fixes this problem.
	 * @param context
	 * @param imageURI
	 * @param reduceSize - if true it will reduce size of the image
	 * @return
	 */
	public static Bitmap readBitmapFromUriInPortraitMode(Context context, Uri imageURI, boolean reduceSize) 
	{ 
		// Get bitmap
		//--------------------------------
		Bitmap bm = null; 
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		
		if(reduceSize)
			options.inSampleSize = 5; 
		
		AssetFileDescriptor fileDescriptor = null; 
		
		try { 
			fileDescriptor = context.getContentResolver().openAssetFileDescriptor(imageURI,"r"); 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}	 
		
		finally
		{ 
			try { 
				bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
				fileDescriptor.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		}	 
		//--------------------------------
		
		// Rotate image to fix orientation
		//--------------------------------
		ExifInterface exif = null;
		
		try {
			if(getRealPathFromUri(context,imageURI) != null)
				exif = new ExifInterface(getRealPathFromUri(context,imageURI));
			else
				exif = new ExifInterface(imageURI.getPath());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
       
		int exifOrientation = exif.getAttributeInt(
		        ExifInterface.TAG_ORIENTATION,
		        ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		// Check orientation and choose correct rotation
        switch (exifOrientation) 
        {
        case ExifInterface.ORIENTATION_ROTATE_90:
        	rotate = 90;
        	break; 

        case ExifInterface.ORIENTATION_ROTATE_180:
        	rotate = 180;
        	break;

        case ExifInterface.ORIENTATION_ROTATE_270:
        	rotate = 270;
        	break;
        }
        
        // Check if image has to be rotated, if so reproduce bitmap and rotate it
        if (rotate != 0) 
        {
        	int w = bm.getWidth();
        	int h = bm.getHeight();

        	//Setting pre rotate
        	Matrix mtx = new Matrix();
        	mtx.preRotate(rotate);

        	// Rotating Bitmap & convert to ARGB_8888, required by tess
        	bm = Bitmap.createBitmap(bm, 0, 0, w, h, mtx, false);
        	bm = bm.copy(Bitmap.Config.ARGB_8888, true);
        }

        return bm; 
	}
	
	/**
	 * Reads bitmap from given Uri. It fixes orientation of the image so that it always is displayed 
	 * in a portrait orientation.
	 * @param imageURI
	 * @return
	 */
	public static Bitmap readBitmapFromUriInPortraitMode(Context context, Uri imageURI) 
	{ 
		// Doesn't reduce size of the image by default
		return readBitmapFromUriInPortraitMode(context, imageURI, false); 
	} 
	
	/**
	 * Reads bitmap from given Uri
	 * @param uri
	 * @return
	 */
	public static Bitmap readBitmapFromUri(Context context, Uri uri) 
	{ 
		Bitmap bm = null; 
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		AssetFileDescriptor fileDescriptor = null; 
		
		try { 
			fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri,"r"); 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}	 
		
		finally
		{ 
			try { 
				bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
				fileDescriptor.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		}	 

        return bm; 
	} 
	
	/**
	 * Reads bitmap from bytes
	 * @param bytes
	 * @return
	 */
	public static Bitmap readBitmap(byte[] data)
	{	 
		if(data != null)
		{
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			return bmp;
		}
		
		return null;
	}
	
	/**
	 * Reads bitmap from byte[] and returns bitmap in requested size
	 * @param data
	 * @param width - of requested bitmap
	 * @param height - of requested bitmap
	 * @return
	 */
	public static Bitmap readBitmap(byte[] data, int width, int height)
	{
    	Bitmap tempBmp = null;
		Options options = null;
		
		try {
			InputStream is = new ByteArrayInputStream(data);
			options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Config.ARGB_8888;
			options.inSampleSize = 5; 
			tempBmp = BitmapFactory.decodeStream(is, null,options);
			options.inSampleSize = AssetsUtilities.calculateInSampleSize(options, width, height);
			options.inJustDecodeBounds = false;
			options.inBitmap = tempBmp;
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			InputStream is = new ByteArrayInputStream(data);
			tempBmp = BitmapFactory.decodeStream(is, null, options);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return tempBmp;
	}
	
	/**
	 * Read bitmap for the purpose of vehicle
	 * @param data
	 * @return
	 */
	public static Bitmap readBitmapVehicle(byte [] data)
	{
		Bitmap tempBmp = null;
		Options tempBmpOptions = null;
		
		try {
			InputStream is = new ByteArrayInputStream(data);
			tempBmpOptions = new BitmapFactory.Options();
			tempBmpOptions.inJustDecodeBounds = true;
			tempBmpOptions.inPreferredConfig = Config.ARGB_8888;
			tempBmp = BitmapFactory.decodeStream(is, null, tempBmpOptions);
			tempBmpOptions.inSampleSize = AssetsUtilities.calculateInSampleSize(
					tempBmpOptions, 80, 80);
			tempBmpOptions.inJustDecodeBounds = false;
			tempBmpOptions.inBitmap = tempBmp;
			is.close();
			
			try {
				is = new ByteArrayInputStream(data);
				tempBmp = BitmapFactory.decodeStream(is, null, tempBmpOptions);
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return tempBmp;
	}
	
	/**
	 * Saves incident video to a local storage
	 * @param data
	 * @return File
	 */
	public static File saveIncidentVideo(Context context, byte[] data) 
	{
		File myFile = new File(context.getFilesDir() + "/" + "incident_video.mp4");
		myFile.setReadable(true,false);
		
		try 
	    {
	    	FileOutputStream fOut = context.openFileOutput("incident_video.mp4",  Context.MODE_WORLD_READABLE);    	
	    	fOut.write(data);
	    	fOut.flush();
	    	fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return myFile;
	}
	
	
	/**
	 * Saves incident photos to a local storage
	 * @param data - ArrayList of incident photos in form of byte[]
	 * @return
	 */
	public static ArrayList<File> saveIncidentPhoto(Context context, ArrayList<byte[]> data) 
	{
		ArrayList<File> files = new ArrayList<File>();
		
		for(int i=0; i<data.size(); i++)
		{
			File file = context.getFileStreamPath("incident_photo" + i + ".jpeg");
			file.setReadable(true,false);
			files.add(file);
			
			try 
		    {
		    	FileOutputStream fOut = context.openFileOutput("incident_photo" + i + ".jpeg",
		    			Context.MODE_WORLD_READABLE);    	
		    	fOut.write(data.get(i));
		    	fOut.flush();
		    	fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return files;
	}
	
	/**
	 * Gets real path from the URI
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromUri(Context context, Uri contentUri) 
	{
		  Cursor cursor = null;
		  try 
		  {
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    
		    if(cursor == null)
		    	return null;
		    
		    if(cursor.getColumnIndex(MediaStore.Images.Media.DATA) != -1)
		    {
		    	int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);	
			    cursor.moveToFirst();
			    return cursor.getString(columnIndex);
		    }
		    
		    return null;
		  } 
		  finally 
		  {
		    if (cursor != null) 
		      cursor.close();  
		  }
	}
}
