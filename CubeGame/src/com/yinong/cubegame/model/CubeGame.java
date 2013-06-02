package com.yinong.cubegame.model;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.yinong.cubegame.util.Vect3D;

public interface CubeGame {
	public static int FACE_FRONT = 0;
	public static int FACE_BACK = 1;
	public static int FACE_SIDE = 2;
	public static int FACE_TOP = 3;
	public static int FACE_BOTTOM = 4;
	public static int FACE_EQUATOR = 5;
	public static int FACE_LEFT = 6;
	public static int FACE_RIGHT = 7;
	public static int FACE_MIDDLE = 8;
	
	
	public void turnFace(Vect3D p1, Vect3D p2);

	public List<Cube> getCubes(int face);

	public void shuffle(int count);

	public List<Cube> getAllCubes();

	public Vect3D getPosition();
}
