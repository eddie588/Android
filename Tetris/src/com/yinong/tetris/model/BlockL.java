package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockL extends Block {
	private int[][] spaces=  {
			{0,0,1,0,0,-1,0,-2},
			{0,0,0,1,1,0,2,0},
			{0,0,-1,0,0,1,0,2},
			{0,0,0,-1,-1,0,-2,0}
	};
	
	public BlockL() {
		setColor(Color.YELLOW);
	}


	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
