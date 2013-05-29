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
		gestureDetector = new GestureDetector(this);
		
		mainView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				//return gestureDetector.onTouchEvent(event);
				return onScreenTouch(event);
			}
		});		
	}
	
	public boolean onScreenTouch(MotionEvent event) {
		if( event.getAction() == MotionEvent.ACTION_DOWN ) {
		cube.rotate(0);
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
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		cube.rotate(0);
		return false;
	}
}
