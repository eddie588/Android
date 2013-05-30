package com.yinong.cubegame;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.yinong.cubegame.model.Cube3By3;

public class GameController  implements GestureDetector.OnGestureListener{
	GestureDetector gestureDetector;	
	View mainView;
	final Cube3By3 cube;
		
	public GameController(View mainView,Cube3By3 cube) {
		this.mainView = mainView;
		this.cube = cube;
		gestureDetector = new GestureDetector(mainView.getContext(), this);
		
		mainView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				//return gestureDetector.onTouchEvent(event);
				return onScreenTouch(event);
			}
		});		
	}
	
	float previousX = 0;
	float previousY = 0;
	long lastClick = 0;
	public boolean onScreenTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			previousX = x;
			previousY = y;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			if (event.getAction() == MotionEvent.ACTION_MOVE && previousX > 0
					&& previousY > 0) {

				float deltaX = (x - previousX) / 6f;
				float deltaY = (y - previousY) / 6f;

				cube.rotate(deltaX, deltaY);

			}

			previousX = x;
			previousY = y;
		}
		
		if( event.getAction() == MotionEvent.ACTION_UP ) {
			if( (event.getEventTime() - lastClick ) < 300 ){
				//if( Math.abs(x-previousX) < 2 && Math.abs(y-previousY) < 2 ) {
					cube.rotateDemo();
				//}
			}
			lastClick = event.getEventTime();
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float dx,
			float dy) {
		cube.rotate(dx/6, dy/6);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		cube.rotateDemo();
		return false;
	}
}
