package com.yinong.loadrunner.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.yinong.loadrunner.control.Assets;

public class Trap {
	int x;
	int y;
	
	float stateTime;
	
	public Trap(int x,int y) {
		this.x = x;
		this.y = y;
		this.stateTime = 0;
	}
	
	public void update(float delta) {
		stateTime += delta;
	}
	
	public void draw(SpriteBatch batch,float delta) {
		if( stateTime < 5 )
			return;
		TextureRegion texture = Assets.brick;
		
		//batch.draw(texture,x, y,1,1);
		float h = (stateTime-5) > 1? 1: (stateTime-5);
		batch.draw(texture,x,y,0,0,1,h,1,1,0);
	}
	
	public boolean isAlive() {
		return stateTime < 6;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
