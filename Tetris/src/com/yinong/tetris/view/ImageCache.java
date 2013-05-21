package com.yinong.tetris.view;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ImageCache extends LruCache<String,Bitmap> {
	private static ImageCache imageCache = new ImageCache();

	public static ImageCache getInstance() {
		return imageCache;
	}
	
	private ImageCache() {
		super(4);
	}
	

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		// TODO Auto-generated method stub
		return bitmap.getByteCount() / 1024;
	}
	
	public void addBitmap(int resourceID,Bitmap bitmap) {
		put(String.valueOf(resourceID),bitmap);
	}
	
	public Bitmap getBitmap(int resourceID) {
		return get(String.valueOf(resourceID));
	}
}
