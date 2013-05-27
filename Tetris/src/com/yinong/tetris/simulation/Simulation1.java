package com.yinong.tetris.simulation;

import java.util.ArrayList;
import java.util.List;
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
	
	public static int RANDOM = 0;
	public static int BESTBIT = 1;
	public static int BESTBIT_ONELOOKAHEAD = 2;
	public static int BESTBIT_TWOLOOKAHEAD = 3;
	
	Block currentBlock = null;
	Queue<TetrisCommand> commandQueue = new ConcurrentLinkedQueue<TetrisCommand>();
	
	
	boolean running = true;
	Thread simuLoop = null;
	int algorithm = RANDOM;
	
	public Simulation1(TetrisGame game) {
		this.game = game;
	}
	
	public void startSimulate()  {
		startSimulate(BESTBIT_ONELOOKAHEAD);
	}

	public void startSimulate(int algorithm) {
		this.algorithm = algorithm;
		simuLoop = new Thread() {
			
			public void run() {
				while(running ) {
					try {
						long before = System.currentTimeMillis();
						update();
						long diff = System.currentTimeMillis() - before;
//						if( diff < PERIOD)
//							sleep(PERIOD-diff);
					}
					catch (Exception e) {
						e.printStackTrace();
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
			if( algorithm == RANDOM ) {
				searchRandomPath(block);
			}
			else if ( algorithm == BESTBIT) {
				searchBestFit(block);
			}
			else if ( algorithm == BESTBIT_ONELOOKAHEAD) {
				searchBestFit(block,game.getNextBlocks().peek());
			}
			else if ( algorithm == BESTBIT_TWOLOOKAHEAD) {
				Object[] next = game.getNextBlocks().toArray();

				searchBestFit(block,(Block)next[0],(Block)next[1]);
			}
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
	int print = 0;
	void searchBestFit(Block active,Block next) {
		NewPosition bestFit=null;
		SimulationHelper helper = new SimulationHelper(game);	
		
		List<NewPosition> activeLanding = getLandingPositions(helper,active);
		int bestBenefit = -100000000; 
		


		for(NewPosition activeP:activeLanding) {
			helper.markPositions(activeP.spaces);

			List<NewPosition> nextLanding = getLandingPositions(helper,next);
			for (NewPosition nextP : nextLanding) {
				Position[] combinedSpaces = new Position[activeP.spaces.length
						+ nextP.spaces.length];
				int p = 0;
				for (int i = 0; i < activeP.spaces.length; i++) {
					combinedSpaces[p++] = activeP.spaces[i];
				}

				for (int i = 0; i < nextP.spaces.length; i++) {
					combinedSpaces[p++] = nextP.spaces[i];
				}
				helper.markPositions(nextP.spaces);
				int benefit = helper.getBenefit(combinedSpaces);
				//int benefit = helper.getBenefit();
				

				if ( print > 0 )
					System.out.println("" + benefit);
				if( bestBenefit < benefit) {
					bestFit = activeP;
					activeP.benefit = benefit;
					bestBenefit = benefit;
				}
				helper.unmarkPositions(nextP.spaces);
			}


			helper.unmarkPositions(activeP.spaces);
		}
		
		if( print > 0 )
			print--;
		helper.printBoard();
		printDebugInfo(bestFit);
		generateCommand(active,bestFit);
	}
	
	void searchBestFit(Block active,Block next1,Block next2) {
		NewPosition bestFit=null;
		SimulationHelper helper = new SimulationHelper(game);	
		
		List<NewPosition> activeLanding = getLandingPositions(helper,active);
		int bestBenefit = -100000000; 
		
		for(NewPosition activeP:activeLanding) {
			helper.markPositions(activeP.spaces);
			List<NewPosition> next1Landing = getLandingPositions(helper,next1);
			for (NewPosition next1P : next1Landing) {
				helper.markPositions(next1P.spaces);
				List<NewPosition> next2Landing = getLandingPositions(helper,next2);
				for (NewPosition next2P : next2Landing) {
					
					Position[] combinedSpaces = new Position[activeP.spaces.length
							+ next1P.spaces.length + next2P.spaces.length];
					int p=0;
					
					
					for (int i = 0; i < activeP.spaces.length; i++) {
						combinedSpaces[p++] = activeP.spaces[i];
					}

					for (int i = 0; i < next1P.spaces.length; i++) {
						combinedSpaces[p++] = next1P.spaces[i];
					}			
					for (int i = 0; i < next2P.spaces.length; i++) {
						combinedSpaces[p++] = next2P.spaces[i];
					}							
					helper.markPositions(next2P.spaces);
					
					int benefit = helper.getBenefit(combinedSpaces);
					//int benefit = helper.getBenefit();					
					if (bestBenefit < benefit) {
						bestFit = activeP;
						activeP.benefit = benefit;
						bestBenefit = benefit;
					}
					
					helper.unmarkPositions(next2P.spaces);
										
				}
				helper.unmarkPositions(next1P.spaces);
			}
			helper.unmarkPositions(activeP.spaces);
		}
				
		printDebugInfo(bestFit);
		generateCommand(active,bestFit);
	}
	
	
	/**
	 * Get all the landing positions for a block
	 * @param block
	 * @param helper
	 * @param prevBlocksPositions
	 * @return
	 */
	
	List<NewPosition> getLandingPositions(SimulationHelper helper,Block block) {
		List<NewPosition> newPositions = new ArrayList<NewPosition>();
		
		for (int orientation = 0; orientation < 4; orientation++) {
			for (int x = 0; x < game.getColumns(); x++) {
				Position[] spaces = block.getSpaces(x, 0, orientation);
				if (game.borderHit(spaces)) {
					continue;
				}

				int y = helper.getLandingY(spaces);
				spaces = block.getSpaces(x, y, orientation);

				if (helper.areSpacesUsed(spaces)) {
					break;
				}
	
				newPositions.add(new NewPosition(x, y, orientation,spaces));
			}
		}
		return newPositions;
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
		SimulationHelper helper = new SimulationHelper(game);	
		
		List<NewPosition> activeLanding = getLandingPositions(helper,active);
		int bestBenefit = -100000000; 
		
		for(NewPosition activeP:activeLanding) {
			helper.markPositions(activeP.spaces);

			int benefit = helper.getBenefit(activeP.spaces);

			if (bestBenefit < benefit) {
				bestFit = activeP;
				activeP.benefit = benefit;
				bestBenefit = benefit;
			}

			helper.unmarkPositions(activeP.spaces);
		}
				
		helper.printBoard();
		printDebugInfo(bestFit);
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
		int totalEmpty=0;
		
		public NewPosition(int x,int y,int orientation,Position[] spaces) {
			this.x = x;
			this.y = y;
			this.spaces = spaces;
			this.orientation = orientation;
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
