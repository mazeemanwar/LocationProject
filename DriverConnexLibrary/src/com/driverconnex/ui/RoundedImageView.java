package com.driverconnex.ui;

import com.driverconnex.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * It's used to draw rounded images.
 * 
 * @author Adrian Klimczak
 * 
 * 
 */

public class RoundedImageView extends ImageView {
	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		Bitmap b = ((BitmapDrawable) drawable).getBitmap();

		if (b != null) {
			Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
			Bitmap roundBitmap = getCroppedBitmap(bitmap);
			this.setImageBitmap(roundBitmap);
			super.onDraw(canvas);
		}
	}

	public Bitmap getCroppedBitmap(Bitmap bmp) {
		Bitmap sbmp;

		// --------------------------
		// Crop bitmap in the centre
		if (bmp.getWidth() >= bmp.getHeight()) {
			sbmp = Bitmap.createBitmap(bmp,
					bmp.getWidth() / 2 - bmp.getHeight() / 2, 0,
					bmp.getHeight(), bmp.getHeight());
		} else {
			sbmp = Bitmap.createBitmap(bmp, 0,
					bmp.getHeight() / 2 - bmp.getWidth() / 2, bmp.getWidth(),
					bmp.getWidth());
		}
		// --------------------------

		// Scale it to fit the view
		if (bmp.getWidth() != getWidth() || bmp.getHeight() != getWidth())
			sbmp = Bitmap.createScaledBitmap(sbmp, getWidth(), getWidth(),
					false);
		else
			sbmp = bmp;

		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getWidth() / 2,
				sbmp.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}
}