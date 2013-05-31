package com.yinong.cubegame.util;

import com.yinong.cubegame.model.Vertex;

public class Vect3D {
	public float x;
	public float y;
	public float z;
	
	public Vect3D() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vect3D(float x,float y,float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vect3D(float a,Vect3D v) {
		x = a*v.x;
		y = a*v.y;
		z = a*v.z;
	}
	
	public Vect3D sub(Vect3D b) {
		return new Vect3D(x - b.x,y - b.y,z - b.z);
	}
	
	public void add(Vect3D b) {
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public static Vect3D sub(Vect3D a,Vect3D b) {
		return new Vect3D( a.x - b.x, a.y - b.y,a.z - b.z);
	}
	
	public static Vect3D add(Vect3D a,Vect3D b) {
		return new Vect3D( a.x + b.x, a.y + b.y,a.z + b.z);
	}
	
	public float getNormSq() {
		return x*x + y*y + z*z;
	}
	
	public float dot(Vect3D v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public static Vect3D cross(Vect3D v1,Vect3D v2) {
		return new Vect3D(v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
	}
	
	@Override
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
	}
}
