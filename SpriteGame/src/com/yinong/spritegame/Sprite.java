package com.yinong.spritegame;

import android.graphics.Point;
import android.graphics.Rect;

public class Sprite {
	private Point position;
	private Point velocity; 
	private Rect bound;
	
	public Sprite()
	{
		position = new Point(-1,-1);
		velocity = new Point(1,1);
		bound = new Rect(0,0,50,50);
	}

	synchronized public Rect getBound() {
		return bound;
	}
	synchronized public void setBound(Rect bound) {
		this.bound = bound;
	}
	synchronized public Point getPosition() {
		return position;
	}
	synchronized public void setPosition(Point position) {
		this.position = position;
	}
	synchronized public Point getVelocity() {
		return velocity;
	}
	synchronized public void setVelocity(Point velocity) {
		this.velocity = velocity;
	}
	
	synchronized public int getWidth() {
		return bound.width();
	}
	
	synchronized public int getHeight() {
		return bound.height();
	}
	
	synchronized public int getX() {
		return position.x;
	}
	
	synchronized public int getY() {
		return position.y;
	}

}
