package com.yinong.tetris.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;

import com.yinong.tetris.R;
import com.yinong.tetris.model.Block;
import com.yinong.tetris.model.BlockDot;
import com.yinong.tetris.model.ScoreCallout;
import com.yinong.tetris.model.TetrisGame;

public class TetrisRenderer {
	TetrisGame game;
	Context context;
	Bitmap gameOver;
	Bitmap led;
	Bitmap lego;
	Bitmap calloutImage;
	CellRenderer cellRenderer;
	
	int offsetX;
	int offsetY;
	int width;
	int height;
	
	public TetrisRenderer(TetrisGame game,Context context) {
		this.game = game;
		this.context = context;
		
		gameOver = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over);
		led = BitmapFactory.decodeResource(context.getResources(), R.drawable.led);
		lego = BitmapFactory.decodeResource(context.getResources(), R.drawable.lego);
		calloutImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.call_out);
		cellRenderer = new ImageCellRenderer(lego);
//		cellRenderer = new PlainCellRenderer();
	}
	
	public synchronized void draw(Canvas canvas,Paint paint,int offsetX,int offsetY,int width,int height) {
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		
		drawBackground(canvas, paint, offsetX, offsetY, width, height);
		Block activeBlock = game.getActiveBlock();
		paint.setColor(activeBlock.getColor());
		
		//	Active block
		drawBlock(activeBlock,canvas, paint, offsetX,offsetY,width,height);
		
		//	Next block
		drawBlock(game.getNextBlock(),canvas, paint, offsetX + width +2,offsetY+20,width/3,height/3);
		
		//	Draw non-moving cells
		for(BlockDot block:game.getBlocks()) {
			if( block.getMoving() == 0 )
				drawBlock(block,canvas,paint,offsetX,offsetY,width,height);
		}	

		//	Draw moving cells
		for(BlockDot block:game.getBlocks()) {
			if( block.getMoving() > 0 ) {
				int delta = (int)(block.getMoving() * height/game.getRows()); 
				drawBlock(block,canvas,paint,offsetX,offsetY-delta,width,height);
			}
		}	
		
		//	Draw deleted cells
		for(BlockDot block:game.getDeletedBlocks()) {
			if( block.getMoving() > 0 ) {
				float moving = block.getMoving();
				int delta = (int)(moving * height/game.getRows()); 
				drawBlock(block,canvas,paint,offsetX,offsetY-delta,width,height,moving>1f?1:moving);
			}
		}	
		
		
		drawScore(canvas,0,30,paint);
		
		
		drawCallout(game.getCallout(),canvas,paint);
		
		FontMetrics fm = new FontMetrics();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.getFontMetrics(fm);
		//canvas.drawText(game.getErrorString(), 10, 100 -(fm.ascent + fm.descent) / 2, paint);	
		
		//	Draw game over
		if( game.isGameOver() ) {
			Xfermode oldMode = paint.getXfermode();
			
			paint.setColor(Color.GRAY);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
			canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),paint);
			
			paint.setXfermode(oldMode);
			Rect src = new Rect(0,0,gameOver.getWidth(),gameOver.getHeight());
			Rect dest = new Rect(150,300,450,500);
			canvas.drawBitmap(gameOver, src, dest, paint);
		}
	}

	public void drawBlock(Block block,Canvas canvas, Paint paint, int offsetX, int offsetY,
			int width, int height) {
		drawBlock(block,canvas, paint, offsetX, offsetY,
				width, height,1f);
	}
	
	public void drawCallout(ScoreCallout callout,Canvas canvas, Paint paint) {
		if( callout == null || !callout.isAlive() )
			return;
		String score = String.valueOf(callout.getScore());
		LedStringRenderer ledRenderer = new LedStringRenderer(led);
		int textHeight = ledRenderer.getCharacterHeight(15);
		int textWidth = score.length()*20;
		
		Rect src = new Rect(0,0,calloutImage.getWidth(),calloutImage.getHeight());
		Rect dest = new Rect(offsetX + callout.getX()*width/game.getColumns(),
				offsetY + callout.getY()*height/game.getRows(),
				offsetX + callout.getX()*width/game.getColumns() + textWidth + 50,
				offsetY + callout.getY()*height/game.getRows() + textHeight + 80);
		
		canvas.drawBitmap(calloutImage,src,dest,paint);
		
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		ledRenderer.drawString(score, canvas, dest.left, dest.top+35, dest.right, dest.bottom, paint);
		
	}

	
	public void drawBlock(Block block,Canvas canvas, Paint paint, int offsetX, int offsetY,
			int width, int height,float partial) {

		int[] space = block.getSpacesUsed();
		
		paint.setColor(block.getColor());
		
		boolean moving = (block instanceof BlockDot) && ((BlockDot)block).getMoving()>0;
		
		for (int i = 0; i < space.length ; i += 2) {
			if( space[i + 1] <game.getRows() || (space[i + 1] >= game.getRows() && moving)) {
				int x = space[i];
				int y = space[i+1];
				
				x = offsetX+x*width/game.getColumns();
				y = offsetY+y*height/game.getRows();
				cellRenderer.drawCell(canvas, paint, x, y, width/game.getColumns(), height/game.getRows(), partial);
			}
		}
	}	

	
	public Rect getUnitRect(int x,int y,int offsetX,int offsetY,int width,int height) {
		return new Rect(offsetX+x*width/game.getColumns()+2,offsetY+y*height/game.getRows()+2,
				offsetX+(x+1)*width/game.getColumns()-2,offsetY+(y+1)*height/game.getRows()-2);
	}
	
	void drawScore(Canvas canvas,int x,int y,Paint paint) {
		String score = String.valueOf(game.getScore());
//		paint.setColor(Color.GREEN);
//		paint.setTextSize(20);
//		canvas.drawText("Score: " + game.getScore(),300,50,paint);
		LedStringRenderer ledRenderer = new LedStringRenderer(led);
		paint.setTextAlign(Align.RIGHT);
		paint.setTextSize(20);
		ledRenderer.drawString(String.valueOf(game.getScore()), canvas, 60, 30, 535, 50, paint);
		
		paint.setTextAlign(Align.LEFT);
		paint.setTextSize(20);
		ledRenderer.drawString(String.valueOf(game.getHighScore()), canvas, 60, 30, 535, 50, paint);
	}
	
	void drawBackground(Canvas canvas,Paint paint,int offsetX,int offsetY,int width,int height)  {
		paint.setColor(Color.DKGRAY);

		// draw grid
		int startX = offsetX;
		int startY = offsetY;
		for(int i=0;i<game.getRows();i++) {
			for(int j=0;j<game.getColumns();j++) {		
				BlockDot block = new BlockDot(j,i,Color.DKGRAY);
				drawBlock(block,canvas,paint,offsetX,offsetY,width,height);
			}
		}		
	}	
}
