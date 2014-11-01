package com.jbs.swipe;

import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.io.AudioProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.gui.GraphicAccessor;
import com.jbs.swipe.gui.LoadingScreen;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeModeEasy;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.states.GameModeSelectionState;
import com.jbs.swipe.states.GameOverState;
import com.jbs.swipe.states.LoadingState;
import com.jbs.swipe.states.MainMenuState;
import com.jbs.swipe.states.PausedState;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileAccessor;

public class Game extends Application {
	
	public final boolean IS_STRICT = true;
	public final String PREFERENCES_NAME = "Prefs";
	
	private FileHandle
		PATH_TO_ASSETS,
		BACKGROUND_MUSIC_SOURCE;
	
	protected AssetManager assetManager;
	protected Preferences preferences;
	protected AudioProxy audioProxy;
	protected Random random;
	
	private TweenManager tweenManager;
	
	private Renderable background;
	
	private LoadingState loadingState;
	private MainMenuState mainMenuState;
	private GameModeSelectionState gameModeSelectionState;
	private LevelState levelState;
	
	private long lastRenderTime;
	private boolean
		created = false; // True when the Game's resources have been initialized.
	
	public Game(int virtualWidth, int virtualHeight) {
		super(virtualWidth, virtualHeight);
		if (IS_STRICT)
			System.out.println("Warning! Game IS_STRICT");
	}
	
	/** Reset the Game to it's MainMenuState. Throws RuntimeException if the Game
	 * is already in it's MainMenuState. */
	public void returnToMainMenu() {
		if (this.IS_STRICT && this.isAtMainMenu())
			throw new RuntimeException("Cannot returnToMainMenu if the Game is already in it's MainMenuState.");
		
		this.setState(mainMenuState());
	}
	
	/** Play the Game's background music. */
	public void playBackgroundMusic(boolean looped) {
		audio().playMusic(BACKGROUND_MUSIC_SOURCE, looped);
	}
	
	/** Stop the Game's background music. */
	public void stopBackgroundMusic() {
		audio().stopMusic(BACKGROUND_MUSIC_SOURCE);
	}
	
	/** Retrieve the Texture from the Game's AssetManager. Throws RuntimeException if
	 * the Game has not yet been created with it's create() method. */
	public Texture getTexture(FileHandle textureSource) {
		if (this.IS_STRICT)
			// Assert that the Game has been created with it's create() method.
			if (!this.isCreated())
				throw new RuntimeException("Error in Game.getTexture() : Cannot get texture before Game is created.");
			// Assert that the textureSource exists.
			else if (!textureSource.exists())
				throw new RuntimeException("Error in Game.getTexture() : texture not found at path " + textureSource.path());
			else if (!textureSource.extension().equals("png") && !textureSource.extension().equals("jpg"))
				throw new RuntimeException("Error in Game.getTexture() : texture does not have a .png or .jpg file extension.");
		
		// Retrieve the texture from the Game's assets.
		return (Texture) assetManager.get(textureSource.path());
	}
	
	public final Texture getTexture(String fileLocation) {
		return getTexture(Gdx.files.internal(fileLocation));
	}
	
	/** @return the Game's Preferences. */
	public final Preferences preferences() {
		return preferences;
	}
	
	/** @return the Game's AudioProxy. This is the interface for using the Game's loaded Audio. */
	public final AudioProxy audio() {
		return this.audioProxy;
	}
	
	/** @return the Game's psuedo-Random number generator. */
	public final Random random() {
		return this.random;
	}
	
	/** Set the Game's state to it's paused state and stop the background music.
	 * Throws a RuntimeException if the Game is already in it's PausedState. */
	@Override
	public final void pause() {
		if (this.applicationState() == levelState() && // If the Game is in it's LevelState,
				this.isCreated()) // And the Game is Created,
			// Pause the Level.
			levelState().pause();
	}
	
	/** Start the Level and play the Game's background music.
	 * Throws a RuntimeException if the Game is not paused. */
	@Override
	public final void resume() {
		// Resume the background music.
		playBackgroundMusic(true); // True because the background Music should be looped.
		// Resume the Level.
		setState(levelState());
	}
	
	@Override
	public final void dispose() {
		// Save the Audio's muted-ness.
		preferences.putBoolean("is_muted", audio().isMuted());
		
		// Flush the preferences to save any new saved data.
		preferences.flush();
	}
	
	@Override
	public void render() {
		super.render();
		
		final long currentTime = System.currentTimeMillis();
		this.tweenManager().update(currentTime - lastRenderTime);
		this.lastRenderTime = currentTime;
	}
	
	@Override
	public final void create() {
		super.create();
		
		initializeFileHandles();
		initializeAssets();
		initializeStates();
		
		// Set our Game's state to the loading state.
		setState(loadingState);
		
		// Mark the Game as created.
		created = true;
		lastRenderTime = System.currentTimeMillis();
	}
	
	public final void setLevelState(LevelState newLevelState) {
		this.levelState = newLevelState;
	}
	
	/** @return the Object that handles all the Game's Tweens. */
	public final TweenManager tweenManager() {
		return this.tweenManager;
	}
	
	/** @return true if the Game's .create() method has been called. */
	public final boolean isCreated() {
		return created;
	}
	
	/** @return true if the Game is in it's MainMenuState. */
	public final boolean isAtMainMenu() {
		return (this.applicationState() == mainMenuState());
	}
	
	/** @return the Game's Screen's virtual width. */
	public final int screenWidth() {
		return screen().virtualWidth();
	}
	
	/** @return the Game's Screen's virtual height. */
	public final int screenHeight() {
		return screen().virtualHeight();
	}
	
	/** @return the center of the Game's virtual screen. */
	public final Vector2 screenCenter() {
		return new Vector2(screenWidth()/2, screenHeight()/2);
	}
	
	/** @return the size of the Game's virtual screen. */
	public final Vector2 screenSize() {
		return new Vector2(screenWidth(), screenHeight());
	}
	
	public final LevelState levelState() {
		return levelState;
	}
	
	public final Renderable background() {
		return background;
	}
	
	/** Set the new high-score to newHighScore. */
	protected void setHighScore(int newHighScore) {
		// Save the new high-score to our preferences.
		preferences.putInteger("high_score", newHighScore);
	}
	
	/** Initialize the Game's FileHandles. */
	protected void initializeFileHandles() {
		PATH_TO_ASSETS = Gdx.files.internal("assets");
		BACKGROUND_MUSIC_SOURCE = Gdx.files.internal("assets/SFX/BackgroundMusic.mp3");
	}
	
	/** Initialize the Game's AssetManager. */
	protected void initializeAssets() {
		// Create our psuedo-random number generator.
		random = new Random();
		
		// Create our Game's AssetManager.
		assetManager = new AssetManager();
		
		// Create the Game's AudioProxy.
		audioProxy = new AudioProxy(assetManager, this.IS_STRICT);
		
		// Create our Game's preferences.
		preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
		
		// Mute the Audio if it was muted the last time the Game exited.
		if (preferences.getBoolean("is_muted"))
			audio().mute();
		
		tweenManager = new TweenManager();
		Tween.registerAccessor(Graphic.class, new GraphicAccessor());
		Tween.registerAccessor(SwipeTile.class, new TileAccessor());
	}
	
	/** Initialize the Game's ApplicationStates. */
	protected void initializeStates() {
		// Set our LoadingState to exit to our MainMenuState when loading is complete.
		loadingState().setExitState(mainMenuState());
		
		background = new Renderable() {
			@Override
			public void renderTo(SpriteBatch batch) {
				batch.draw(mainMenuState().backgroundTexture(), 0, 0, screen().virtualWidth(), screen().virtualHeight());
			}
		};
	}
	
	protected GameModeSelectionState gameModeSelectionState() {
		if (this.gameModeSelectionState == null)
			gameModeSelectionState = new GameModeSelectionState(this);
		
		return gameModeSelectionState;
	}
	
	/** Create the Game's MainMenuState if it has not yet been created and return it. */
	protected MainMenuState mainMenuState() {
		// If our Game's MainMenuState has not yet been created,
		if (this.mainMenuState == null)
			// Create the state with our Game's AssetManager and Screen.
			mainMenuState = new MainMenuState(this, screen().virtualWidth(), screen().virtualHeight()) {
				@Override
				protected void exitMainMenu() {
					setState(gameModeSelectionState());
				}
		};
		
		return mainMenuState;
	}
	
	/** Create the Game's LoadingState if it has not yet been created and return it. */
	protected LoadingState loadingState() {
		// If our Game's LoadingState has not yet been created,
		if (this.loadingState == null)
			// Create our game's loading state with our AssetManager and known path to our assets.
			loadingState = new LoadingState(this, assetManager, PATH_TO_ASSETS);
		
		return loadingState;
	}
}