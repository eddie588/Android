package com.yinong.cubegame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

public class ImageBox {
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
	
	Bitmap bitmap;
	
	public ImageBox(float x,float y,float z,float width,float height,Bitmap bitmap) {
		this.bitmap = bitmap;
		//	setup vertices
		vertices[0] = x-width/2;   // bottom left
		vertices[1] = y-height/2;
		vertices[2] = z;
		
		vertices[3] = x-width/2;   // top left
		vertices[4] = y+height/2;
		vertices[5] = z;
		
		vertices[6] = x+width/2;   // bottom right
		vertices[7] = y-height/2;
		vertices[8] = z;

		vertices[9] = x+width/2;   // top right
		vertices[10] = y+height/2;
		vertices[11] = z;
		
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
	
	public void draw(GL10 gl) {
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		loadBitmap(gl);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// bind the previously generated texture
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
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);		
	}

	
	public void loadBitmap(GL10 gl) {
		if( ! genTextures)
			return;	

		//Generate one texture pointer…
		gl.glGenTextures(1, textures, 0);
		genTextures = false;
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
