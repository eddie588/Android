package com.yinong.cubegame.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.Matrix;

import com.yinong.cubegame.GameStats;
import com.yinong.cubegame.R;
import com.yinong.cubegame.util.Ray;
import com.yinong.cubegame.util.Vect3D;

public class CubeWorld {
	public static final int CUBE_2X2X2 = 0;
	public static final int CUBE_3X3X3 = 1;
	public static final int CUBE_2X2X4 = 2;
	public static final int CUBE_4X4X4 = 3;

	private static final long PERIOD = 60;
	private static final Integer CLICKSOUND = null;
	private AudioManager mAudioManager;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private SoundPool soundPool;
	private boolean enableSound = true;

	private CubeGame game = null;
	private Queue<TurnRequest> rotateRequests = new ConcurrentLinkedQueue<TurnRequest>();

	float[] accumulatedRotation = new float[16];
	float[] currentRotation = new float[16];
	float[] projectionM = null;
	private FloatBuffer matrixBuffer;
	private GameStats stats = new GameStats();

	long startTime = 0;
	int moves = 0;

	public CubeWorld(Context context) {
		game = new Cube3By3(this, 0f, 0f, -11f);

		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(accumulatedRotation.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		matrixBuffer = byteBuf.asFloatBuffer();

		restartGame(CUBE_3X3X3);
		setupSound(context);
	}

	public void selectCube(int cube) {
		restartGame(CUBE_3X3X3);
	}

	public void setupSound(Context context) {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mSoundPoolMap = new HashMap();
		// load fx
		mSoundPoolMap.put(CLICKSOUND,
				soundPool.load(context, R.raw.clicksound, 1));
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

	public IntersectCube intersect(int width, int height, float x, float y) {

		int closestIndex = -1;

		float[] tempM = new float[16];
		float[] modelM = new float[16];
		Matrix.setIdentityM(tempM, 0);
		Matrix.translateM(tempM, 0, game.getPosition().x, game.getPosition().y,
				game.getPosition().z);
		Matrix.multiplyMM(modelM, 0, tempM, 0, accumulatedRotation, 0);
		Ray ray = new Ray(width, height, x, y, projectionM, modelM);

		Vect3D hit = null;
		Vect3D hitP = null;
		Cube hitCube = null;

		int i = 0;
		for (Cube cube : game.getAllCubes()) {
			Vect3D ret = intersectCube(ray, cube.getTriangles());
			if (ret != null) {
				if (hit == null || ret.x < hit.x) {
					closestIndex = i;
					hit = ret;
					hitP = ray.getIntersectCoord(ret);
					hitCube = cube;
				}
			}
		}

		if (hit != null) {
			System.out.println("closes hit: " + hitP);
			System.out.println("index: " + closestIndex);

			return new IntersectCube(hitP,hitCube);
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

	public boolean checkTurn(float x1,float y1,float x2,float y2,int viewPortWidth,int viewPortHeight) {
		int CHECKNUM = 5;
		float dx = (x2-x1)/CHECKNUM;
		float dy = (y2-y1)/CHECKNUM;
		
		//	Get first and last hit
		IntersectCube[] hitP = new IntersectCube[CHECKNUM];
		int hitCount=0;
		for(int i=0;i<CHECKNUM;i++) {
			IntersectCube c = intersect(viewPortWidth, viewPortHeight, x1, y1);

			if( c != null ) {
				hitP[hitCount++] = c;
			}
			x1 += dx;
			y1 += dy;
		}
		if( hitCount < 2)
			return false;
		
		return turnFace(hitP,hitCount);
	}

	
	public boolean turnFace(IntersectCube[] hitC,int hitCount) {
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		
		float ax=0,ay=0,az=0;
		for(int i=0;i<hitCount-1;i++) {
			ax += Math.abs(hitC[i].hitP.x - hitC[i+1].hitP.x);
			ay += Math.abs(hitC[i].hitP.y - hitC[i+1].hitP.y);
			az += Math.abs(hitC[i].hitP.z - hitC[i+1].hitP.z);
		}
		ax = ax/hitCount;
		ay = ay/hitCount;
		az = az/hitCount;
		int direction;
		
		Vect3D P0 = hitC[0].hitP;
		Vect3D P1 = hitC[hitCount-1].hitP;
		
		Cube hitCube = hitC[0].hitCube;
		
		if( ax <CubeGame.EPSILON && ax < ay && ax < az ) {
			// Swipe on X face
			if(Math.abs(P0.y-P1.y) > Math.abs(P0.z-P1.z)) {
				direction = (hitCube.getHitFace(P0.x,0,0) == Cube.CUBE_LEFT )?-1:1;
				direction *= ((P0.y-P1.y)>0)? 1:-1;
				
				requestTurnFace(Cube.PLANE_Z, hitCube.getCenter().z, 90*direction);
				moves++;
				return true;
			}
			else {
				direction = (hitCube.getHitFace(P0.x,0,0) == Cube.CUBE_LEFT )?-1:1;
				direction *= ((P0.z-P1.z)<0) ? 1:-1;
				
				requestTurnFace(Cube.PLANE_Y, hitCube.getCenter().y, 90*direction);
				moves++;
				return true;
			}
		}
		if( ay <CubeGame.EPSILON && ay < ax && ay < az ) {
			// Swipe on Y face
			if(Math.abs(P0.x-P1.x) > Math.abs(P0.z-P1.z)) {
				direction = (hitCube.getHitFace(0,P0.y,0) == Cube.CUBE_TOP )?1:-1;
				direction *= ((P0.x-P1.x)>0)? -1:1;
				
				requestTurnFace(Cube.PLANE_Z, hitCube.getCenter().z, 90*direction);
				moves++;
				return true;
				
			}
			else {
				direction = (hitCube.getHitFace(0,P0.y,0) == Cube.CUBE_TOP )?1:-1;
				direction *= ((P0.z-P1.z)<0) ? -1:1;
				
				requestTurnFace(Cube.PLANE_X, hitCube.getCenter().x, 90*direction);
				moves++;
				return true;
			}
		}
		if( az <CubeGame.EPSILON && az < ay && az < ax ) {
			// Swipe on Z face
			if(Math.abs(P0.x-P1.x) > Math.abs(P0.y-P1.y)) {

				direction = (hitCube.getHitFace(0,0,P0.z) == Cube.CUBE_FRONT )?1:-1;
				direction *= ((P0.x-P1.x)>0)? 1:-1;
				
				requestTurnFace(Cube.PLANE_Y, hitCube.getCenter().y, 90*direction);
				moves++;
				return true;
				
			}
			else {

				direction = (hitCube.getHitFace(0,0,P0.z) == Cube.CUBE_FRONT )?1:-1;
				direction *= ((P0.y-P1.y)<0) ? 1:-1;
				
				requestTurnFace(Cube.PLANE_X, hitCube.getCenter().x, 90*direction);
				moves++;
				return true;
			}
		}		
		return false;
	}

	public long getTime() {
		if (startTime == 0)
			return 0;
		return System.currentTimeMillis() - startTime;
	}

	public String getTimeStr() {
		long t = getTime();
		if (getTime() == 0)
			return "00:00:00";
		int sec = (int)(t / 1000) % 60;
		int min = (int)((t / 1000) / 60) % 60;
		int hour = (int)(t / 1000) / 60 / 60;

		return String.format("%02d:%02d:%02d",hour,min,sec);
	}

	public void requestTurnFace(int plane, float centerP, float angle) {
		rotateRequests.add(new TurnRequest(plane, centerP, angle));
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

	void playClickingSound() {
		if (!enableSound)
			return;

		float streamVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		soundPool.play(mSoundPoolMap.get(CLICKSOUND), streamVolume,
				streamVolume, 1, 0, 1f);
	}

	private TurnRequest currentRequest = null;
	private float remainingAngle = 0;
	private float ANIMATE_ANGLE = 22.5f;

	void handleFaceRotateRequests() {
		if (remainingAngle == 0 && !rotateRequests.isEmpty()) {
			TurnRequest r = rotateRequests.remove();
			if( !game.isValidTurn(r.plane, r.centerP, r.angle)) {
				return;
			}
			remainingAngle = r.angle;
			currentRequest = r;
//			try {
//				Thread.sleep(100);
//			}
//			catch(Exception e)
//			{
//				
//			}

			playClickingSound();
		}

		if (remainingAngle != 0) {
			// keep rotating current layer
			List<Cube> list = game.getCubes(currentRequest.plane,
					currentRequest.centerP);
			
			list.addAll(game.getStickyCubes(currentRequest.plane, currentRequest.centerP));

			float angle;

			if (remainingAngle > 0) {
				angle = (remainingAngle > ANIMATE_ANGLE) ? ANIMATE_ANGLE
						: remainingAngle;

				remainingAngle -= ANIMATE_ANGLE;
				if (remainingAngle < 0)
					remainingAngle = 0;
			} else {
				angle = (remainingAngle < -ANIMATE_ANGLE) ? -ANIMATE_ANGLE
						: remainingAngle;

				remainingAngle += ANIMATE_ANGLE;
				if (remainingAngle > 0)
					remainingAngle = 0;
			}

			for (Cube cube : list) {
				switch (currentRequest.plane) {
				case Cube.PLANE_Z:
					cube.rotate(angle, 0f, 0f, -1f);
					break;
				case Cube.PLANE_X:
					cube.rotate(angle, -1f, 0f, 0f);
					break;
				case Cube.PLANE_Y:
					cube.rotate(angle, 0f, -1f, 0f);
					break;
				}
			}
			return;
		}
	}

	public synchronized void draw(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();

		gl.glTranslatef(game.getPosition().x, game.getPosition().y,
				game.getPosition().z);
		gl.glMultMatrixf(matrixBuffer);
		for (Cube cube : game.getAllCubes()) {
			cube.draw(gl);
		}

		// draw stats
		stats.draw(gl, getTimeStr(), String.valueOf(getMoves()));
	}

	class TurnRequest {
		float angle;
		int plane;
		float centerP;

		public TurnRequest(int plane, float centerP, float angle) {
			this.plane = plane;
			this.centerP = centerP;
			this.angle = angle;
		}
	}

	public void setProjectionM(float[] currentProjection) {
		projectionM = currentProjection;
	}

	public void restartGame(int cubeType) {
		switch (cubeType) {
		case CUBE_2X2X2:
			//game = new Cube2By2(this, 0f, 0f, -8f);
			game = new Cube233(this, 0f, 0f, -14f);
			break;
		case CUBE_3X3X3:
			game = new Cube3By3(this, 0f, 0f, -11f);
			break;
		case CUBE_4X4X4:
			game = new Cube4By4(this, 0f, 0f, -13f);
			break;
		case CUBE_2X2X4:
			game = new Cube224(this, 0f, 0f, -10f);
			break;
		}

		// reset matrix
		Matrix.setIdentityM(accumulatedRotation, 0);
		Matrix.setIdentityM(currentRotation, 0);

		Matrix.rotateM(accumulatedRotation, 0, 45f, 1f, 1f, 1f);

		matrixBuffer.put(accumulatedRotation);
		matrixBuffer.position(0);
		startTime = 0;
		moves = 0;
	}
	
	public void addMove() {
		moves++;
	}

	public int getMoves() {
		return moves;
	}
	
	public CubeGame getGame() {
		return game;
	}
	
	
	//	Class for holding the intersected cube info
	class IntersectCube {
		Vect3D hitP;
		Cube hitCube;
		public IntersectCube(Vect3D hitP,Cube hitCube) {
			this.hitP = hitP;
			this.hitCube = hitCube;
		}
	}
}
