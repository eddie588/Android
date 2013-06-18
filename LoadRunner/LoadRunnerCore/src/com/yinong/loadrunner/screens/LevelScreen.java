package com.yinong.loadrunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector3;
import com.yinong.loadrunner.control.Assets;
import com.yinong.loadrunner.model.Man;
import com.yinong.loadrunner.model.World;

public class LevelScreen implements Screen {
	Game game;
	Camera camera;
	SpriteBatch batch;
	GestureDetector gestureDetector;
	World world;
	
	long lastUpdate;
	

	public LevelScreen(Game game) {
		this.game = game;
		batch = new SpriteBatch();
		
		world  = new World();
	
		
		gestureDetector = new GestureDetector(new GestureAdapter() {
			long lastTap = 0;
			@Override
			public boolean tap(float x, float y, int count, int button) {
				if( world.isStopped() ) {
					world.restart();
				}
				else {
					long now = System.currentTimeMillis();
					if( now - lastTap < 500) {
						Vector3 vec = new Vector3(x,y,0);
						camera.unproject(vec);
						world.dig(vec.x,vec.y);
					}
					lastTap = now;
				}
				return true;
			}
			
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				// TODO Auto-generated method stub
				return super.touchDown(x, y, pointer, button);
			}
			
			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
				// TODO Auto-generated method stub
				if( Math.abs(velocityX) > Math.abs(velocityY) ) {
					if(velocityX > 0 )
						world.setMovingDirection(World.MOVE_RIGHT);
					else 
						world.setMovingDirection(World.MOVE_LEFT);
				}
				else {
					if(velocityY > 0 )
						world.setMovingDirection(World.MOVE_DOWN);
					else 
						world.setMovingDirection(World.MOVE_UP);
				}
				return true;
			}
			
		});		
		Gdx.input.setInputProcessor(gestureDetector);		
	}
	

	@Override
	public void render(float delta) {
		//System.out.println("Delta: " + delta);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
		
		//adjustCamera();
		batch.begin();
		
		world.draw(batch,delta);
		
		batch.end();
		
		world.update(delta);
		
		
	}
	
	void adjustCamera() {
		if( world.getMe().getX() + 1 > camera.position.x+camera.viewportWidth/2)
			camera.translate(camera.viewportWidth/2, 0, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	@Override
	public void hide() {
		batch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
        camera = CameraHelper.GetCamera(40, 21);
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
        camera.update();  
        
		batch.setProjectionMatrix(camera.combined);
		
		world.resetGame(1);
	}
}
