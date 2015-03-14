package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.gui.buttons.ShopButton;
import com.jbs.swipe.gui.buttons.SpinningButton;
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
	protected SpinningButton startButton, shopButton;
	protected Graphic background, title;
	
	private Vector2
		menuCenter, // The center of the MainMenu.
		menuSize; // The width and height of the MainMenu.
	
	public MainMenuState(Game game, int width, int height) {
		this.game = game;
		this.menuCenter = new Vector2(width/2, height/2);
		this.menuSize = new Vector2(width, height);
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering MainMenuState.");
		
		initialize();
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting MainMenuState.");
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background.renderTo(batch);
		
		game.beginIODChange(batch, 3f);
			startButton.renderTo(batch);
			shopButton.renderTo(batch);
		game.endIODChange(batch, 3f);
		
		game.beginIODChange(batch, 5f);
			title.renderTo(batch);
		game.endIODChange(batch, 5f);
	}
	
	@Override
	public void updateApplication(Application app) {
		startButton.updateWith(app.input);
		shopButton.updateWith(app.input);
	}
	
	protected abstract void exitMainMenu();
	
	/** Initialize the MainMenu's components. */
	protected void initialize() {
		game.playBackgroundMusic(true); // True because the background Music should play looped.
		initializeTitle();
		initializeStartButton();
		initializeShopButton();
		initializeBackground();
	}
	
	/** @return the Texture of the MainMenu's background. */
	public final Texture backgroundTexture() {
		return game.getTexture(BACKGROUND_SOURCE);
	}
	
	/** @return the Texture of the title logo. */
	public final Texture titleTexture() {
		return game.getTexture(TITLE_SOURCE);
	}
	
	/** @return the top center of the MainMenu. */
	protected final Vector2 menuTopCenter() {
		return new Vector2(menuCenter.x, menuSize.y);
	}
	
	/** Create and position the MainMenu's title. */
	private void initializeTitle() {
		// Create the title graphic at its position with the title's texture.
		title = new Graphic(new Vector2(), titleTexture());
		title.setPosition(initialTitlePosition());
	}
	
	/** Create and position the MainMenu's StartButton. */
	private void initializeStartButton() {
		// Create the StartButton at its position with the specified scale.
		startButton = new StartButton(game, new Vector2(), START_BUTTON_SCALE) {
			@Override
			public void onTrigger() {
				exitMainMenu();
			}
			@Override
			public void onPress() {
				final float
					SLIDE_DURATION = 500;
				new Animator(game)
					.slideGraphicOffscreen(SLIDE_DURATION, Direction.LEFT, title);
			}
		};
		startButton.setPosition(initialStartButtonPosition().x, initialStartButtonPosition().y);
	}
	
	private void initializeShopButton() {
		shopButton = new ShopButton(game, new Vector2());
		// Scale the ShopButton down.
		shopButton.scale(7/10f);
		shopButton.setPosition(initialShopButtonPosition().x, initialStartButtonPosition().y);
	}
	
	/** Create and position the MainMenu's background. */
	private void initializeBackground() {
		background = new Graphic(menuCenter, menuSize, backgroundTexture());
	}
	
	private Vector2 initialTitlePosition() {
		return menuTopCenter().add(0, -title.height()/2).add(0, -TITLE_TOP_MARGIN);
	}
	
	private Vector2 initialStartButtonPosition() {
		return initialTitlePosition()
				// Translate the StartButton down by half the button's height to align it's top edge with the title's center.
				.add(0, -startButton.height()/2)
				// Translate the StartButton down by half the title's height to align it's top edge with the title's bottom edge.
				.add(0, -title.height()/2)
				// Translate the StartButton down by the desired top margin.
				.add(0, -START_BUTTON_TOP_MARGIN);
	}
	
	private Vector2 initialShopButtonPosition() {
		return initialStartButtonPosition()
				// Translate the ShopButton right to get out of the StartButton's way.
				.add(startButton.width()/2 + shopButton.width()/2, -startButton.height() / 4);
	}
}