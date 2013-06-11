package com.yinong.stack.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	static TextureAtlas atlas;
	
	public static TextureRegion brick;

	
	public static void Load()
	{

//		LoadFont();
		LoadTextures();
		
	}

	private static void LoadTextures() {
		// TODO Auto-generated method stub
		atlas = new TextureAtlas(Gdx.files.internal("texture/texture.txt"));
		brick=atlas.findRegion("brick");
	}
	

	public static void Dispose()
	{
		atlas.dispose();
	}
}