package com.yinong.stack.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
	static TextureAtlas atlas;
	
	public static Texture brick;

	
	public static void Load()
	{
		brick = new Texture(Gdx.files.internal("brick.jpg"));

//		LoadFont();
		//LoadTextures();
		
	}

//	private static void LoadTextures() {
//		// TODO Auto-generated method stub
//		dropletRegion=atlas.findRegion("droplet");
//	}
	

	public static void Dispose()
	{
		atlas.dispose();
	}
}