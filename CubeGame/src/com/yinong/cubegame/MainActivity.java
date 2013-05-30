package com.yinong.cubegame;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.yinong.cubegame.model.Cube3By3;

public class MainActivity extends Activity  {
	GLSurfaceView glView;
	Cube3By3      cube;
	GameController controller;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		
		cube = new Cube3By3();
		
		glView = new GLSurfaceView(getApplicationContext());
		glView.setRenderer(new GameRenderer(glView,cube));

		setContentView(glView);
	
		controller = new GameController(glView,cube);
		//glView.setGLWrapper(controller);  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
