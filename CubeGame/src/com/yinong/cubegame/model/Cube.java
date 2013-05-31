package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

import com.yinong.cubegame.util.Vect3D;

public class Cube {
	 private FloatBuffer mVertexBuffer;
	    private FloatBuffer mColorBuffer;
	    private ByteBuffer  mIndexBuffer;
	    private ByteBuffer  mLineBuffer;
	    private float size;
	    private Vertex center;

	        
	    private float verticesTemplate[] = {
	                                -1.0f, -1.0f, -1.0f,
	                                1.0f, -1.0f, -1.0f,
	                                1.0f,  1.0f, -1.0f,
	                                -1.0f, 1.0f, -1.0f,
	                                -1.0f, -1.0f,  1.0f,
	                                1.0f, -1.0f,  1.0f,
	                                1.0f,  1.0f,  1.0f,
	                                -1.0f,  1.0f,  1.0f
	                                };
	    private float colors[] = {
	                               0.0f,  1.0f,  0.0f,  1.0f,
	                               0.0f,  1.0f,  0.0f,  1.0f,
	                               1.0f,  0.5f,  0.0f,  1.0f,
	                               1.0f,  0.5f,  0.0f,  1.0f,
	                               1.0f,  0.0f,  0.0f,  1.0f,
	                               1.0f,  1.0f,  0.0f,  1.0f,
	                               0.0f,  0.0f,  1.0f,  1.0f,
	                               1.0f,  0.0f,  1.0f,  1.0f
	                            };
	   
//	    private byte indices[] = {
//	                              0, 4, 5, 0, 5, 1,
//	                              1, 5, 6, 1, 6, 2,
//	                              2, 6, 7, 2, 7, 3,
//	                              3, 7, 4, 3, 4, 0,
//	                              4, 7, 6, 4, 6, 5,
//	                              3, 0, 1, 3, 1, 2
//	                              };
	    
	    private byte indices[] = {
                0, 4, 5, 1, 0, 5,
                1, 5, 6, 2, 1, 6, 
                2, 6, 7, 3, 2, 7,
                7, 4, 3, 4, 0, 3, 
                7, 6, 4, 6, 5, 4,  
                3, 0, 1, 2, 3, 1 
                };	    
	    private byte indicesLine[] = {
                0, 1, 1,2, 2,3, 3,0,
                4, 5, 5,6,6,7,7,4,3,7,4,0,1,5,6,2
                };	    
	               
	    
	    public Cube(float x,float y,float z,float size) {
	    	this.size = size; 
	    	center = new Vertex(x,y,z);
	    	for(int i=0;i<24;i+=3) {
	    		verticesTemplate[i] = verticesTemplate[i]*size/2+x;
	    		verticesTemplate[i+1] = verticesTemplate[i+1]*size/2+y;
	    		verticesTemplate[i+2] = verticesTemplate[i+2]*size/2+z;
	    	}
            setupBuffer();    
	    }
	    
	    
	    void setupBuffer() {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(verticesTemplate.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mVertexBuffer = byteBuf.asFloatBuffer();
            mVertexBuffer.put(verticesTemplate);
            mVertexBuffer.position(0);
                
            byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mColorBuffer = byteBuf.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0);
                
            mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
            mIndexBuffer.put(indices);
            mIndexBuffer.position(0);
            
            mLineBuffer = ByteBuffer.allocateDirect(indicesLine.length);
            mLineBuffer.put(indicesLine);
            mLineBuffer.position(0);            
	    }
	    
	    
	    public float getSize() {
	    	return size;
	    }
	    
	    public Vertex getCenter() {
//	    	center.x = (verticesTemplate[0] + verticesTemplate[18])/2;
//	    	center.y = (verticesTemplate[1] + verticesTemplate[19])/2;
//	    	center.z = (verticesTemplate[2] + verticesTemplate[20])/2;
	    	return center;
	    }

	    public void draw(GL10 gl) {             
	            gl.glFrontFace(GL10.GL_CW);
	
	            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
	            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
	            
	            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	            
	            
	            gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, 
	                            mIndexBuffer);
	            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	            gl.glLineWidth(6f);
	            //gl.glEnable(gl.GL_LINE_SMOOTH);
	            gl.glDrawElements(GL10.GL_LINES, 24, GL10.GL_UNSIGNED_BYTE, 
                        mLineBuffer);
	            
	            
	            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	    }
	    	    
	    
	    public void rotate(float angle,float x,float y,float z) {
		    float[] mModelMatrix = new float[16];    
		    Matrix.setIdentityM(mModelMatrix, 0);
	    	Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);  	
	    	float[] inVec = new float[4];
	    	float[] outVec = new float[4];

	    	for(int i=0;i<verticesTemplate.length;i+=3) {
	    		inVec[0] = verticesTemplate[i];
	    		inVec[1] = verticesTemplate[i+1];
	    		inVec[2] = verticesTemplate[i+2];
	    		inVec[3] = 1;
	    		Matrix.multiplyMV(outVec, 0, mModelMatrix, 0, inVec, 0);
	    		verticesTemplate[i] = outVec[0];
	    		verticesTemplate[i+1] = outVec[1];
	    		verticesTemplate[i+2] = outVec[2];
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
	    }
	    
	    public Triangle[] getTriangles() {
	    	Triangle[] triangle = new Triangle[indices.length/3];
	    	for(int i=0;i<indices.length;i++) {
	    		triangle[i] = new Triangle(
	    			new Vect3D(verticesTemplate[indices[i]*3],verticesTemplate[indices[i]*3+1],verticesTemplate[indices[i]*3+2]),
	    			new Vect3D(verticesTemplate[indices[i+1]*3],verticesTemplate[indices[i+1]*3+1],verticesTemplate[indices[i+1]*3+2]),
	    			new Vect3D(verticesTemplate[indices[i+2]*3],verticesTemplate[indices[i+2]*3+1],verticesTemplate[indices[i+2]*3+2])
	    		);
	    	}
	    	return triangle;
	    }
}
