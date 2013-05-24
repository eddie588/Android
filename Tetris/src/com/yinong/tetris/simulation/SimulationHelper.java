package com.yinong.tetris.simulation;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Point;

import com.yinong.tetris.model.TetrisGame;

public class SimulationHelper  {
	TetrisGame game;
	
	public SimulationHelper(TetrisGame game) {
		this.game = game;
	}
	
	public int getAllHolesBelowRow(int row) {
		int holes = 0;

		for(int x=0;x<game.getColumns();x++) {
			for(int y=row;y<game.getRows();y++) {
				if( !game.isSpaceUsed(x, y))
					holes++;
			}
		}
		return holes;
	}	
	
	public int getHolesBelowSpaces(Point[] spaces) {
		Set<Point> set = new HashSet<Point>();
		
		for(int i=0;i<spaces.length;i++) {
			for(int r=spaces[i].y+1;r<game.getRows();r++) {
				if( !game.isSpaceUsed(spaces[i].x, r))
					set.add(spaces[i]);
			}
		}
		return set.size();
	}
		
	
	public float getAverageY(Point[] spaces) {
		float ay = 0 ;
		for(int i=0;i<spaces.length;i++) {
			ay += spaces[i].y;
		} 
		return ay/(spaces.length);
	}
	
	public float getAverageX(Point[] spaces) {
		float ax = 0 ;
		for(int i=0;i<spaces.length;i++) {
			ax += spaces[i].x;
		} 
		return ax/(spaces.length);
	}	
	
	public int getClearedRows(Point[] spaces) {
		int clearRows = 0;
		for(int row=0;row<game.getRows();row++) {
			boolean cleared = true;
			for(int col=0;col<game.getColumns();col++)	{
				if( !game.isSpaceUsed(col,row) && !usesSpace(spaces,col,row)) {
					cleared = false;
					break;
				}
			}
			if(cleared)
				clearRows++;
		}
		return clearRows;
	}	
	
	public boolean usesSpace(Point[] spaces,int col,int row) {
		for(int i=0;i<spaces.length;i++) {
			if( col == spaces[i].x && row == spaces[i].y)
				return true;
		}
		return false;
	}
}
