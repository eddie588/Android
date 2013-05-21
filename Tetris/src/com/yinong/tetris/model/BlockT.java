package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockT extends Block {
	private int[][] spaces=  {
			{0,0,-1,0,1,0,0,-1},
			{0,0,0,-1,0,1,1,0},
			{0,0,1,0,-1,0,0,1},
			{0,0,0,1,0,-1,-1,0}
	};
	
	public BlockT() {
		setColor(Color.MAGENTA);
	}

	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
