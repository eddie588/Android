package com.yinong.tetris.simulation;

import java.util.Queue;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yinong.tetris.model.Block;
import com.yinong.tetris.model.Position;
import com.yinong.tetris.model.TetrisCommand;
import com.yinong.tetris.model.TetrisGame;

public class Simulation1 {
	long lastSimulate=0;
	static int PERIOD = 50;
	TetrisGame game;
	
	Block currentBlock = null;
	Queue<TetrisCommand> commandQueue = new ConcurrentLinkedQueue<TetrisCommand>();
	
	boolean running = true;
	SimulationHelper helper;
	
	public Simulation1(TetrisGame game) {
		this.game = game;
		helper = new SimulationHelper(game);
		
		Thread simuLoop = new Thread() {
			
			public void run() {
				while(running) {
					update();
					try {
						sleep(20);
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
					Position[] spaces = block.getSpaces(x,y,orientation);
					if( game.borderHit(spaces) ) {
						continue;
					}
					if( game.blockHit(spaces)) {
						break;
					}
					NewPosition p = new NewPosition(game, x, y, orientation,spaces);
					p.calculateCost(block.getX(), block.getY());
					newPositions.add(p);
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
		Position[] spaces;
		float benefit=0;
		TetrisGame game;
		int totalEmpty=0;
		
		public NewPosition(TetrisGame game, int x,int y,int orientation,Position[] spaces) {
			this.x = x;
			this.y = y;
			this.spaces = spaces;
			this.orientation = orientation;
			this.game = game;
		}
		
		
		private  float clearBenefits[] = {0f,1f,3f,5f,8f};
		public void calculateCost(int startX,int startY) {
		       benefit = 500*clearBenefits[helper.getClearedRows(spaces)];

		       //	penalties for higher average Y
	           benefit -= 1000*((float)(game.getRows()-helper.getAverageY(spaces)));
	           
//	           //	penalties for holes
//	           benefit -= 0.5*getHolesBelowMe();

	           //	penalties for holes
	           benefit -= 500*helper.getAllHolesBelowRow((int)helper.getAverageY(spaces))/5;
	           
	           //	penalties for new holes
	           benefit -= 200*helper.getNewHolesBelowSpaces(spaces);
	       	
	           //	penalties for put block in center
//	           if( x < game.getColumns()/2 )
//	            	benefit -= 0.01*((float)(getAverageX()))/game.getColumns();
//	           else
//	            	benefit -= 0.01*(game.getColumns()-(float)(getAverageX()))/game.getColumns();
		}
		

		

		/**
		 * Compare to positions and order by benefit descending order
		 */
		
		@Override
		public int compareTo(NewPosition another) {
			if (benefit > another.benefit)
				return -1;
			else if( benefit < another.benefit)
				return 1;
			return 0;
		}
		
	}
}
