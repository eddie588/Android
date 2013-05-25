package com.yinong.tetris.model;
/**
 * Stats for game
 * @author Yinong Jiang
 *
 */
public class GameStats {
	protected int highScore;
	protected int score;
	protected int clearedRows;
	protected int totalBlocks;
	protected boolean visible = false;
	
	public void resetStats() {
		score = 0;
		clearedRows = 0;
		totalBlocks = 0;
		if( score > highScore )
			highScore = score;
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
		clearedRows += cleared;
	}
	
	
	public int getClearedRows() {
		return clearedRows;
	}
	public void setClearedRows(int clearedRows) {
		this.clearedRows = clearedRows;
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
