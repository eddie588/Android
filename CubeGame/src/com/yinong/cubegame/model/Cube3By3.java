package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    
    private int shuffle  = 0;
    private long period = 200;
    private long lastUpdate = 0;
    
    private Vertex position;
	
	public Cube3By3(float x,float y,float z) {
		position = new Vertex(x,y,z);
		cubes = new Cube[27];
		int p=0;
		int cx=0;
		int cy=0;
		int cz=0;
		for(cx=-1;cx<2;cx++) {
			for(cy=-1;cy<2;cy++) {
				for(cz=1;cz>-2;cz--) {
					cubes[p++] = new Cube( cubeSize*cx,cubeSize*cy,cubeSize*cz,cubeSize );
				}
			}
		}		
		//controller = new GameController(view,cube);
		Matrix.setIdentityM(accumulatedRotation,0);
		Matrix.setIdentityM(currentRotation,0);	
		
		Matrix.rotateM(accumulatedRotation,0,45f,1f,1f,1f);
		
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(accumulatedRotation.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        matrixBuffer = byteBuf.asFloatBuffer();		
        matrixBuffer.put(accumulatedRotation);
        matrixBuffer.position(0);
	}
	
	public Vertex getPosition() {
		return position;
	}
	
	public synchronized void draw(GL10 gl) {
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(position.x, position.y, position.z);		
		gl.glMultMatrixf(matrixBuffer);		
		for (int i = 0; i < 27; i++) {
			if( cubes[i] != null )
				cubes[i].draw(gl);
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
	
	int rotateCount1 = 0;
	public void rotateDemo() {
//		rotate(rotateCount1/2,45);
//		//rotate(FACE_EQUATOR,45);
//		if( ++rotateCount1 == 18)
//			rotateCount1 = 0;
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
	
	public synchronized void rotate(float dx,float dy) {
		float[] temporaryMatrix = new float[16];
		Matrix.setIdentityM(currentRotation, 0);
		Matrix.rotateM(currentRotation, 0, dx, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(currentRotation, 0, dy, 1.0f, 0.0f, 0.0f);
		
		// Multiply the current rotation by the accumulated rotation, and then set the accumulated
		// rotation to the result.
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, accumulatedRotation.length);
        matrixBuffer.put(accumulatedRotation);
        matrixBuffer.position(0);	
	}	
	
	int lastFace=0;
	int rotateCount = 0;
	public void update() {
		long now = System.currentTimeMillis();
		if( now - lastUpdate  < period ) {
			return;
		}
		lastUpdate = now;
		if( shuffle > 0 ) {

			if( rotateCount == 0) {
				Random r = new Random();
				lastFace = r.nextInt(9);
				
				rotate(lastFace,45);
				rotateCount++;
			}
			else {
				rotateCount = 0;
				rotate(lastFace,45);
				shuffle--;
			}

		}
	}
	
	public void shuffle() {
		lastFace = 0;
		rotateCount = 0;
		shuffle = 20;
	}
	
	public void onClick(Vertex p) {
		float[] inV=new float[4];
		float[] outV=new float[4];
		float[] tmpM = new float[16];
		
		inV[0]=p.x;
		inV[1]=p.y;
		inV[2]=p.z;
		inV[3]=1f;
		
		Matrix.invertM(accumulatedRotation, 0, tmpM, 0);
		Matrix.multiplyMV(outV, 0, accumulatedRotation, 0, inV, 0);
		
		System.out.println("Cube P:" + outV[0] + "," 
				+ outV[1] + "," 
				+ outV[2] + "," 
				+ outV[3]);
		
	}
}
