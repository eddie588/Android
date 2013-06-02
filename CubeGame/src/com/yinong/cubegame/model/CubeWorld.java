package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import android.media.AudioManager;
import android.opengl.Matrix;

import com.yinong.cubegame.util.Ray;
import com.yinong.cubegame.util.Vect3D;

public class CubeWorld {
	private static final long PERIOD = 100;
	private AudioManager  mAudioManager;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private int mStream1 = 0;
	
	private CubeGame game = null;
	private Queue<TurnRequest> rotateRequests = new ConcurrentLinkedQueue<TurnRequest>();

	float[] accumulatedRotation = new float[16];
	float[] currentRotation = new float[16];
	private FloatBuffer matrixBuffer;
	
	public CubeWorld() {
//		mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
//		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		mSoundPoolMap = new HashMap();
//		//load fx
//		mSoundPoolMap.put(SOUND_FX_01, mSoundPool.load(this, R.raw.set_trap, 1));		
		game = new Cube3By3(this,0f,0f,-11f);
		
		Matrix.setIdentityM(accumulatedRotation, 0);
		Matrix.setIdentityM(currentRotation, 0);

		Matrix.rotateM(accumulatedRotation,0,45f,1f,1f,1f);

		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(accumulatedRotation.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		matrixBuffer = byteBuf.asFloatBuffer();
		matrixBuffer.put(accumulatedRotation);
		matrixBuffer.position(0);		
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

	public Vect3D intersect(int width, int height, float x, float y,
			float[] projectionM) {

		int cloestIndex = -1;

		float[] tempM = new float[16];
		float[] modelM = new float[16];
		Matrix.setIdentityM(tempM, 0);
		Matrix.translateM(tempM, 0, game.getPosition().x, game.getPosition().y, game.getPosition().z);
		Matrix.multiplyMM(modelM, 0, tempM, 0, accumulatedRotation, 0);
		Ray ray = new Ray(width, height, x, y, projectionM, modelM);

		Vect3D hit = null;
		Vect3D hitP = null;

		int i=0;
		for (Cube cube:game.getAllCubes()) {
			Vect3D ret = intersectCube(ray, cube.getTriangles());
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

	public void turnFace(Vect3D p1, Vect3D p2) {
		game.turnFace(p1,p2);
	}
	
	public void requestTurnFace(int face,int angle) {
		rotateRequests.add(new TurnRequest(face,angle));		
	}

	public void shuffle(int count) {
		game.shuffle(count);
	}

	long lastUpdate = 0;
	public void update() {
		long now = System.currentTimeMillis();
		if (now - lastUpdate < PERIOD) {
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
			TurnRequest r = rotateRequests.remove();
			
			remainingAngle = r.angle;
			currentFace = r.face;
		}
		
		if( remainingAngle != 0 ) {
			//	keep rotating current layer
			List<Cube> list = game.getCubes(currentFace);
			
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
				if (currentFace == CubeGame.FACE_FRONT || currentFace == CubeGame.FACE_SIDE || currentFace == CubeGame.FACE_BACK)
					cube.rotate(angle, 0f, 0f, -1f);
				if (currentFace == CubeGame.FACE_LEFT || currentFace == CubeGame.FACE_MIDDLE || currentFace == CubeGame.FACE_RIGHT)
					cube.rotate(angle, -1f, 0f, 0f);
				if (currentFace == CubeGame.FACE_TOP || currentFace == CubeGame.FACE_EQUATOR || currentFace == CubeGame.FACE_BOTTOM)
					cube.rotate(angle, 0f, -1f, 0f);
			}			
			return;
		}
	}	
	


	public synchronized void draw(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();

		gl.glTranslatef(game.getPosition().x, game.getPosition().y, game.getPosition().z);
		gl.glMultMatrixf(matrixBuffer);
		for (Cube cube:game.getAllCubes()) {
			cube.draw(gl);
		}
	}

	
	class TurnRequest {
		int angle;
		int face;
		public TurnRequest(int face,int angle) {
			this.face = face;
			this.angle = angle;
		}
	}	
}
