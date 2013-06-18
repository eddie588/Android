package com.yinong.loadrunner.control;


import com.badlogic.gdx.Game;
import com.yinong.loadrunner.screens.LevelScreen;

public class LoadRunnerGame extends Game {

	@Override
	public void create() {
		Assets.Load();
		setScreen(new LevelScreen(this));
	}

}
