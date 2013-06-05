package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.List;

import com.yinong.cubegame.util.Vect3D;

public abstract class CubeGame {
	public static float EPSILON = 0.00001f;
	
//	public static final int PLANE_X = 0;
//	public static final int PLANE_Y = 1;
//	public static final int PLANE_Z = 2;
//	
//	public static final int FACE_FRONT = 0;
//	public static final int FACE_BACK = 1;
//
//	public static final int FACE_TOP = 2;
//	public static final int FACE_BOTTOM = 3;
//
//	public static final int FACE_LEFT = 4;
//	public static final int FACE_RIGHT = 5;

	
	protected float cubeSize = 1f;
	protected float cubeMargin = 0.05f;
	protected Vect3D cubeOrigin = new Vect3D(0,0,0);
	
	
	public boolean turnFace(Vect3D p1, Vect3D p2) {
		return false;
	}

	public abstract List<Cube> getCubes(int face);

	public abstract void shuffle(int count);

	public abstract List<Cube> getAllCubes();

	public abstract Vect3D getPosition();

	
	public Vect3D getCubeOrigin() {
		return cubeOrigin;
	}

	public void setCubeOrigin(Vect3D cubeOrigin) {
		this.cubeOrigin = cubeOrigin;
	}

	protected void setupColors() {
		for(Cube cube:getCubes(Cube.CUBE_FRONT)) {
			cube.setColor(Cube.CUBE_FRONT,0.75f,0f,0f,1f); // RED
		}
		
		for(Cube cube:getCubes(Cube.CUBE_BACK)) {
			cube.setColor(Cube.CUBE_BACK,1,0.5f,0f,0f);   // ORANGE			
		}
		
		for(Cube cube:getCubes(Cube.CUBE_LEFT)) {
			cube.setColor(Cube.CUBE_LEFT,1f,1f,1f,1f);	// WHITE
		}
		
		for(Cube cube:getCubes(Cube.CUBE_RIGHT)) {
			cube.setColor(Cube.CUBE_RIGHT,1f,1f,0f,1f);  // YELLOW
		}
		
		for(Cube cube:getCubes(Cube.CUBE_TOP)) {
			cube.setColor(Cube.CUBE_TOP,0f,0f,1f,1f);	// BLUE
		}
		
		for(Cube cube:getCubes(Cube.CUBE_BOTTOM)) {
			cube.setColor(Cube.CUBE_BOTTOM,0f,0.75f,0f,1f);  // GREEN
		}			
	}	
	
	public List<Cube> getCubes(int plane,float centerP) {
		List<Cube> cubes = new ArrayList<Cube>();
		for(Cube cube:getAllCubes()) {
			switch(plane) {
			case Cube.PLANE_X:
				if( Math.abs(cube.getCenter().x-centerP) < EPSILON + cubeSize/2 )
					cubes.add(cube);
				break;
			case Cube.PLANE_Y:
				if( Math.abs(cube.getCenter().y-centerP) < EPSILON + cubeSize/2)
					cubes.add(cube);
				break;
			case Cube.PLANE_Z:
				if( Math.abs(cube.getCenter().z-centerP) < EPSILON + cubeSize/2)
					cubes.add(cube);
				break;			
			}
		}
		return cubes;
	}
	
	
	public List<Cube> getStickyCubes(int plane,float centerP) {
		return new ArrayList<Cube>();
	}
	
	public boolean isValidTurn(int plane,float centerP,float angle) {
		return true;
	}
	
	public boolean isSwipeOnFrontBack(Vect3D p1,Vect3D p2,float unitsFromCenter) {
		return (Math.abs(p1.z - unitsFromCenter* cubeSize+0.5*cubeMargin) < EPSILON && Math.abs(p2.z - unitsFromCenter
				* cubeSize+0.5*cubeMargin) < EPSILON )
				|| ((Math.abs(p1.z + unitsFromCenter * cubeSize-0.5*cubeMargin) < EPSILON && Math.abs(p2.z
						+ unitsFromCenter * cubeSize - 0.5*cubeMargin) < EPSILON ));
	}
	
	public boolean isSwipeOnLeftRight(Vect3D p1,Vect3D p2,float unitsFromCenter) {
		return (Math.abs(p1.x - unitsFromCenter* cubeSize+0.5*cubeMargin) < EPSILON && Math.abs(p2.x - unitsFromCenter
				* cubeSize+0.5*cubeMargin) < EPSILON )
				|| ((Math.abs(p1.x + unitsFromCenter * cubeSize-0.5*cubeMargin) < EPSILON && Math.abs(p2.x
						+ unitsFromCenter * cubeSize - 0.5*cubeMargin) < EPSILON ));
	}	
	
	boolean isSwipeOnTopBottom(Vect3D p1,Vect3D p2,float unitsFromCenter) {
		return (Math.abs(p1.y - unitsFromCenter* cubeSize+0.5*cubeMargin) < EPSILON && Math.abs(p2.y - unitsFromCenter
				* cubeSize+0.5*cubeMargin) < EPSILON )
				|| ((Math.abs(p1.y + unitsFromCenter * cubeSize-0.5*cubeMargin) < EPSILON && Math.abs(p2.y
						+ unitsFromCenter * cubeSize - 0.5*cubeMargin) < EPSILON ));
	}		
}
