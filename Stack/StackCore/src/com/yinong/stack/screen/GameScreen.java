package com.yinong.stack.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import com.yinong.stack.control.Assets;
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
	World world = new World(new Vector2(0, -100), true); 	
	Box2DDebugRenderer 	debugRenderer;	
    ShapeRenderer shapeRenderer ;
	int screenWidth;
	int screenHeight;
	
	List<StackObject> boxes = new ArrayList<StackObject>();
	
	public GameScreen(Game game) {
		this.game = game;
		batch = new SpriteBatch();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
		update(delta);

        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
        //debugRenderer.render(world, camera.combined);  
        
        batch.begin();
        for(StackObject box:boxes) {
        	box.draw(batch,box.getWidth(),box.getHeight());
        }
        
        
        batch.end();
        

//        for(Box box:boxes) {
//        	box.draw(shapeRenderer);
//        }        

        //testDraw();
	}
	
	int angle=0;
	int p = 0;
	void testDraw() {
		
		shapeRenderer.begin(ShapeType.Line);
		Matrix4 m = new Matrix4();
		m.idt();
		m.translate(100,camera.viewportHeight-p,0);
		m.rotate(0, 0, 1, angle);
		
		shapeRenderer.setTransformMatrix(m);
		shapeRenderer.line(-5, camera.viewportHeight-5, 5,camera.viewportHeight+5);
		shapeRenderer.line(-5, camera.viewportHeight+5, 5,camera.viewportHeight-5);
		shapeRenderer.end();
		
		angle += 1;
		p--;
		if( p <0 ) 
			p = (int)camera.viewportHeight;
	}
	
	
	float time = 0;	
	int count=0;
	void update(float delta) {
		Random r = new Random();
		
		if( delta < 1/2f)
			time += delta;
		
		if( time > 1 ) { 
			int size = r.nextInt(5)+5;
			boxes.add(new StackObject(world,(float)size,(float)size,
					BodyType.DynamicBody,3,0.01f,
					camera.viewportWidth/2,camera.viewportHeight,0,Assets.brick));
//			System.out.println("new box" + (count++));
	        
	        time = 0;
		}
	}
	


	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void show() {
        camera = new OrthographicCamera();  
        camera.viewportHeight = 320;  
        camera.viewportWidth = 480;  
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
        camera.update();  
        
        //Ground body  
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 10));  
        Body groundBody = world.createBody(groundBodyDef);  
        EdgeShape groundBox = new EdgeShape();  
        groundBox.set(0, 0,camera.viewportWidth,0);
        groundBody.createFixture(groundBox, 0.0f);  
        
		batch.setProjectionMatrix(camera.combined);
        debugRenderer = new Box2DDebugRenderer();  
        
        shapeRenderer  = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        
//		boxes.add(new Box(world,10,10,
//				BodyType.DynamicBody,1,0.5f,
//				camera.viewportWidth/2,camera.viewportHeight,0,Assets.brick));
        
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

}
