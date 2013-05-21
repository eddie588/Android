package com.yinong.tetris.simulation;

import com.yinong.tetris.model.Block;
import com.yinong.tetris.model.TetrisGame;

public class Simulation1 implements Simulation {
	long lastSimulate=0;
	static int PERIOD = 200;
	SimulationHelper helper = new SimulationHelper();

	@Override
	public void simulate(TetrisGame game) {
		Block activeBlock = game.getActiveBlock();
		
		
	}

}
