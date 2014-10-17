package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Animator;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.buttons.StartButton;
import com.jbs.swipe.tiles.Direction;

public abstract class MainMenuState implements ApplicationState {
	
	public final float
		START_BUTTON_SCALE = 1f;
	
	public final int
		START_BUTTON_TOP_MARGIN = 30,
		TITLE_TOP_MARGIN = 15;
	
	public final String
		BACKGROUND_SOURCE = "assets/GUI/MainMenu/bg.png",
		TITLE_SOURCE = "assets/GUI/MainMenu/Logo.png";
	
	protected final Game game;
	protected StartButton startButton;
	protected Graphic background, title;
	
	private Vector2
		menuCenter, // The center of the MainMenu.
		menuSize; // The width and height of the MainMenu.
	/* True when the MainMenu has been initialized. */
	private boolean initialized = false;
	
	public MainMenuState(Game game, int width, int height) {
		this.game = game;
		this.menuCenter = new Vector2(width/2, height/2);
		this.menuSize = new Vector2(width, height);
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering MainMenuState.");
		
		if (!initialized())
			initialize();
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting MainMenuState.");
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background.renderTo(batch);
		startButton.renderTo(batch);
		title.renderTo(batch);
	}
	
	@Override
	public void updateApplication(Application app) {
		startButton.updateWith(app.input);
	}
	
	/* @return true when the MainMenu has been initialized. */
	protected boolean initialized() {
		return initialized;
	}
	
	protected abstract void exitMainMenu();
	
	/* Initialize the MainMenu's components. */
	protected void initialize() {
		game.playBackgroundMusic(true); // True because the background Music should play looped.
		initializeTitle();
		initializeStartButton();
		initializeBackground();
	}
	
	/* @return the Texture of the MainMenu's background. */
	public final Texture backgroundTexture() {
		return game.getTexture(BACKGROUND_SOURCE);
	}
	
	/* @return the Texture of the title logo. */
	public final Texture titleTexture() {
		return game.getTexture(TITLE_SOURCE);
	}
	
	/* @return the top center of the MainMenu. */
	protected final Vector2 menuTopCenter() {
		return new Vector2(menuCenter.x, menuSize.y);
	}
	
	/* Create and position the MainMenu's title. */
	private void initializeTitle() {
		// Create the title graphic at the Menu's top-center position with the title's texture.
		title = new Graphic(menuTopCenter(), titleTexture());
		// Translate the title down by half of it's height to make it completely on screen.
		title.translate(0, -title.height()/2);
		// Translate the title down by the desired top margin.
		title.translate(0, -TITLE_TOP_MARGIN);
	}
	
	/* Create and position the MainMenu's StartButton. */
	private void initializeStartButton() {
		// Create the StartButton at the position of the Title with the specified scale.
		startButton = new StartButton(game, new Vector2(title.x(), title.y()), START_BUTTON_SCALE) {
			@Override
			public void onTrigger() {
				exitMainMenu();
			}
			@Override
			public void onPress() {
				new Animator(game)
					.slideGraphicOffscreen(500, Direction.LEFT, title);
			}
		};
		// Translate the StartButton down by half the button's height to align it's top edge with the title's center.
		startButton.translate(0, -startButton.height()/2);
		// Translate the StartButton down by half the title's height to align it's top edge with the title's bottom edge.
		startButton.translate(0, -title.height()/2);
		// Translate the StartButton down by the desired top margin.
		startButton.translate(0, -START_BUTTON_TOP_MARGIN);
	}
	
	/* Create and position the MainMenu's background. */
	private void initializeBackground() {
		background = new Graphic(menuCenter, menuSize, backgroundTexture());
	}
}