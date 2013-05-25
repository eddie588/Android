package com.yinong.tetris.model;

public class Position {
	public int x;
	public int y;
	
	public Position(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		return x==((Position)o).x && y == ((Position)o).y;
	}
}
