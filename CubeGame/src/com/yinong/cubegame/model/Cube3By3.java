package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

public class Cube3By3 {
	Cube[] cubes;
	public static int FACE_FRONT = 0;
	public static int FACE_BACK = 1;
	public static int FACE_SIDE = 2;
	public static int FACE_UP = 3;
	public static int FACE_DOWN = 4;
	public static int FACE_EQUATOR = 5;
	public static int FACE_LEFT = 6;
	public static int FACE_RIGHT = 7;
	public static int FACE_MIDDLE = 8;
	
	private float cubeSize = 1f;
	float[] accumulatedRotation = new float[16];
	float[] currentRotation = new float[16];
    private FloatBuffer matrixBuffer;
	
	public Cube3By3() {
		cubes = new Cube[27];
		int p=0;
		int x=0;
		int y=0;
		int z=0;
		for(x=-1;x<2;x++) {
			for(y=-1;y<2;y++) {
				for(z=1;z>-2;z--) {
					cubes[p++] = new Cube( cubeSize*x,cubeSize*y,cubeSize*z,cubeSize );
				}
			}
		}		
		//controller = new GameController(view,cube);
		Matrix.setIdentityM(accumulatedRotation,0);
		Matrix.setIdentityM(currentRotation,0);	
		
		Matrix.rotateM(accumulatedRotation,0,45f,1f,1f,1f);
		
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(currentRotation.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        matrixBuffer = byteBuf.asFloatBuffer();		
        matrixBuffer.put(accumulatedRotation);
        matrixBuffer.position(0);
	}
	
	public void draw(GL10 gl,float rotation) {
		for (int i = 0; i < 27; i++) {
			gl.glLoadIdentity();

			gl.glTranslatef(0.0f, 0.0f, -10.0f);
			
			gl.glMultMatrixf(matrixBuffer);

			//gl.glRotatef(rotation, 1.0f, 1.0f, 1.0f);

			if( cubes[i] != null )
				cubes[i].draw(gl);
			gl.glLoadIdentity();
		}	
	}
	
	
	public List<Cube> getCubes(int face) {
		List<Cube> list = new ArrayList<Cube>();
		
		if( face == FACE_FRONT || face == FACE_SIDE || face == FACE_BACK ){
			float z = 0;
			z = (face==FACE_FRONT)? cubeSize :((face==FACE_BACK)?-cubeSize:0);
			for(int i=0;i<27;i++) {
				if ( Math.abs(cubes[i].getCenter().z - z) < 0.0001 )
					list.add(cubes[i]);
			}
		}
		
		if( face == FACE_LEFT || face == FACE_MIDDLE || face == FACE_RIGHT ){
			float x = 0;
			x = (face==FACE_RIGHT)? cubeSize :((face==FACE_LEFT)?-cubeSize:0);
			for(int i=0;i<27;i++) {
				if ( Math.abs(cubes[i].getCenter().x - x) < 0.0001 )
					list.add(cubes[i]);
			}
		}

		if( face == FACE_UP || face == FACE_EQUATOR || face == FACE_DOWN ){
			float y = 0;
			y = (face==FACE_UP)? cubeSize :((face==FACE_DOWN)?-cubeSize:0);
			for(int i=0;i<27;i++) {
				if ( Math.abs(cubes[i].getCenter().y - y) < 0.0001 )
					list.add(cubes[i]);
			}
		}	
		return list;
	}
	
	int rotateCount = 0;
	public void rotateDemo() {
		rotate(rotateCount/2,45);
		//rotate(FACE_EQUATOR,45);
		if( ++rotateCount == 18)
			rotateCount = 0;
	}
	
	public void rotate(int face,int angle) {
		List<Cube> list = getCubes(face);
		
		for(Cube cube:list) {
			if( face == FACE_FRONT || face == FACE_SIDE || face == FACE_BACK )
				cube.rotate(angle,0f,0f,-1f);
			if( face == FACE_LEFT || face == FACE_MIDDLE || face == FACE_RIGHT )
				cube.rotate(angle,-1f,0f,0f);
			if( face == FACE_UP || face == FACE_EQUATOR || face == FACE_DOWN )
				cube.rotate(angle,0f,-1f,0f);
		}
	}
	
	public void rotate(float dx,float dy) {
		float[] temporaryMatrix = new float[16];
		Matrix.setIdentityM(currentRotation, 0);
		Matrix.rotateM(currentRotation, 0, dx, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(currentRotation, 0, dy, 1.0f, 0.0f, 0.0f);
		
		// Multiply the current rotation by the accumulated rotation, and then set the accumulated
		// rotation to the result.
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);
        matrixBuffer.put(accumulatedRotation);
        matrixBuffer.position(0);		
//		// Rotate the cube taking the overall rotation into account.
//		Matrix.multiplyMM(temporaryMatrix, 0, mModelMatrix, 0, accumulatedRotation, 0);
//		System.arraycopy(temporaryMatrix, 0, mModelMatrix, 0, 16);		
	}	
}
