package com.yinong.loadrunner.model;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.yinong.loadrunner.control.Assets;

public class World {
	public static final int ROWS = 20;
	public static final int COLS = 40;
	
	public static final int POS_EMPTY = 0;
	public static final int POS_BRICK = 1;
	public static final int POS_CONCRETE = 2;
	public static final int POS_LADDER = 3;
	public static final int POS_GOLD = 4;
	public static final int POS_ROPE = 5;
	
	public static final int MOVE_STAND = -1;
	public static final int MOVE_UP = 0;
	public static final int MOVE_DOWN = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_RIGHT = 3;
	
	public static final int ST_NOT_STARTED=0;
	public static final int ST_PAUSED=1;
	public static final int ST_STARTED=2;
	public static final int ST_STOPPED = 3;
	
	Man me;
	List<Man> guards = new ArrayList<Man>();
	List<Trap> traps = new ArrayList<Trap>();
	
	int[] levelInfo;
	int curLevel = 1;
	
	int totalGold = 0;
	
	long lastUpdate = 0;
	int lives = 3;
	
	int newX;
	int newY;
	
	int status = ST_NOT_STARTED;
	
	public World() {
		levelInfo = new int[ROWS*COLS];		
	}
	
	public int getPosition(int x,int y) {
		return levelInfo[x+y*COLS];
	}
	
	public void setPosition(int x,int y,int type) {
		levelInfo[x+y*COLS] = type;
	}
	
	public void resetGame(int level) {
		lives = 3;
		status = ST_NOT_STARTED;
		initLevel(curLevel);
	}
	
	public boolean isStopped() {
		return status == ST_STOPPED;
	}
	
	
	public void initLevel(int level) {
		try {
			curLevel = level;
			FileHandle file = Gdx.files.internal("level/level" + level);
			BufferedReader reader = file.reader(512);
			totalGold = 0;
			for(int y=0;y<ROWS;y++) {
				String str= reader.readLine();
				for(int x=0;x<COLS;x++) {
					char ch;
					ch = (x>=str.length())?' ':str.charAt(x);
					setPosition(x,ROWS-y-1,POS_EMPTY);
					switch(ch) {
					case ' ':
						setPosition(x,ROWS-y-1,POS_EMPTY);
						break;
					case 'b':
						setPosition(x,ROWS-y-1,POS_BRICK);
						break;
					case 'c':
						setPosition(x,ROWS-y-1,POS_CONCRETE);
						break;
					case 'l':
						setPosition(x,ROWS-y-1,POS_LADDER);
						break;
					case 'g':
						setPosition(x,ROWS-y-1,POS_GOLD);
						totalGold++;
						break;						
					case 'r':
						setPosition(x,ROWS-y-1,POS_ROPE);
						break;
					case 'm':
						me = new Man(this,x,ROWS-y-1,Man.ME);
						break;
					case 'e':
						guards.add(new Man(this,x,ROWS-y-1,Man.GUARD));
						break;
					case 'n':
						newX = x;
						newY = ROWS-y-1;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set moving direction for me. Don't really move me.
	 * @param direction
	 */
	
	public void setMovingDirection(int direction) {
		if( status != ST_NOT_STARTED && status != ST_STARTED && status != ST_PAUSED )
			return;
		// TODO add moving constraint here?
		me.setMovingDirection(direction);
		status = ST_STARTED;
	}
	
	public void update(float delta) {
		if( status != ST_STARTED)
			return;
		
		//	update me
		me.update(delta);	
		
		//	update guards
		for(Man guard:guards) {
			guard.update(delta);
			
			if( me.getX() == guard.getX() && me.getY() == guard.getY() ) {
				status = ST_STOPPED;
				//TODO sound effect
			}
		}
		
		//	Update traps, if it is not alive remove it and cover with brick
		Iterator<Trap> it = traps.iterator();
		while(it.hasNext()) {
			Trap trap = it.next();
			trap.update(delta);
			
			if( !trap.isAlive()) {
				it.remove();
				setPosition(trap.getX(),trap.getY(),POS_BRICK);
				
				// Activate trapped guard
				activateTrappedGuard(trap.getX(),trap.getY());
			}
		}
	}
	
	void activateTrappedGuard(int x,int y) {
		for(Man guard:guards) {
			if( guard.getX() == x && guard.getY() == y) {
				guard.setLocation(newX,newY);
				guard.setMovingDirection(MOVE_DOWN);
			}
		}
	}
	
	public Man getMe() {
		return me;
	}

	public void takeGold(int x, int y) {
		setPosition(x,y,POS_EMPTY);
		totalGold--;
		//	TODO sound effect
		
		if( totalGold == 0)
			levelComplete();
	}
	
	void levelComplete() {
		//	TODO
	}

	public List<Man> getGuards() {
		return guards;
	}

	public void fall() {
		setMovingDirection(MOVE_DOWN);
		// TODO sound effect
	}

	public boolean dig(float clickX,float clickY) {
		if( status != ST_NOT_STARTED && status != ST_STARTED && status != ST_PAUSED )
			return false;		
		int x = me.getX();
		int y = me.getY();
		
		if( y == 0 )
			return false;
		
		if( clickX < COLS/2) {
			if( getPosition(x-1,y-1) == POS_BRICK ) {
				setPosition(x-1,y-1,POS_EMPTY);
				traps.add(new Trap(x-1,y-1));
			}
			//	TODO show animation
		}
		else {
			if( getPosition(x+1,y-1) == POS_BRICK ) {
				setPosition(x+1,y-1,POS_EMPTY);
				traps.add(new Trap(x+1,y-1));
			}
			//	TODO show animation
		}
		return true;
	}

	public void restart() {
		me.resetPosition();
		for(Man guard:guards) {
			guard.resetPosition();
		}
		status = ST_NOT_STARTED;
		if( lives > 0 )
			lives--;
	}
	
	
	public int getLives() {
		return lives;
	}
	
	public void draw(SpriteBatch batch,float delta) {
		drawScene(batch);
		me.draw(batch,delta);
		
		for(Man guard:guards) {
			guard.draw(batch,delta);
		}
		
		for(Trap trap:traps) {
			trap.draw(batch, delta);
		}
		drawLives(batch);
	}
	
	void drawLives(SpriteBatch batch) {
		for(int i=0;i<getLives();i++) {
			batch.draw(Assets.stand,World.COLS-1-i,World.ROWS,1,1);
		}		
	}
		
	void drawScene(SpriteBatch batch) {		
		for(int x=0;x<World.COLS;x++) {
			for(int y=0;y<World.ROWS;y++) {
				TextureRegion texture=null;
				switch(getPosition(x,y) ) {
				case World.POS_BRICK:
					texture = Assets.brick;
					break;
				case World.POS_CONCRETE:
					texture = Assets.concrete;
					break;
				case World.POS_LADDER:
					texture = Assets.ladder;
					break;
				case World.POS_GOLD:
					texture = Assets.gold;
					break;					
				case World.POS_ROPE:
					texture = Assets.rope;
					break;					
				}
				
				if( texture != null)
					batch.draw(texture,x, y,1,1);	
			}
		}
	}	
}
