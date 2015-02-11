package com.jbs.swipe.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.TouchManager;
import com.jbs.swipe.gui.Score;
import com.jbs.swipe.gui.buttons.MuteButton;
import com.jbs.swipe.gui.buttons.PauseButton;
import com.jbs.swipe.states.GameOverState;
import com.jbs.swipe.states.OverlayState;
import com.jbs.swipe.states.PausedState;
import com.jbs.swipe.tiles.SwipeTile;

public abstract class LevelState implements ApplicationState {
	
	private final Game game;
	
	private Button pauseButton, muteButton;
	
	/** The Level's score-keeping and score-rendering mechanism. */
	private Score score;
	/** The Level's TouchManager handles the detecting and calling of touch-related events. */
	private TouchManager touchManager;
	/** The Level's Tutorial. */
	private TutorialState tutorialState;
	/** The State to enter when the Level is failed. */
	private GameOverState gameOverState;
	/** The State to enter when the Level is paused. */
	private OverlayState pausedState;
	
	private boolean
		/** True when the Tutorial has shown. */
		tutorialHasShown = false,
		/** True if the Level has been initialized. */
		initialized = false;
	
	/**
	 * Create a LevelState, an Object that is designed as the foundation of all TurboFingerSwipe
	 * Levels. The LevelState handles activities that are common across all Levels.
	 * @param game
	 */
	public LevelState(Game game) {
		this.game = game;
	}
	
	@Override
	public final void enterState() {
		if (!initialized)
			initialize();
		
		// If the tutorial has not shown yet,
		// and the user has not opted out of seeing this Level's tutorials,
		if (!tutorialHasShown && !game.settings().hasOptedOutOfTutorial()) {
			game.setState(tutorialState);
			tutorialHasShown = true;
			// Return without initializing.
			return;
		}
	}
	
	@Override
	public final void updateApplication(Application app) {
		touchManager.update(app.input);
		pauseButton.updateWith(app.input);
		muteButton.updateWith(app.input);
		
		updateLevelWith(app.input);
	}
	
	@Override
	public final void renderTo(SpriteBatch batch) {
		game.background().renderTo(batch);
		
		game.beginIODChange(batch, 2);
			muteButton.renderTo(batch);
			pauseButton.renderTo(batch);
			score.renderTo(batch);
		game.endIODChange(batch, 2);
		
		game.beginIODChange(batch, 4);
			renderLevelTo(batch);
		game.endIODChange(batch, 4);
	}
	
	/** Initialize the Level's base components. */
	public final void initialize() {
		this.touchManager = new TouchManager(4); // 4 is the max possible concurrent touches to track.
		this.score = new Score(game()); // Initialize the score-keeping mechanism.
		
		this.tutorialState = createTutorial(); // Retrieve the Tutorial from the subclass.
		this.gameOverState = createGameOverState(); // Retrieve the GameOverState from the subclass.
		this.pausedState = createPausedState(); // Retrieve the PausedState from the subclass.
		
		this.initializeGUI();
		
		// Initialize abstract components.
		create();
		initialized = true;
	}
	
	/** Reset the Level to its initial state, then start it. */
	public final void restart() {
		// Reset the score.
		score.reset();
		
		// Reset abstract Level components.
		reset();
		// Start the Level.
		start();
	}
	
	/** Set the Game to the game-over state. */
	public final void fail() {
		submitScore(score.count());
		game.setState(gameOverState);
	}
	
	/** Set the Game to the paused state. */
	public final void pause() {
		game.setState(pausedState);
	}
	
	/** Decrement the Level's score. */
	public final void decrementScore() {
		this.score.decrement();
	}
	
	/** Increment the Level's score. */
	public final void incrementScore() {
		this.score.increment();
	}
	
	/** @return the Level's TouchManager, a mechanism that notifies listeners of touch events. */
	public final TouchManager touchManager() {
		return this.touchManager;
	}
	
	/** @return the Level's score as an integer. */
	public final int score() {
		return this.score.count();
	}
	
	/** @return the Level's Game. */
	public final Game game() {
		return this.game;
	}
	
	/**
	 * Submit a score, if it's higher than the high-score, this score will
	 * become the new highscore.
	 * @param score The score to submit.
	 */
	protected final void submitScore(int score) {
		if (score > highscore())
			setHighscore(score);
	}
	
	/** Set the highscore of the Level to the specified value. */
	protected final void setHighscore(int newHighscore) {
		game.user().setHighScore(newHighscore);
	}
	
	/** @return all the SwipeTiles in the Level. */
	public abstract SwipeTile[] tiles();
	/** Render the abstract Renderables to the specified SpriteBatch. */
	protected abstract void renderLevelTo(SpriteBatch batch);
	/** Update the abstract components of the Level. */
	protected abstract void updateLevelWith(InputProxy input);
	/** Instantiate the Level's Components. */
	protected abstract void create();
	/** Begin the Level. */
	protected abstract void start();
	/** Reset the Level to its initial state. */
	protected abstract void reset();
	/** @return the new tutorial state of the Level. */
	protected abstract TutorialState createTutorial();
	/** @return the name of the Level. */
	protected abstract String levelName();
	
	@Override
	public void exitState() { /* Do nothing */ }
	
	/** @return the Level's highscore. */
	private int highscore() {
		return game.user().highScore();
	}
	
	/** Create and @return the State to enter when the Level is failed. */
	private GameOverState createGameOverState() {
		return new GameOverState(game, this) {
			@Override
			public int currentScore() {
				return score.count();
			}
			
			@Override
			public int highestScore() {
				return highscore();
			}
		};
	}
	
	/** Create and @return the State to enter when the Level is paused. */
	private OverlayState createPausedState() {
		return new PausedState(game, this);
	}
	
	/** Initialize the Level's visual components. */
	private void initializeGUI() {
		pauseButton = new PauseButton(game);
		
		muteButton = new MuteButton(game, new Vector2(pauseButton.x(), pauseButton.y()));
		muteButton.translate(- muteButton.width(), 0);
	}
}