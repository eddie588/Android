package com.yinong.kilobolt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Robot {
	// In Java, Class Variables should be private so that only its methods can
	// change them.
	private int centerX = 40;

	private int centerY = 100;
	private boolean jumped = false;

	private int speedX = 0;
	private int speedY = 1;
	private Bitmap image;
	private int canvasWidth = 0;
	private int canvasHeight = 0;
	
	


	public Robot(Bitmap image)
	{
		this.image = image;

	}
	
	public void setCanvasSize(int canvasWidth,int canvasHeight) {
		this.canvasHeight = canvasHeight;
		this.canvasWidth = canvasWidth;		
	}

	public void update() {

		// Moves Character or Scrolls Background accordingly.
		if (speedX < 0) {
			centerX += speedX;
		} else if (speedX == 0) {
			System.out.println("Do not scroll the background.");

		} else {
			if (centerX <= canvasWidth) {
				centerX += speedX;
			} else {
				System.out.println("Scroll Background Here");
			}
		}

		// Updates Y Position

		if (centerY + speedY >= canvasHeight - image.getHeight()/2) {
			centerY = canvasHeight - image.getHeight()/2;
		} else {
			centerY += speedY;
		}

		// Handles Jumping
		if (jumped == true) {
			speedY += 1;

			if (centerY + speedY >= canvasHeight - image.getHeight()/2) {
				centerY = canvasHeight - image.getHeight()/2;
				speedY = 0;
				jumped = false;
			}

		}

		// Prevents going beyond X coordinate of 0
		if (centerX + speedX <= image.getWidth()/2) {
			centerX = image.getWidth()/2+1;
		}
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public void setetImage(Bitmap image) {
		this.image = image;
	}


	public void moveRight() {
		speedX = 6;
	}

	public void moveLeft() {
		speedX = -6;
	}

	public void stop() {
		speedX = 0;
	}

	public void jump() {
		if (jumped == false) {
			speedY = -15;
			jumped = true;
		}

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

	public boolean isJumped() {
		return jumped;
	}

	public void setJumped(boolean jumped) {
		this.jumped = jumped;
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

}
