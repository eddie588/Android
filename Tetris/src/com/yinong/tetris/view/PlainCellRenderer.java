package com.yinong.tetris.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class PlainCellRenderer implements CellRenderer {
	
	@Override
	public void drawCell(Canvas canvas, Paint paint, int x, int y, int cellWidth,
			int cellHeight, float partial) {
		Rect dest = new Rect(x+2,y+2,x + cellWidth-2, y+(int)(cellHeight*partial)-2);

		canvas.drawRect(dest, paint);
	}
	
}
