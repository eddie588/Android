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
