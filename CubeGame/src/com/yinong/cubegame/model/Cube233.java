package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.yinong.cubegame.util.Vect3D;

public class Cube233 extends CubeGame {
	Cube[] cubes;

	private Vect3D position;

	private CubeWorld world;

	public Cube233(CubeWorld world,float x, float y, float z) {
		this.world = world;
		position = new Vect3D(x, y, z);
		cubes = new Cube[18];
		int p = 0;
		int cx = 0;
		int cy = 0;
		int cz = 0;
		for (cx = 0; cx < 3; cx++) {
			for (cy = 0; cy < 2; cy++) {
				for (cz = 0; cz <3; cz++) {
					cubes[p++] = new Cube(cubeSize * cx-1f* cubeSize, cubeSize * cy-0.5f*cubeSize,
							cubeSize * cz-1f*cubeSize, cubeSize-cubeMargin);
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


	/**
	 * Randomly rotate face for the specified number of times. This simply add the rotate request 
	 * to the queue
	 * @param count
	 */
	

	@Override
	public void shuffle(int count) {
		// TODO: shuffle should only support 6 faces
		Random r = new Random();
		for(int i=0;i<2*count;i++) {
			int plane = r.nextInt(3);
			if( plane == Cube.PLANE_Y )
				world.requestTurnFace(plane,r.nextInt(2)-0.5f, 90);
			else
				world.requestTurnFace(plane,r.nextInt(2)-1f, 90);				
		}
	}

	@Override
	public List<Cube> getAllCubes() {
		return Arrays.asList(cubes);
	}

	@Override
	public boolean isValidTurn(int plane,float centerP,float angle) {
		if( getCubes(plane,centerP-cubeSize/2).size() > 0 )
			return false;
		if( getCubes(plane,centerP+cubeSize/2).size() > 0 )
			return false;
		return true;
	}
	
}
