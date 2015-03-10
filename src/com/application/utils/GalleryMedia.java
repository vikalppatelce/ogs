package com.application.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class GalleryMedia {
		public static Drawable geSingleDrawable(LayerDrawable layerDrawable){
	        int resourceBitmapHeight = 136, resourceBitmapWidth = 153;
	        float widthInInches = 0.9f;
	        int widthInPixels = (int)(widthInInches * ApplicationLoader.getApplication().getResources().getDisplayMetrics().densityDpi);
	        int heightInPixels = widthInPixels * resourceBitmapHeight / resourceBitmapWidth;
	        int insetLeft = 10, insetTop = 10, insetRight = 10, insetBottom = 10;
	        layerDrawable.setLayerInset(1, insetLeft, insetTop, insetRight, insetBottom);     
	        Bitmap bitmap = Bitmap.createBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap);
	        layerDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
	        layerDrawable.draw(canvas);
	        BitmapDrawable bitmapDrawable = new BitmapDrawable(ApplicationLoader.getApplication().getResources(), bitmap);
	        bitmapDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
	        return bitmapDrawable;
	}
		public static Bitmap drawableToBitmap (Drawable drawable) {
		    if (drawable instanceof BitmapDrawable) {
		        return ((BitmapDrawable)drawable).getBitmap();
		    }
		    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(bitmap); 
		    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		    drawable.draw(canvas);
		    return bitmap;
		}
}
