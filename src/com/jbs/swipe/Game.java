package com.jbs.swipe;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.io.AudioProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.Screen;
import com.jbs.swipe.gui.LoadingScreen;
import com.jbs.swipe.levels.Level;
import com.jbs.swipe.levels.arcade.ArcadeModeEasy;
import com.jbs.swipe.states.GameOverState;
import com.jbs.swipe.states.LevelState;
import com.jbs.swipe.states.LoadingState;
import com.jbs.swipe.states.MainMenuState;
import com.jbs.swipe.states.PausedState;

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
	
	private LoadingState loadingState;
	private PausedState pausedState;
	private Renderable loadingScreen;
	private MainMenuState mainMenuState;
	private LevelState levelState;
	private Level level;
	private GameOverState gameOverState;
	
	private boolean created = false; // True when the Game's resources have been initialized.
	
	public Game(Screen screen) {
		super(screen);
		if (IS_STRICT)
			System.out.println("Warning! Game IS_STRICT");
	}
	
	public Game(int actualWidth, int actualHeight, int virtualWidth, int virtualHeight) {
		this(new Screen(actualWidth, actualHeight, virtualWidth, virtualHeight));
	}
	
	/* Reset the Game to it's MainMenuState. Throws RuntimeException if the Game
	 * is already in it's MainMenuState. */
	public void returnToMainMenu() {
		if (this.IS_STRICT && this.isAtMainMenu())
			throw new RuntimeException("Cannot returnToMainMenu if the Game is already in it's MainMenuState.");
		
		this.setState(mainMenuState());
	}
	
	/* Play the Game's background music. */
	public void playBackgroundMusic(boolean looped) {
		audio().playMusic(BACKGROUND_MUSIC_SOURCE, looped);
	}
	
	/* Stop the Game's background music. */
	public void stopBackgroundMusic() {
		audio().stopMusic(BACKGROUND_MUSIC_SOURCE);
	}
	
	/* @return the highest submitted score. */
	public int highScore() {
		return preferences.getInteger("high_score");
	}
	
	/* Retrieve the Texture from the Game's AssetManager. Throws RuntimeException if
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
	
	/* @return the Game's AudioProxy. This is the interface for using the Game's loaded Audio. */
	public AudioProxy audio() {
		return this.audioProxy;
	}
	
	/* @return the Game's psuedo-Random number generator. */
	public Random random() {
		return this.random;
	}
	
	/* Submit the potential high-score for review. If it is higher than the Game's current high-score
	 * it will be set as the new high-score. */
	public final void submitScore(int potentialHighScore) {
		if (potentialHighScore > this.highScore())
			setHighScore(potentialHighScore);
	}
	
	/* Set the Game's state to it's paused state and stop the background music.
	 * Throws a RuntimeException if the Game is already in it's PausedState. */
	@Override
	public final void pause() {
		if (this.IS_STRICT && this.applicationState() == pausedState())
			throw new RuntimeException("Cannot pause() : the Game is already in it's PausedState.");
		
		if (this.applicationState() == levelState() && // If the Game is in it's LevelState,
				this.isCreated()) // And the Game is Created,
			// Set the Game's state to it's paused state.
			setState(pausedState());
	}
	
	/* Start the Level and play the Game's background music.
	 * Throws a RuntimeException if the Game is not paused. */
	@Override
	public final void resume() {
		if (this.IS_STRICT && !this.isPaused())
			throw new RuntimeException("Cannot resume : Game not in it's PausedState.");
		
		// Resume the background music.
		this.playBackgroundMusic(true); // True because the background Music should be looped.
		// Start the Level.
		this.startLevel();
	}
	
	@Override
	public final void dispose() {
		// Save the Audio's muted-ness.
		preferences.putBoolean("is_muted", audio().isMuted());
		
		// Flush the preferences to save any new saved data.
		preferences.flush();
	}
	
	/* Set the Game's ApplicationState to the LevelState and reset the Level's Tile.
	 * Throws RuntimeException if the Game is already in it's LevelState. */
	public final void startLevel() {
		if (this.IS_STRICT && this.applicationState() == levelState())
			throw new RuntimeException("Cannot startLevel : Game already in it's LevelState.");
		
		// If the LevelState has already been initialized,
		if (levelState().initialized())
			// Reset the LevelState.
			levelState().reset();
		
		// Set the Game's state to the LevelState, effectively resuming the Level.
		this.setState(levelState());
	}
	
	/* Resets the Game's LevelState. Does not require the Game to be in its
	 * LevelState to reset it. */
	public final void resetLevel() {
		levelState().reset();
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
	}
	
	/* @return true if the Game's .create() method has been called. */
	public final boolean isCreated() {
		return created;
	}
	
	/* @return true if the Game is not updating the Level. */
	public final boolean isPaused() {
		return (this.applicationState() != levelState());
	}
	
	/* @return true if the Game is in it's MainMenuState. */
	public final boolean isAtMainMenu() {
		return (this.applicationState() == mainMenuState());
	}
	
	/* @return the Game's Screen's virtual width. */
	public final int screenWidth() {
		return screen().virtualWidth();
	}
	
	/* @return the Game's Screen's virtual height. */
	public final int screenHeight() {
		return screen().virtualHeight();
	}
	
	/* @return the center of the Game's virtual screen. */
	public final Vector2 screenCenter() {
		return new Vector2(screenWidth()/2, screenHeight()/2);
	}
	
	/* @return the size of the Game's virtual screen. */
	public final Vector2 screenSize() {
		return new Vector2(screenWidth(), screenHeight());
	}
	
	/* Set the new high-score to newHighScore. */
	protected void setHighScore(int newHighScore) {
		// Save the new high-score to our preferences.
		preferences.putInteger("high_score", newHighScore);
	}
	
	/* Initialize the Game's FileHandles. */
	protected void initializeFileHandles() {
		PATH_TO_ASSETS = Gdx.files.internal("assets");
		BACKGROUND_MUSIC_SOURCE = Gdx.files.internal("assets/SFX/BackgroundMusic.mp3");
	}
	
	/* Initialize the Game's AssetManager. */
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
	}
	
	/* Initialize the Game's ApplicationStates. */
	protected void initializeStates() {
		// Set our LoadingState's screen to our LoadingScreen.
		loadingState().setLoadingScreen(loadingScreen());
		// Set our LoadingState to exit to our MainMenuState when loading is complete.
		loadingState().setExitState(mainMenuState());
		
		// Set our Level to use the same background as the MainMenu.
		level().setBackground(new Renderable() {
			@Override
			public void renderTo(SpriteBatch batch) {
				batch.draw(mainMenuState().backgroundTexture(), 0, 0, screen().virtualWidth(), screen().virtualHeight());
			}
		});
		// Set our LevelState's game-over state.
		levelState().setGameOverState(this.gameOverState());
		// Set our LevelState's Level.
		levelState().setLevel(level());
	}
	
	/* Create the Game's GameOverState if it has not yet been created and return it. */
	protected GameOverState gameOverState() {
		// If our Game's GameOverState has not yet been created,
		if (this.gameOverState == null)
			// Create our Game's GameOverState with our Game.
			gameOverState = new GameOverState(this, levelState()) {
			@Override
			public int currentScore() {
				return level().score();
			}
			@Override
			public int highestScore() {
				return highScore();
			}
		};
		
		return gameOverState;
	}
	
	/* Create the Game's PausedState if it has not yet been created and return it. */
	protected PausedState pausedState() {
		// If our Game's PausedState has not yet been created,
		if (this.pausedState == null)
			// Create it with the Game and LevelState.
			pausedState = new PausedState(this, levelState());
		
		return pausedState;
	}
	
	/* Create the Game's MainMenuState if it has not yet been created and return it. */
	protected MainMenuState mainMenuState() {
		// If our Game's MainMenuState has not yet been created,
		if (this.mainMenuState == null)
			// Create the state with our Game's AssetManager and Screen.
			mainMenuState = new MainMenuState(this, screen().virtualWidth(), screen().virtualHeight());
		
		return mainMenuState;
	}
	
	/* Create the Game's LoadingState if it has not yet been created and return it. */
	protected LoadingState loadingState() {
		// If our Game's LoadingState has not yet been created,
		if (this.loadingState == null)
			// Create our game's loading state with our AssetManager and known path to our assets.
			loadingState = new LoadingState(assetManager, PATH_TO_ASSETS);
		
		return loadingState;
	}
	
	/* Create the Game's LevelState if it has not yet been created and return it. */
	protected LevelState levelState() {
		// If our Game's LevelState has not yet been created,
		if (this.levelState == null)
			levelState = new LevelState(this);
		
		return levelState;
	}
	
	protected Level level() {
		if (this.level == null)
			level = new ArcadeModeEasy(this);
		
		return level;
	}
	
	/* Create the Game's loadingScreen if it has not yet been created and return it. */
	protected Renderable loadingScreen() {
		// If the Game's loading screen has not yet been created,
		if (loadingScreen == null)
			// Create our Game's LoadingScreen with the Game and screen.
			loadingScreen = new LoadingScreen(screen().virtualWidth(), screen().virtualHeight()) {
				// Set the LoadingScreen's percent completeness to be the percent completion of our LoadingState.
				@Override
				public float percentComplete() {
					return loadingState.percentComplete();
				}
			};
			
		return loadingScreen;
	}
}