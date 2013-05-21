package com.yinong.spritegame;

import java.util.Random;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpriteActivity extends Activity implements OnClickListener{
    
    private Handler frame = new Handler();
    //Velocity includes the speed and the direction of our sprite motion
    private Point sprite1Velocity;
    private Point sprite2Velocity;
    private int sprite1MaxX;
    private int sprite1MaxY;
    private int sprite2MaxX;
    private int sprite2MaxY;    
    private boolean isAccelerating=false;
    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 20; //50 frames per second

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sprite);
        Handler h = new Handler();
        ((Button)findViewById(R.id.the_button)).setOnClickListener(this);
        //We can't initialize the graphics immediately because the layout manager
        //needs to run first, thus we call back in a sec.
        h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                initGfx();
                        }
         }, 1000);			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sprite, menu);
	
		return true;
	}
	
    @Override
    synchronized public void onClick(View v) {
             initGfx();
    }	
    
    synchronized public void initGfx() {
        ((GameBoard)findViewById(R.id.the_canvas)).resetStarField();
        ((Button)findViewById(R.id.the_button)).setEnabled(true);
        //It's a good idea to remove any existing callbacks to keep
        //them from inadvertently stacking up.
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);
        
        Point p1, p2;
        do {
               p1 = getRandomPoint();
               p2 = getRandomPoint();
       } while (Math.abs(p1.x - p2.x) <
    		   ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getWidth());
        ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().setPosition(p1);
        ((GameBoard)findViewById(R.id.the_canvas)).getSprite2().setPosition(p2);	     
        
        //Give the asteroid a random velocity
        sprite1Velocity = getRandomVelocity();
        //Fix the ship velocity at a constant speed for now
        sprite2Velocity = new Point(1,1);
        //Set our boundaries for the sprites
        sprite1MaxX = findViewById(R.id.the_canvas).getWidth() -
        		((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getWidth();
        sprite1MaxY = findViewById(R.id.the_canvas).getHeight() -
        		((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getHeight();
        sprite2MaxX = findViewById(R.id.the_canvas).getWidth() -
        		((GameBoard)findViewById(R.id.the_canvas)).getSprite2().getWidth();
        sprite2MaxY = findViewById(R.id.the_canvas).getHeight() -
        		((GameBoard)findViewById(R.id.the_canvas)).getSprite2().getHeight();        
    }    

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
                frame.removeCallbacks(frameUpdate);
                //First get the current positions of both sprites
                Point sprite1 = new Point
    (((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getX(),
                                  ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getY()) ;
                Point sprite2 = new Point
    (((GameBoard)findViewById(R.id.the_canvas)).getSprite2().getX(),
                                  ((GameBoard)findViewById(R.id.the_canvas)).getSprite2().getY());
                //Now calc the new positions.
                //Note if we exceed a boundary the direction of the velocity gets reversed.
                sprite1.x = sprite1.x + sprite1Velocity.x;
                if (sprite1.x > sprite1MaxX || sprite1.x < 5) {
                        sprite1Velocity.x *= -1;
                }
                sprite1.y = sprite1.y + sprite1Velocity.y;
                if (sprite1.y > sprite1MaxY || sprite1.y < 5) {
                       sprite1Velocity.y *= -1;
                }
                sprite2.x = sprite2.x + sprite2Velocity.x;
                if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
                       sprite2Velocity.x *= -1;
                }
                sprite2.y = sprite2.y + sprite2Velocity.y;
                if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
                       sprite2Velocity.y *= -1;
                }
                updateVelocity();
                
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().setPosition(sprite1);
               ((GameBoard)findViewById(R.id.the_canvas)).getSprite2().setPosition(sprite2);   
                            
                //make any updates to on screen objects here
                //then invoke the on draw by invalidating the canvas
                ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
                frame.postDelayed(frameUpdate, FRAME_RATE);
        }
   };
   
   @Override
   synchronized public boolean onTouchEvent(MotionEvent ev) {
   final int action = ev.getAction();
   switch (action & MotionEvent.ACTION_MASK) {
         case MotionEvent.ACTION_DOWN:
         case MotionEvent.ACTION_POINTER_DOWN:
                 isAccelerating = true;
          break;
     case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
                isAccelerating = false;
         break;
   }
       return true;
   }   
   
   //Increase the velocity towards five or decrease
   //back to one depending on state
   private void updateVelocity() {
         int xDir = (sprite2Velocity.x > 0) ? 1 : -1;
         int yDir = (sprite2Velocity.y > 0) ? 1 : -1;
         int speed = 0;
         if (isAccelerating) {
                speed = Math.abs(sprite2Velocity.x)+1;
         } else {
                speed = Math.abs(sprite2Velocity.x)-1;
         }
         if (speed>5) speed =5;
         if (speed<1) speed =1;
         sprite2Velocity.x=speed*xDir;
         sprite2Velocity.y=speed*yDir;
  }   
   
   private Point getRandomPoint() {
       Random r = new Random();
    int minX = 0;
    int maxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getWidth();
       int x = 0;
    int minY = 0;
    int maxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1().getHeight();
    int y = 0;
       x = r.nextInt(maxX-minX+1)+minX;
       y = r.nextInt(maxY-minY+1)+minY;
       return new Point (x,y);
  }  
   
   private Point getRandomVelocity() {
       Random r = new Random();
       int min = 1;
       int max = 5;
       int x = r.nextInt(max-min+1)+min;
       int y = r.nextInt(max-min+1)+min;
       return new Point (x,y);
 }   
}
