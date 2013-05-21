package com.yinong.kilobolt;

import android.graphics.Bitmap;

public abstract class  Sprite  {
	private int centerX = 0;
	private int centerY = 0;	
	private int speedX = 0;
	private int speedY = 1;
	private Bitmap image;
	private int canvasWidth = 0;
	private int canvasHeight = 0;	
	
	public Sprite() {
		// TODO Auto-generated constructor stub
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public int getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public abstract void update();

}
