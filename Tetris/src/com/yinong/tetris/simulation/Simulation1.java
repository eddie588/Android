package com.yinong.tetris.simulation;

import java.util.Queue;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yinong.tetris.model.Block;
import com.yinong.tetris.model.TetrisCommand;
import com.yinong.tetris.model.TetrisGame;

public class Simulation1 {
	long lastSimulate=0;
	static int PERIOD = 200;
	TetrisGame game;
	
	Block currentBlock = null;
	Queue<TetrisCommand> commandQueue = new ConcurrentLinkedQueue<TetrisCommand>();
	
	boolean running = true;
	
	public Simulation1(TetrisGame game) {
		this.game = game;
		
		Thread simuLoop = new Thread() {
			
			public void run() {
				while(running) {
					update();
					try {
						sleep(PERIOD);
					}
					catch (Exception e) {
						
					}
				}
			}
		};
		simuLoop.start();
	}

	
	public void stopSimulation() {
		running = false;
	}
	
	public void update() {
		if( game.isGameOver() ) {
			commandQueue.clear();
			try {
				Thread.sleep(5000);
				//game.resetGame();
			}
			catch(Exception e) {
				
			}
			return;
		}
		Block block = game.getActiveBlock();
		if( block != currentBlock ) {
			currentBlock = block;
			commandQueue.clear();			
			searchLowestFit(block);
		} 
		else {
			if( !commandQueue.isEmpty() ) {
				TetrisCommand command = commandQueue.remove();
				game.addCommand(command);
			}
		}
	}
	
	//	Random 3 moves then drop
	void searchRandomPath(Block block) {
		Random r = new Random();
		int steps = r.nextInt(10);
		for(int i=0;i<steps;i++) {

			switch(r.nextInt(3) ) {
			case 0:
				commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_RIGHT));
				break;
			case 1:
				commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_LEFT));
				break;
			case 2:
				commandQueue.add(new TetrisCommand(TetrisCommand.ROTATE_RIGHT));
				break;
			}
		}
		commandQueue.add(new TetrisCommand(TetrisCommand.DROP));
	}
	
	void searchLowestFit(Block block) {
		SortedSet<NewPosition> newPositions = new TreeSet<NewPosition>();
		
		for(int orientation=0;orientation<4;orientation++) {
			for(int x=0;x<game.getColumns();x++) {
				for(int y=0;y<game.getRows();y++) {
					int[] spaces = block.getSpaces(x,y,orientation);
					if( !game.borderHit(spaces) && !game.blockHit(spaces)) {
						NewPosition p = new NewPosition(game,x,y,orientation,spaces);
						p.calculateCost(block.getX(),block.getY());
						newPositions.add(p);
					}
				}
			}
		}
		
		try {
			NewPosition p = newPositions.first();
            //System.out.println("x: " + p.x + " y: " + p.y + " avgY: " + p.getAverageY() + " cost: " + p.cost);
			int rotates = p.orientation - block.getOrientation()>=0?p.orientation - block.getOrientation():
				p.orientation - block.getOrientation()+4;
            
			for(int i=0;i<rotates;i++) {
				commandQueue.add(new TetrisCommand(TetrisCommand.ROTATE_RIGHT));
			}
			if( p.x > block.getX()) {
				for(int i=block.getX();i<p.x;i++) {
					commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_RIGHT));
				}
			}
			if( p.x < block.getX()) {
				for(int i=block.getX();i>=p.x;i--) {
					commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_LEFT));
				}
			}

			commandQueue.add(new TetrisCommand(TetrisCommand.DROP));			
		} catch(Exception e) {
			
		}
	}
	
	class NewPosition implements Comparable<NewPosition>{
		int x;
		int y;
		int orientation;
		int[] spaces;
		float costY=100f;
		float costX=100f;
		TetrisGame game;
		int totalEmpty=0;
		
		public NewPosition(TetrisGame game, int x,int y,int orientation,int[]spaces) {
			this.x = x;
			this.y = y;
			this.spaces = spaces;
			this.orientation = orientation;
			this.game = game;
		}
		
		public void calculateCost(int startX,int startY) {
		       Random r = new Random();

	            costY = ((float)(game.getRows()-getAverageY()))/game.getRows();

	            if( x < game.getColumns()/2 )
	              costX = ((float)(getAverageX()))/game.getColumns();
	            else
	              costX = (game.getColumns()-(float)(getAverageX()))/game.getColumns();

	            //cost += r.nextFloat()/300;
	            //System.out.println("x: " + x + " y: " + y + " avgY: " + getAverageY() + " cost: " + costX);
//	            for(int col=0;col<game.getColumns();col++) {
//	            	for(int row=y;row<game.getRows();row++) {
//	            		totalEmpty += 
//	            	}
//	            }
		}
		
		public float getAverageY() {
			float ay = 0 ;
			for(int i=0;i<spaces.length;i+=2) {
				ay += spaces[i+1];
			} 
			return ay/(spaces.length/2);
		}
		
		public float getAverageX() {
			float ax = 0 ;
			for(int i=0;i<spaces.length;i+=2) {
				ax += spaces[i];
			} 
			return ax/(spaces.length/2);
		}
		
		@Override
		public int compareTo(NewPosition another) {
			if( costY > another.costY )
				return 1;
			else if( costY < another.costY )
				return -1;

			if( costX > another.costX )
				return 1;
			else if( costX < another.costX )
				return -1;
			
			return 0;
		}
		
	}
}
