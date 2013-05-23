package com.yinong.tetris.model;

import java.util.Random;

import android.graphics.Color;

public abstract class Block  {
	public static final int LEFT=0;
	public static final int DOWN=1;
	public static final int RIGHT=2;
	public static final int ROTATE_RIGHT=3;
	
	public static final int COLORS[] = {
		Color.RED,Color.BLUE,Color.MAGENTA,Color.YELLOW,Color.GREEN,Color.CYAN,Color.DKGRAY
	};

	
	int x=0;
	int y=0;
	int orientation=0;
	private int color = Color.CYAN;
	private int[] colors;
	
	public Block() {
		colors = new int[getNumberOfPieces()];
		for(int i=0;i<colors.length;i++) {
			colors[i] = color;
		}		
	}
	
	public int[] getColors() {
		return colors;
	}
	
	public void initRandomColors() {
		Random r = new Random();
		for(int i=0;i<colors.length;i++) {
			colors[i] = COLORS[r.nextInt(6)];
		}
	}
	
	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		for(int i=0;i<colors.length;i++) {
			colors[i] = color;
		}		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y= y;
	}


	public void rotateRight() {
		if( ++orientation == 4 )
			orientation = 0;
	}
	
	public void left() {
		x--;
	}

	public void right() {
		x++;
	}
	
	public void down() {
		y++;
	}
	


	public void move(int direction) {
		switch(direction) {
		case DOWN:
			y++;
			break;
		case LEFT:
			x--;
			break;
		case RIGHT:
			x++;
			break;
		case ROTATE_RIGHT:
			if( ++orientation == 4 )
				orientation = 0;
			break;
		}
	}
	

	public int[] getSpaceNeeded(int direction) {
		// Rotation
		if( direction == ROTATE_RIGHT) {
			return getSpaces(orientation+1>=4?0:orientation+1);
		}
		int[] spaces = getSpacesUsed();
		
		// move down add Y
		if (direction == DOWN) {
			for (int i = 1; i < spaces.length; i+=2) {
					spaces[i]++;
			}
		}
		else if (direction == LEFT) {
			for (int i = 0; i < spaces.length; i+=2) {
				spaces[i]-- ;
			}
		}
		else if (direction == RIGHT) {
			for (int i = 0; i < spaces.length; i+=2) {
				spaces[i]++ ;
			}
		}		
		return spaces;
	}

	
	public int[] getSpacesUsed() {
		return getSpaces(orientation);
	}
	
	public int[] getSpaces(int orientation) {
		// TODO Auto-generated method stub
		int[] template = getSpacesTemplate(orientation);
		int[] space = new int[template.length];
		
		for(int i=0;i<space.length;i+=2) {
			space[i] = template[i] + getX();
			space[i+1] = template[i+1] + getY();
		}
		return space;
	}	
	
	public int[] getSpaces(int x,int y,int orientation) {
		// TODO Auto-generated method stub
		int[] template = getSpacesTemplate(orientation);
		int[] space = new int[template.length];
		
		for(int i=0;i<space.length;i+=2) {
			space[i] = template[i] + x;
			space[i+1] = template[i+1] + y;
		}
		return space;
	}		
	
	public int getNumberOfPieces() {
		return 4;
	}
	
	public abstract int[] getSpacesTemplate(int orientation);

	@Override
	public boolean equals(Object o) {
		try {
			Block block = (Block) o;
			return block.getX() ==  getX() && block.getY() == getY();
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public void update(long now) {
		
	}

}
