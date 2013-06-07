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
	
	

	public List<Cube> getCubes(int face) {
		if( face == Cube.CUBE_FRONT ) {
			float z = -1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().z > z)
					z = cube.getCenter().z;
			}
			return getCubes(Cube.PLANE_Z,z);
		}
		if( face == Cube.CUBE_BACK ) {
			float z = 1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().z < z)
					z = cube.getCenter().z;
			}
			return getCubes(Cube.PLANE_Z,z);
		}	
		
		if( face == Cube.CUBE_LEFT ) {
			float x = 1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().x < x)
					x = cube.getCenter().x;
			}
			return getCubes(Cube.PLANE_X,x);
		}
		if( face == Cube.CUBE_RIGHT ) {
			float x = -1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().x > x)
					x = cube.getCenter().x;
			}
			return getCubes(Cube.PLANE_X,x);
		}		
		
		if( face == Cube.CUBE_TOP ) {
			float y = -1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().y > y)
					y = cube.getCenter().y;
			}
			return getCubes(Cube.PLANE_Y,y);
		}
		if( face == Cube.CUBE_BOTTOM ) {
			float y = 1000f;
			for(Cube cube:getAllCubes()) {
				if(cube.getCenter().y < y)
					y = cube.getCenter().y;
			}
			return getCubes(Cube.PLANE_Y,y);
		}				
		
		return new ArrayList<Cube>();
	}

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
	
	/**
	 * Check if all faces have matching colors
	 * @return
	 */
	
	public boolean isSolved() {
		int lastColor = -1;
		for(Cube cube:getCubes(Cube.CUBE_FRONT)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_FRONT) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_FRONT);
		}
		
		lastColor = -1;
		for(Cube cube:getCubes(Cube.CUBE_BACK)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_BACK) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_BACK);	
		}
		
		lastColor = -1;		
		for(Cube cube:getCubes(Cube.CUBE_LEFT)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_LEFT) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_LEFT);	
		}
		
		lastColor = -1;
		for(Cube cube:getCubes(Cube.CUBE_RIGHT)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_RIGHT) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_RIGHT);	
		}
		
		lastColor = -1;
		for(Cube cube:getCubes(Cube.CUBE_TOP)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_TOP) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_TOP);	
		}
		
		lastColor = -1;
		for(Cube cube:getCubes(Cube.CUBE_BOTTOM)) {
			if( lastColor != -1 && lastColor != cube.getFaceColor(Cube.CUBE_BOTTOM) ) 
				return false;
			lastColor = cube.getFaceColor(Cube.CUBE_BOTTOM);	
		}	
		return true;
	}	
	
	
	public List<Cube> getCubes(int plane,float centerP) {
		List<Cube> cubes = new ArrayList<Cube>();
		for(Cube cube:getAllCubes()) {
			switch(plane) {
			case Cube.PLANE_X:
				if( Math.abs(cube.getCenter().x-centerP) < EPSILON )
					cubes.add(cube);
				break;
			case Cube.PLANE_Y:
				if( Math.abs(cube.getCenter().y-centerP) < EPSILON)
					cubes.add(cube);
				break;
			case Cube.PLANE_Z:
				if( Math.abs(cube.getCenter().z-centerP) < EPSILON )
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
	
	public void printColors() {
		System.out.println("\nfront:");
		for(Cube cube:getCubes(Cube.CUBE_FRONT)) {

			System.out.print(" " + cube.getFaceColor(Cube.CUBE_FRONT));
		}
		
		System.out.println("\nBack:");
		for(Cube cube:getCubes(Cube.CUBE_BACK)) {
			System.out.print(" " + cube.getFaceColor(Cube.CUBE_BACK));
		}
		
		System.out.println("\nLeft:");	
		for(Cube cube:getCubes(Cube.CUBE_LEFT)) {
			System.out.print(" " + cube.getFaceColor(Cube.CUBE_LEFT));
		}
		
		System.out.println("\nRight:");	
		for(Cube cube:getCubes(Cube.CUBE_RIGHT)) {
			System.out.print(" " + cube.getFaceColor(Cube.CUBE_RIGHT));
		}
		
		System.out.println("\nTop:");
		for(Cube cube:getCubes(Cube.CUBE_TOP)) {
			System.out.print(" " + cube.getFaceColor(Cube.CUBE_TOP));
		}
		
		System.out.println("\nBottom:");
		for(Cube cube:getCubes(Cube.CUBE_BOTTOM)) {
			System.out.print(" " + cube.getFaceColor(Cube.CUBE_BOTTOM));
		}			
	}

	public void updateColors(int plane, float centerP, float angle) {
		for(Cube cube:getCubes(plane, centerP)) {
			cube.turnFaceColors(plane, angle);
		}
	}
	
	public void turnLayer(int plane,float centerP,float angle) {
		List<Cube> list = getCubes(plane,centerP);
		
		list.addAll(getStickyCubes(plane, centerP));

		for (Cube cube : list) {
			switch (plane) {
			case Cube.PLANE_Z:
				cube.rotate(angle, 0f, 0f, -1f);
				break;
			case Cube.PLANE_X:
				cube.rotate(angle, -1f, 0f, 0f);
				break;
			case Cube.PLANE_Y:
				cube.rotate(angle, 0f, -1f, 0f);
				break;
			}
		}		
	}
}
