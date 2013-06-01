package com.yinong.cubegame.util;

import android.opengl.Matrix;

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
	
	public Vect3D transform(float[] matrix) {
		float[] inV = new float[4];
		float[] outV = new float[4];
		
		inV[0] = x;
		inV[1] = y;
		inV[2] = z;
		inV[3] = 1;
		
		Matrix.multiplyMV(outV, 0, matrix, 0, inV, 0);
		
		return new Vect3D(outV[0]/outV[3],outV[1]/outV[3],outV[2]/outV[3]);
	}
	
	@Override
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
	}
}
