package com.yinong.tetris.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class LedStringRenderer {
	Bitmap led;
	
	public LedStringRenderer(Bitmap led) {
		this.led = led; 
	}
	
	
	public void drawString(String string,Canvas canvas,int startX,int startY,int endX,int endY,Paint paint) {
		float ratio = led.getWidth()/10.0f/led.getHeight();
		int size = (int) paint.getTextSize();
		if( paint.getTextAlign() == Align.LEFT)  {
			endX = (int) (startX + size * string.length());
		}
		else if ( paint.getTextAlign() == Align.CENTER ) {
			endX = endX - size - (int)(endX-startX-size*string.length())/2;
		}
		else {
			endX -= (int)size ;
		}
		
		
		for(int i=0;i<string.length();i++) {
			int value = Integer.parseInt(string.substring(string.length()-i-1,string.length()-i));
			if( value == 0 )
				value = 10;
			value--;

			Rect src = new Rect((int)(value*led.getWidth()/10),0,
					(int)((value+1)*led.getWidth()/10),led.getHeight());
			Rect dest = new Rect(endX,startY,endX+size,startY+(int)(size/ratio));
			endX -=size;
			canvas.drawBitmap(led, src, dest, paint);
		}
	}
	
	public int getCharacterHeight(int width) {
		float ratio = led.getWidth()/10.0f/led.getHeight();
		return (int) (width/ratio);
	}
}
