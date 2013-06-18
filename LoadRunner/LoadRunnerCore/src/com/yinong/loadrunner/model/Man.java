package com.yinong.loadrunner.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.yinong.loadrunner.control.Assets;

public class Man {
	public static final int ME = 0;
	public static final int GUARD = 1;
	
	public static final float PERIOD = 0.3f;
	
	public static final int POSE_BRICK_LEFT = 0;
	public static final int POSE_BRICK_RIGHT = 1;
	public static final int POSE_ROPE_LEFT = 2;
	public static final int POSE_ROPE_RIGHT = 3;
	public static final int POSE_LADDER_UP = 4;
	public static final int POSE_LADDER_DOWN = 5;
	public static final int POSE_FALL_DOWN = 6;
	public static final int POSE_STAND = 7;
	
	World world;
	
	int x;
	int y;
	int orgX;
	int orgY;
	int type;
	boolean falling = false;
	int direction = World.MOVE_STAND;
	
	int pose;
	
	Vector3 moveBy;
	
	float stateTime;

	public Man(World world,int x,int y,int type) {
		this.x = x;
		this.y = y;
		this.orgX = x;
		this.orgY = y;
		this.world = world;
		this.type = type;
		moveBy = new Vector3(0,0,0);
		stateTime = 0;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void resetPosition() {
		x = orgX;
		y = orgY;
	}
	
	boolean canMove(int direction) {
		if( direction == World.MOVE_DOWN) {
			if ( y <= 0 )
				return false;
			if( type == GUARD && !canMoveGuard(x,y-1))
				return false;
			int pos = world.getPosition(x, y-1);
			return pos == World.POS_GOLD || pos == World.POS_LADDER || pos == World.POS_ROPE ||
				pos == World.POS_EMPTY;	
		}
		
		if( direction == World.MOVE_UP ) {
			if( y+1 >= World.ROWS)
				return false;
			if( type == GUARD && !canMoveGuard(x,y+1))
				return false;
			int pos = world.getPosition(x, y+1);
			return pos == World.POS_GOLD || pos == World.POS_LADDER || pos == World.POS_ROPE || 
				( pos == World.POS_EMPTY && world.getPosition(x, y) == World.POS_LADDER);			
		}
		
		if( direction == World.MOVE_LEFT) {
			if( x <= 0 )
				return false;

			if( type == GUARD && !canMoveGuard(x-1,y))
				return false;
			
			int pos = world.getPosition(x-1, y);		
			return pos == World.POS_EMPTY ||  pos == World.POS_ROPE || pos == World.POS_GOLD || pos == World.POS_LADDER;
		}
		
		if( direction == World.MOVE_RIGHT) {
			if( x+1 >= World.COLS)
				return false;
			if( type == GUARD && !canMoveGuard(x+1,y))
				return false;
			int pos = world.getPosition(x+1, y);
	
			return pos == World.POS_EMPTY || pos == World.POS_ROPE || pos == World.POS_GOLD || pos == World.POS_LADDER;
		}		
		
		return false;
	}
	
	boolean canMoveGuard(int newX,int newY) {
		for(Man guard:world.getGuards()) {
			if( guard.getX() == newX && guard.getY() == newY)
				return false;
		}
		return true;
	}
	
	public boolean left() {
		if( canMove(World.MOVE_LEFT)) {
			x--;
			moveBy.x -= 1;			
			if( world.getPosition(x, y) == World.POS_GOLD && type == ME) {
				world.takeGold(x,y);
			}
			checkFall();			
			return true;
		}
		return false;
	}
	
	public boolean right() {
		if( canMove(World.MOVE_RIGHT)) {
			x++;
			moveBy.x += 1;
			if( world.getPosition(x, y) == World.POS_GOLD && type == ME) {
				world.takeGold(x,y);
			}
			checkFall();			
			return true;
		}
		return false;
	}
	
	public boolean up() {
		if( canMove(World.MOVE_UP)) {
			y++;
			moveBy.y += 1;
			if( world.getPosition(x, y) == World.POS_GOLD && type == ME) {
				world.takeGold(x,y);
			}
			return true;
		}
		return false;
	}
	
	public boolean down() {
		if( canMove(World.MOVE_DOWN)) {
			y--;
			moveBy.y -= 1;
			if( world.getPosition(x, y) == World.POS_GOLD && type == ME) {
				world.takeGold(x,y);
			}
			return true;
		}
		return false;
	}
	
	public boolean isFalling() {
		return falling;
	}
	
	void checkFall() {
		if( y==0)
			return;
		for(Man guard:world.getGuards()) {
			if( guard.x == x && guard.y == y-1) {
				falling = false;
				return;
			}
		}
		
		if( (world.getPosition(x, y-1) == World.POS_EMPTY ||
				world.getPosition(x, y-1) == World.POS_ROPE )) {
			falling = true;
			direction = World.MOVE_STAND;
		}
	}
	
	
	public void update(float delta) {
		stateTime += delta;
		if( stateTime < PERIOD )
			return;

		//	update state every PERIOD
		stateTime -= PERIOD;
		//	falling
		if( falling  ) {
			if( !down() )
				falling = false;
			return;
		}
		
		if( type == ME )
			updateMe();
		else
			updateGuard();
	}
	
	void updateMe() {
		if(!move(direction))
			direction = World.MOVE_STAND;
	}
	
	void updateGuard() {
		int d = World.MOVE_STAND;
		float cost = findCost(x,y);
		float newCost = 0;
		
		if( canMove(World.MOVE_DOWN) ) {
			newCost= findCost(x,y-1);
			if( newCost < cost ) {
				cost = newCost;
				d = World.MOVE_DOWN;
			}
		}

		if( canMove(World.MOVE_UP)  ) {
			newCost=findCost(x,y+1);
			if( newCost < cost ) {
				cost = newCost;
				d = World.MOVE_UP;
			}
		}
		
		if( canMove(World.MOVE_LEFT) ) {
			newCost=findCost(x-1,y);
			if( newCost < cost ) {
				cost = newCost;
				d = World.MOVE_LEFT;
			}
		}
		
		if( canMove(World.MOVE_RIGHT) ) {
			newCost=findCost(x+1,y);
			if( newCost < cost ) {
				cost = newCost;
				d = World.MOVE_RIGHT;
			}
		}
		move(d);
	}

	float findCost(int gx,int gy) {
		int mx = world.getMe().getX();
		int my = world.getMe().getY();
		
		return (mx-gx)*(mx-gx) + (my-gy)*(my-gy);
	}
	
	public boolean move(int direction) {
		boolean moved = false;
		switch(direction) {
		case World.MOVE_DOWN:
			moved = down();
			if( world.getPosition(x, y) == World.POS_LADDER )
				pose = POSE_LADDER_DOWN;
			else
				pose = POSE_FALL_DOWN;
			break;
		case World.MOVE_UP:
			moved = up();
			pose = POSE_LADDER_UP;
			break;
		case World.MOVE_LEFT:
			moved = left();
			if( world.getPosition(x, y) == World.POS_ROPE )
				pose = POSE_ROPE_LEFT;
			else
				pose = POSE_BRICK_LEFT;
			break;
		case World.MOVE_RIGHT:
			moved = right();
			if( world.getPosition(x, y) == World.POS_ROPE )
				pose = POSE_ROPE_RIGHT;
			else
				pose = POSE_BRICK_RIGHT;
			break;
		default:
			pose = POSE_STAND;
		}
		return moved;
	}
	
	public void setLocation(int x,int y) {
		this.x = x;
		this.y = y;
	}

	public void setMovingDirection(int direction) {
		this.direction = direction;
	}
	
	public void draw(SpriteBatch batch,float delta) {
		if( type == GUARD)
			batch.setColor(Color.BLUE);
		TextureRegion region = new TextureRegion(getTextureRegion());
		
		if( pose == POSE_ROPE_LEFT || pose == POSE_BRICK_LEFT)
			region.flip(true, false);
		
		batch.draw(region,x-moveBy.x, y-moveBy.y,1,1);
		float step = delta/PERIOD;
		if( moveBy.x > 0 ) {
			moveBy.x = moveBy.x > step? moveBy.x - step:0;
		}
		else if (moveBy.x < 0 ) {
			moveBy.x = moveBy.x < -step? moveBy.x + step:0;
		}
		
		if( moveBy.y > 0 ) {
			moveBy.y = moveBy.y > step? moveBy.y - step:0;
		}
		else if (moveBy.y < 0 ) {
			moveBy.y = moveBy.y < -step? moveBy.y + step:0;
		}
		
		if( type == GUARD)
			batch.setColor(Color.WHITE);
	}	
	
	public TextureRegion getTextureRegion() {
		if( pose == POSE_ROPE_RIGHT || pose == POSE_BRICK_RIGHT || 
				pose == POSE_BRICK_LEFT  || pose == POSE_BRICK_LEFT )
		{
			int i = ((int)(stateTime/(PERIOD/8)))%8;
			return Assets.run[i];
		}
		return Assets.stand;
	}
}
