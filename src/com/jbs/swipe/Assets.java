package com.jbs.swipe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	static TextureAtlas atlas;
	public static AssetManager assets;
	
	public static void init() {
		assets = new AssetManager();
	}
	
	/**
	 * Add all assets from an resource file pointed by resource to the AssetManagers queue.
	 * Supported Texture file extensions are : .png, .jpeg, and .jpg
	 * Supported Music file extensions are : .mp3
	 * Supported Sound file extendions are : .wav
	 * If the file has another extension, it is not added to the AssetManager's
	 * loading queue and instead prints a warning in the console.
	 */
	public static void loadAssetsFrom(FileHandle resource) {
		if(assets==null) init();
		
		String[] resourceLines = resource.readString().split("\r\n");
		for(int i=0;i<resourceLines.length;i++) {
			String path = resourceLines[i];
			// the file doesn't exist, so we tell them about it and continue onto the next.
			if(!Gdx.files.internal(path).exists()) {
				System.err.println("Asset at path: "+path+" does not exist!");
				continue;
			}
			// If the file has a Texture file extension,
			if (path.endsWith("png") || path.endsWith("jpg") || path.endsWith("jpeg")) {
				TextureParameter texParams = new TextureParameter();
				texParams.minFilter = texParams.magFilter = TextureFilter.Linear; //lets make smooth textures the default yay!
				texParams.genMipMaps = false; //we aren't in need of mip maps so lets free up some memory.
				assets.load(path, Texture.class, texParams);
			}
			else if (path.endsWith("mp3"))
				assets.load(path, Music.class);
			else if (path.endsWith("wav"))
				assets.load(path, Sound.class);
			else if (path.endsWith("fnt")) {
				BitmapFontParameter fontParams = new BitmapFontParameter();
				//libgdx has a typo in minFilter lol
				fontParams.minFitler = fontParams.maxFilter = TextureFilter.Linear; //lets make smooth textures the default yay!
				assets.load(path, BitmapFont.class, fontParams);
			}
			else if(path.endsWith("txt")){
				//TextureAtlas pack = new TextureAtlas(path);
				assets.load(path, TextureAtlas.class);
			}
			//else if (path.endsWith("fx"))
			//	assets.load(path, ParticleEffect.class);
			else {
				// The file does not have a valid file extension, alert the user via the console.
				System.out.println("Assets could not load asset \"" + resource.name() + "\", unknown file extension.");
				continue;
			}
			System.out.println("Successfully loaded resource: "+path);
		}
	}
	
	public static boolean update(){
		if(assets.update()){
			atlas = assets.get("assets/output/texturepack_0.txt");
			for(AtlasRegion region : atlas.getRegions()) {
				System.out.println("Atlas region: "+region.name+" loaded successfully!");
			}
			return true;
		}
		return false;
	}
	
	public static <T> T get(String path, Class<T> type){
		return assets.get(path, type);
	}
	
	public static TextureRegion getAtlasRegion(String path){
		return atlas.findRegion(path);
	}

	public static float getProgress() {
		return assets.getProgress();
	}
}
