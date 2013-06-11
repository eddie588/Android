package com.yinong.stack.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class StackObject {
	public static final int PUSH_LEFT = 0;
	public static final int PUSH_RIGHT = 1;
	public static final int PUSH_DOWN = 2;
	
	Body body;
	float width;
	float height;
	Vector2 pos;

	
	TextureRegion texture;
	boolean isAlive=true;
	
	public StackObject(World world,float width,float height,
			BodyType bodyType,float density,float restitution,float px,float py,
			float angle,TextureRegion texture) {
		// TODO Auto-generated constructor stub
		pos=new Vector2(px,py);
		makeBody(world,width, height,  bodyType, density, restitution, pos, angle);
		this.texture=texture;
		
		this.width = width;
		this.height = height;
	}	
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void push(int direction) {
		switch(direction) {
		case PUSH_LEFT:
			body.applyLinearImpulse(-100000, 0, body.getPosition().x, getPosition().y);
			break;
		case PUSH_RIGHT:
			body.applyLinearImpulse(100000, 0, body.getPosition().x, getPosition().y);
			break;			
		}
	}

	void makeBody(World world,float width,float height,BodyDef.BodyType bodyType,
			float density,float restitution, Vector2 pos,float angle){		
      BodyDef bodyDef = new BodyDef();  
      bodyDef.type = BodyType.DynamicBody;  
      bodyDef.position.set(pos.x,pos.y);  
      body = world.createBody(bodyDef);  
      PolygonShape dynamicShape = new PolygonShape();  

      dynamicShape.setAsBox(width,height,new Vector2(0,0),0);  
      FixtureDef fixtureDef = new FixtureDef();  
      fixtureDef.shape = dynamicShape;  
      fixtureDef.density = density;  
      fixtureDef.friction = 0.5f;  
      fixtureDef.restitution = restitution;  
      body.createFixture(fixtureDef); 	 		

	//	bodyShape.dispose();
	}	
	

	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public float getAngle() {
		return body.getAngle();
	}
	
	public boolean isMoving() {
		return body.getLinearVelocity().y < 0.00001;
	}
	
	public void draw(SpriteBatch batch) {
		if( isAlive) {

			Matrix4 m = new Matrix4();
			m.idt();
			m.translate(getPosition().x,getPosition().y,0);
			m.rotate(0, 0, 1, getAngle()*MathUtils.radiansToDegrees);
			
			batch.setTransformMatrix(m);

			batch.draw(texture,-width, -height,width*2,height*2);					
		}
	}
	
	public void draw(ShapeRenderer renderer) {
		if( isAlive) {
			//texture.Position.set(getPosition());
			//texture.rotation=getAngle();	
		
//			Matrix4 m = new Matrix4();
//			m.idt();
//			m.translate(100,p,0);
//			m.rotate(0, 0, 1, angle);
//			
			Matrix4 m = new Matrix4();
			m.idt();

			m.translate(getPosition().x,getPosition().y,0);
			m.rotate(0,0,1,getAngle()*MathUtils.radiansToDegrees);
			renderer.setTransformMatrix(m);		
			
			renderer.begin(ShapeType.Line);
			//renderer.setColor(new Color());
			renderer.line(-width, +height, +width,-height);
			renderer.line(-width, -height, +width,+height);
			renderer.end();			

		}
	}	
}
