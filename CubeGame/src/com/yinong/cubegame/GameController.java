package com.yinong.cubegame;

import java.util.List;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLSurfaceView;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.yinong.cubegame.model.Cube;
import com.yinong.cubegame.model.CubeGame;
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
//		if( event2.getY() <gl.getViewportHeight()-200 || event1.getY() < gl.getViewportHeight()-200) {
//			return checkTurn(event1, event2);
//		}
	
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
		
		//	check plane
		turnFace(hitP,hitCount);
		//cubeWorld.turnFace(hitP[0],hitP[hitCount-1]);
		return true;
	}
	
	void turnFace(Vect3D[] hitP,int hitCount) {
		float ax=0,ay=0,az=0;
		for(int i=0;i<hitCount-1;i++) {
			ax += Math.abs(hitP[i].x - hitP[i+1].x);
			ay += Math.abs(hitP[i].y - hitP[i+1].y);
			az += Math.abs(hitP[i].z - hitP[i+1].z);
		}
		ax = ax/hitCount;
		ay = ay/hitCount;
		az = az/hitCount;
		List<Cube> cubes;
		float centerP;
		int direction;
		if( ax <CubeGame.EPSILON && ax < ay && ax < az ) {
			// Swipe on X face
			if(Math.abs(hitP[0].y-hitP[hitCount-1].y) > Math.abs(hitP[0].z-hitP[hitCount-1].z)) {
				centerP = (hitP[0].z+hitP[hitCount-1].z)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_Z,centerP);
				
				direction = (getFace(cubes,hitP[0].x,0,0) == Cube.CUBE_LEFT )?-1:1;
				direction *= ((hitP[0].y-hitP[hitCount-1].y)>0)? 1:-1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_Z, centerP, 90*direction);
				
			}
			else {
				centerP = (hitP[0].y+hitP[hitCount-1].y)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_Y,centerP);
				
				direction = (getFace(cubes,hitP[0].x,0,0) == Cube.CUBE_LEFT )?-1:1;
				direction *= ((hitP[0].z-hitP[hitCount-1].z)<0) ? 1:-1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_Y, centerP, 90*direction);
			}
		}
		if( ay <CubeGame.EPSILON && ay < ax && ay < az ) {
			// Swipe on Y face
			if(Math.abs(hitP[0].x-hitP[hitCount-1].x) > Math.abs(hitP[0].z-hitP[hitCount-1].z)) {
				centerP = (hitP[0].z+hitP[hitCount-1].z)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_Z,centerP);
				
				direction = (getFace(cubes,0,hitP[0].y,0) == Cube.CUBE_TOP )?1:-1;
				direction *= ((hitP[0].x-hitP[hitCount-1].x)>0)? -1:1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_Z, centerP, 90*direction);
				
			}
			else {
				centerP = (hitP[0].x+hitP[hitCount-1].x)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_X,centerP);
				
				direction = (getFace(cubes,0,hitP[0].y,0) == Cube.CUBE_TOP )?1:-1;
				direction *= ((hitP[0].z-hitP[hitCount-1].z)<0) ? -1:1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_X, centerP, 90*direction);
			}
		}
		if( az <CubeGame.EPSILON && az < ay && az < ax ) {
			// Swipe on Z face
			if(Math.abs(hitP[0].x-hitP[hitCount-1].x) > Math.abs(hitP[0].y-hitP[hitCount-1].y)) {
				centerP = (hitP[0].y+hitP[hitCount-1].y)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_Y,centerP);
				
				direction = (getFace(cubes,0,0,hitP[0].z) == Cube.CUBE_FRONT )?1:-1;
				direction *= ((hitP[0].x-hitP[hitCount-1].x)>0)? 1:-1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_Y, centerP, 90*direction);
				
			}
			else {
				centerP = (hitP[0].x+hitP[hitCount-1].x)/2;
				cubes = cubeWorld.getGame().getCubes(Cube.PLANE_X,centerP);
				
				direction = (getFace(cubes,0,0,hitP[0].z) == Cube.CUBE_FRONT )?1:-1;
				direction *= ((hitP[0].y-hitP[hitCount-1].y)<0) ? 1:-1;
				
				cubeWorld.requestTurnFace(Cube.PLANE_X, centerP, 90*direction);
			}
		}		
	}
	
	int getFace(List<Cube> cubes,float x,float y,float z) {
		float maxX=0,minX = 0,maxY =0, minY = 0,maxZ = 0,minZ = 0;
		for(Cube cube:cubes) {
			if( (cube.getCenter().x-cube.getSize()/2)<minX)
				minX = (cube.getCenter().x-cube.getSize()/2);
			if( (cube.getCenter().x+cube.getSize()/2)>maxX)
				maxX = (cube.getCenter().x+cube.getSize()/2);
			
			if( (cube.getCenter().y-cube.getSize()/2)<minY)
				minY = (cube.getCenter().y-cube.getSize()/2);
			if( (cube.getCenter().y+cube.getSize()/2)>maxY)
				maxY = (cube.getCenter().y+cube.getSize()/2);
			
			if( (cube.getCenter().z-cube.getSize()/2)<minZ)
				minZ = (cube.getCenter().z-cube.getSize()/2);
			if( (cube.getCenter().z+cube.getSize()/2)>maxZ)
				maxZ = (cube.getCenter().z+cube.getSize()/2);			
		}
		if( x != 0 &&  Math.abs(x- minX) < CubeGame.EPSILON )
			return Cube.CUBE_LEFT;
		else if( x != 0 && Math.abs(x- maxX) < CubeGame.EPSILON  )
			return Cube.CUBE_RIGHT;
		
		if( y != 0 &&  Math.abs(y- minY) < CubeGame.EPSILON )
			return Cube.CUBE_BOTTOM;
		else if( y != 0 && Math.abs(y- maxY) < CubeGame.EPSILON  )
			return Cube.CUBE_TOP;
		
		if( z != 0 &&  Math.abs(z- minZ) < CubeGame.EPSILON )
			return Cube.CUBE_BACK;
		else if( z != 0 && Math.abs(z- maxZ) < CubeGame.EPSILON  )
			return Cube.CUBE_FRONT;		
			
		return -1;
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
