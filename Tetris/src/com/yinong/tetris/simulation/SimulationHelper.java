package com.yinong.tetris.simulation;

import java.util.HashSet;
import java.util.Set;

import com.yinong.tetris.model.Position;
import com.yinong.tetris.model.TetrisGame;

/**
 * The helper class assuming the game board does not change during the helper's life cycle.
 * @author Yinong Jiang
 *
 */

public class SimulationHelper  {
//	TetrisGame game;
	int rows;
	int cols;
	int[] gameBoard;
	static int[] COL_MASK={0x1,0x2,0x4,0x8,0x10,0x20,0x40,0x80,0x100,0x200,0x400,0x800};
	static int[] FULL_MASK={0x1,0x3,0x7,0xf,0x1f,0x3f,0x7f,0xff,0x1ff,0x3ff,0x7ff,0xff};
	

	public SimulationHelper(TetrisGame game) {
		rows = game.getRows();
		cols = game.getColumns();
		gameBoard = new int[rows];
		for(int y=0;y<rows;y++) {
			int value = 0;
			for(int x=0;x<cols;x++) {
				if( game.isSpaceUsed(x, y) ) {
					value |= COL_MASK[x];
				}
			}
			gameBoard[y] = value;
		}	
	}

	
	boolean isSpaceUsed(int x,int y) {
		if( x < 0 || x>= cols || y < 0 || y >= rows)
			return false;
		return (gameBoard[y] & COL_MASK[x]) != 0;
	}
	
	public int getAllHolesBelowRow(int row) {
		int holes = 0;

		for(int x=0;x<cols;x++) {
			for(int y=row+1;y<rows;y++) {
				if( !isSpaceUsed(x, y))
					holes++;
			}
		}
		return holes;
	}	
	
	public int getLowestY(final Position[] spaces) {
		for(int y=0;y<rows;y++) {
			for(int i=0;i<spaces.length;i++) {
				if( isSpaceUsed(spaces[i].x,spaces[i].y+y) )
					return y-1;
			}
		}
		return rows-1;
	}	
	
	public int getLowestY(final Position[] spaces,final Position[] previousSpaces) {
		for(int y=0;y<rows;y++) {
			for(int i=0;i<spaces.length;i++) {
				if( isSpaceUsed(spaces[i].x,spaces[i].y+y) )
					return y-1;
				for(int j=0;j<previousSpaces.length;j++) {
					if( spaces[i].x == previousSpaces[j].x && spaces[i].y+y ==  previousSpaces[j].y)
						return y-1;
				}
			}
		}
		return rows-1;
	}
	
	public int getAllHolesBelowSpaces(final Position[] spaces) {
		Set<Position> set = new HashSet<Position>();
		
		for(int i=0;i<spaces.length;i++) {
			for(int r=spaces[i].y+1;r<rows;r++) {
				if( !isSpaceUsed(spaces[i].x, r) && !usesSpace(spaces, spaces[i].x, r));
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
			if(spaces[i].y+1>=rows)
				continue;
			if( !isSpaceUsed(spaces[i].x, spaces[i].y+1) &&  !usesSpace(spaces,spaces[i].x, spaces[i].y+1))
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
		for(int row=0;row<rows;row++) {
			boolean cleared = true;
			for(int col=0;col<cols;col++)	{
				if( !isSpaceUsed(col,row) && !usesSpace(spaces,col,row)) {
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
		benefit -= 100 * ((float) (rows - getAverageY(spaces)));

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
