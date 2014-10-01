package com.jbs.swipe.levels.arcade;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.levels.Level;
import com.jbs.swipe.tiles.Row;
import com.jbs.swipe.tiles.RowController;
import com.jbs.swipe.tiles.SwipeListener;
import com.jbs.swipe.tiles.SwipeTile;

public abstract class ArcadeMode extends Level implements SwipeListener {
	
	public final int
		NUMBER_OF_ROWS = 3;
	
	/* The Rows to be rendered and updated */
	protected Row[] rows;
	/* The Controllers to dictate the automatic expansion and contraction of the Rows. */
	protected RowController[] rowControllers;
	
	private boolean
		activeTileExpired, // True if the Level's active tile has expired.
		activeTileIncorrectlySwiped; // True if the Level's active tile has been incorrectly swiped.
	
	public ArcadeMode(Game game) {
		super(game);
	}

	@Override
	public void create() {
		super.initialize();
		initializeRows();
		initializeControllers();
		initializeDifficulty();
	}

	@Override
	public void start() {
		
	}

	@Override
	public void reset() {
		// Reset the score.
		super.resetScore();
		// Reset failure condition.
		activeTileExpired = false;
		activeTileIncorrectlySwiped = false;
		// Reset all the Level's Tiles.
		resetTiles();
		// Update the RowControllers
		updateRowControllers();
	}

	@Override
	public void updateWith(InputProxy input) {
		// For all non-null Rows in the Level,
		for (Row row : this.rows)
			if (row != null)
				row.updateWith(input);
	}

	@Override
	public void renderTo(SpriteBatch batch) {
		// Render the background under all the rest of the Level.
		super.renderBackgroundTo(batch);
		for (Row row : this.rows)
			if (row != null)
				row.renderTo(batch);
	}

	@Override
	/* @return true when the player has lost and the Level is in its fail state. */
	public boolean failureConditionMet() {
		// Return true if the any of the Level's activeTiles expired or were incorrectly swiped.
		return (this.activeTileExpired || this.activeTileIncorrectlySwiped);
	}

	@Override
	public void onCorrectSwipe(SwipeTile tile) {
		// Increment the correct swipe count.
		super.incrementScore();
		
		updateRowControllers();
	}

	@Override
	public void onIncorrectSwipe(SwipeTile tile) {
		System.out.println("Level's active tile was incorrectly swipped!");
		
		// Mark the Level as having it's activeTile incorrectly swiped.
		activeTileIncorrectlySwiped = true;
	}

	@Override
	public void onExpire(SwipeTile tile) {
		System.out.println("Level's active tile expired!");
		
		// Mark the Level as having it's activeTile expired.
		activeTileExpired = true;
	}
	
	/* Update all the RowControllers with the Level's score. */
	protected final void updateRowControllers() {
		topRowController().update(this.score());
		centerRowController().update(this.score());
		bottomRowController().update(this.score());
	}
	
	/* @return the Level's top Row. */
	protected Row topRow() {
		return rows[2];
	}
	
	/* @return the Level's center Row. */
	protected Row centerRow() {
		return rows[1];
	}
	
	/* @return the Level's bottom Row. */
	protected Row bottomRow() {
		return rows[0];
	}
	
	/* @return the Level's top-Row controller. */
	protected RowController topRowController() {
		return rowControllers[2];
	}
	
	/* @return the Level's center-Row controller. */
	protected RowController centerRowController() {
		return rowControllers[1];
	}
	
	/* @return the Level's bottom-Row controller. */
	protected RowController bottomRowController() {
		return rowControllers[0];
	}
	
	/* @return the initial number of Tiles to populate each of the Level's Rows. */
	protected abstract int initialTilesPerRow();
	/* Set the Level's components to facilitate a certain difficulty. */
	protected abstract void initializeDifficulty();
	
	/* Reset all the SwipeTiles active in the Level. */
	public void resetTiles() {
		for (Row row : this.rows)
			if (row != null)
				row.resetTiles();
	}
	
	private void initializeRows() {
		this.rows = new Row[NUMBER_OF_ROWS];
		
		// Initialize the center Row.
		rows[1] = new Row(game(), game().screenCenter(), initialTilesPerRow());
		// Initialize the bottom Row below the center Row.
		rows[0] = new Row(game(), game().screenCenter().add(0, -rows[1].height()), initialTilesPerRow());
		// Initialize the top Row above the center Row.
		rows[2] = new Row(game(), game().screenCenter().add(0, rows[1].height()), initialTilesPerRow());
		
		// Set all the Rows to notify the Level of SwipeTile events.
		for (Row row : this.rows)
			row.setSwipeListener(this);
	}
	
	private void initializeControllers() {
		this.rowControllers = new RowController[3];
		
		rowControllers[0] = new RowController(bottomRow());
		rowControllers[1] = new RowController(centerRow());
		rowControllers[2] = new RowController(topRow());
	}
}