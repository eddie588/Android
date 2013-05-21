package com.yinong.tetris.view;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface CellRenderer {
	void drawCell(Canvas canvas,Paint paint,int x,int y,int width,int height,float partial);
}
