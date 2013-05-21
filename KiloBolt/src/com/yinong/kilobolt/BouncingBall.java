package com.yinong.kilobolt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BouncingBall {
	static float ACCELERATION = 0.5f;
	float speedY=0;
	int x;
	int y;
	long lastUpdate;
	int height;
	int radius = 10;
	
	public BouncingBall() {
		lastUpdate = System.currentTimeMillis();
	}
	
	public float getSpeedY() {
		return speedY;
	}
	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void update() {
		long now = System.currentTimeMillis();
		speedY += ACCELERATION;		
		if( y + speedY >= height -radius ) {
			y = height-radius;
			speedY = -(speedY+ACCELERATION);
		}
		else {
			y = y+(int)speedY;
		}
	}
	
	public void draw(Canvas canvas,Paint p) {
		p.setColor(Color.RED);
		canvas.drawCircle(x, y, radius, p);
	}

}
