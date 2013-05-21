package com.yinong.tetris.model;

public class ScoreCallout {
	int x;
	int y;
	int score;
	float moving;
	int period=150;
	long lastUpdate = 0;
	float showPercent = 1f;
	
	public ScoreCallout(int x,int y,int score) {
		this.x = x;
		this.y = y;
		this.score = score;
		lastUpdate = System.currentTimeMillis();
	}
	
	public void update(long now) {
		if( now - lastUpdate < period )
			return;
		lastUpdate = now;
		showPercent -= 0.3;
		if( showPercent <0 )
			showPercent = 0;
	}
	
	public boolean isAlive() {
		return showPercent > 0;
	}

	public int getScore() {
		return score;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
