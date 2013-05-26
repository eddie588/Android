package com.yinong.tetris.model;
/**
 * Stats for game
 * @author Yinong Jiang
 *
 */
public class GameStats {
	protected int highScore;
	protected int score;
	protected int[] clearedRows;
	protected int totalBlocks;
	protected boolean visible = false;
	
	public void resetStats() {
		score = 0;
		for(int i=0;i<clearedRows.length;i++) {
			clearedRows[i] = 0;
		}
		totalBlocks = 0;
		if( score > highScore )
			highScore = score;
	}
	
	public GameStats() {
		clearedRows = new int[4];
		resetStats();
	}
	
	public int getHighScore() {
		return highScore;
	}
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}
	public int getScore() {
		return score;
	}
	public void addScore(int score) {
		this.score += score;
	}
	
	public void addBlock() {
		this.totalBlocks++;
	}
	
	public void addClearedRows(int cleared) {
		if( cleared> 0 )
			clearedRows[cleared-1]++;
	}
	

	public int getClearedRows(int rows) {
		return clearedRows[rows];
	}

	public int getClearedRows() {
		int rows = 0;
		for(int i=0;i<clearedRows.length;i++) {
			rows += (i+1) * clearedRows[i];
		}
		return rows;
	}
	
	public int getTotalBlocks() {
		return totalBlocks;
	}
	public void setTotalBlock(int totalBlocks) {
		this.totalBlocks = totalBlocks;
	}
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
