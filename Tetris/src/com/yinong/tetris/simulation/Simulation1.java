package com.yinong.tetris.simulation;

import java.util.Queue;
import java.util.Random;
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
	Thread simuLoop = null;
	
	public Simulation1(TetrisGame game) {
		this.game = game;
		helper = new SimulationHelper(game);
		

	}
	

	public void startSimulate() {
		simuLoop = new Thread() {
			
			public void run() {
				while(running ) {
					try {
						long before = System.currentTimeMillis();
						update();
						long diff = System.currentTimeMillis() - before;
						if( diff < PERIOD)
							sleep(PERIOD-diff);
					}
					catch (Exception e) {
						
					}
				}
			}
		};
		currentBlock = null;
		running = true;
		simuLoop.start();	
	}

	
	public void stopSimulation() {
		running = false;
		try {
			simuLoop.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			long before = System.currentTimeMillis();
			//searchBestFit(block);
			searchBestFit(block,game.getNextBlock());
			System.out.println("Search best fit: " + (System.currentTimeMillis()-before) + " ms");			
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
	
	/**
	 * Search for best fit with one look ahead
	 * @param active
	 * @param next
	 */
	
	void searchBestFit(Block active,Block next) {
		NewPosition bestFit=null;
		
		for(int orientation=0;orientation<4;orientation++) {
			for(int x=0;x<game.getColumns();x++) {
				Position[] spaces = active.getSpaces(x,0,orientation);
				if( game.borderHit(spaces) ) {
					continue;
				}
				
				int y = helper.getLowestY(spaces);
				spaces = active.getSpaces(x,y,orientation);
				
				if( game.blockHit(spaces)) {
					break;
				}
				
				NewPosition pa = new NewPosition(game, x, y, orientation,spaces);
				pa.benefit = getLookAheadBenefit(pa,next);
				if( bestFit == null || pa.benefit > bestFit.benefit)
					bestFit = pa;
			}
		}
		
		printDebugInfo(bestFit);
		generateCommand(active,bestFit);
	}
	
	int getLookAheadBenefit(NewPosition pActive,Block next) {
		int best=-1000000000;
		for(int orientation=0;orientation<4;orientation++) {
			for(int x=0;x<game.getColumns();x++) {
				Position[] prevSpaces = pActive.spaces;
				
				Position[] nextSpaces = next.getSpaces(x, 0, orientation);
				if (game.borderHit(nextSpaces)) {
					continue;
				}
				
				int y = helper.getLowestY(nextSpaces, prevSpaces);
				
				nextSpaces = next.getSpaces(x, y, orientation);
				
				if (game.blockHit(nextSpaces)) {
					break;
				}

				Position[] combinedSpaces = new Position[prevSpaces.length
						+ nextSpaces.length];
				for (int i = 0; i < pActive.spaces.length; i++) {
					combinedSpaces[i] = prevSpaces[i];
				}

				for (int i = pActive.spaces.length; i < prevSpaces.length
						+ nextSpaces.length; i++) {
					combinedSpaces[i] = nextSpaces[i - prevSpaces.length];
				}

				int benefit = helper.getBenefit(combinedSpaces);
				if (benefit > best)
					best = benefit;

			}
		}
		return best;
	}
	
	boolean spacesOverlap(Position[] p1,Position[] p2) {
		for(int i=0;i<p1.length;i++) {
			for(int j=0;j<p2.length;j++) {
				if( p1[i].x == p2[j].x && p1[i].y == p2[j].y)
					return true;
			}
		}
		return false;
	}
	
	void generateCommand(Block active, NewPosition bestFit) {
		if (bestFit == null)
			return;
		int rotates = bestFit.orientation - active.getOrientation() >= 0 ? 
				bestFit.orientation	- active.getOrientation()
				: bestFit.orientation - active.getOrientation() + 4;

		for (int i = 0; i < rotates; i++) {
			commandQueue.add(new TetrisCommand(TetrisCommand.ROTATE_RIGHT));
		}
		if (bestFit.x > active.getX()) {
			for (int i = active.getX(); i < bestFit.x; i++) {
				commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_RIGHT));
			}
		}
		if (bestFit.x < active.getX()) {
			for (int i = active.getX(); i > bestFit.x; i--) {
				commandQueue.add(new TetrisCommand(TetrisCommand.MOVE_LEFT));
			}
		}
		commandQueue.add(new TetrisCommand(TetrisCommand.DROP));
	}
	
	/**
	 * Search for best bit without look ahead
	 * @param block
	 */
	void searchBestFit(Block active) {
		NewPosition bestFit=null;
		
		
		for (int orientation = 0; orientation < 4; orientation++) {
			for (int x = 0; x < game.getColumns(); x++) {
				Position[] spaces = active.getSpaces(x, 0, orientation);
				if (game.borderHit(spaces)) {
					continue;
				}
				
				int y = helper.getLowestY(spaces);
				spaces = active.getSpaces(x, y, orientation);
				if (game.blockHit(spaces)) {
					break;
				}
				NewPosition p = new NewPosition(game, x, y, orientation, spaces);
				p.benefit = helper.getBenefit(p.spaces);
				if (bestFit == null || bestFit.benefit < p.benefit)
					bestFit = p;
			}
		}
		
		//printDebugInfo(bestFit);
		generateCommand(active,bestFit);
	}
	
	int pieces =0;
	void printDebugInfo(NewPosition newPosition) {
		//	Debugging 
		System.out.println("" + (pieces++) + "/" + game.getScore() + "     |   " 
				+ newPosition.toString() + " benefit: " + newPosition.benefit);
//		for(int i=0;i<game.getRows();i++) {
//
//			System.out.print("" + i%10 + " | ");
//            for(int j=0;j<game.getColumns();j++) {
//            	if( game.isSpaceUsed(j, i) )
//            		System.out.print("*");
//            	else if (helper.usesSpace(newPosition.spaces, j, i))
//            		System.out.print("o");
//            	else 
//            		System.out.print("-");
//            }
//		}	
	}
	
	class NewPosition implements Comparable<NewPosition>{
		int x;
		int y;
		int orientation;
		Position[] spaces;
		int benefit=0;
		TetrisGame game;
		int totalEmpty=0;
		
		public NewPosition(TetrisGame game, int x,int y,int orientation,Position[] spaces) {
			this.x = x;
			this.y = y;
			this.spaces = spaces;
			this.orientation = orientation;
			this.game = game;
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
		
		@Override
		public String toString() {
			String s = "[";
			for(int i=0;i<spaces.length;i++) {
				if( i != spaces.length-1)
					s += String.valueOf(spaces[i].x) + "," + String.valueOf(spaces[i].y) + ",";
				else
					s += String.valueOf(spaces[i].x) + "," + String.valueOf(spaces[i].y) ;
			}
			s += "]";	
			return s;
		}
	}
}
