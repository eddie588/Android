package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockJ extends Block {
	private int[][] spaces=  {
			{0,0,-1,0,0,-1,0,-2},
			{0,0,0,-1,1,0,2,0},
			{0,0,0,1,0,2,1,0},
			{0,0,0,1,-1,0,-2,0}
	};
	
	public BlockJ() {
		setColor(Color.RED);		
	}


	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
