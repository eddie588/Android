package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.yinong.cubegame.util.Vect3D;

public class Cube4By4 extends CubeGame {
	Cube[] cubes;

	private float cubeSize = 1f;
	private float cubeMargin = 0.05f;

	private Vect3D position;

	private CubeWorld world;

	public Cube4By4(CubeWorld world,float x, float y, float z) {
		this.world = world;
		position = new Vect3D(x, y, z);
		cubes = new Cube[64];
		int p = 0;
		int cx = 0;
		int cy = 0;
		int cz = 0;
		for (cx = 0; cx < 4; cx++) {
			for (cy = 0; cy < 4; cy++) {
				for (cz = 0; cz <4; cz++) {
					cubes[p++] = new Cube(cubeSize * cx-1-cubeSize/2, cubeSize * cy-1-cubeSize/2,
							cubeSize * cz-1-cubeSize/2, cubeSize-cubeMargin);
				}
			}
		}
		
		setupColors();
		// controller = new GameController(view,cube);
	}
	

	@Override
	public Vect3D getPosition() {
		return position;
	}

	@Override
	public List<Cube> getCubes(int face) {

		List<Cube> list = new ArrayList<Cube>();

		if (face == FACE_FRONT || face == FACE_SIDE1 || face == FACE_SIDE2 || face == FACE_BACK) {
			float z = 0;
			switch( face ) {
			case CubeGame.FACE_FRONT:
				z = 1.5f*cubeSize;
				break;
			case CubeGame.FACE_SIDE1:
				z = 0.5f*cubeSize;
				break;
			case CubeGame.FACE_SIDE2:
				z = -0.5f*cubeSize;
				break;
			case CubeGame.FACE_BACK:
				z = -1.5f*cubeSize;
				break;
			}

			for (int i = 0; i < cubes.length; i++) {
				if (Math.abs(cubes[i].getCenter().z - z) < EPSILON)
					list.add(cubes[i]);
			}
		}

		if (face == FACE_LEFT || face == FACE_MIDDLE1 || face == FACE_MIDDLE2 || face == FACE_RIGHT) {
			float x = 0;
			switch( face ) {
			case CubeGame.FACE_RIGHT:
				x = 1.5f*cubeSize;
				break;
			case CubeGame.FACE_MIDDLE1:
				x = 0.5f*cubeSize;
				break;
			case CubeGame.FACE_MIDDLE2:
				x = -0.5f*cubeSize;
				break;
			case CubeGame.FACE_LEFT:
				x = -1.5f*cubeSize;
				break;
			}
			for (int i = 0; i < cubes.length; i++) {
				if (Math.abs(cubes[i].getCenter().x - x) < EPSILON)
					list.add(cubes[i]);
			}
		}

		if (face == FACE_TOP || face == FACE_EQUATOR1 || face == FACE_EQUATOR2 || face == FACE_BOTTOM) {
			float y = 0;
			switch( face ) {
			case CubeGame.FACE_TOP:
				y = 1.5f*cubeSize;
				break;
			case CubeGame.FACE_EQUATOR1:
				y = 0.5f*cubeSize;
				break;
			case CubeGame.FACE_EQUATOR2:
				y = -0.5f*cubeSize;
				break;
			case CubeGame.FACE_BOTTOM:
				y = -1.5f*cubeSize;
				break;
			}
			for (int i = 0; i < cubes.length; i++) {
				if (Math.abs(cubes[i].getCenter().y - y) < EPSILON)
					list.add(cubes[i]);
			}
		}
		return list;
	}
	

	@Override
	public void turnFace(Vect3D p1, Vect3D p2) {
		// Swipe on front face
		if ((Math.abs(p1.z - 2.0 * cubeSize+0.5*cubeMargin) < EPSILON && Math.abs(p2.z - 2.0
				* cubeSize+0.5*cubeMargin) < EPSILON)
				|| (Math.abs(p1.z + 2.0 * cubeSize-0.5*cubeMargin) < EPSILON && Math.abs(p2.z
						+ 2.0 * cubeSize - 0.5*cubeMargin) < EPSILON)) {
			handleFrontBackSwipe(p1, p2);
		}
		
		if ((Math.abs(p1.x - 2.0 * cubeSize + 0.5*cubeMargin) < EPSILON && Math.abs(p2.x - 2.0
				* cubeSize + 0.5*cubeMargin) < EPSILON)
				|| (Math.abs(p1.x + 2.0 * cubeSize - 0.5*cubeMargin) < EPSILON && Math.abs(p2.x
						+ 2.0 * cubeSize - 0.5*cubeMargin) < EPSILON)) {
			handleLeftRightSwipe(p1, p2);
		}
		
		if ((Math.abs(p1.y - 2.0 * cubeSize + 0.5*cubeMargin) < EPSILON && Math.abs(p2.y - 2.0
				* cubeSize + 0.5*cubeMargin) < EPSILON)
				|| (Math.abs(p1.y + 2.0 * cubeSize - 0.5*cubeMargin) < EPSILON && Math.abs(p2.y
						+ 2.0 * cubeSize - 0.5*cubeMargin) < EPSILON)) {
			handleBottomTopSwipe(p1, p2);
		}	
	}

	private void handleFrontBackSwipe(Vect3D p1, Vect3D p2) {
		System.out.println("Front/back swipe");
		if (Math.abs(p1.x - p2.x) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 2.0);
			int row2 = (int) (p2.y / cubeSize + 2.0);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (row1 == row2) {
				switch(row1) {
				case 0:
					world.requestTurnFace(FACE_BOTTOM, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_EQUATOR2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_EQUATOR1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_TOP, 90 * direction);
					break;
				
				}
			}
		} else {
			// check to rotate left , middle or right
			int col1 = (int) (p1.x / cubeSize + 2.0);
			int col2 = (int) (p2.x / cubeSize + 2.0);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (col1 == col2) {
				switch(col1) {
				case 0:
					world.requestTurnFace(FACE_LEFT, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_MIDDLE2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_MIDDLE1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_RIGHT, 90 * direction);
					break;
				
				}
			}
		}
	}
	
	private void handleLeftRightSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 2.0);
			int row2 = (int) (p2.y / cubeSize + 2.0);
			int direction = (p2.z >= p1.z) ? 1 : -1;
			if (p1.x < 0)
				direction *= -1;
			if (row1 == row2) {
				switch(row1) {
				case 0:
					world.requestTurnFace(FACE_BOTTOM, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_EQUATOR2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_EQUATOR1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_TOP, 90 * direction);
					break;
				
				}
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 2.0);
			int col2 = (int) (p2.z / cubeSize + 2.0);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.x > 0)
				direction *= -1;
			if (col1 == col2) {
				switch(col1) {
				case 0:
					world.requestTurnFace(FACE_BACK, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_SIDE2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_SIDE1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_FRONT, 90 * direction);
					break;
				
				}
			}
		}
	}
	
	private void handleBottomTopSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.x - p2.x))) {
			// check to rotate left, middle or right
			int col1 = (int) (p1.x / cubeSize + 2.0);
			int col2 = (int) (p2.x / cubeSize + 2.0);
			int direction = (p1.z >= p2.z) ? 1 : -1;
			if (p1.y < 0)
				direction *= -1;
			if (col1 == col2) {
				switch(col1) {
				case 0:
					world.requestTurnFace(FACE_LEFT, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_MIDDLE2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_MIDDLE1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_RIGHT, 90 * direction);
					break;
				
				}
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 2.0);
			int col2 = (int) (p2.z / cubeSize + 2.0);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.y > 0)
				direction *= -1;
			if (col1 == col2) {
				switch(col1) {
				case 0:
					world.requestTurnFace(FACE_BACK, 90 * direction);
					break;
				case 1:
					world.requestTurnFace(FACE_SIDE2, 90 * direction);
					break;
				case 2:
					world.requestTurnFace(FACE_SIDE1, 90 * direction);
					break;
				case 3:
					world.requestTurnFace(FACE_FRONT, 90 * direction);
					break;
				
				}
			}
		}
	}


	/**
	 * Randomly rotate face for the specified number of times. This simply add the rotate request 
	 * to the queue
	 * @param count
	 */
	
	private static int FACES[] = {FACE_FRONT,FACE_BACK,FACE_TOP,FACE_BOTTOM,FACE_LEFT,FACE_RIGHT,
		FACE_MIDDLE1,FACE_MIDDLE2,FACE_EQUATOR1,FACE_EQUATOR2,FACE_SIDE1,FACE_SIDE2};
	@Override
	public void shuffle(int count) {
		// TODO: shuffle should only support 6 faces
		Random r = new Random();
		for(int i=0;i<count;i++) {
			world.requestTurnFace(FACES[r.nextInt(6)], 90);
		}
	}

	@Override
	public List<Cube> getAllCubes() {
		return Arrays.asList(cubes);
	};
}
