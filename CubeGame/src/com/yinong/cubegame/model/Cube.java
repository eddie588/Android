package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

import com.yinong.cubegame.util.Vect3D;

public class Cube {
	public static int CUBE_FRONT = 0;
	public static int CUBE_BACK = 1;
	public static int CUBE_RIGHT = 2;
	public static int CUBE_LEFT = 3;
	public static int CUBE_TOP = 4;
	public static int CUBE_BOTTOM = 5;

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;
	private FloatBuffer mNormalsBuffer;
	private ByteBuffer mLineBuffer;
	private float size;
	private Vect3D center;

	    // all rectangles counter clock wise
	    private float verticesTemplate[] = {
	    								 1f, 1f, 1f,   // front
		    							-1f,-1f, 1f,
		    							 1f,-1f, 1f,

		    							 1f, 1f, 1f,
		    							-1f, 1f, 1f,
		    							-1f,-1f, 1f,
		    							
	    								-1f,-1f,-1f,	// back
	    								 1f, 1f,-1f,
	    								 1f,-1f,-1f,
	    								 
	    								-1f,-1f,-1f,
	    								-1f, 1f,-1f,
	    								 1f, 1f,-1f,
	    								 
	    								 1f, 1f, 1f,	// right
	    								 1f,-1f, 1f,
	    								 1f,-1f,-1f,
	    								 
	    								 1f,-1f,-1f,
	    								 1f, 1f,-1f,
	    								 1f, 1f, 1f,
	    								 
	    								-1f,-1f, 1f,	// left
	    								-1f, 1f, 1f,
	    								-1f, 1f, -1f,
	    								
	    								-1f, 1f, -1f,
	    								-1f,-1f,-1f,
	    								-1f,-1f, 1f,
	    								
	    								-1f, 1f, -1f,  // top
	    								-1f, 1f, 1f,
	    								 1f, 1f, 1f,
	    								 
	    								 1f, 1f, 1f,
	    								 1f, 1f, -1f,
	    								 -1f, 1f, -1f,
	    								 
	    								 -1f,-1f,-1f, // bottom
	    								 1f,-1f,-1f,
	    								 1f,-1f,1f,
	    								 
	    								 1f,-1f,1f,
	    								 -1f,-1f,1f,
	    								 -1f,-1f,-1f
	                                };
	    private float colors[] = {
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  0.0f,  1.0f

	                            };
	    
	    private float[] normals = {
	    		0.0f, 0.0f, 1.0f, 
	    		0.0f, 0.0f, 1.0f, 
	    		0.0f, -0.0f, 1.0f, 

	    		0.0f, 0.0f, 1.0f, 
	    		-0.0f, 0.0f, 1.0f, 
	    		0.0f, 0.0f, 1.0f, 

	    		0.0f, 0.0f, -1.0f, 
	    		0.0f, -0.0f, -1.0f, 
	    		0.0f, 0.0f, -1.0f, 

	    		0.0f, 0.0f, -1.0f, 
	    		0.0f, 0.0f, -1.0f, 
	    		-0.0f, 0.0f, -1.0f, 

	    		1.0f, 0.0f, 0.0f, 
	    		1.0f, -0.0f, 0.0f, 
	    		1.0f, 0.0f, 0.0f, 

	    		1.0f, 0.0f, 0.0f, 
	    		1.0f, 0.0f, -0.0f, 
	    		1.0f, 0.0f, 0.0f, 

	    		-1.0f, 0.0f, 0.0f, 
	    		-1.0f, -0.0f, -0.0f, 
	    		-1.0f, 0.0f, 0.0f, 

	    		-1.0f, 0.0f, 0.0f, 
	    		-1.0f, 0.0f, 0.0f, 
	    		-1.0f, 0.0f, 0.0f, 

	    		0.0f, 1.0f, 0.0f, 
	    		-0.0f, 1.0f, 0.0f, 
	    		0.0f, 1.0f, 0.0f, 

	    		0.0f, 1.0f, 0.0f, 
	    		0.0f, 1.0f, -0.0f, 
	    		0.0f, 1.0f, 0.0f, 

	    		0.0f, -1.0f, 0.0f, 
	    		0.0f, -1.0f, 0.0f, 
	    		0.0f, -1.0f, -0.0f, 

	    		-0.0f, -1.0f, 0.0f, 
	    		0.0f, -1.0f, 0.0f, 
	    		0.0f, -1.0f, 0.0f, 
	    		
	    };
	       
	    private byte indicesLine[] = {
	    		4,5,5,2,2,0,0,4,
	    		6,8,8,7,7,10,10,6,
	    		4,10,0,7,2,8,5,6
                };	    
	               
	    
	    public Cube(float x,float y,float z,float size) {
	    	this.size = size; 
	    	center = new Vect3D(x,y,z);
	    	for(int i=0;i<verticesTemplate.length;i+=3) {
	    		verticesTemplate[i] = verticesTemplate[i]*size/2+x;
	    		verticesTemplate[i+1] = verticesTemplate[i+1]*size/2+y;
	    		verticesTemplate[i+2] = verticesTemplate[i+2]*size/2+z;
	    		
	    		normals[i] = normals[i]*size/2+x;
	    		normals[i+1] = normals[i+1]*size/2+y;
	    		normals[i+2] = normals[i+2]*size/2+z;	    		
	    	}
	    	
	  	
	    	setDefaultColor(0.f, 0.f, 0.f, 0.5f);
            setupBuffer();    
	    }
	    
	    public void setDefaultColor(float red,float green,float blue,float alpha) {
	    	for(int i=0;i<colors.length;i+=4) {
	    		colors[i] = red;
		    	colors[i+1] = green;
		    	colors[i+2] = blue;
		    	colors[i+3] = alpha;	    		
	    	}
	    }
	    
	    public void setColor(int face,float red,float green,float blue,float alpha) {
	    	//	Assuming face number is the same order in verticesTemplate.
	    	//	Set the third point of both of the two triangles in rectangle.
	    	int start = (face*6)*4;
	    	
	    	for(int i=0;i<6;i++) {
		    	colors[start+i*4] = red;
		    	colors[start+i*4+1] = green;
		    	colors[start+i*4+2] = blue;
		    	colors[start+i*4+3] = alpha;
	    	}
            mColorBuffer.put(colors);
            mColorBuffer.position(0);	    	
	    }
	    
	    
	    void setupBuffer() {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(verticesTemplate.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mVertexBuffer = byteBuf.asFloatBuffer();
            mVertexBuffer.put(verticesTemplate);
            mVertexBuffer.position(0);
            
            byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mNormalsBuffer = byteBuf.asFloatBuffer();
            mNormalsBuffer.put(normals);
            mNormalsBuffer.position(0);            
                
            byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mColorBuffer = byteBuf.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0);
            
            mLineBuffer = ByteBuffer.allocateDirect(indicesLine.length);
            mLineBuffer.put(indicesLine);
            mLineBuffer.position(0);            
	    }
	    
	    
	public float getSize() {
		return size;
	}

	public Vect3D getCenter() {

		return center;
	}

	public void draw(GL10 gl) {

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalsBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 36);
//		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glLineWidth(10f);
//		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
//		// gl.glEnable(gl.GL_LINE_SMOOTH);
//		gl.glDrawElements(GL10.GL_LINES, 24, GL10.GL_UNSIGNED_BYTE, mLineBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);		
	}

	/**
	 * This function will transform all the coordinates data of the object including: 
	 * 	vertex, normal and center.
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate(float angle, float x, float y, float z) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
		float[] inVec = new float[4];
		float[] outVec = new float[4];

		for (int i = 0; i < verticesTemplate.length; i += 3) {
			inVec[0] = verticesTemplate[i];
			inVec[1] = verticesTemplate[i + 1];
			inVec[2] = verticesTemplate[i + 2];
			inVec[3] = 1;
			Matrix.multiplyMV(outVec, 0, mModelMatrix, 0, inVec, 0);
			verticesTemplate[i] = outVec[0];
			verticesTemplate[i + 1] = outVec[1];
			verticesTemplate[i + 2] = outVec[2];
			
			inVec[0] = normals[i];
			inVec[1] = normals[i + 1];
			inVec[2] = normals[i + 2];
			inVec[3] = 1;
			Matrix.multiplyMV(outVec, 0, mModelMatrix, 0, inVec, 0);
			normals[i] = outVec[0];
			normals[i + 1] = outVec[1];
			normals[i + 2] = outVec[2];			
		}
		inVec[0] = center.x;
		inVec[1] = center.y;
		inVec[2] = center.z;
		inVec[3] = 1;
		Matrix.multiplyMV(outVec, 0, mModelMatrix, 0, inVec, 0);
		center.x = outVec[0];
		center.y = outVec[1];
		center.z = outVec[2];
		
		mVertexBuffer.put(verticesTemplate);
		mVertexBuffer.position(0);
		
		mNormalsBuffer.put(normals);
		mNormalsBuffer.position(0);
	}

	public Triangle[] getTriangles() {
		Triangle[] triangle = new Triangle[verticesTemplate.length / 9];
		for (int i = 0; i < verticesTemplate.length; i += 9) {
			triangle[i / 9] = new Triangle(new Vect3D(verticesTemplate[i],
					verticesTemplate[i + 1], verticesTemplate[i + 2]),
					new Vect3D(verticesTemplate[i + 3],
							verticesTemplate[i + 4], verticesTemplate[i + 5]),
					new Vect3D(verticesTemplate[i + 6],
							verticesTemplate[i + 7], verticesTemplate[i + 8]));
			// System.out.println(triangle[i/3].v1 + " - " + triangle[i/3].v2 +
			// " - " + triangle[i/3].v3 );
		}
		return triangle;
	}
}
