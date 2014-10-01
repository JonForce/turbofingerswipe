package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.buttons.MuteButton;
import com.jbs.swipe.gui.buttons.PauseButton;
import com.jbs.swipe.levels.Level;

public class LevelState implements ApplicationState {
	
	private final Game game;
	private Button pauseButton, muteButton;
	private Level level;
	
	/* The state to set the application to when the failure condition is met. */
	private ApplicationState gameOverState;
	private boolean initialized = false; // True when the Level has been entered.
	
	public LevelState(Game game) {
		this.game = game;
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering LevelState.");
		
		// If the level has not been initialized,
		if (!initialized())
			initialize();
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting LevelState.");
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		// Render the Level
		level().renderTo(batch);
		
		// If the Game is not in it's PausedState or in it's MainMenuState,
		if (!game.isPaused() && !game.isAtMainMenu()) {
			// Render the Level's Buttons.
			pauseButton.renderTo(batch);
			muteButton.renderTo(batch);
		}
		
		// If the player has not lost the Level,
		if (!level().failureConditionMet())
			// The Level's score still needs to be rendered.
			level().renderScoreTo(batch);
	}
	
	@Override
	public void updateApplication(Application app) {
		// If the Game is not in it's PausedState or in it's MainMenuState,
		if (!game.isPaused() && !game.isAtMainMenu()) {
			// Update the Level's Buttons to check for press/release.
			pauseButton.updateWith(app.input);
			muteButton.updateWith(app.input);
		}
		
		// If the player has has not lost the Level,
		if (!level().failureConditionMet())
			// Update the Level
			level().updateWith(app.input);
		else
			// Switch the Application to the game-over State.
			app.setState(gameOverState);
	}
	
	/* Reset the LevelState and it's Level. */
	public void reset() {
		// Reset the Level
		level().reset();
	}
	
	/* Set the ApplicationState that will be used when the failure condition is met. */
	public void setGameOverState(ApplicationState newGameOverState) {
		this.gameOverState = newGameOverState;
	}
	
	/* Set the LevelState's Level. */
	public final void setLevel(Level newLevel) {
		this.level = newLevel;
	}
	
	/* @return the LevelState's Level. */
	public final Level level() {
		return this.level;
	}
	
	/* @return true if the LevelState has been initialized. */
	public final boolean initialized() {
		return this.initialized;
	}
	
	/* Initialize the LevelState's components. */
	protected final void initialize() {
		// Create the Level.
		level().create();
		
		pauseButton = new PauseButton(game);
		
		muteButton = new MuteButton(game, new Vector2(pauseButton.x(), pauseButton.y()));
		muteButton.translate(- muteButton.width(), 0);
		
		this.initialized = true;
	}
}