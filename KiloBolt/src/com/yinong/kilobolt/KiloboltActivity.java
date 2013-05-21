package com.yinong.kilobolt;

import com.yinong.kilobolt.GameBoard;
import com.yinong.kilobolt.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Button;

public class KiloboltActivity extends Activity {
    private static final int FRAME_RATE = 20; //50 frames per second
    
	private Handler frame = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kilobolt);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kilobolt, menu);
		
        Handler h = new Handler();
        //We can't initialize the graphics immediately because the layout manager
        //needs to run first, thus we call back in a sec.
        h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                initGame();
                        }
         }, 1000);			
		return true;
	}
	
	private void initGame() {
		frame.removeCallbacks(frameUpdate);
		frame.postDelayed(frameUpdate, FRAME_RATE);
	}
	
    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
        	// Remove any callback.
        	frame.removeCallbacks(frameUpdate);
        	
        	// Main control 
        	
            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        	
        }
  	
    };
    
	synchronized public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			// isAccelerating = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// isAccelerating = false;
			break;
		}
		return true;
	}   

}	
