package com.yinong.tetris;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapButton {
	
	Rect rect;
	Bitmap bitmap;
	boolean show=true;
	
	public BitmapButton(Bitmap bitmap,Rect rect) {
		this.rect = rect;
		this.bitmap = bitmap;
	}

	void draw(Canvas canvas,Paint paint) {
		Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		canvas.drawBitmap(bitmap, src, rect, paint);		
	}
	
	public boolean contains(int x,int y) {
		return rect.contains(x,y);
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
}
