package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.yinong.cubegame.util.Vect3D;

public class Cube2By2 extends CubeGame {
	Cube[] cubes;

	private Vect3D position;

	private CubeWorld world;

	public Cube2By2(CubeWorld world,float x, float y, float z) {
		this.world = world;
		position = new Vect3D(x, y, z);
		cubes = new Cube[8];
		int p = 0;
		int cx = 0;
		int cy = 0;
		int cz = 0;
		for (cx = 0; cx < 2; cx++) {
			for (cy = 0; cy < 2; cy++) {
				for (cz = 0; cz< 2; cz++) {
					cubes[p++] = new Cube(cubeSize * cx-cubeSize/2, cubeSize * cy-cubeSize/2,
							cubeSize * cz-cubeSize/2, cubeSize-cubeMargin);
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
		switch(face) {
		case Cube.CUBE_FRONT:
			return getCubes(Cube.PLANE_Z,0.5f*cubeSize);
		case Cube.CUBE_BACK:
			return getCubes(Cube.PLANE_Z,-0.5f*cubeSize);
		case Cube.CUBE_LEFT:
			return getCubes(Cube.PLANE_X,-0.5f*cubeSize);
		case Cube.CUBE_RIGHT:
			return getCubes(Cube.PLANE_X,0.5f*cubeSize);
		case Cube.CUBE_TOP:
			return getCubes(Cube.PLANE_Y,0.5f*cubeSize);
		case Cube.CUBE_BOTTOM:
			return getCubes(Cube.PLANE_Y,-0.5f*cubeSize);
		}
		return new ArrayList<Cube>();
	}
	

	@Override
	public void turnFace(Vect3D p1, Vect3D p2) {
		// Swipe on front face
		if (isSwipeOnFrontBack(p1,p2,1.0f)) {
			handleFrontBackSwipe(p1, p2);
		}
		
		if (isSwipeOnLeftRight(p1,p2,1.0f)) {
			handleLeftRightSwipe(p1, p2);
		}
		
		if (isSwipeOnTopBottom(p1,p2,1.0f)) {
			handleBottomTopSwipe(p1, p2);
		}	
	}

	private void handleFrontBackSwipe(Vect3D p1, Vect3D p2) {
		System.out.println("Front/back swipe");
		if (Math.abs(p1.x - p2.x) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 1.0);
			int row2 = (int) (p2.y / cubeSize + 1.0);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (row1 == row2) {
				world.requestTurnFace(Cube.PLANE_Y,(row1-0.5f)*cubeSize, 90f * direction);
			}
		} else {
			// check to rotate left , middle or right
			int col1 = (int) (p1.x / cubeSize + 1.0);
			int col2 = (int) (p2.x / cubeSize + 1.0);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (col1 == col2) {
				world.requestTurnFace(Cube.PLANE_X,(col1-0.5f)*cubeSize, 90f * direction);
			}
		}
	}
	
	private void handleLeftRightSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 1.0);
			int row2 = (int) (p2.y / cubeSize + 1.0);
			int direction = (p2.z >= p1.z) ? 1 : -1;
			if (p1.x < 0)
				direction *= -1;
			if (row1 == row2) {
				world.requestTurnFace(Cube.PLANE_Y,(row1-0.5f)*cubeSize, 90f * direction);
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 1.0);
			int col2 = (int) (p2.z / cubeSize + 1.0);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.x > 0)
				direction *= -1;
			if (col1 == col2) {
				world.requestTurnFace(Cube.PLANE_Z,(col1-0.5f)*cubeSize, 90f * direction);
			}
		}
	}
	
	private void handleBottomTopSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.x - p2.x))) {
			// check to rotate left, middle or right
			int col1 = (int) (p1.x / cubeSize + 1.0);
			int col2 = (int) (p2.x / cubeSize + 1.0);
			int direction = (p1.z >= p2.z) ? 1 : -1;
			if (p1.y < 0)
				direction *= -1;
			if (col1 == col2) {
				world.requestTurnFace(Cube.PLANE_X,(col1-0.5f)*cubeSize, 90f * direction);
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 1.0);
			int col2 = (int) (p2.z / cubeSize + 1.0);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.y > 0)
				direction *= -1;
			if (col1 == col2) {
				world.requestTurnFace(Cube.PLANE_Z,(col1-0.5f)*cubeSize, 90f * direction);
			}
		}
	}


	/**
	 * Randomly rotate face for the specified number of times. This simply add the rotate request 
	 * to the queue
	 * @param count
	 */
	

	@Override
	public void shuffle(int count) {
		// TODO: shuffle should only support 6 faces
		Random r = new Random();
		for(int i=0;i<count;i++) {
			world.requestTurnFace(r.nextInt(3),r.nextInt(2)-0.5f, 90);
		}
	}

	@Override
	public List<Cube> getAllCubes() {
		return Arrays.asList(cubes);
	};
}
