package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockO extends Block {
	private int[][] spaces=  {
			{0,0,0,-1,1,-1,1,0},
			{0,0,1,0,1,1,0,1},
			{0,0,0,1,-1,1,-1,0},
			{0,0,-1,0,-1,-1,0,-1}

	};
	
	public BlockO() {
		setColor(Color.BLUE);
	}


	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
