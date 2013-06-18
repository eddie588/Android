package com.yinong.stack.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import com.yinong.stack.control.Assets;
import com.yinong.stack.control.CameraHelper;
import com.yinong.stack.model.StackObject;

public class GameScreen implements Screen {
    static final float BOX_STEP=1/60f;  
    static final int BOX_VELOCITY_ITERATIONS=6;  
    static final int BOX_POSITION_ITERATIONS=2;  
    static final float WORLD_TO_BOX=0.01f;  
    static final float BOX_WORLD_TO=100f;  	
	Game game;
	SpriteBatch batch;
	Camera camera;
	World world = new World(new Vector2(0, -100f), true); 	
	Box2DDebugRenderer 	debugRenderer;	
	int screenWidth;
	int screenHeight;
	
	StackObject activeObject=null;
	
	List<StackObject> boxes = new ArrayList<StackObject>();
	
	GestureDetector gestureDetector;
	
	int push = -1;
	
	public GameScreen(Game game) {
		this.game = game;
		batch = new SpriteBatch();
	
		gestureDetector = new GestureDetector(new GestureAdapter() {
			@Override
			public boolean tap(float x, float y, int count, int button) {
				if( x < screenWidth/2 ) {
					push = StackObject.PUSH_LEFT;
				}
				else  {
					push = StackObject.PUSH_RIGHT;
				}
				return true;
			}
			
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				// TODO Auto-generated method stub
				return super.touchDown(x, y, pointer, button);
			}
			
			
		});
		
		Gdx.input.setInputProcessor(gestureDetector);
		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
		update(delta);

        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
        debugRenderer.render(world, camera.combined);  
        
//        batch.begin();
//        for(StackObject box:boxes) {
//        	box.draw(batch);
//        }
//        batch.end();
	}
	
	float time = 0;	
	int count=0;
	void update(float delta) {
		time += delta;
		
		if( push == StackObject.PUSH_LEFT)
			activeObject.push(StackObject.PUSH_LEFT);
		if( push == StackObject.PUSH_RIGHT)
			activeObject.push(StackObject.PUSH_RIGHT);
		if( !activeObject.isMoving() ) {
			createStackObject();
			time = 0;
		}
	}
	
	void createStackObject() {
		Random r = new Random();
		int size = (r.nextInt(5)+5)*5;
		StackObject  obj = new StackObject(world,(float)size,(float)size,
				BodyType.DynamicBody,0.1f,0.01f,
				r.nextInt(500) + 50,camera.viewportHeight,0,Assets.brick);
		boxes.add(obj);
		activeObject = obj;
//		System.out.println("new box" + (count++));     
	}
	


	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void show() {
        camera = CameraHelper.GetCamera(600, 1000);
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
        camera.update();  
        
		batch.setProjectionMatrix(camera.combined);
      //  debugRenderer = new Box2DDebugRenderer();  
                
        //	Create walls
        createWalls();
        
        createStackObject();
        
	}
	
	void createWalls() {
        //Ground body  
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 0));  
        Body groundBody = world.createBody(groundBodyDef);  
        EdgeShape groundBox1 = new EdgeShape();  
        groundBox1.set(0, 0,camera.viewportWidth,0);
        groundBody.createFixture(groundBox1, 0.0f);  

        //left wall body  
        groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 0));  
        groundBody = world.createBody(groundBodyDef);  
        EdgeShape groundBox2 = new EdgeShape();  
        groundBox2.set(0, 0,0,camera.viewportHeight);
        groundBody.createFixture(groundBox2, 0.0f);  

        //right wall body  
        Vector3 vec = new Vector3(screenWidth,screenHeight,0);
        System.out.println("Screen X edge: " + vec.x);
        camera.unproject(vec);
        groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 0));  
        groundBody = world.createBody(groundBodyDef);  
        EdgeShape groundBox3 = new EdgeShape();  
        groundBox3.set(600, 0,600,800);
        groundBody.createFixture(groundBox3, 0.0f);  
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
		batch.dispose();
	}
}
