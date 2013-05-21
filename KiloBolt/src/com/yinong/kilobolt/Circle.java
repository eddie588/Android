package com.yinong.kilobolt;
import android.graphics.Color;



public class Circle extends Sprite {
	int radius =10;
	int color;

	public Circle(int x,int y,int color) {
		setCenterX(x);
		setCenterY(y);
		setSpeedX(0);
		setSpeedY(1);
		this.color = color;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public void update() {
		if( getCenterY() + getSpeedY() >= getCanvasHeight()-radius )  {
			setCenterY(getCanvasHeight()-radius);
			setSpeedY(-getSpeedY());
		}
		else if ( getCenterY() + getSpeedY() <= radius ) {
			setCenterY(radius);
			setSpeedY(-getSpeedY());			
		}
		else {
			setCenterY(getCenterY() + getSpeedY());
		}


		if( getCenterX() + getSpeedX() >= getCanvasWidth()-radius )  {
			setCenterX(getCanvasWidth()-radius);
			setSpeedX(-getSpeedX());
		}
		else if ( getCenterX() + getSpeedX() <= radius ) {
			setCenterX(radius);
			setSpeedX(-getSpeedX());			
		}
		else {
			setCenterX(getCenterX() + getSpeedX());
		}
	}

}
