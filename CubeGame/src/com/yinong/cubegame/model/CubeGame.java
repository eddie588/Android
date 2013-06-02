package com.yinong.cubegame.model;

import java.util.List;

import com.yinong.cubegame.util.Vect3D;

public abstract class CubeGame {
	public static float EPSILON = 0.00001f;
	
	public static final int FACE_FRONT = 0;
	public static final int FACE_BACK = 1;
	public static final int FACE_SIDE = 2;
	public static final int FACE_TOP = 3;
	public static final int FACE_BOTTOM = 4;
	public static final int FACE_EQUATOR = 5;
	public static final int FACE_LEFT = 6;
	public static final int FACE_RIGHT = 7;
	public static final int FACE_MIDDLE = 8;
	
	//	for 4X4
	public static final int FACE_MIDDLE1 = 9;
	public static final int FACE_MIDDLE2 = 10;
	public static final int FACE_EQUATOR1 = 11;
	public static final int FACE_EQUATOR2 = 12;
	public static final int FACE_SIDE1 = 13;
	public static final int FACE_SIDE2 = 14;
	
	public abstract void turnFace(Vect3D p1, Vect3D p2);

	public abstract List<Cube> getCubes(int face);

	public abstract void shuffle(int count);

	public abstract List<Cube> getAllCubes();

	public abstract Vect3D getPosition();
	
	protected void setupColors() {
		for(Cube cube:getCubes(FACE_FRONT)) {
			cube.setColor(Cube.CUBE_FRONT,0.75f,0f,0f,1f);
		}
		
		for(Cube cube:getCubes(FACE_BACK)) {
			cube.setColor(Cube.CUBE_BACK,1f,1f,0f,1f);
		}
		
		for(Cube cube:getCubes(FACE_LEFT)) {
			cube.setColor(Cube.CUBE_LEFT,0f,0f,1f,1f);
		}
		
		for(Cube cube:getCubes(FACE_RIGHT)) {
			cube.setColor(Cube.CUBE_RIGHT,0f,0.75f,0f,1f);
		}
		
		for(Cube cube:getCubes(FACE_TOP)) {
			cube.setColor(Cube.CUBE_TOP,1,0.5f,0f,0f);
		}
		
		for(Cube cube:getCubes(FACE_BOTTOM)) {
			cube.setColor(Cube.CUBE_BOTTOM,1f,1f,1f,1f);
		}			
	}	
}
