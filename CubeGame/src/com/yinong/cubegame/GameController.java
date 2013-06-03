package com.yinong.cubegame;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLSurfaceView;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yinong.cubegame.model.CubeWorld;
import com.yinong.cubegame.util.Vect3D;

public class GameController implements OnGestureListener,
		GLSurfaceView.GLWrapper, OnClickListener {
	final CubeWorld cubeWorld;
	MatrixTrackingGL gl;
	GameRenderer renderer;


	public GameController(CubeWorld cubeWorld) {
		this.cubeWorld = cubeWorld;

	}
	
	void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
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
		if( event2.getY() <gl.getViewportHeight()-200 || event1.getY() < gl.getViewportHeight()-200) {
			return checkTurn(event1, event2);
		}
	
		System.out.println("onScroll");
		cubeWorld.rotate(-(dx) / 6f, -(dy) );
		unprocessedX = 0;
		unprocessedY = 0;	
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float vx,
			float vy) {
		System.out.println("OnFling");

		return checkTurn(e1,e2);
	}
	
	boolean checkTurn(MotionEvent e1, MotionEvent e2) {
		//	Check intersected cubes along the swipe line		
		int CHECKNUM = 5;
		float dx = (e2.getX()-e1.getX())/CHECKNUM;
		float dy = (e2.getY()-e1.getY())/CHECKNUM;
		float x = e1.getX();
		float y = e1.getY();
		

		//	Get first and last hit
		Vect3D[] hitP = new Vect3D[CHECKNUM];
		int hitCount=0;
		for(int i=0;i<CHECKNUM;i++) {
			Vect3D p = cubeWorld.intersect(gl.getViewportWidth(), gl.getViewportHeight(), x, y);

			if( p != null ) {
				hitP[hitCount++] = p;
				System.out.println("Hit: " + p);
			}
			x += dx;
			y += dy;
		}
		if( hitCount < 2)
			return false;
		cubeWorld.turnFace(hitP[0],hitP[hitCount-1]);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("onLongPress");

		renderer.toggleLight();
		
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
		if (y < 100) {
			cubeWorld.shuffle(20);
		}
		System.out.println("hit: " + cubeWorld.intersect(gl.getViewportWidth(), 
				gl.getViewportHeight(),x,y));
		
		return true;
	}


	@Override
	public GL wrap(GL gl) {
		this.gl = new MatrixTrackingGL(gl);
		return this.gl;
	}
	


	@Override
	public void onClick(View view) {
		
		switch(view.getId()) {
			case R.id.btn2X2:
				cubeWorld.restartGame(CubeWorld.CUBE_2X2X2);
				break;			
			case R.id.btn3X3:
				cubeWorld.restartGame(CubeWorld.CUBE_3X3X3);
				break;			
			case R.id.btn4X4:
				cubeWorld.restartGame(CubeWorld.CUBE_4X4X4);
				break;			
			case R.id.btn224:
				cubeWorld.restartGame(CubeWorld.CUBE_2X2X4);
				break;			
		}	
	}
	
}
