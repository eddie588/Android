package com.yinong.tetris.model;


public class BlockDot extends Block {
	
	float moving = 0;
	int period = 100;
	long lastUpdate=0;
	
	public BlockDot(int x,int y,int color) {
		this.x = x;
		this.y = y;
		this.orientation =0;
		setColor(color);	
		moving = 0;
	}
	
	public BlockDot(Block block) {
		this.x = block.getX();
		this.y = block.getY();
		this.orientation = 0;
		this.setColor(block.getColor());
		moving =0;
	}


	@Override
	public int[] getSpacesTemplate(int orientation) {
		// TODO Auto-generated method stub
		return new int[] {0,0};
	}
	
	@Override
	public int getNumberOfPieces() {
		return 1;
	}	
	
	public void move(int direction) {
		super.move(direction);
		if( direction == DOWN ) {
			moving += 1f;
		}
	}
	
	public float getMoving() {
		return moving;
	}
	
	@Override
	public void update(long now) {
		if( now - lastUpdate > period ) {
			lastUpdate = now;
			if( moving >0 )
				moving -= 0.2;
			if( moving <0 )
				moving = 0;
		}
	}
}
