package com.yinong.cubegame.model;

import com.yinong.cubegame.util.Vect3D;

public class Triangle {
	public Vect3D v1;
	public Vect3D v2;
	public Vect3D v3;
	
	public Triangle(Vect3D v1,Vect3D v2,Vect3D v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
}
