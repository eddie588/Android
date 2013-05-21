package com.yinong.tetris.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageCellRenderer implements CellRenderer {
	Bitmap image;
	
	public ImageCellRenderer(Bitmap image) {
		this.image = image;
	}

	@Override
	public void drawCell(Canvas canvas, Paint paint, int x, int y, int cellWidth,
			int cellHeight, float partial) {
		int imageIndex = getImageIndex(paint.getColor());
		Rect src = new Rect(imageIndex*image.getWidth()/7,0,(imageIndex+1)*image.getWidth()/7,(int)(image.getHeight()*partial));
		Rect dest = new Rect(x+2,y+2,x + cellWidth-2, y+(int)(cellHeight*partial)-2);

		canvas.drawBitmap(image, src, dest, paint);
	}
	
	int getImageIndex(int color) {
		int imageIndex=0;
		switch(color) {
			case Color.RED:
				imageIndex = 0;
				break;
			case Color.BLUE:
				imageIndex = 1;
				break;
			case Color.MAGENTA:
				imageIndex = 2;
				break;
			case Color.YELLOW:
				imageIndex = 3;
				break;
			case Color.GREEN:
				imageIndex = 4;
				break;
			case Color.CYAN:
				imageIndex = 5;
				break;
			case Color.DKGRAY:
				imageIndex = 6;
				break;
			default: 
				imageIndex = 0;
				
		}
		return imageIndex;
	}	
}
