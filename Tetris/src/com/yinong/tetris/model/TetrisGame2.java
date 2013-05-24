package com.yinong.tetris.model;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.graphics.Point;


public class TetrisGame2 extends TetrisGame {
	
	Block createNewBlock() {
		Block block = super.createNewBlock();
		block.initRandomColors();
		return block;
	}	
	
	void clearCompletedCells() {
		super.clearCompletedCells();
		
		int clearedCells = 0;
		SortedSet<Block> cells = new TreeSet<Block>(new Comparator<Block>() {
			public int compare(Block a, Block b) {
				//	order by left to right, top to bottom
				if( a.getY() < b.getY())
					return -1;
				if( a.getY() > b.getY())
					return 1;
				if( a.getX() < b.getX())
					return -1;
				if( a.getX() > b.getX())
					return 1;
				// both X and Y equals
				return 0;
				
			}
		});
		
		//	Check all cells that need to be cleared.
		for(int y=0;y<getRows();y++) {
			for(int x=0;x<getColumns();x++) {
				getAllMatchedCells(cells,x,y);
			}
		}
		
		//	for all matched cells, move cell on top of them down
		for(Block block:cells) {
			moveCelledAboveClearedCell(block.getX(),block.getY());
		}
		//score += cells.size()*5;
	}
	
	/**
	 * Clear adjacentCell if they match color. It is assumed it is always called from top 
	 * to bottom.
	 * @param x
	 * @param y
	 * @return
	 */
	
	void getAllMatchedCells(SortedSet<Block> cells,int x,int y) {
		Block block = getBlockAt(x,y);
		
		if( block == null ) {
			return ;
		}
		
		Block block2 = getBlockAt(x-1,y);
		int c;
		if( block2 != null && block2.getColor() == block.getColor() ) {
			//cleared += clearMatchedCells(block,x-1,y);
			cells.add(block2);
			cells.add(block);
		}
		
		block2 = getBlockAt(x+1,y);
		if( block2 != null && block2.getColor() == block.getColor() ) {
			cells.add(block2);
			cells.add(block);
		}
		

		block2 = getBlockAt(x,y+1);		
		if( block2 != null && block2.getColor() == block.getColor() ) {
			cells.add(block2);
			cells.add(block);			
		}

	}
	
	void moveCelledAboveClearedCell(int x,int y) {
		for(int i=y-1;i>=0;i--) {
			BlockDot block = getBlockAt(x,i);
			setBlockAt(x,i+1,block);
			if( block!=null )
				block.move(Block.DOWN);
		}		
	}

}