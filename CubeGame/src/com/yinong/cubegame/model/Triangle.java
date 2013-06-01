package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.yinong.cubegame.util.GLColor;
import com.yinong.cubegame.util.Vect3D;

public class Triangle {
	public Vect3D v1;
	public Vect3D v2;
	public Vect3D v3;
	public GLColor color;
	
	 private FloatBuffer mVertexBuffer;
	 private FloatBuffer mColorBuffer;
	 private ByteBuffer  mIndexBuffer;
	 
	public Triangle(Vect3D v1, Vect3D v2, Vect3D v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = null;
	}

	public Triangle(Vect3D v1,Vect3D v2,Vect3D v3,GLColor color) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = color;
	}
	
	public synchronized void buildBuffer() {
		float[] vertices = new float[9];
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        vertices[0] = v1.x;
        vertices[1] = v1.y;
        vertices[2] = v1.z;
        vertices[3] = v2.x;
        vertices[4] = v2.y;
        vertices[5] = v2.z;
        vertices[6] = v3.x;
        vertices[7] = v3.y;
        vertices[8] = v3.z;        
        
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
            
	    float colors[] = {
                0.0f,  1.0f,  0.0f,  1.0f,
                0.0f,  1.0f,  0.0f,  1.0f,
                1.0f,  0.5f,  0.0f,  1.0f};
	    colors[8] = color.red;
	    colors[9] = color.green;
	    colors[10] = color.blue;
	    colors[11] = color.alpha;
	    
        byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuf.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
          
        byte indices[] = {0,1,2};
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);		
	}
	
	public synchronized void draw(GL10 gl) {

    	
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
          
        gl.glDrawElements(GL10.GL_TRIANGLES, 3 , GL10.GL_UNSIGNED_BYTE, 
                        mIndexBuffer);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
  
	}
}
