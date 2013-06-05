package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.yinong.cubegame.util.Vect3D;

public class Cube224 extends CubeGame {

	Cube[] cubes;

	private float cubeSize = 1f;
	private float cubeMargin = 0.05f;

	private Vect3D position;

	private CubeWorld world;

	public Cube224(CubeWorld world,float x, float y, float z) {
		this.world = world;
		position = new Vect3D(x, y, z);
		cubes = new Cube[16];
		int p = 0;
		int cx = 0;
		int cy = 0;
		int cz = 0;
		for (cx = 0; cx < 4; cx++) {
			for (cy = 0; cy < 2; cy++) {
				for (cz = 0; cz <2; cz++) {
					cubes[p++] = new Cube(cubeSize * cx-1.5f* cubeSize, cubeSize * cy-0.5f*cubeSize,
							cubeSize * cz-0.5f*cubeSize, cubeSize-cubeMargin);
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
			return getCubes(Cube.PLANE_X,-1.5f*cubeSize);
		case Cube.CUBE_RIGHT:
			return getCubes(Cube.PLANE_X,1.5f*cubeSize);
		case Cube.CUBE_TOP:
			return getCubes(Cube.PLANE_Y,0.5f*cubeSize);
		case Cube.CUBE_BOTTOM:
			return getCubes(Cube.PLANE_Y,-0.5f*cubeSize);
		}
		return new ArrayList<Cube>();
	}
	
	
	public boolean isValidTurn(int plane,float centerP,float angle) {
		return getCubes(plane,centerP).size() >=4;
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
			int plane = r.nextInt(3);
			float centerP = r.nextInt(4)-1.5f;
//			if( !isValidTurn(plane,centerP,90) ) {
//				i--;
//				continue;
//			}
			world.requestTurnFace(r.nextInt(3),r.nextInt(4)-1.5f, 90);
		}
	}
	
	public List<Cube> getStickyCubes(int plane,float centerP) {
		List<Cube> list = new ArrayList<Cube>();
		List<Cube> neighbour = getCubes(plane,centerP+cubeSize);
		
		if( neighbour.size() <4 )
			list.addAll(neighbour);
		
		neighbour = getCubes(plane,centerP-cubeSize);
		
		if( neighbour.size() <4 )
			list.addAll(neighbour);		
		return list;
	}

	@Override
	public List<Cube> getAllCubes() {
		return Arrays.asList(cubes);
	};
}
