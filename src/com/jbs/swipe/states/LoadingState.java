package com.jbs.swipe.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
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
		if (!resources.isDirectory())
			throw new RuntimeException("Error in LoadingState constructor : \"" + resources.path() + "\" exists, but is not a directory");
		
		// Set our LoadingState's AssetManager and exit-state to the constructor data.
		this.assetManager = assetManager;
		
		// Load all the textures from our resource directory.
		this.loadAssetsFrom(resources);
		
		this.loadingScreen = new LoadingScreen(game.screenWidth(), game.screenHeight()) {
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
	 * If the resource is a texture, add it to the LoadingState's AssetManager's
	 * loading queue. If it is a directory, add all textures within all of the
	 * child directories to the LoadingState's AssetManager's loading queue.
	 * 
	 * Supported Texture file extensions are : .png, .jpeg, and .jpg
	 * Supported Music file extensions are : .mp3
	 * Supported Sound file extendions are : .wav
	 * If the file has another extension, it is not added to the AssetManager's
	 * loading queue and instead prints a warning in the console.
	 */
	public void loadAssetsFrom(FileHandle resource) {
		// If the file handle points to a directory.
		if (resource.isDirectory())
			// For each child file or directory in our parent directory.
			for (FileHandle child : resource.list())
				// Recurse this method with the child resource.
				loadAssetsFrom(child);
		// The resource is a file.
		else
			// If the file has a Texture file extension,
			if (resource.extension().equals("png") || resource.extension().equals("jpg") || resource.extension().equals("jpeg"))
				assetManager.load(resource.path(), Texture.class);
			// If the file has a Music file extension,
			else if (resource.extension().equals("mp3"))
				assetManager.load(resource.path(), Music.class);
			// If the file has a Sound file extension,
			else if (resource.extension().equals("wav"))
				assetManager.load(resource.path(), Sound.class);
			else
				// The file does not have a valid file extension, alert the user via the console.
				System.out.println("LoadingState could not load texture \"" + resource.name() + "\", unknown file extension.");
	}
}