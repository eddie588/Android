package com.yinong.tetris.simulation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yinong.tetris.model.Position;
import com.yinong.tetris.model.TetrisGame;
import com.yinong.tetris.simulation.Simulation1.NewPosition;

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
	int[] tempBoard;
	static int[] COL_MASK={0x1,0x2,0x4,0x8,0x10,0x20,0x40,0x80,0x100,0x200,0x400,0x800};
	static int[] FULL_MASK={0x1,0x3,0x7,0xf,0x1f,0x3f,0x7f,0xff,0x1ff,0x3ff,0x7ff,0xff};
	int fullRow =0;

	public SimulationHelper(TetrisGame game) {
		rows = game.getRows();
		cols = game.getColumns();
		gameBoard = new int[rows];
		tempBoard = new int[rows];
		for(int y=0;y<rows;y++) {
			int value = 0;
			for(int x=0;x<cols;x++) {
				if( game.isSpaceUsed(x, y) ) {
					value |= COL_MASK[x];
				}
			}
			gameBoard[y] = value;
			tempBoard[y] = 0;
		}	
		
		//	Setup full row value
		fullRow= 1;
		for(int i=1;i<cols;i++) {
			fullRow = fullRow<<1;
			fullRow |= 1;
		}		
	}

	
	boolean isSpaceUsed(int x,int y) {
		if( x < 0 || x>= cols || y < 0 || y >= rows)
			return false;
		return (( gameBoard[y] | tempBoard[y]) & COL_MASK[x]) != 0;
	}
	
	boolean isSpaceUsed(Position p) {
		return isSpaceUsed(p.x,p.y);
	}
	
	boolean areSpacesUsed(Position[] p) {
		for(int i=0;i<p.length;i++) {
			if( isSpaceUsed(p[i]) )
				return true;
		}
		return false;
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
	
	public int getLandingY(final Position[] spaces) {
		for(int y=0;y<rows;y++) {
			for(int i=0;i<spaces.length;i++) {
				if( isSpaceUsed(spaces[i].x,spaces[i].y+y) || spaces[i].y+y >=rows )
					return y-1;
			}
		}
		return rows-1;
	}	
	
	void markPosition(int x,int y) {
		if( x < 0 || x>= cols || y < 0 || y >= rows)
			return ;
		tempBoard[y] |= COL_MASK[x] ;
	}
	
	void unmarkPosition(int x,int y) {
		if( x < 0 || x>= cols || y < 0 || y >= rows)
			return ;
		tempBoard[y] &= ~COL_MASK[x];
	}	
	
	public void markPositions(Position[] positions) {
		for(int i=0;i<positions.length;i++) {
			markPosition(positions[i].x,positions[i].y);
		}
	}
	
	public void unmarkPositions(Position[] positions) {
		for(int i=0;i<positions.length;i++) {
			unmarkPosition(positions[i].x,positions[i].y);
		}
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
	
	public float getAverageY() {
		float ay = 0 ;
		int p = 0;
		for(int i=0;i<tempBoard.length;i++) {
			int count=0;
			// count 1s.
			int n = tempBoard[i];
			while (n > 0) {
				count++;
				n = n & (n - 1);
			}
			ay += i*count;
			p += count;
		} 
		return p==0?0:ay/p;
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
	
	private  int clearBenefits[] = {0,1,3,5,8,16,32};
	
	public int getBenefit (final Position[] spaces) {
		int benefit;

//		benefit = 500 * clearBenefits[getClearedRows()];
//
//		// penalties for higher average Y
//		benefit -= 100 * ((float) (rows - getAverageY(spaces)));
//
//		// // penalties for holes
//		// benefit -= 0.5*getHolesBelowMe();
//
//		// // penalties for holes
//		benefit -= 500 * getAllHolesBelowRow((int) getAverageY(spaces)) / 5;
//		
//		
//		// penalties for holes
//		benefit -= 800 * getAllHolesBelowSpaces(spaces);
//		
//		// penalties for new holes
//		benefit -= 200 * getNewHolesBelowSpaces(spaces);
		

		benefit = 500 * clearBenefits[getClearedRows()];

		// penalties for higher average Y
		benefit -= 100 * ((float) (rows - getAverageY()));

		// // penalties for holes
		// benefit -= 0.5*getHolesBelowMe();

		// // penalties for holes
		
		//benefit -= 100* getUnfilled((int) getAverageY(spaces));
		benefit -= 100* getUnfilled((int) getAverageY());		
		//benefit -= 100* getUnfilled((int) getMinY()+1);
		
		//System.out.println("average: " + ((int) getAverageY(spaces)) + " min " + getMinY());
		
		
		// penalties for holes
		benefit -= 800 * getAllHolesBelowSpaces(spaces);
//		
//		// penalties for new holes
		benefit -= 200 * getNewHolesBelowSpaces(spaces);	
		return benefit;
	}	
	
	void printBoard() {
		for(int y=0;y<rows;y++) {
			if( gameBoard[y] == 0 )
				continue;
			System.out.print("" + (y%10) + ":  ");
			for(int x=0;x<cols;x++) {
				if( isSpaceUsed(x,y)) 
					System.out.print("*");
				else
					System.out.print("-");
			}
			System.out.println("");
		}
	}
	
	int getClearedRows() {
		int cleared = 0;
		for(int y=0;y<rows;y++) {
			if ( (gameBoard[y] | tempBoard[y]) == fullRow )
				cleared++;
		}
		return cleared;
	}
	
	int getMinY() {
		for(int y=0;y<rows;y++) {
			if ( gameBoard[y] != 0 )
				return y;
		}
		return rows;
	}
	
	
	/**
	 * Get all holes in the current game board
	 * @return
	 */
	int getAllHoles() {
		int holes = 0;
		for(int x=0;x<cols;x++) {
			boolean start = false;
			for(int y=0;y<rows;y++) {
				if( !start && (gameBoard[y] & COL_MASK[x]) != 0 ) {
					start = true;
				}
				if( start && (gameBoard[y] & COL_MASK[x]) == 0 ) {
					holes++;
				}
			}
		}
		return holes;
	}
	
	int getUnfilled(int row) {
		int unfilled = 0;
		for (int y =row; y < rows; y++) {
			int n = gameBoard[y] | tempBoard[y];
			// count 1s in number
			int count=0;
			while (n > 0) {
				count++;
				n = n & (n - 1);
			}
			unfilled += cols-count;
		}
		return unfilled;
	}
	
	int getWeightedUnfilled() {
		int unfilled = 0;
		for (int y = getMinY(); y < rows; y++) {
			int n = gameBoard[y];
			// count 1s in number
			int count=0;
			while (n > 0) {
				count++;
				n = n & (n - 1);
			}
			unfilled += (cols-count)*(rows-y);
		}
		return unfilled;
	}
}
