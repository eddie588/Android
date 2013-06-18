package com.yinong.loadrunner.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	static TextureAtlas atlas;
	
	public static TextureRegion brick;
	public static TextureRegion concrete;
	public static TextureRegion gold;
	public static TextureRegion ladder;
	public static TextureRegion rope;
	public static TextureRegion man_l;
	public static TextureRegion stand;
	
	public static TextureRegion[] run;

	
	public static void Load()
	{

//		LoadFont();
		LoadTextures();
		
	}

	private static void LoadTextures() {
		// TODO Auto-generated method stub
		atlas = new TextureAtlas(Gdx.files.internal("texture/texture.txt"));
		brick=atlas.findRegion("brick");
		concrete=atlas.findRegion("concrete");
		gold=atlas.findRegion("gold");
		ladder=atlas.findRegion("ladder");
		rope=atlas.findRegion("rope");
		man_l=atlas.findRegion("man_l");
		stand=atlas.findRegion("stand");
		
		run = new TextureRegion[9];
		
		for(int i=0;i<run.length;i++) {
			run[i] = atlas.findRegion("run" +(i+1));
		}
	}
	

	public static void Dispose()
	{
		atlas.dispose();
	}
}