package com.yinong.kilobolt;

import android.graphics.Bitmap;

public class Heliboy extends Enemy {
	
	private Bitmap image;

	public Heliboy(Bitmap image,int centerX,int centerY) {
		setCenterX(centerX);
		setCenterY(centerY);
	}

}
