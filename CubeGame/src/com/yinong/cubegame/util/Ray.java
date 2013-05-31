package com.yinong.cubegame.util;

import java.text.DecimalFormat;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.yinong.cubegame.MatrixTrackingGL;

public class Ray {
	float[] P0;
	float[] P1;

	public Ray(MatrixTrackingGL gl, int width, int height, float xTouch,
			float yTouch) {

		int[] viewport = { 0, 0, width, height };

		float[] nearCoOrds = new float[3];
		float[] farCoOrds = new float[3];
		float[] temp = new float[4];
		float[] temp2 = new float[4];
		
		float[] identityM = new float[16];
		
		float[] projectionM;
		float[] modelViewM;

		// get the near and far ords for the click
		float winx = xTouch, winy = (float) viewport[3] - yTouch;

		// Log.d(TAG, "modelView is =" +
		// Arrays.toString(matrixGrabber.mModelView));
		// Log.d(TAG, "projection view is =" + Arrays.toString(
		// matrixGrabber.mProjection ));
		
		Matrix.setIdentityM(identityM, 0);
		
		projectionM = getCurrentProjection(gl);
		modelViewM = getCurrentModelView(gl);
		modelViewM = identityM;
		printM(projectionM);
		printM(modelViewM);
			

		int result = GLU.gluUnProject(winx, winy, 0f,
				modelViewM, 0, projectionM, 0,
//				identityM, 0, identityM, 0,
				viewport, 0, temp, 0);

		Matrix.multiplyMV(temp2, 0, modelViewM, 0, temp, 0);
		if (result == GL10.GL_TRUE) {
			nearCoOrds[0] = temp2[0] / temp2[3];
			nearCoOrds[1] = temp2[1] / temp2[3];
			nearCoOrds[2] = temp2[2] / temp2[3];

		}

		result = GLU.gluUnProject(winx, winy, 1f, 
				modelViewM, 0, projectionM, 0,
//				identityM, 0, identityM, 0, 
				viewport, 0, temp, 0);
		Matrix.multiplyMV(temp2, 0, modelViewM, 0, temp, 0);
		if (result == GL10.GL_TRUE) {
			farCoOrds[0] = temp2[0] / temp2[3];
			farCoOrds[1] = temp2[1] / temp2[3];
			farCoOrds[2] = temp2[2] / temp2[3];
		}
		this.P0 = farCoOrds;
		this.P1 = nearCoOrds;
		
		System.out.println("Near:" + nearCoOrds[0] +"," 
				+ nearCoOrds[1] + "," 
				+nearCoOrds[2]);
		System.out.println("Far:" + farCoOrds[0] +"," 
				+ farCoOrds[1] + "," 
				+farCoOrds[2]);		
	}

	public float[] getCurrentModelView(GL10 gl) {
		float[] mModelView = new float[16];
		getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
		return mModelView;
	}

	public float[] getCurrentProjection(GL10 gl) {
		float[] mProjection = new float[16];
		getMatrix(gl, GL10.GL_PROJECTION, mProjection);
		return mProjection;
	}

	/**
	 * Fetches a specific matrix from opengl
	 * 
	 * @param gl
	 *            context
	 * @param mode
	 *            of the matrix
	 * @param mat
	 *            initialized float[16] array to fill with the matrix
	 */
	private void getMatrix(GL10 gl, int mode, float[] mat) {
		MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
		gl2.glMatrixMode(mode);
		gl2.getMatrix(mat, 0);
	}
	
	void printM(float[] matrix) {
		DecimalFormat fmt = new DecimalFormat("00.000");
		for(int i=0;i<4;i++) {
			System.out.println(fmt.format(matrix[i]) + " " 
					 + fmt.format(matrix[i+4]) + " "
					 + fmt.format(matrix[i+8]) + " "
					 + fmt.format(matrix[i+12]) );
		}
	}
}