package com.yinong.tetris;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.yinong.tetris.model.TetrisGameListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 */
public class FullscreenActivity extends Activity implements TetrisGameListener {
	private GameBoard gameBoard;
	
	public static String PREFS_NAME = "MyTetris";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);
		//getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		//final View controlsView = findViewById(R.id.fullscreen_content_controls);
		
		gameBoard = (GameBoard) findViewById(R.id.fullscreen_content);
		
		gameBoard.getGame().addListener(this);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		gameBoard.getGame().setHighScore(settings.getInt("high_score",0));
	    
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onGameOver(int highScore) {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("high_score", highScore);

	    // Commit the edits!
        editor.commit();		    
	}
		
}
