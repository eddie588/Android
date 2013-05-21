package com.yinong.spritegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GameBoard extends View {
	private Paint p;
	private List<Point> starField = null;
	private int starAlpha = 80;
	private int starFade = 2;

	// Add private variables to keep up with sprite position and size
	Sprite sprite1 = new Sprite();
	Sprite sprite2 = new Sprite();

	// Bitmaps that hold the actual sprite images
	private Bitmap bm1 = null;
	private Bitmap bm2 = null;
	private Matrix m = null;
	private int sprite1Rotation = 0;
	private Point lastCollision = new Point(-1, -1);
	private boolean collisionDetected = false;

	//
	private static final int NUM_OF_STARS = 25;

	synchronized public void resetStarField() {
		starField = null;
	}

	// Allow our controller to get and set the sprite positions





	public GameBoard(Context context, AttributeSet aSet) {
		super(context, aSet);
		// it's best not to create any new objects in the on draw
		// initialize them as class variables here
		p = new Paint();
		m = new Matrix();
		// load our bitmaps and set the bounds for the controller
		sprite1.setPosition(new Point(-1, -1));
		sprite2.setPosition(new Point(-1, -1));
		p = new Paint();
		bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
		bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.ufo);
		sprite1.setBound(new Rect(0, 0, bm1.getWidth(), bm1.getHeight()));
		sprite1.setBound(new Rect(0, 0, bm2.getWidth(), bm2.getHeight()));
	}

	private void initializeStars(int maxX, int maxY) {
		starField = new ArrayList<Point>();
		for (int i = 0; i < NUM_OF_STARS; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			starField.add(new Point(x, y));
		}
		collisionDetected = false;
	}

	@Override
	synchronized public void onDraw(Canvas canvas) {
		// create a black canvas
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);
		// initialize the starfield if needed
		if (starField == null) {
			initializeStars(canvas.getWidth(), canvas.getHeight());
		}
		// draw the stars
		p.setColor(Color.CYAN);
		p.setAlpha(starAlpha += 2 * starFade);
		// fade them in and out
		if (starAlpha >= 252 || starAlpha <= 80)
			starFade = starFade * -1;
		p.setStrokeWidth(5);
		for (int i = 0; i < NUM_OF_STARS; i++) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}

		// Now we draw our sprites. Items drawn in this function are stacked.
		// The items drawn at the top of the loop are on the bottom of the
		// z-order.
		// Therefore we draw our set, then our actors, and finally any fx.
		if (sprite1.getX() >= 0) {
			canvas.drawBitmap(bm1, sprite1.getX(), sprite1.getY(), null);
		}
		if (sprite2.getX() >= 0) {
			canvas.drawBitmap(bm2, sprite2.getX(), sprite2.getY(), null);
		}

		// Therefore we draw our set, then our actors, and finally any fx.
		if (sprite1.getX() >= 0) {
			m.reset();
			m.postTranslate((float) (sprite1.getX()), (float) (sprite1.getY()));
			m.postRotate(sprite1Rotation,
					(float) (sprite1.getX() + sprite1.getWidth() / 2.0),
					(float) (sprite1.getY() + sprite1.getWidth() / 2.0));
			canvas.drawBitmap(bm1, m, null);
			sprite1Rotation += 5;
			if (sprite1Rotation >= 360)
				sprite1Rotation = 0;
		}
		if (sprite2.getX() >= 0) {
			canvas.drawBitmap(bm2, sprite2.getX(), sprite2.getY(), null);
		}

		collisionDetected = checkForCollision();
		if (collisionDetected) {
			// if there is one lets draw a red X
			p.setColor(Color.RED);
			p.setAlpha(255);
			p.setStrokeWidth(5);
			canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
					lastCollision.x + 5, lastCollision.y + 5, p);
			canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
					lastCollision.x - 5, lastCollision.y + 5, p);
		}
		
	}

	private boolean checkForCollision() {
		if (sprite1.getX() < 0 && sprite2.getX() < 0 && sprite1.getY() < 0 && sprite2.getY() < 0)
			return false;
		Rect r1 = new Rect(sprite1.getX(), sprite1.getY(), sprite1.getX()
				+ sprite1.getWidth(), sprite1.getY() + sprite1.getHeight());
		Rect r2 = new Rect(sprite2.getX(), sprite2.getY(), sprite2.getX()
				+ sprite2.getWidth(), sprite2.getY() + sprite2.getHeight());
		Rect r3 = new Rect(r1);
		if (r1.intersect(r2)) {
			for (int i = r1.left; i < r1.right; i++) {
				for (int j = r1.top; j < r1.bottom; j++) {
					if (bm1.getPixel(i - r3.left, j - r3.top) != Color.TRANSPARENT) {
						if (bm2.getPixel(i - r2.left, j - r2.top) != Color.TRANSPARENT) {
							lastCollision = new Point(sprite2.getX() + i - r2.left,
									sprite2.getY() + j - r2.top);
							return true;
						}
					}
				}
			}
		}
		lastCollision = new Point(-1, -1);
		return false;
	}
	
	public Sprite getSprite1() {
		return sprite1;
	}

	public Sprite getSprite2() {
		return sprite2;
	}
}
