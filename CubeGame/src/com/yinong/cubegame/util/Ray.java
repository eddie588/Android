package com.yinong.cubegame.util;

import java.text.DecimalFormat;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.yinong.cubegame.model.Vertex;

public class Ray {
	Vect3D near = new Vect3D();
	Vect3D far = new Vect3D();

	Vect3D edge1 = new Vect3D();
	Vect3D edge2 = new Vect3D();
	Vect3D pvec = new Vect3D();
	Vect3D qvec = new Vect3D();
	Vect3D tvec = new Vect3D();
	Vect3D direction = new Vect3D();

	static float EPSILON = 0.000001f;
	
	public Ray(Vect3D p1,Vect3D p2) {
		near = p1;
		far = p2;
		calculationDirection();
	}

	public Ray(int width, int height, float xTouch, float yTouch,
			float[] projectionM, float[] modelViewM) {

		int[] viewport = { 0, 0, width, height };

		float[] nearCoOrds = new float[3];
		float[] farCoOrds = new float[3];
		float[] temp = new float[4];
		float[] temp2 = new float[4];
		float[] invertM = new float[16];

		float[] identityM = new float[16];

		// get the near and far ords for the click
		float winx = xTouch, winy = (float) viewport[3] - yTouch;

		// Log.d(TAG, "modelView is =" +
		// Arrays.toString(matrixGrabber.mModelView));
		// Log.d(TAG, "projection view is =" + Arrays.toString(
		// matrixGrabber.mProjection ));

		Matrix.setIdentityM(identityM, 0);
		if( modelViewM != null)
			Matrix.invertM(invertM, 0, modelViewM, 0);

		if( modelViewM == null)
			modelViewM = identityM;
//		printM(projectionM);
//		printM(modelViewM);

		int result = GLU.gluUnProject(winx, winy, 0f, identityM, 0,
				projectionM, 0,
				// identityM, 0, identityM, 0,
				viewport, 0, temp, 0);
		if( modelViewM != null)
			Matrix.multiplyMV(temp2, 0, invertM, 0, temp, 0);
		if (result == GL10.GL_TRUE) {
			nearCoOrds[0] = temp2[0] / temp2[3];
			nearCoOrds[1] = temp2[1] / temp2[3];
			nearCoOrds[2] = temp2[2] / temp2[3];

		}

		result = GLU.gluUnProject(winx, winy, 1f, identityM, 0, projectionM,
				0,
				// identityM, 0, identityM, 0,
				viewport, 0, temp, 0);
		if( modelViewM != null)
			Matrix.multiplyMV(temp2, 0, invertM, 0, temp, 0);
		if (result == GL10.GL_TRUE) {
			farCoOrds[0] = temp2[0] / temp2[3];
			farCoOrds[1] = temp2[1] / temp2[3];
			farCoOrds[2] = temp2[2] / temp2[3];
		}

		far.x = farCoOrds[0];
		far.y = farCoOrds[1];
		far.z = farCoOrds[2];

		near.x = nearCoOrds[0];
		near.y = nearCoOrds[1];
		near.z = nearCoOrds[2];

//		System.out.println("Near:" + nearCoOrds[0] + "," + nearCoOrds[1] + ","
//				+ nearCoOrds[2]);
//		System.out.println("Far:" + farCoOrds[0] + "," + farCoOrds[1] + ","
//				+ farCoOrds[2]);
		
		calculationDirection();
	}
	
	void calculationDirection() {
		
		// Calculate direction
		Vect3D delta = far.sub(near);
		final double norm2 = delta.getNormSq();

		this.direction = new Vect3D((float) (1.0 / Math.sqrt(norm2)), delta);
	}
	
	public Vect3D getDirection() {
		return direction;
	}

	public Vect3D getNear() {
		return near;
	}

	public Vect3D getFar() {
		return far;
	}

	void printM(float[] matrix) {
		DecimalFormat fmt = new DecimalFormat("00.000");
		for (int i = 0; i < 4; i++) {
			System.out.println(fmt.format(matrix[i]) + " "
					+ fmt.format(matrix[i + 4]) + " "
					+ fmt.format(matrix[i + 8]) + " "
					+ fmt.format(matrix[i + 12]));
		}
	}
	
	public Vect3D getIntersectCoord(Vect3D intersectRet) {
		Vect3D p = new Vect3D(intersectRet.x,direction);
		p.add(near);
		return p;
	}

	public Vect3D intersectTriangle(Vect3D v0, Vect3D v1, Vect3D v2) {
		// Find vectors for two edges sharing v0
		edge1 = Vect3D.sub(v1,v0);
		edge2 = Vect3D.sub(v2,v0);

		// Begin calculating determinant -- also used to calculate U parameter
		pvec = Vect3D.cross(direction, edge2);

		// If determinant is near zero, ray lies in plane of triangle
		float det = edge1.dot(pvec);

		if (det > -EPSILON && det < EPSILON)
			return null;

		float invDet = 1.0f / det;

		// Calculate distance from v0 to ray origin
		tvec = Vect3D.sub(near, v0);

		// Calculate U parameter and test bounds
		float u = tvec.dot(pvec) * invDet;
		if (u < 0.0f || u > 1.0f)
			return null;

		// Prepare to test V parameter
		qvec = Vect3D.cross(tvec, edge1);

		// Calculate V parameter and test bounds
		float v = direction.dot(qvec) * invDet;
		if (v < 0.0f || (u + v) > 1.0f)
			return null;

		// Calculate t, ray intersects triangle
		float t = edge2.dot(qvec) * invDet;

		return new Vect3D(t, u, v);
	}

//	public boolean intersectTriangleBackfaceCulling(Line ray, Vec3f vert0,
//			Vec3f vert1, Vec3f vert2, Vec3f tuv) {
//		// Find vectors for two edges sharing vert0
//		edge1.sub(vert1, vert0);
//		edge2.sub(vert2, vert0);
//
//		// Begin calculating determinant -- also used to calculate U parameter
//		pvec.cross(ray.getDirection(), edge2);
//
//		// If determinant is near zero, ray lies in plane of triangle
//		float det = edge1.dot(pvec);
//
//		if (det < EPSILON)
//			return false;
//
//		// Calculate distance from vert0 to ray origin
//		tvec.sub(ray.getPoint(), vert0);
//
//		// Calculate U parameter and test bounds
//		float u = tvec.dot(pvec);
//		if (u < 0.0f || u > det)
//			return false;
//
//		// Prepare to test V parameter
//		qvec.cross(tvec, edge1);
//
//		// Calculate V parameter and test bounds
//		float v = ray.getDirection().dot(qvec);
//		if (v < 0.0f || (u + v) > det)
//			return false;
//
//		// Calculate t, scale parameters, ray intersects triangle
//		float t = edge2.dot(qvec);
//		float invDet = 1.0f / det;
//		t *= invDet;
//		u *= invDet;
//		v *= invDet;
//		tuv.set(t, u, v);
//		return true;
//	}
}