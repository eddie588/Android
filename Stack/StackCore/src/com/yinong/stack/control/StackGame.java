package com.yinong.stack.control;

import com.badlogic.gdx.Game;
import com.yinong.stack.screen.GameScreen;

public class StackGame extends Game {

	@Override
	public void create() {
		Assets.Load();
		setScreen(new GameScreen(this));
	}

}
