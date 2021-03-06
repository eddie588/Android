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
import com.yinong.tetris.model.GameStats;
import com.yinong.tetris.model.Position;
import com.yinong.tetris.model.ScoreCallout;
import com.yinong.tetris.model.TetrisGame;

public class TetrisRenderer {
	TetrisGame game;
	Context context;
	Bitmap gameOver;
	Bitmap led;
	Bitmap lego;
	Bitmap calloutImage;
	Bitmap blackboardImage;	
	CellRenderer cellRenderer;
	public static int SLOW_DROP = 0;
	public static int NORMAL_DROP = 1;
	
	int offsetX;
	int offsetY;
	int width;
	int height;
	int blockWidth;
	int blockHeight;
	
	public TetrisRenderer(TetrisGame game,Context context) {
		this.game = game;
		this.context = context;
		
		gameOver = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over);
		led = BitmapFactory.decodeResource(context.getResources(), R.drawable.led);
		lego = BitmapFactory.decodeResource(context.getResources(), R.drawable.lego);
		calloutImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.call_out);
		blackboardImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackboard);
		cellRenderer = new ImageCellRenderer(lego);
//		cellRenderer = new PlainCellRenderer();
	}
	
	public synchronized void draw(int drawMode,Canvas canvas,Paint paint,int offsetX,int offsetY,int width,int height) {
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		
		blockWidth = width/game.getColumns();
		blockHeight = height/game.getRows();
		
		drawBackground(canvas, paint);
		Block activeBlock = game.getActiveBlock();
		paint.setColor(activeBlock.getColor());
		
		//	Active block
		drawBlock(activeBlock,canvas, paint);
		
		//	Next block
		int i=0;
		for(Block block:game.getNextBlocks()) {
			drawPreviewBlock(block,canvas, paint, offsetX + width +10,offsetY+30 + i*60,15);
			i++;
		}

		if( drawMode == SLOW_DROP ) {
			drawSlowDroppingDeck(canvas,paint);
		}
		else
			drawNormalDroppingDeck(canvas,paint);
			
		drawScore(canvas,0,30,paint);
		
		
		drawCallout(game.getCallout(),canvas,paint);
		
		FontMetrics fm = new FontMetrics();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.getFontMetrics(fm);
		//canvas.drawText(game.getErrorString(), 10, 100 -(fm.ascent + fm.descent) / 2, paint);	
		
		//	Draw stats
		GameStats gameStats = game.getGameStats();
		if( gameStats.isVisible() ) {
			
			Rect src = new Rect(0,0,blackboardImage.getWidth(),blackboardImage.getHeight());
			Rect dest = new Rect(80,100,380,300);
			int textHight = 25;
			
			canvas.drawBitmap(blackboardImage, src, dest, paint);
			
			paint.setColor(Color.LTGRAY);
			int orgX = 220;
			int orgY = 165;
			canvas.drawText("Score: " + gameStats.getScore() , 
					orgX , orgY, paint);
			canvas.drawText("High Score: " + gameStats.getHighScore()  , 
					orgX , orgY + textHight, paint);
			canvas.drawText("Cleared Rows: " + gameStats.getClearedRows() , 
					orgX , orgY + 2*textHight, paint);
			canvas.drawText("Total Blocks: " + gameStats.getTotalBlocks() , 
					orgX , orgY + 3*textHight, paint);
		}
		
		
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
	
	void drawSlowDroppingDeck(Canvas canvas,Paint paint) {
		//	Draw non-moving cells
		for(BlockDot block:game.getBlocks()) {
			if( block.getMoving() == 0 )
				drawBlock(block,canvas,paint);
		}	

		//	Draw moving cells
		for(BlockDot block:game.getBlocks()) {
			if( block.getMoving() > 0 ) {
				int delta = (int)(block.getMoving() * blockHeight); 
				drawBlock(block,canvas,paint,delta,1);
			}
		}	
		
		//	Draw deleted cells
		for(BlockDot block:game.getDeletedBlocks()) {
			if( block.getMoving() > 0 ) {
				int delta = (int)(block.getMoving() * blockHeight); 
				drawBlock(block,canvas,paint,delta,block.getMoving()>1?1:block.getMoving());
			}
		}	
	}
	
	void drawNormalDroppingDeck(Canvas canvas,Paint paint) {
		//	Draw non-moving cells
		for(BlockDot block:game.getBlocks()) {
			drawBlock(block,canvas,paint);
		}	
	}	

	
	public void drawCallout(ScoreCallout callout,Canvas canvas, Paint paint) {
		if( callout == null || !callout.isAlive() )
			return;
		String score = String.valueOf(callout.getScore());
		LedStringRenderer ledRenderer = new LedStringRenderer(led);
		int textHeight = ledRenderer.getCharacterHeight(15);
		int textWidth = score.length()*20;
		
		Rect src = new Rect(0,0,calloutImage.getWidth(),calloutImage.getHeight());
		Rect dest = new Rect(offsetX + callout.getX()*blockWidth,
				offsetY + callout.getY()*blockHeight,
				offsetX + callout.getX()*blockWidth + textWidth + 50,
				offsetY + callout.getY()*blockHeight + textHeight + 80);
		
		canvas.drawBitmap(calloutImage,src,dest,paint);
		
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		ledRenderer.drawString(score, canvas, dest.left, dest.top+35, dest.right, dest.bottom, paint);
		
	}

	public void drawBlock(Block block,Canvas canvas, Paint paint) {
		drawBlock(block,canvas, paint,0,1f);
	}
	
	public void drawBlock(Block block,Canvas canvas, Paint paint, int deltaY,float partial) {

		Position[] spaces = block.getSpacesUsed();
		
		paint.setColor(block.getColor());
		
		boolean moving = (block instanceof BlockDot) && ((BlockDot)block).getMoving()>0;
		
		for (int i = 0; i < spaces.length ; i++) {
			if( spaces[i].y <game.getRows() || (spaces[i].y >= game.getRows() && moving)) {
				int x = spaces[i].x;
				int y = spaces[i].y;
				
				x = offsetX + x*blockWidth;
				y = offsetY + y*blockWidth - deltaY;

				cellRenderer.drawCell(canvas, paint, x, y, blockWidth, blockHeight, partial);
			}
		}
	}	
	
	public void drawPreviewBlock(Block block, Canvas canvas, Paint paint,
			int offsetX, int offsetY, int cellSize) {

		Position[] spaces = block.getSpaces(0, 0, 0);

		paint.setColor(block.getColor());

		for (int i = 0; i < spaces.length; i++) {
			int x = spaces[i].x;
			int y = spaces[i].y;

			x = offsetX + x * cellSize;
			y = offsetY + y * cellSize;
			cellRenderer.drawCell(canvas, paint, x, y, cellSize, cellSize, 1);
		}
	}	

	
	public Rect getUnitRect(int x,int y,int offsetX,int offsetY,int width,int height) {
		return new Rect(offsetX+x*blockWidth+2,offsetY+y*blockHeight+2,
				offsetX+(x+1)*blockWidth-2,offsetY+(y+1)*blockHeight-2);
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
	
	void drawBackground(Canvas canvas,Paint paint)  {
		paint.setColor(Color.DKGRAY);

		// draw grid
		int startX = offsetX;
		int startY = offsetY;
		for(int i=0;i<game.getRows();i++) {
			for(int j=0;j<game.getColumns();j++) {		
				BlockDot block = new BlockDot(j,i,Color.DKGRAY);
				drawBlock(block,canvas,paint);
			}
		}		
	}	
}
