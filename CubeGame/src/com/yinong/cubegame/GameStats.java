package com.yinong.cubegame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.opengl.GLUtils;

public class GameStats {
	private FloatBuffer vertexBuffer;	// buffer holding the vertices
	private FloatBuffer textureBuffer;	// buffer holding the vertices
	
	private float vertices[] = {
			-1.0f, 0.85f,  0.0f,		// V1 - bottom left
			-1.0f,  1.0f,  0.0f,		// V2 - top left
			 1.0f, 0.85f,  0.0f,		// V3 - bottom right
			 1.0f,  1.0f,  0.0f			// V4 - top right
	};
	private float texture[] = {    		
			// Mapping coordinates for the vertices
			0.0f, 1.0f,		// top left		(V2)
			0.0f, 0f,		// bottom left	(V1)
			1.0f, 1.0f,		// top right	(V4)
			1.0f, 0f		// bottom right	(V3)
	};	
	
	public GameStats() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4); 
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuffer.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}
	
	int[] textures = new int[1];
	
	boolean genTextures = true;
	
	public void draw(GL10 gl,String time,String moves) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glOrthof(-1f, 1f, -1f, 1f, 1.0f, -1.0f);
		gl.glLoadIdentity();
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// bind the previously generated texture
		prepareContent(gl,time,moves);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		
		//gl.glColor4f(0f, 1f, 0f, 1f);
		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);	
		gl.glDisable(GL10.GL_TEXTURE_2D);		

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();	
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);		
	}

	
	void prepareContent(GL10 gl,String time,String moves) {
		// Create an empty, mutable bitmap
		Bitmap bitmap = Bitmap.createBitmap(512,64,Bitmap.Config.ARGB_8888);
		// get a canvas to paint over the bitmap
		Canvas canvas = new Canvas(bitmap);
		//bitmap.eraseColor(0x4c4c4c);
		// get a background image from resources
		// note the image format must match the bitmap format

		// Draw the text
		Paint textPaint = new Paint();
		
		canvas.drawRGB(0x4c, 0x4c, 0x4c);
		
		//	prepare for text
		textPaint.setTextSize(16);
		
		textPaint.setAntiAlias(true);
		textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
		textPaint.setStrokeWidth(0);
		
		// draw the text centered
		canvas.drawText("Time: " + time, 20,32, textPaint);
					
		textPaint.measureText("Moves: " + moves);
		canvas.drawText("Moves: " + moves, (512-20-textPaint.measureText("Moves: " + moves)),32, textPaint);
		
		//Generate one texture pointer…
		if( genTextures ) {
			gl.glGenTextures(1, textures, 0);
			genTextures = false;
		}
		//…and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		//Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		//Clean up
		bitmap.recycle();		
	}
}
