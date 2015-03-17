package com.jbs.swipe.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.LoadingScreen;

public class LoadingState implements ApplicationState {
	
	private AssetManager assetManager;
	
	/* The state to exit to when our loading is complete */
	private ApplicationState exitState;
	
	/* The object to defer rendering to while loading */
	private Renderable loadingScreen;
	
	public LoadingState(Game game, final AssetManager assetManager, FileHandle resources) {
		// Assert 'resources' is exists and is a directory.
		if (!resources.exists())
			throw new RuntimeException("Error in LoadingState constructor : Resource folder not found at \"" + resources.path() + "\"");
		//if (!resources.isDirectory())
		//	throw new RuntimeException("Error in LoadingState constructor : \"" + resources.path() + "\" exists, but is not a directory");
		
		// Set our LoadingState's AssetManager and exit-state to the constructor data.
		this.assetManager = assetManager;
		
		// Load all the textures from our resource directory.
		this.loadAssetsFrom(resources);
		
		this.loadingScreen = new LoadingScreen(game, game.screenWidth(), game.screenHeight()) {
			public float percentComplete() {
				return assetManager.getProgress();
			}
		};
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering LoadingState.");
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting LoadingState.");
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		loadingScreen.renderTo(batch);
	}
	
	@Override
	public void updateApplication(Application app) {
		// If the AssetManager is not finished loading, load some of them and print our progress to the console.
		if (!assetManager.update())
			System.out.println("Assets are " + (assetManager.getProgress()*100) + "% loaded.");
		else {
			System.out.println("Assets are 100% loaded.");
			// Switch the app's state to the state designated to be switched to when we are finished loading assets.
			app.setState(exitState);
		}
	}
	
	/**
	 * @return the percentage of loading completion in decimal form.
	 */
	public float percentComplete() {
		return assetManager.getProgress();
	}
	
	/**
	 * @return the LoadingState's assetManager.
	 */
	public AssetManager assetManager() {
		return assetManager;
	}
	
	/**
	 * Set the AssetManager to use.
	 */
	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	/**
	 * Set the state to enter when loading is complete.
	 */
	public void setExitState(ApplicationState newExitState) {
		this.exitState = newExitState;
	}
	
	/**
	 * Add all assets from an resource file pointed by resource to the AssetManagers queue.
	 * Supported Texture file extensions are : .png, .jpeg, and .jpg
	 * Supported Music file extensions are : .mp3
	 * Supported Sound file extendions are : .wav
	 * If the file has another extension, it is not added to the AssetManager's
	 * loading queue and instead prints a warning in the console.
	 */
	public void loadAssetsFrom(FileHandle resource) {
		
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
				assetManager.load(path, Texture.class, texParams);
			}
			else if (path.endsWith("mp3"))
				assetManager.load(path, Music.class);
			else if (path.endsWith("wav"))
				assetManager.load(path, Sound.class);
			else if (path.endsWith("fnt"))
				assetManager.load(path, BitmapFont.class);
			//else if (path.endsWith("fx"))
			//	assetManager.load(path, ParticleEffect.class);
			else {
				// The file does not have a valid file extension, alert the user via the console.
				System.out.println("LoadingState could not load asset \"" + resource.name() + "\", unknown file extension.");
				continue;
			}
			System.out.println("Successfully loaded resource: "+path);
		}
		
	}
}