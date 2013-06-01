package com.yinong.cubegame;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLSurfaceView;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.yinong.cubegame.model.Cube3By3;
import com.yinong.cubegame.util.Vect3D;

public class GameController implements OnGestureListener,
		GLSurfaceView.GLWrapper {
	final Cube3By3 cube;
	MatrixTrackingGL gl;
	

	public GameController(Cube3By3 cube) {
		this.cube = cube;
	}
	
	

	long lastClick = 0;
	float unprocessedX = 0;
	float unprocessedY = 0;
	
	boolean rotateEnabled = false;

	@Override
	public boolean onDown(MotionEvent event) {

		lastClick = System.currentTimeMillis();
		unprocessedX = 0;
		unprocessedY = 0;
		rotateEnabled = false;
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float dx,
			float dy) {

		// allow fling a chance to handle
		if( System.currentTimeMillis() - lastClick < 200 ) {
			unprocessedX += dx;
			unprocessedY += dy;
			return false;
		}
	
		System.out.println("onScroll");
		cube.rotate(-(dx+unprocessedX) / 6f, -(dy+unprocessedY) / 6f);
		unprocessedX = 0;
		unprocessedY = 0;	
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float vx,
			float vy) {
		System.out.println("OnFling");
		
		//	Check intersected cubes along the swipe line		
		float[] projectionM = gl.getCurrentProjection();
		int CHECKNUM = 5;
		float dx = (e2.getX()-e1.getX())/CHECKNUM;
		float dy = (e2.getY()-e1.getY())/CHECKNUM;
		float x = e1.getX();
		float y = e1.getY();
		

		//	Get first and last hit
		Vect3D[] hitP = new Vect3D[CHECKNUM];
		int hitCount=0;
		for(int i=0;i<CHECKNUM;i++) {
			Vect3D p = cube.intersect(gl.getViewportWidth(), gl.getViewportHeight(), x, y, projectionM);

			if( p != null ) {
				hitP[hitCount++] = p;
				System.out.println("Hit: " + p);
			}
			x += dx;
			y += dy;
		}
		if( hitCount < 2)
			return false;
		cube.rotate(hitP[0],hitP[hitCount-1]);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("onLongPress");
		rotateEnabled = true;

	}



	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		System.out.println("onSingleTapUp");
		float x = event.getX();
		float y = event.getY();
		if (event.getY() < 100) {
			cube.shuffle();
		}
		System.out.println("hit: " + cube.intersect(gl.getViewportWidth(), 
				gl.getViewportHeight(),x,y, gl.getCurrentProjection()));
		
		return true;
	}

	public void getSelection(float touchX, float touchY) {

	}

	@Override
	public GL wrap(GL gl) {
		this.gl = new MatrixTrackingGL(gl);
		return this.gl;
	}
}
