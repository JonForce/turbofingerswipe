package com.jbs.swipe.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.TouchManager;
import com.jbs.swipe.gui.Score;

public abstract class Level implements Renderable {
	
	private final Game game;
	
	/* The Level's score-keeping and score-rendering mechanism. */
	private Score score;
	private Renderable background;
	private TouchManager touchManager;
	
	public Level(Game game) {
		this.game = game;
	}
	
	/* Instantiate the Level's Components. */
	public abstract void create();
	/* Begin the Level. */
	public abstract void start();
	/* Reset the Level to its initial state. */
	public abstract void reset();
	/* Update the Level with the specified InputProxy. */
	public abstract void updateWith(InputProxy input);
	/* Render the Level to the SpriteBatch. */
	public abstract void renderTo(SpriteBatch batch);
	/* @return true when the Level has had it's failure condition met. */
	public abstract boolean failureConditionMet();
	
	/* Initialize the Level's base components. */
	public final void initialize() {
		this.score = new Score(game());
		this.touchManager = new TouchManager(4);
	}
	
	/* Render the Level's background to the specified SpriteBatch. */
	public final void renderBackgroundTo(SpriteBatch batch) {
		this.background.renderTo(batch);
	}
	
	/* Set the Level's background to the specified Renderable. */
	public final void setBackground(Renderable newBackground) {
		this.background = newBackground;
	}
	
	/* Render the Level's score to the SpriteBatch. */
	public final void renderScoreTo(SpriteBatch batch) {
		this.score.renderTo(batch);
	}
	
	/* Reset the Level's Score. */
	public final void resetScore() {
		this.score.reset();
	}
	
	/* Decrement the Level's score. */
	public final void decrementScore() {
		this.score.decrement();
	}
	
	/* Increment the Level's score. */
	public final void incrementScore() {
		this.score.increment();
	}
	
	public final TouchManager touchManager() {
		return this.touchManager;
	}
	
	/* @return the Level's score as an integer. */
	public final int score() {
		return this.score.count();
	}
	
	/* @return the Level's Game. */
	public final Game game() {
		return this.game;
	}
	
}