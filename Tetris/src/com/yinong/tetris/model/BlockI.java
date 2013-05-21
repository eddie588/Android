package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockI extends Block {
	private int[][] spaces=  {
			{0,0,-1,0,1,0,2,0},
			{0,0,0,-1,0,1,0,2},
			{0,0,1,0,-1,0,-2,0},
			{0,0,0,1,0,-1,0,-2}
	};

	public BlockI() {
		setColor(Color.CYAN);	
	}
	
	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
