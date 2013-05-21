package com.yinong.kilobolt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class SpriteAnimated extends Sprite {
	private Bitmap bitmap;
	private int width;
	private int height;
	private int frameCount;
	private int currentFrame;
	private int fps;
	private long lastUpdate;

	public SpriteAnimated(Bitmap bitmap,int fps,int frameCount) {
		this.bitmap = bitmap;
		this.frameCount = frameCount;
		this.fps = fps;
		width = bitmap.getWidth()/this.frameCount;
		height = bitmap.getHeight();
		currentFrame=0;
		lastUpdate = System.currentTimeMillis();
		setSpeedX(1);
		setSpeedY(0);
	}

	public void update() {
		long now = System.currentTimeMillis();
		if( now - lastUpdate >= 1000/fps ) {
			if( ++currentFrame >= frameCount )
				currentFrame = 0;
			lastUpdate = now;
			setCenterX(getCenterX() + getSpeedX());
			setCenterY(getCenterY() + getSpeedY());				
		}
	
	}
	
	public void draw(Canvas canvas) {
		// where to draw the sprite
		Rect destRect = new Rect(getCenterX()-width/2, getCenterY()-height/2, 
				getCenterX() + width/2, getCenterY() + height/2);
		Rect sourceRect = new Rect(currentFrame*width,0,(currentFrame+1)*width,height);
		
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
//		canvas.drawBitmap(bitmap, 20, 150, null);
//		Paint paint = new Paint();
//		paint.setARGB(50, 0, 255, 0);
//		canvas.drawRect(20 + (currentFrame * destRect.width()), 150, 20
//				+ (currentFrame * destRect.width()) + destRect.width(),
//				150 + destRect.height(), paint);
	}
}
