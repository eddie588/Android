package com.yinong.tetris.simulation;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Point;

import com.yinong.tetris.model.Position;
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
	
	public int getAllHolesBelowSpaces(Position[] spaces) {
		Set<Position> set = new HashSet<Position>();
		
		for(int i=0;i<spaces.length;i++) {
			for(int r=spaces[i].y+1;r<game.getRows();r++) {
				if( !game.isSpaceUsed(spaces[i].x, r) && !usesSpace(spaces, spaces[i].x, r));
					set.add(spaces[i]);
			}
		}
		return set.size();
	}
	
	/**
	 * Get New holes that will be created by this piece
	 * @param spaces
	 * @return
	 */
	public int getNewHolesBelowSpaces(Position[] spaces) {
		int holes = 0;
		for(int i=0;i<spaces.length;i++) {
			if(spaces[i].y+1>=game.getRows())
				continue;
			if( !game.isSpaceUsed(spaces[i].x, spaces[i].y+1) &&  !usesSpace(spaces,spaces[i].x, spaces[i].y+1))
				holes++;

		}
		return holes;
	}	
		
	
	public float getAverageY(Position[] spaces) {
		float ay = 0 ;
		for(int i=0;i<spaces.length;i++) {
			ay += spaces[i].y;
		} 
		return ay/(spaces.length);
	}
	
	public float getAverageX(Position[] spaces) {
		float ax = 0 ;
		for(int i=0;i<spaces.length;i++) {
			ax += spaces[i].x;
		} 
		return ax/(spaces.length);
	}	
	
	/**
	 * Get the number of cleared cells if spaces will be filled.
	 * @param spaces
	 * @return
	 */
	
	public int getClearedRows(Position[] spaces) {
		int clearRows = 0;
		for(int row=0;row<game.getRows();row++) {
			boolean cleared = true;
			for(int col=0;col<game.getColumns();col++)	{
				if( !game.isSpaceUsed(col,row) || !usesSpace(spaces,col,row)) {
					cleared = false;
					break;
				}
			}
			if(cleared)
				clearRows++;
		}
		return clearRows;
	}	
	
	/**
	 * Check if any of the cells in a block uses the space
	 * @param spaces
	 * @param col
	 * @param row
	 * @return
	 */
	
	public boolean usesSpace(Position[] spaces,int col,int row) {
		for(int i=0;i<spaces.length;i++) {
			if( col == spaces[i].x && row == spaces[i].y)
				return true;
		}
		return false;
	}
}
