package com.yinong.cubegame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class MainView extends GLSurfaceView {
	private GameController controller;
	GestureDetector gestureDetector;	
	public MainView(Context context,GameController controller) {
		super(context);
		this.controller = controller;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		super.surfaceCreated(holder);
		
		gestureDetector = new GestureDetector(controller);
		
		setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
				//return onScreenTouch(event);
			}
		});			
	}
}
