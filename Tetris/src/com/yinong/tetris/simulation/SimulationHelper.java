package com.yinong.tetris.simulation;

import java.util.HashSet;
import java.util.Set;

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
			for(int y=row+1;y<game.getRows();y++) {
				if( !game.isSpaceUsed(x, y))
					holes++;
			}
		}
		return holes;
	}	
	
	public int getLowestY(final Position[] spaces) {
		for(int y=0;y<game.getRows();y++) {
			for(int i=0;i<spaces.length;i++) {
				if( game.isSpaceUsed(spaces[i].x,spaces[i].y+y) )
					return y-1;
			}
		}
		return game.getRows()-1;
	}
	
	public int getAllHolesBelowSpaces(final Position[] spaces) {
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
	public int getNewHolesBelowSpaces(final Position[] spaces) {
		int holes = 0;
		for(int i=0;i<spaces.length;i++) {
			if(spaces[i].y+1>=game.getRows())
				continue;
			if( !game.isSpaceUsed(spaces[i].x, spaces[i].y+1) &&  !usesSpace(spaces,spaces[i].x, spaces[i].y+1))
				holes++;

		}
		return holes;
	}	
		
	
	public float getAverageY(final Position[] spaces) {
		float ay = 0 ;
		for(int i=0;i<spaces.length;i++) {
			ay += spaces[i].y;
		} 
		return ay/(spaces.length);
	}
	
	public float getAverageX(final Position[] spaces) {
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
	
	public int getClearedRows(final Position[] spaces) {
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
	
	/**
	 * Check if any of the cells in a block uses the space
	 * @param spaces
	 * @param col
	 * @param row
	 * @return
	 */
	
	public boolean usesSpace(final Position[] spaces,int col,int row) {
		for(int i=0;i<spaces.length;i++) {
			if( col == spaces[i].x && row == spaces[i].y)
				return true;
		}
		return false;
	}
	
	private  int clearBenefits[] = {0,1,3,5,8};
	
	public int getBenefit (final Position[] spaces) {
		int benefit;

		benefit = 500 * clearBenefits[getClearedRows(spaces)];

		// penalties for higher average Y
		benefit -= 100 * ((float) (game.getRows() - getAverageY(spaces)));

		// // penalties for holes
		// benefit -= 0.5*getHolesBelowMe();

		// // penalties for holes
		benefit -= 500 * getAllHolesBelowRow((int) getAverageY(spaces)) / 5;

		// penalties for holes
		benefit -= 800 * getAllHolesBelowSpaces(spaces);
		// penalties for new holes
		benefit -= 200 * getNewHolesBelowSpaces(spaces);
		return benefit;
	}
}
