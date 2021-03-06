package com.yinong.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.yinong.tetris.model.TetrisCommand;
import com.yinong.tetris.model.TetrisGame;
import com.yinong.tetris.simulation.Simulation1;
import com.yinong.tetris.view.TetrisRenderer;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback ,
	OnGestureListener {

	GameLoop gameLoop ;
	TetrisGame game = new TetrisGame();
	TetrisRenderer gameRenderer=null;
	GestureDetector gestureDetector;	
	
	Simulation1 simu;
	
	Bitmap play;
	Bitmap demo;
	
	static int PLAY_MODE=0;
	static int DEMO_MODE=1;
	
	long lastTouch = System.currentTimeMillis();
	static final int TOUCH_PERIOD = 500;
	
	int mode = PLAY_MODE;
	
	BitmapButton btnStats;

	
	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameRenderer = new TetrisRenderer(game,context);
		SurfaceHolder holder = getHolder();

		play = BitmapFactory.decodeResource(context.getResources(), R.drawable.play);
		demo = BitmapFactory.decodeResource(context.getResources(), R.drawable.demo);
		
		btnStats = new BitmapButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.call_out),
				new Rect(5,60,55,110));
		

		simu = new Simulation1(game);

		holder.addCallback(this);
	}

	public TetrisGame getGame() {
		return game;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		//initGame();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		gestureDetector = new GestureDetector(this);

		setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);				
			}
		});			
		//
		gameLoop = new GameLoop(this,holder);
		
		gameLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		gameLoop.setStop();
		simu.stopSimulation();

	}
	
	void doUpdate() {
		game.update();
	}

	
	
	void  doDraw(Canvas canvas) {

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		
		canvas.drawRect(0, 0,600,1024,paint);		

		gameRenderer.draw(mode==DEMO_MODE?TetrisRenderer.NORMAL_DROP:TetrisRenderer.SLOW_DROP,
				canvas,paint,60,60,480,880);	
		
		btnStats.draw(canvas, paint);	
	}
	

	class GameLoop extends Thread {
		SurfaceHolder holder;
		GameBoard gameBoard;
		boolean running=true;
		GameLoop(GameBoard gameBoard,SurfaceHolder holder) {
			this.holder = holder;
			this.gameBoard = gameBoard;
		}
		
		public void run() {
			while (running) {
				Canvas canvas = holder.lockCanvas();

				gameBoard.doUpdate();
				gameBoard.doDraw(canvas);

				holder.unlockCanvasAndPost(canvas);

				try {
					sleep(50);
				} catch (Exception e) {

				}
			}
		}
		
		public void setStop() {
			running=false;
		}
	}
	


	@Override
	public boolean onDown(MotionEvent event) {
		
		return true;
	}

	@Override
	public boolean onFling(MotionEvent startEvent, MotionEvent endEvent, float xVelocity,
			float yVelocity) {
		if(endEvent.getY() - startEvent.getY() > 100 ) {
			game.addCommand(new TetrisCommand(TetrisCommand.DROP));
			return true;
		}
		else if ( endEvent.getY() - startEvent.getY() < -100 ) {
			toggleMode();
		}
		else if (endEvent.getX() - startEvent.getX() > 50) {
			game.addCommand(new TetrisCommand(TetrisCommand.MOVE_RIGHT));
		}
		else if (endEvent.getX() - startEvent.getX() < -50) {
			game.addCommand(new TetrisCommand(TetrisCommand.MOVE_LEFT));
		}
			
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		game.pause();
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		if( !game.isRunning() ) {
			game.resume();
			return true;
		}
		if( btnStats.contains((int)event.getX(), (int)event.getY())) {
			if( game.getGameStats().isVisible() )
				game.addCommand(new TetrisCommand(TetrisCommand.HIDE_STATS));
			else 
				game.addCommand(new TetrisCommand(TetrisCommand.SHOW_STATS));
		}
		else if( game.isGameOver() ) {
			Rect rect = new Rect(150,300,450,500);
			if( rect.contains((int)event.getX(),(int)event.getY()) )
				game.resetGame();
			return true;
		} else {
			if( event.getX() < getWidth()*0.1) {
				game.addCommand(new TetrisCommand(TetrisCommand.MOVE_LEFT));
			} 
			else if (event.getX() > getWidth()*0.9) {
				game.addCommand(new TetrisCommand(TetrisCommand.MOVE_RIGHT));
			}
			else  {
				game.addCommand(new TetrisCommand(TetrisCommand.ROTATE_RIGHT));
			}
		}
		return true;
	}
	
	void toggleMode() {
		if( mode == PLAY_MODE ) {
			game.resetGame();
			simu.startSimulate();
			mode = DEMO_MODE;
		}
		else {
			simu.stopSimulation();
			game.resetGame();
			mode = PLAY_MODE;
		}
	}
}
