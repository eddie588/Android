package com.yinong.kilobolt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GameBoard extends View  {

	Robot  robot = null;
	Background bg1 = null;
	Background bg2 = null;
	ElainAnimated elain = null;
	Explosion explosion = null;
	SpriteAnimated heliboy;
	BouncingBall ball;
	
	Paint paint = new Paint();
	
	
	ArrayList<Circle> circles= new ArrayList<Circle>();

	public GameBoard(Context context, AttributeSet aSet) {
		super(context, aSet);
		// TODO Auto-generated constructor stub
		robot = new Robot(BitmapFactory.decodeResource(getResources(), R.drawable.character));
		bg1 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background),0,0);
		bg2 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background),0,0);
		bg2.setBgX(bg2.getImage().getWidth());
		
		elain = new ElainAnimated(BitmapFactory.decodeResource(getResources(), R.drawable.walk_elaine),5,5);
		elain.setCenterX(0);
		elain.setCenterY(100);
		elain.setSpeedX(1);
		
		heliboy = new SpriteAnimated(BitmapFactory.decodeResource(getResources(), R.drawable.heliboy),5,5);
		heliboy.setCenterX(0);
		heliboy.setCenterY(200);
		heliboy.setSpeedX(5);

		explosion = new Explosion(100,100,100);
		ball = new BouncingBall();
		ball.setX(100);
		ball.setY(0);
		
					
		
		Thread run = new Thread() {
			public void run() {
				while (true) {
					createNewCircle();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}
				}
			}
		};
		
		//run.start();

	}

	@Override
	synchronized public void onDraw(Canvas canvas) {
		// Update
		//robot.setCanvasSize(getWidth(), getHeight());
		//robot.update();
		bg1.setCanvasSize(getWidth(), getHeight());
		bg1.update();
		
		bg2.setCanvasSize(getWidth(), getHeight());
		bg2.update();
		elain.update();
		heliboy.update();
		explosion.update();
		ball.setHeight(getHeight());
		ball.update();
		
		// update all circle
		updateCircles();
	
		// Draw
		canvas.drawBitmap(bg1.getImage(), bg1.getBgX(), bg1.getBgY(), null);
		canvas.drawBitmap(bg2.getImage(), bg2.getBgX(), bg2.getBgY(), null);
				
		//canvas.drawBitmap(robot.getImage(), robot.getCenterX()-robot.getImage().getWidth()/2, robot.getCenterY()-robot.getImage().getHeight()/2, null);
		
		Iterator<Circle> it = circles.iterator();
		while( it.hasNext()) {
			Circle circle = it.next();
			paint.setColor(circle.getColor());
			canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), paint);
		}
		
		elain.draw(canvas);
		heliboy.draw(canvas);
		explosion.draw(canvas);
		ball.draw(canvas, paint);
	}
	
	private synchronized void updateCircles() {
		Iterator<Circle> it = circles.iterator();
		while( it.hasNext()) {
			Circle circle = it.next();
			circle.setCanvasHeight(getHeight());
			circle.setCanvasWidth(getWidth());			
			circle.update();
		}
				
	}

	static int colors[] = {Color.BLUE,Color.RED,Color.CYAN,Color.YELLOW};
	
	synchronized private void createNewCircle() {
		if( circles.size() >=40 )
			return;
		Random r = new Random();
		int min = 1;
		int max = 900;
		int x = r.nextInt(max - min + 1) + min;
		
		Circle circle = new Circle(x,0,colors[r.nextInt(4)]);
		
		circle.setSpeedX(r.nextInt(5));
		circle.setSpeedX(r.nextInt(5));
		
		circles.add(circle);
		
	}
}
