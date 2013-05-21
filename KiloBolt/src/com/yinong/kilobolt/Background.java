package com.yinong.kilobolt;

import android.graphics.Bitmap;

public class Background {
	
	private int bgX;
	private int bgY;
	private int speedX;
	private int canvasWidth = 0;
	private int canvasHeight = 0;
	Bitmap image;
	


	public Background(Bitmap image,int x,int y) {
		bgX = x;
		bgY = y;
		speedX = -1;
		this.image = image;
	}
	
	public void update() {
		bgX += speedX;

		if (bgX <= -image.getWidth()) {
			bgX += 2*image.getWidth();
		}
	}
	
	public void setCanvasSize(int canvasWidth,int canvasHeight) {
		this.canvasHeight = canvasHeight;
		this.canvasWidth = canvasWidth;		
	}

	public int getBgX() {
		return bgX;
	}

	public void setBgX(int bgX) {
		this.bgX = bgX;
	}

	public int getBgY() {
		return bgY;
	}

	public void setBgY(int bgY) {
		this.bgY = bgY;
	}

	public Bitmap getImage() {
		return image;
	}
		
}
