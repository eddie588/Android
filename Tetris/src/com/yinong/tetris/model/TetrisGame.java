package com.yinong.tetris.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.util.Log;

public class TetrisGame  {
	public static final int ROWS=18;
	public static final int COLUMNS=10;
	private boolean gameOver=false;
	private boolean running = true;
	
	ArrayList<TetrisGameListener> gameListeners = new ArrayList<TetrisGameListener>();
	
	int score=0;
	int highScore=0;

	
	private HashMap<String,BlockDot> allBlocks = new HashMap<String,BlockDot> ();
	
	private String error ="";
	
	private ArrayList<BlockDot> deletedBlocks = new ArrayList<BlockDot>();
	
	
	//ArrayList<BlockDot> blocks;
	Block	activeBlock;
	Block	nextBlock;

	int	periodActive = 500;
	int periodOther = 100;
	
	
	ScoreCallout callout=null;
	
	
	public TetrisGame() {
		activeBlock = new BlockT();
		activeBlock.setX(4);
		activeBlock.setY(0);	
		resetGame();
	}
	

	public void addListener(TetrisGameListener listener) {
		gameListeners.add(listener);
	}
	

	public boolean isRunning() {
		return running;
	}
	

	public synchronized int getScore() {
		return score;
	}

	public ScoreCallout getCallout() {
		return callout;
	}


	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	
	public synchronized boolean isGameOver() {
		return gameOver;
	}

	
	public synchronized Block getActiveBlock() {
		return activeBlock;
	}
	
	public Block getNextBlock() {
		return nextBlock;
	}

	
	public synchronized void resetGame() {
		if( score > highScore)
		Log.d("Tetris","Game started");
		activeBlock = createNewBlock();
		nextBlock = createNewBlock();
		gameOver = false;
		score = 0;
		allBlocks.clear();
		deletedBlocks.clear();
	}

	
	public synchronized ArrayList<BlockDot> getBlocks() {
		ArrayList<BlockDot> blocks = new ArrayList<BlockDot>();
		
		for(int y=0;y<ROWS;y++) {
			for(int x=0;x<COLUMNS;x++) {
				BlockDot block = getBlockAt(x,y);
				if( block != null)
					blocks.add(block);
			}
		}
		return blocks;
	}

	
	public ArrayList<BlockDot> getDeletedBlocks() {
		return deletedBlocks;
	}

	
	public synchronized void move(int direction) {
		Log.d("Tetris","Move " + direction);
		if( canMove(activeBlock,direction) ) {
			activeBlock.move(direction);
		}
	}

	
	public synchronized void update() {
		long now = System.currentTimeMillis();
		if( !running )
			return;
		
		if( isGameOver() )
			return;
		
		updateActiveBlock(now);
		updateOtherBlocks(now);
		if( callout != null )
			callout.update(now);
	}
	
	private long lastUpdateActive=0;
	private void updateActiveBlock(long now) {
	
		if( now - lastUpdateActive < periodActive )
			return;
		lastUpdateActive = now;
		try {
			if (canMove(activeBlock,Block.DOWN)) {
				activeBlock.move(Block.DOWN);
			} else {
				blockLanded();
			}
		} catch (Exception e) {
			error = e.getStackTrace()[0].toString();
		}		
		
	}
	
	private long lastUpdateOther = 0;
	private void updateOtherBlocks(long now) {
		if( now - lastUpdateOther < periodOther )
			return;
		lastUpdateOther = now;
		for(int y=0;y<ROWS;y++) {
			for(int x=0;x<COLUMNS;x++) {
				BlockDot block = getBlockAt(x,y);
				if( block != null)
					block.update(now);
			}
		}	
		
		//	Update delete blocks for animation
		for(Iterator<BlockDot> it = deletedBlocks.iterator();it.hasNext(); ) {
			BlockDot block = it.next();
			block.update(now);
			if (block.getMoving() == 0)
				it.remove();
		}
	}
	
	public String getErrorString() {
		return error;
	}
	
	/**
	 * Move the active block all the way to the bottom. 
	 */
	
	public synchronized void drop() {
		int addScore = 0;
		while(canMove(activeBlock,Block.DOWN)) {
			activeBlock.move(Block.DOWN);
			addScore += 5;
		}
		score += addScore;
		if( addScore > 0)
			callout = new ScoreCallout(1,10,addScore);
		blockLanded();
	}
	
	synchronized void  blockLanded() {
		int[] spaces = activeBlock.getSpacesUsed();
		// break active block into pieces
		
		for(int i=0;i<spaces.length;i+=2) {
			BlockDot block = new BlockDot(spaces[i],spaces[i+1],activeBlock.getColors()[i/2]);
			if( isPositionOnBoard(spaces[i],spaces[i+1]) )
				setBlockAt(spaces[i],spaces[i+1], block);
		}
		clearCompletedCells();
		activeBlock = nextBlock;
		nextBlock = createNewBlock();
		
		activeBlock.setY(0);
		activeBlock.setX(4);
		if(! canMove(activeBlock,Block.DOWN) ) {
			gameOver = true;
			if( score > highScore )
				highScore = score;
			
			//	Game over notify all listers
			for(TetrisGameListener listener:gameListeners) {
				listener.onGameOver(highScore);
			}
		}		
	}
	
	boolean isPositionOnBoard(int x,int y) {
		return x>=0 && x< COLUMNS && y >=0 && y <ROWS;
	}
	
	/**
	 * Check bottom rows can be cleared and scoring accordingly. If any blocks are not visible they
	 * will be removed from the memory.
	 */
	void clearCompletedCells() {
		int clearedRows = 0;
		for(int i=0;i<getRows();i++) {
			if( clearRow(i) ) {
				Log.d("Tetris","Cleared row:" +i);
				clearedRows++;
				// also move deleted Blocks that used to be above this row
				for(BlockDot block:deletedBlocks) {
					if(block.getY()<=i)
						block.move(Block.DOWN);
				}
			}
		}
		int addScore = 0;
		switch(clearedRows) {
		case 1:
			addScore += 100;
			break;
		case 2:
			addScore += 250;
			break;
		case 3:
			addScore += 500;
			break;
		case 4:
			addScore += 800;
			break;
		}	
		
		if( addScore > 0 )
			callout = new ScoreCallout(1,10,addScore);
		score += addScore;
	}
	
	/**
	 * Check the specified row can be cleared. If it can be cleared, move the row above this row down.
	 * @param row
	 * @return
	 */
	
	boolean clearRow(int row) {
		boolean canClean = true;
		for(int i=0;i<COLUMNS;i++) {
			if( !isSpaceUsed(i,row) ) {
				canClean = false;
				break;
			}
		}
		//	Move all rows above this row down by 1 row
		if( canClean && row != 0) {
			for(int c=0;c<COLUMNS;c++) {
				BlockDot block = getBlockAt(c,row);
				deletedBlocks.add(block);  // keep deleted for animation
				block.move(Block.DOWN);    // simulating moving for the deleted cell				
				for(int r=row-1;r>=0;r--) {
					setBlockAt(c,r+1,getBlockAt(c,r) );
					if( getBlockAt(c,r) != null) {
						getBlockAt(c,r).move(Block.DOWN);
					}
				}
			}
		}
		return canClean;
	}
	
	/**
	 * Check if a block is visible
	 */
	boolean isBlockVisible(Block block) {
		int[] spaces = block.getSpacesUsed();
		for(int i=1;i<spaces.length;i+=2) {
			if( spaces[i] < ROWS)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Check if a space is used by a block
	 * @param x
	 * @param y
	 * @return return true is is space is used, and false if the space is not used
	 */
			
	synchronized boolean isSpaceUsed(int x,int y) {
		return getBlockAt(x,y) !=null;
	}

	
	public synchronized BlockDot getBlockAt(int x,int y) {
		if( x < 0 || x >= getColumns() || y<0 || y >= getRows() )
			return null;
		String key = String.valueOf(x) + "_" + String.valueOf(y);
		return allBlocks.get(key);
	}
	
	
	public synchronized void setBlockAt(int x,int y,BlockDot block) {
		if( x < 0 || x >= getColumns() || y<0 || y >= getRows() )
			return;
		String key = String.valueOf(x) + "_" + String.valueOf(y);
		allBlocks.put(key,block);
	}
		
	/**
	 * Create a new block
	 */
	Block createNewBlock() {
		Block block=null;
		Random r = new Random();
		
		switch(r.nextInt(6)) {
		case 0:
			block = new BlockT();
			break;
		case 1:
			block = new BlockI();
			break;
		case 2:
			block = new BlockS();
			break;			
		case 3:
			block = new BlockL();
			break;			
		case 4:
			block = new BlockO();
			break;		
		case 5:
			block = new BlockJ();
			break;				
		}
		block.setX(1);
		block.setY(0);
		block.setOrientation(r.nextInt(4));	
		return block;
	}
	
	/**
	 * Check if the block can move in certain direction
	 * @param direction
	 * @return
	 */
	
	boolean canMove(Block block,int direction) {
		int[] spaceNeeded = block.getSpaceNeeded(direction);
		if( borderHit(spaceNeeded)) {
			return false;
		}

		if( blockHit(spaceNeeded))
				return false;

		return true;
	}
	
	/**
	 * Check is the needed space will hit a block
	 * @param spaceNeeded
	 * @param block
	 * @return
	 */
	
	private boolean blockHit(int[] spaceNeeded) {
		for (int s = 0; s < spaceNeeded.length; s += 2) {
			if( isSpaceUsed(spaceNeeded[s],spaceNeeded[s+1]) )
				return true;
		}
		return false;
	}
	
	/**
	 * Check is the needed spaces will hit border
	 * @param spaceNeeded
	 * @return
	 */
	
	private boolean borderHit(int[]spaceNeeded) {
		for(int i=0;i<spaceNeeded.length;i+=2) {
			// X
			if(spaceNeeded[i] < 0 || spaceNeeded[i] >= COLUMNS )
				return true;
			// Y
			if(spaceNeeded[i+1] >= ROWS )
				return true;
		}
		return false;
	}

	public int getRows() {
		return ROWS;
	}
	
	public int getColumns() {
		return COLUMNS;
	}

	
	public synchronized void pause() {
		// TODO Auto-generated method stub
		running = false;
	}
	
	
	public synchronized void resume() {
		running = true;
	}
}
