package com.yinong.cubegame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.yinong.cubegame.model.Cube;

public class GameRenderer implements Renderer {
	private Cube[] cubes;
	float cubeRotation=0;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glShadeModel(GL10.GL_FLAT);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		cubes = new Cube[27];
		
		int p=0;
		int x=0;
		int y=0;
		int z=0;
		for(x=0;x<3;x++) {
			for(y=0;y<3;y++) {
				for(z=0;z<3;z++) {
					cubes[p++] = new Cube( 1f*x,1f*y,1f*z,0.5f );
				}
			}
		}
		
//		mCube = new Cube[2];
//		
//		mCube[0] = new Cube( 0f,0f,0f,0.25f );
//		mCube[1] = new Cube( 0.5f,0.5f,0f,0.25f );
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for (int i = 0; i < 27; i++) {
			gl.glLoadIdentity();

			gl.glTranslatef(0.0f, 0.0f, -10.0f);
			 //mCubeRotation = -90;
			gl.glRotatef(cubeRotation, 1.0f, 1.0f, 1.0f);

			if( cubes[i] != null )
				cubes[i].draw(gl);
			gl.glLoadIdentity();
		}

		cubeRotation -= 2f;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				100.0f);
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}





}
