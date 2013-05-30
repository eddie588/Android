package com.yinong.cubegame;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.yinong.cubegame.model.Cube3By3;
import com.yinong.cubegame.model.Vertex;

public class GameController  implements GestureDetector.OnGestureListener,GLSurfaceView.GLWrapper{
	GestureDetector gestureDetector;	
	View mainView;
	final Cube3By3 cube;
	MatrixTrackingGL gl;
		
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
		
//			Vertex v = getWorldCoords(x,y);
//			System.out.println("touch: " + v.x + "," + v.y +"," + v.z);
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
			
			if( y < 100 ) {
				cube.shuffle();
			}
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
	
	
	public Vertex getWorldCoords(float touchX,float touchY) {
		// Initialize auxiliary variables.

		// SCREEN height & width (ej: 320 x 480)
		float screenW = mainView.getWidth();
		float screenH = mainView.getHeight();

		// Auxiliary matrix and vectors
		// to deal with ogl.
		float[] invertedMatrix, transformMatrix, normalizedInPoint, outPoint;
		invertedMatrix = new float[16];
		transformMatrix = new float[16];
		normalizedInPoint = new float[4];
		outPoint = new float[4];

		// Invert y coordinate, as android uses
		// top-left, and ogl bottom-left.
		int oglTouchY = (int) (screenH - touchY);

		/*
		 * Transform the screen point to clip space in ogl (-1,1)
		 */
		normalizedInPoint[0] = (float) ((touchX) * 2.0f / screenW - 1.0);
		normalizedInPoint[1] = (float) ((oglTouchY) * 2.0f / screenH - 1.0);
		normalizedInPoint[2] = -1.0f;
		normalizedInPoint[3] = 1.0f;

		/*
		 * Obtain the transform matrix and then the inverse.
		 */


		Matrix.multiplyMM(transformMatrix, 0, getCurrentProjection(gl), 0,
				getCurrentModelView(gl), 0);
		Matrix.invertM(invertedMatrix, 0, transformMatrix, 0);

		/*
		 * Apply the inverse to the point in clip space
		 */
		Matrix.multiplyMV(outPoint, 0, invertedMatrix, 0, normalizedInPoint, 0);

		if (outPoint[3] == 0.0) {
			// Avoid /0 error.
			//Log.e("World coords", "ERROR!");
			return null;
		}

		// Divide by the 3rd component to find
		// out the real position.
		return new Vertex(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3],outPoint[2]/outPoint[3]);
	}
	
	/**
	    * Record the current modelView matrix
	    * state. Has the side effect of
	    * setting the current matrix state
	    * to GL_MODELVIEW
	    * @param gl context
	    */
	   public float[] getCurrentModelView(GL10 gl)
	   {
	        float[] mModelView = new float[16];
	        getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
	        return mModelView;
	   }
	 
	   /**
	    * Record the current projection matrix
	    * state. Has the side effect of
	    * setting the current matrix state
	    * to GL_PROJECTION
	    * @param gl context
	    */
	   public float[] getCurrentProjection(GL10 gl)
	   {
	       float[] mProjection = new float[16];
	       getMatrix(gl, GL10.GL_PROJECTION, mProjection);
	       return mProjection;
	   }
	 
	   /**
	    * Fetches a specific matrix from opengl
	    * @param gl context
	    * @param mode of the matrix
	    * @param mat initialized float[16] array
	    * to fill with the matrix
	    */
	   private void getMatrix(GL10 gl, int mode, float[] mat)
	   {
	       MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
	       gl2.glMatrixMode(mode);
	       gl2.getMatrix(mat, 0);
	   }

	@Override
	public GL wrap(GL gl) {
		this.gl = new MatrixTrackingGL(gl);
		return this.gl;
	}	
}
