package com.yinong.cubegame.model;

public class Vertex {
	public float x;
	public float y;
	public float z;
	
	public Vertex() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vertex(float x,float y,float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void sub(Vertex a,Vertex b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
	}
	
	@Override
	public String toString() {
		return String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
	}	
}
