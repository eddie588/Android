package com.yinong.cubegame.model;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Cube3By3 {
	Cube[] cubes;
	
	public Cube3By3() {
		cubes = new Cube[27];
		int p=0;
		int x=0;
		int y=0;
		int z=0;
		for(x=-1;x<2;x++) {
			for(y=-1;y<2;y++) {
				for(z=1;z>-2;z--) {
					cubes[p++] = new Cube( 1f*x,1f*y,1f*z,0.5f );
				}
			}
		}		
	}
	
	public void draw(GL10 gl,float rotation) {
		for (int i = 0; i < 27; i++) {
			gl.glLoadIdentity();

			gl.glTranslatef(0.0f, 0.0f, -10.0f);

			gl.glRotatef(rotation, 1.0f, 1.0f, 1.0f);

			if( cubes[i] != null )
				cubes[i].draw(gl);
			gl.glLoadIdentity();
		}	
	}
	
	private int[] faces={
		0,3,6,9,12,15,18,21,24	
	};
	
	public List<Cube> getCubes(int face) {
		List<Cube> list = new ArrayList<Cube>();
		for(int i=0;i<9;i++) {
			list.add(cubes[faces[9*face+i]]);
		}
		return list;
	}
	
	public void rotate(int face) {
		List<Cube> list = getCubes(0);
		
		for(Cube cube:list) {
			cube.rotate(45f,0f,0f,-1f);
		}
	}	
}
