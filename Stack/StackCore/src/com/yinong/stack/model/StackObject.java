package com.yinong.stack.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	Body body;
	float width;
	float height;
	Vector2 pos;

	
	TextureWrapper texture;
	boolean isAlive=true;
	
	public StackObject(World world,float width,float height,
			BodyType bodyType,float density,float restitution,float px,float py,
			float angle,Texture texture) {
		// TODO Auto-generated constructor stub
		pos=new Vector2(px,py);
		makeBody(world,width, height,  bodyType, density, restitution, pos, angle);
		this.texture=new TextureWrapper(texture, pos);
	}	
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}

	void makeBody(World world,float width,float height,BodyDef.BodyType bodyType,
			float density,float restitution, Vector2 pos,float angle){		
		
		this.width = width;
		this.height = height;

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
	
	public void draw(SpriteBatch batch,float width,float height) {
		if( isAlive) {
			texture.Position.set(getPosition());
			//texture.rotation=getAngle();	
			
			
			texture.SetTexture(2*width,2*height);
			
			Matrix4 m = new Matrix4();
			m.idt();
			m.translate(getPosition().x,getPosition().y,0);
			m.rotate(0, 0, 1, getAngle()*MathUtils.radiansToDegrees);
			batch.setTransformMatrix(m);
			texture.Draw(batch);
		}
	}
	
	public void draw(ShapeRenderer renderer) {
		if( isAlive) {
			//texture.Position.set(getPosition());
			//texture.rotation=getAngle();	
			
			
			texture.SetTexture(2*width,2*height);
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
