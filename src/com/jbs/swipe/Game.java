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
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.arcade.ArcadeModeEasy;
import com.jbs.swipe.shop.DefaultBillingAPI;
import com.jbs.swipe.shop.HugeCoinPurchase;
import com.jbs.swipe.shop.LargeCoinPurchase;
import com.jbs.swipe.shop.BillingAPI;
import com.jbs.swipe.shop.ShopState;
import com.jbs.swipe.shop.SmallCoinPurchase;
import com.jbs.swipe.shop.TinyCoinPurchase;
import com.jbs.swipe.shop.TrapPurchase;
import com.jbs.swipe.states.GameModeSelectionState;
import com.jbs.swipe.states.LoadingState;
import com.jbs.swipe.states.MainMenuState;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileAccessor;
import com.jbs.swipe.traps.Bomb;
import com.jbs.swipe.traps.DarkHole;

public class Game extends Application {
	
	public final boolean IS_STRICT = false;
	
	private FileHandle
		PATH_TO_ASSETS,
		BACKGROUND_MUSIC_SOURCE;
	
	protected AssetManager assetManager;
	protected AudioProxy audioProxy;
	protected Random random;
	
	/** The utility for easily creating and updating animations. */
	private TweenManager tweenManager;
	/** The interface for saving User data. */
	private User user;
	/** The interface for saving Game settings. */
	private Settings settings;
	/** The interface to charge the User money. */
	private BillingAPI billingAPI;
	
	private Renderable background;
	
	private LoadingState loadingState;
	private MainMenuState mainMenuState;
	private GameModeSelectionState gameModeSelectionState;
	private ShopState shopState;
	
	private long
		lastRenderTime;
	private boolean
		created = false; // True when the Game's resources have been initialized.
	
	public Game(BillingAPI billingAPI, int virtualWidth, int virtualHeight) {
		super(virtualWidth, virtualHeight);
		this.billingAPI = billingAPI;
		if (IS_STRICT)
			System.out.println("Warning! Game IS_STRICT");
	}
	
	public Game(int virtualWidth, int virtualHeight) {
		this(new DefaultBillingAPI(), virtualWidth, virtualHeight);
	}
	
	/** Reset the Game to it's MainMenuState. Throws RuntimeException if the Game
	 * is already in it's MainMenuState. */
	public void returnToMainMenu() {
		if (this.applicationState() == mainMenuState())
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
	
	/** @return the Texture if it has been loaded from the Game's assets folder. */
	public final Texture getTexture(String fileLocation) {
		return getTexture(Gdx.files.internal(fileLocation));
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
		// If we're in a Level, pause it.
		if (this.applicationState() instanceof LevelState && this.isCreated())
			((LevelState) applicationState()).pause();
	}
	
	/** Start the Level and play the Game's background music.
	 * Throws a RuntimeException if the Game is not paused. */
	@Override
	public final void resume() {
		// If the Game's assets are loaded,
		if (loadingState().percentComplete() == 1f)
			// Resume the background music.
			playBackgroundMusic(true); // True because the background Music should be looped.
	}
	
	@Override
	public final void dispose() {
		// Save the Audio's muted-ness.
		settings.setMuted(audio().isMuted());
		
		settings().save();
		user().save();
		
		billingAPI.destroy();
	}
	
	@Override
	public void render() {
		super.render();
		
		final long currentTime = System.currentTimeMillis();
		this.tweenManager().update(currentTime - lastRenderTime);
		this.lastRenderTime = currentTime;
	}
	
	@Override
	public void create() {
		super.create();
		
		if (created)
			throw new RuntimeException("Game already created!?");
		
		initializeFileHandles();
		initializeAssets();
		initializeStates();
		
		// Set our Game's state to the loading state.
		setState(loadingState);
		
		// Mark the Game as created.
		created = true;
		lastRenderTime = System.currentTimeMillis();
	}
	
	public void beginIODChange(SpriteBatch batch, float deltaIOD) { }
	public void endIODChange(SpriteBatch batch, float deltaIOD) { }
	
	public final void openShop() {
		if (this.applicationState() instanceof ShopState)
			throw new RuntimeException("Already in ShopState!");
		
		setState(shopState());
	}
	
	/** @return the Object that handles all the Game's Tweens. */
	public final TweenManager tweenManager() {
		return this.tweenManager;
	}
	
	/** @return the interface for saving User data. */
	public final User user() {
		return this.user;
	}
	
	/* @return the interface for Game settings. */
	public final Settings settings() {
		return this.settings;
	}
	
	/** @return true if the Game's .create() method has been called. */
	public final boolean isCreated() {
		return created;
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
	
	public final Renderable background() {
		return background;
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
		
		tweenManager = new TweenManager();
		// Register all the accessors with their respective classes.
		Tween.registerAccessor(Graphic.class, new GraphicAccessor());
		Tween.registerAccessor(SwipeTile.class, new TileAccessor());
		Tween.registerAccessor(Vector2.class, new Vector2Accessor());
		Tween.setWaypointsLimit(2);
		
		final int DEFAULT_USER_ID = 0;
		final Preferences prefs = Gdx.app.getPreferences("prefs");
		user = new User(DEFAULT_USER_ID, prefs);
		settings = new Settings(prefs);
		
		
		
		settings.optIntoTutorial();
		settings.preferences().putInteger(Settings.LAUNCHES, 0);
		
		
		
		
		// Mute the Audio if it was muted the last time the Game exited.
		if (settings.isMuted())
			audio().mute();
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
//				final LevelState level = new ArcadeModeEasy(Game.this);
				@Override
				protected void exitMainMenu() {
					settings.addLaunch();
					if (settings.numberOfLaunches() == 1) {
						user.setStock(new Bomb(Game.this), 10);
						user.setStock(new DarkHole(Game.this), 5);
					}
					
					setState(gameModeSelectionState());
					
//					setState(level);
//					if (level.initialized())
//						level.restart();
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
	
	/** Create the Game's ShopState if it has not yet been created and return it. */
	protected ShopState shopState() {
		// If our Game's ShopState has not yet been created,
		if (this.shopState == null) {
			// Create our game's shop state.
			shopState = new ShopState(this, billingAPI, mainMenuState());
			
			shopState.addPurchase(new HugeCoinPurchase(this, billingAPI));
			shopState.addPurchase(new LargeCoinPurchase(this, billingAPI));
			shopState.addPurchase(new SmallCoinPurchase(this, billingAPI));
			shopState.addPurchase(new TinyCoinPurchase(this, billingAPI));
			
			shopState.addPurchase(new TrapPurchase(new Bomb(this)));
			shopState.addPurchase(new TrapPurchase(new DarkHole(this)));
		}
		
		return shopState;
	}
}