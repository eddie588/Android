package com.yinong.tetris.model;

/**
 * This is the commands that Tetris game will use
 * @author Yinong Jiang
 *
 */

public class TetrisCommand {
	public static int MOVE_LEFT = 1;
	public static int MOVE_RIGHT = 2;
	public static int ROTATE_RIGHT = 3;
	public static int ROTATE_LEFT = 4;

	public static int DROP = 5;
	public static int RESTART = 6;
	
	public static int SHOW_STATS = 7;
	public static int HIDE_STATS = 8;
	
	private int command;
	
	public TetrisCommand(int command) {
		this.command = command;
	}
	
	public int getCommand() {
		return command;
	}
	
}
