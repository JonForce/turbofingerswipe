package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.Font;
import com.jbs.swipe.gui.ScreenOverlay;
import com.jbs.swipe.gui.buttons.HomeButton;
import com.jbs.swipe.gui.buttons.RestartButton;
import com.jbs.swipe.levels.LevelState;

public abstract class GameOverState implements ApplicationState {
	
	public static final String
		OVERLAY_SOURCE = "assets/Windows/Overlay.png",
		WINDOW_SOURCE = "assets/Windows/GameOver.png",
		FONT_SOURCE = "assets/GUI/Font/Digits.png";
	
	private static final int
		BUTTON_SPACING = 10,
		BUTTON_BOTTOM_MARGIN = 10,
		CURRENT_SCORE_VERTICAL_OFFSET = 100,
		HIGHEST_SCORE_VERTICAL_OFFSET = -150;
	
	protected Button restartButton, homeButton;
	protected Font font;
	protected LevelState level;
	protected Game game;
	
	private Graphic
		overlay, // The gray texture that renders over the Level.
		window; // The window texture that will contain the current and highest score.
	
	public GameOverState(Game game, LevelState level) {
		this.level = level;
		this.game = game;
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering GameOverState.");
		
		// Stop the background music.
		game.stopBackgroundMusic();
		
		// Initialize the gray overlay Graphic.
		overlay = new ScreenOverlay(game);
		// Initialize the window Graphic at the center of the Game's screen.
		window = new Graphic(game.screenCenter(), game.getTexture(WINDOW_SOURCE));
		// Initialize the Screen's Font.
		font = new Font(game.getTexture(FONT_SOURCE));
		font.setAlignment(Font.ALIGNMENT_CENTER);
		
		// If the buttons have not been initialized,
		if (!buttonsInitialized())
			initializeButtons();
	}
	
	@Override
	public void updateApplication(Application app) {
		restartButton.updateWith(app.input);
		homeButton.updateWith(app.input);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		level.renderTo(batch);
		
		game.beginIODChange(batch, 3);
			overlay.renderTo(batch);
			window.renderTo(batch);
			
			renderCurrentScoreTo(batch, currentScoreCenter());
			renderHighestScoreTo(batch, highestScoreCenter());
			
			homeButton.renderTo(batch);
			restartButton.renderTo(batch);
		game.endIODChange(batch, 3);
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting GameOverState.");
	}
	
	public abstract int currentScore();
	public abstract int highestScore();
	
	/* @return the position on the screen where the current score should be centered around. */
	protected Vector2 currentScoreCenter() {
		// Return the position of the center of the window translated vertically by the CURRENT_SCORE_VERTICAL_OFFSET.
		return new Vector2(window.x(), window.y() + CURRENT_SCORE_VERTICAL_OFFSET);
	}
	
	/* @return the position on the screen where the highest score should be centered around. */
	protected Vector2 highestScoreCenter() {
		// Return the position of the center of the window translated vertically by the HIGHEST_SCORE_VERTICAL_OFFSET.
		return new Vector2(window.x(), window.y() + HIGHEST_SCORE_VERTICAL_OFFSET);
	}
	
	/* Render the high-score to the SpriteBatch with the GameOverScreen's Font. */
	protected void renderHighestScoreTo(SpriteBatch batch, Vector2 renderingPosition) {
		font.renderIntegerTo(batch, highestScore(), (int)renderingPosition.x, (int)renderingPosition.y);
	}
	
	/* Render the high-score to the SpriteBatch with the GameOverScreen's Font. */
	protected void renderCurrentScoreTo(SpriteBatch batch, Vector2 renderingPosition) {
		font.renderIntegerTo(batch, currentScore(), (int)renderingPosition.x, (int)renderingPosition.y);
	}
	
	/* @return true if the GameOverScreen's Buttons have been initialized. */
	protected final boolean buttonsInitialized() {
		return (restartButton != null && homeButton != null);
	}
	
	/* Initialize the Buttons. */
	protected final void initializeButtons() {
		final Vector2 SCREEN_CENTER = game.screenCenter();
		
		// Initialize the Buttons at the center of the screen.
		homeButton = new HomeButton(game, SCREEN_CENTER);
		restartButton = new RestartButton(game, SCREEN_CENTER) {
			@Override
			public void resetLevel() {
				level.restart();
			}
			@Override
			public void resumeLevel() {
				game.setState(level);
			}
		};
		
		// Translate the Buttons' bottom edges to reside on the middle of the bottom of the screen.
		homeButton.setPosition((int)SCREEN_CENTER.x, homeButton.texture().getHeight() / 2);
		restartButton.setPosition((int)SCREEN_CENTER.x, restartButton.texture().getHeight() / 2);
		
		// Translate the Buttons vertically by the desired bottom margin.
		homeButton.translate(0, BUTTON_BOTTOM_MARGIN);
		restartButton.translate(0, BUTTON_BOTTOM_MARGIN);
		
		// Separate the Buttons.
		homeButton.translate(homeButton.texture().getWidth() / 2, 0);
		restartButton.translate(- restartButton.texture().getWidth() / 2, 0);
		
		// Translate the Buttons away from each other to achieve the desired spacing.
		homeButton.translate(BUTTON_SPACING / 2, 0);
		restartButton.translate(- BUTTON_SPACING / 2, 0);
	}
}