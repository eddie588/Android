package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

import com.yinong.cubegame.util.Ray;
import com.yinong.cubegame.util.Vect3D;

public class Cube3By3 {
	Cube[] cubes;
	public static int FACE_FRONT = 0;
	public static int FACE_BACK = 1;
	public static int FACE_SIDE = 2;
	public static int FACE_TOP = 3;
	public static int FACE_BOTTOM = 4;
	public static int FACE_EQUATOR = 5;
	public static int FACE_LEFT = 6;
	public static int FACE_RIGHT = 7;
	public static int FACE_MIDDLE = 8;

	private float cubeSize = 1f;
	float[] accumulatedRotation = new float[16];
	float[] currentRotation = new float[16];
	private FloatBuffer matrixBuffer;

	private int shuffle = 0;
	private long period = 200;
	private long lastUpdate = 0;

	private Vertex position;

	private static float EPSILON = 0.00001f;
	

	
	private Queue<RotateRequest> rotateRequests = new ConcurrentLinkedQueue<RotateRequest>();

	public Cube3By3(float x, float y, float z) {
		position = new Vertex(x, y, z);
		cubes = new Cube[27];
		int p = 0;
		int cx = 0;
		int cy = 0;
		int cz = 0;
		for (cx = -1; cx < 2; cx++) {
			for (cy = -1; cy < 2; cy++) {
				for (cz = 1; cz > -2; cz--) {
					cubes[p++] = new Cube(cubeSize * cx, cubeSize * cy,
							cubeSize * cz, cubeSize);
				}
			}
		}
		// controller = new GameController(view,cube);
		Matrix.setIdentityM(accumulatedRotation, 0);
		Matrix.setIdentityM(currentRotation, 0);

		// Matrix.rotateM(accumulatedRotation,0,45f,1f,1f,1f);

		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(accumulatedRotation.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		matrixBuffer = byteBuf.asFloatBuffer();
		matrixBuffer.put(accumulatedRotation);
		matrixBuffer.position(0);
	}

	public Vertex getPosition() {
		return position;
	}

	public synchronized void draw(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();

		gl.glTranslatef(position.x, position.y, position.z);
		gl.glMultMatrixf(matrixBuffer);
		for (int i = 0; i < 27; i++) {
			if (cubes[i] != null)
				cubes[i].draw(gl);
		}
	}

	public List<Cube> getCubes(int face) {
		List<Cube> list = new ArrayList<Cube>();

		if (face == FACE_FRONT || face == FACE_SIDE || face == FACE_BACK) {
			float z = 0;
			z = (face == FACE_FRONT) ? cubeSize
					: ((face == FACE_BACK) ? -cubeSize : 0);
			for (int i = 0; i < 27; i++) {
				if (Math.abs(cubes[i].getCenter().z - z) < EPSILON)
					list.add(cubes[i]);
			}
		}

		if (face == FACE_LEFT || face == FACE_MIDDLE || face == FACE_RIGHT) {
			float x = 0;
			x = (face == FACE_RIGHT) ? cubeSize
					: ((face == FACE_LEFT) ? -cubeSize : 0);
			for (int i = 0; i < 27; i++) {
				if (Math.abs(cubes[i].getCenter().x - x) < EPSILON)
					list.add(cubes[i]);
			}
		}

		if (face == FACE_TOP || face == FACE_EQUATOR || face == FACE_BOTTOM) {
			float y = 0;
			y = (face == FACE_TOP) ? cubeSize
					: ((face == FACE_BOTTOM) ? -cubeSize : 0);
			for (int i = 0; i < 27; i++) {
				if (Math.abs(cubes[i].getCenter().y - y) < EPSILON)
					list.add(cubes[i]);
			}
		}
		return list;
	}


	public void rotateFace(int face, int angle) {
		rotateRequests.add(new RotateRequest(face,angle));
	}
	

	public void rotate(Vect3D p1, Vect3D p2) {
		// Swipe on front face
		if ((Math.abs(p1.z - 1.5 * cubeSize) < EPSILON && Math.abs(p2.z - 1.5
				* cubeSize) < EPSILON)
				|| (Math.abs(p1.z + 1.5 * cubeSize) < EPSILON && Math.abs(p2.z
						+ 1.5 * cubeSize) < EPSILON)) {
			handleFrontBackSwipe(p1, p2);
		}
		
		if ((Math.abs(p1.x - 1.5 * cubeSize) < EPSILON && Math.abs(p2.x - 1.5
				* cubeSize) < EPSILON)
				|| (Math.abs(p1.x + 1.5 * cubeSize) < EPSILON && Math.abs(p2.x
						+ 1.5 * cubeSize) < EPSILON)) {
			handleLeftRightSwipe(p1, p2);
		}
		
		if ((Math.abs(p1.y - 1.5 * cubeSize) < EPSILON && Math.abs(p2.y - 1.5
				* cubeSize) < EPSILON)
				|| (Math.abs(p1.y + 1.5 * cubeSize) < EPSILON && Math.abs(p2.y
						+ 1.5 * cubeSize) < EPSILON)) {
			handleBottomTopSwipe(p1, p2);
		}	
	}

	private void handleFrontBackSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.x - p2.x) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 1.5);
			int row2 = (int) (p2.y / cubeSize + 1.5);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (row1 == row2) {
				if (row1 == 2)
					rotateFace(FACE_TOP, 90 * direction);
				else if (row1 == 1)
					rotateFace(FACE_EQUATOR, 90 * direction);
				else if (row1 == 0)
					rotateFace(FACE_BOTTOM, 90 * direction);
			}
		} else {
			// check to rotate left , middle or right
			int col1 = (int) (p1.x / cubeSize + 1.5);
			int col2 = (int) (p2.x / cubeSize + 1.5);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.z < 0)
				direction *= -1;
			if (col1 == col2) {
				if (col1 == 2)
					rotateFace(FACE_RIGHT, 90 * direction);
				else if (col1 == 1)
					rotateFace(FACE_MIDDLE, 90 * direction);
				else if (col1 == 0)
					rotateFace(FACE_LEFT, 90 * direction);
			}
		}
	}
	
	private void handleLeftRightSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.y - p2.y))) {
			// check to rotate top, equator or bottom
			int row1 = (int) (p1.y / cubeSize + 1.5);
			int row2 = (int) (p2.y / cubeSize + 1.5);
			int direction = (p2.z >= p1.z) ? 1 : -1;
			if (p1.x < 0)
				direction *= -1;
			if (row1 == row2) {
				if (row1 == 2)
					rotateFace(FACE_TOP, 90 * direction);
				else if (row1 == 1)
					rotateFace(FACE_EQUATOR, 90 * direction);
				else if (row1 == 0)
					rotateFace(FACE_BOTTOM, 90 * direction);
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 1.5);
			int col2 = (int) (p2.z / cubeSize + 1.5);
			int direction = (p2.y >= p1.y) ? 1 : -1;
			if (p1.x > 0)
				direction *= -1;
			if (col1 == col2) {
				if (col1 == 2)
					rotateFace(FACE_FRONT, 90 * direction);
				else if (col1 == 1)
					rotateFace(FACE_SIDE, 90 * direction);
				else if (col1 == 0)
					rotateFace(FACE_BACK, 90 * direction);
			}
		}
	}
	
	private void handleBottomTopSwipe(Vect3D p1, Vect3D p2) {
		if (Math.abs(p1.z - p2.z) > (Math.abs(p1.x - p2.x))) {
			// check to rotate left, middle or right
			int col1 = (int) (p1.x / cubeSize + 1.5);
			int col2 = (int) (p2.x / cubeSize + 1.5);
			int direction = (p1.z >= p2.z) ? 1 : -1;
			if (p1.y < 0)
				direction *= -1;
			if (col1 == col2) {
				if (col1 == 2)
					rotateFace(FACE_RIGHT, 90 * direction);
				else if (col1 == 1)
					rotateFace(FACE_MIDDLE, 90 * direction);
				else if (col1 == 0)
					rotateFace(FACE_LEFT, 90 * direction);
			}
		} else {
			// check to rotate front,side or back
			int col1 = (int) (p1.z / cubeSize + 1.5);
			int col2 = (int) (p2.z / cubeSize + 1.5);
			int direction = (p2.x <= p1.x) ? 1 : -1;
			if (p1.y > 0)
				direction *= -1;
			if (col1 == col2) {
				if (col1 == 2)
					rotateFace(FACE_FRONT, 90 * direction);
				else if (col1 == 1)
					rotateFace(FACE_SIDE, 90 * direction);
				else if (col1 == 0)
					rotateFace(FACE_BACK, 90 * direction);
			}
		}
	}
	public synchronized void rotate(float dx, float dy) {
		float[] temporaryMatrix = new float[16];
		Matrix.setIdentityM(currentRotation, 0);
		Matrix.rotateM(currentRotation, 0, dx, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(currentRotation, 0, dy, 1.0f, 0.0f, 0.0f);

		// Multiply the current rotation by the accumulated rotation, and then
		// set the accumulated
		// rotation to the result.
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0,
				accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0,
				accumulatedRotation.length);
		matrixBuffer.put(accumulatedRotation);
		matrixBuffer.position(0);
	}



	public void update() {
		long now = System.currentTimeMillis();
		if (now - lastUpdate < period) {
			return;
		}
		lastUpdate = now;
		handleFaceRotateRequests();
	}
	
	private int currentFace = 0;
	private float remainingAngle=0;
	private float ANIMATE_ANGLE = 22.5f;
	
	void handleFaceRotateRequests() {
		if( remainingAngle == 0 && !rotateRequests.isEmpty()) {
			RotateRequest r = rotateRequests.remove();
			
			remainingAngle = r.angle;
			currentFace = r.face;
		}
		
		if( remainingAngle != 0 ) {
			//	keep rotating current layer
			List<Cube> list = getCubes(currentFace);
			
			float angle;
			
			if( remainingAngle > 0 ) {
				angle = (remainingAngle> ANIMATE_ANGLE)?ANIMATE_ANGLE:remainingAngle;
			
				remainingAngle -= ANIMATE_ANGLE;
				if( remainingAngle < 0 )
					remainingAngle = 0;
			}
			else {
				angle = (remainingAngle < -ANIMATE_ANGLE)?-ANIMATE_ANGLE:remainingAngle;
				
				remainingAngle += ANIMATE_ANGLE;
				if( remainingAngle > 0 )
					remainingAngle = 0;
			}


			for (Cube cube : list) {
				if (currentFace == FACE_FRONT || currentFace == FACE_SIDE || currentFace == FACE_BACK)
					cube.rotate(angle, 0f, 0f, -1f);
				if (currentFace == FACE_LEFT || currentFace == FACE_MIDDLE || currentFace == FACE_RIGHT)
					cube.rotate(angle, -1f, 0f, 0f);
				if (currentFace == FACE_TOP || currentFace == FACE_EQUATOR || currentFace == FACE_BOTTOM)
					cube.rotate(angle, 0f, -1f, 0f);
			}			
			return;
		}
	}	

	/**
	 * Randomly rotate face for the specified number of times. This simply add the rotate request 
	 * to the queue
	 * @param count
	 */
	public void shuffle(int count) {
		Random r = new Random();
		for(int i=0;i<count;i++) {
			rotateFace(r.nextInt(9), 90);
		}
	}

	public Vect3D intersect(int width, int height, float x, float y,
			float[] projectionM) {

		int cloestIndex = -1;

		float[] tempM = new float[16];
		float[] modelM = new float[16];
		Matrix.setIdentityM(tempM, 0);
		Matrix.translateM(tempM, 0, position.x, position.y, position.z);
		Matrix.multiplyMM(modelM, 0, tempM, 0, accumulatedRotation, 0);
		Ray ray = new Ray(width, height, x, y, projectionM, modelM);

		Vect3D hit = null;
		Vect3D hitP = null;

		for (int i = 0; i < 27; i++) {
			Vect3D ret = intersectCube(ray, cubes[i].getTriangles());
			if (ret != null) {
				if (hit == null || ret.x < hit.x) {
					cloestIndex = i;
					hit = ret;
					hitP = ray.getIntersectCoord(ret);
				}
			}
		}

		if (hit != null) {
			System.out.println("closes hit: " + hitP);
			System.out.println("index: " + cloestIndex);

			return hitP;
		} else {
			System.out.println("missed");
		}
		return null;
	}

	/**
	 * Find the intersect point for a list of triangles
	 * 
	 * @param ray
	 * @param triangles
	 * @return
	 */

	Vect3D intersectCube(Ray ray, Triangle[] triangles) {
		Vect3D hit = null;

		for (int i = 0; i < triangles.length; i++) {
			Vect3D ret = ray.intersectTriangle(triangles[i].v1,
					triangles[i].v2, triangles[i].v3);

			if (ret != null) {
				// System.out.println("hit: " + ray.getIntersectCoord(ret) +
				// " t = " + ret.x);
				if (hit == null || ret.x < hit.x)
					hit = ret;
			}
		}
		return hit;
	}

	
	class RotateRequest {
		int angle;
		int face;
		public RotateRequest(int face,int angle) {
			this.face = face;
			this.angle = angle;
		}
	};
}
