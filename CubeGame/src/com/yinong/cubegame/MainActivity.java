package com.yinong.cubegame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yinong.cubegame.model.CubeWorld;

public class MainActivity extends Activity  {
	MainView 		glView;
	CubeWorld      cubeWorld;
	GameController controller;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		
		setContentView(R.layout.activity_main);		
		
		cubeWorld = new CubeWorld(getApplicationContext());

		
		controller = new GameController(cubeWorld);
		glView = (MainView) findViewById(R.id.mainView);
		glView.setController(controller);
	
		GameRenderer renderer = new GameRenderer(glView,cubeWorld);
		glView.setRenderer(renderer);

	
		controller.setRenderer(renderer);
		
		glView.setGLWrapper(controller);  
		
		//	Setup button listener
		View btn = findViewById(R.id.btn222);
		btn.setOnClickListener(controller);
		
		btn = findViewById(R.id.btn333);
		btn.setOnClickListener(controller);
		
		btn = findViewById(R.id.btn444);
		btn.setOnClickListener(controller);
		
		btn = findViewById(R.id.btn224);
		btn.setOnClickListener(controller);
		
		btn = findViewById(R.id.btn233);
		btn.setOnClickListener(controller);
		
		btn = findViewById(R.id.btn555);
		btn.setOnClickListener(controller);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
