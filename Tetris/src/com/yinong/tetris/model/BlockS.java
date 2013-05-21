package com.yinong.tetris.model;

import android.graphics.Color;

public class BlockS extends Block {
	private int[][] spaces=  {
			{0,0,-1,0,0,-1,1,-1},
			{0,0,0,-1,1,0,1,1},
			{0,0,1,0,0,1,-1,1},
			{0,0,0,1,-1,0,-1,-1}

	};
	
	public BlockS() {
		setColor(Color.GREEN);
	}


	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return spaces[orientation];
	}
}
