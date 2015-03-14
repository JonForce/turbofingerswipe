package com.jbs.swipe.levels.arcade;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.Pattern;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.Row;
import com.jbs.swipe.tiles.RowController;
import com.jbs.swipe.tiles.SwipeTile.TileState;
import com.jbs.swipe.tiles.TileListener;
import com.jbs.swipe.tiles.SwipeTile;

public abstract class ArcadeMode extends LevelState implements TileListener {
	
	public final int
		NUMBER_OF_ROWS = 3,
		DEFAULT_PATTERN_LENGTH = 2;
	
	/* The Rows to be rendered and updated */
	protected Row[] rows;
	/* The Controllers to dictate the automatic expansion and contraction of the Rows. */
	protected RowController[] rowControllers;
	
	
	public ArcadeMode(Game game) {
		super(game);
	}
	
	@Override
	public final void recieveTileStateChange(final SwipeTile tile, TileState oldState, TileState newState) {
		if (newState == TileState.CORRECTLY_SWIPED) {
			// Increment the correct swipe count.
			super.score().increment();
		} else if (newState == TileState.INCORRECTLY_SWIPED) {
			final float
				SHAKE_AMPLITUDE = 100f,
				SHAKE_DURATION = 300; // Milliseconds.
			final int
				SHAKES = 5;
			
			new Animator(game())
				// Shake the Tile in the opposite direction that it was pointing.
				.shakeTile(tile, SwipeTile.createSwipe(tile.direction(), SHAKE_AMPLITUDE).mul(-1), SHAKE_DURATION, SHAKES)
				.fadeTileAway(tile, SHAKE_DURATION);
			
			delayEvent(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					remove(tile);
				}
			}, SHAKE_DURATION);
			
			super.addScoreChange(new Vector2(tile.x(), tile.y()), -5);
		} else if (newState == TileState.EXPIRED) {
			final float
				SHAKE_AMPLITUDE = 100f,
				SHAKE_DURATION = 300; // Milliseconds.
			final int
				SHAKES = 5;
			
			new Animator(game())
				// Shake the Tile in the opposite direction that it was pointing.
				.shakeTile(tile, SwipeTile.createSwipe(tile.direction(), SHAKE_AMPLITUDE).mul(-1), SHAKE_DURATION, SHAKES)
				.fadeTileAway(tile, SHAKE_DURATION);
			
				delayEvent(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						fail();
					}
				}, SHAKE_DURATION * 1.5f);
		}
		
		// Update the Rows.
		updateRowControllers();
	}
	
	@Override
	public final SwipeTile[] tiles() {
		final int
			numberOfTiles = centerRow().numberOfTiles() + topRow().numberOfTiles() + bottomRow().numberOfTiles();
		final SwipeTile[]
			collection = new SwipeTile[numberOfTiles];
		int tiles = 0;
		for (Row row : rows)
			for (SwipeTile tile : row.tiles())
				collection[tiles++] = tile;
		return collection;
	}
	
	/** Remove the SwipeTile from the Level. */
	public final void remove(SwipeTile tile) {
		for (Row row : rows) {
			if (row.contains(tile)) {
				row.collapseTile(tile);
				return;
			}
		}
		throw new RuntimeException("Could not remove SwipeTile " + tile);
	}
	
	@Override
	protected void renderLevelTo(SpriteBatch batch) {
		game().beginIODChange(batch, 3);
			for (Row row : this.rows)
				if (row != null && row.isVisible())
					row.renderTo(batch);
		game().endIODChange(batch, 3);
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		// For all non-null Rows in the Level,
		for (Row row : this.rows)
			if (row != null && row.isVisible())
				row.updateWith(input);
	}
	
	@Override
	protected void create() {
		initializeRows();
		initializeControllers();
		initializeDifficulty();
	}
	
	@Override
	protected void reset() {
		// Reset all the Level's Tiles.
		resetTiles();
		// Update the RowControllers.
		updateRowControllers();
		
		// Clear the TouchManager.
		super.touchManager().clearListeners();
	}
	
	@Override
	protected TutorialState createTutorial() {
		ArcadeTutorialState tutorial = new ArcadeTutorialState(game());
		tutorial.setExitState(this);
		return tutorial;
	}
	
	/** Animate the top-Row in from the top of the screen. */
	protected final void revealTopRow() {
		topRow().animateTilesIn();
		topRow().setVisible(true);
		topRow().resetTiles();
	}
	
	/** Animate the bottom-Row in from the bottom of the screen. */
	protected final void revealBottomRow() {
		bottomRow().animateTilesIn();
		bottomRow().setVisible(true);
		bottomRow().resetTiles();
	}
	
	/** Disable the top Row. */
	protected final void hideTopRow() {
		topRow().setVisible(false);
	}
	
	/** Disable the bottom Row. */
	protected final void hideBottomRow() {
		bottomRow().setVisible(false);
	}
	
	/* Update all the RowControllers with the Level's score. */
	protected final void updateRowControllers() {
		topRowController().update(this.score().count());
		bottomRowController().update(this.score().count());
		centerRowController().update(this.score().count());
	}
	
	protected final void setPatternLengths(int newLength) {
		for (Row row : rows) {
			final Pattern<Direction> pattern = new Pattern<Direction>(newLength, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.DOWN);
			pattern.scramble(super.game().random());
			row.setPattern(pattern);
		}
	}
	
	/** @return the Level's top Row. */
	protected final Row topRow() {
		return rows[2];
	}
	
	/** @return the Level's center Row. */
	protected final Row centerRow() {
		return rows[1];
	}
	
	/** @return the Level's bottom Row. */
	protected final Row bottomRow() {
		return rows[0];
	}
	
	/** @return the Level's top-Row controller. */
	protected RowController topRowController() {
		return rowControllers[2];
	}
	
	/** @return the Level's center-Row controller. */
	protected RowController centerRowController() {
		return rowControllers[1];
	}
	
	/** @return the Level's bottom-Row controller. */
	protected RowController bottomRowController() {
		return rowControllers[0];
	}
	
	/** @return the initial number of Tiles to populate each of the Level's Rows. */
	protected abstract int initialTilesPerRow();
	/** Set the Level's components to facilitate a certain difficulty. */
	protected abstract void initializeDifficulty();
	
	/** Reset all the SwipeTiles active in the Level. */
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
		
		// Only the center Row is visible by default.
		centerRow().setVisible(true);
		// Other Rows must be revealed.
		bottomRow().setVisible(false);
		topRow().setVisible(false);
		
		for (Row row : this.rows) {
			row.setSwipeListener(this);
			row.setPattern(new Pattern<Direction>(DEFAULT_PATTERN_LENGTH, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.DOWN));
			row.scramblePattern();
		}
	}
	
	private void initializeControllers() {
		rowControllers = new RowController[3];
		
		rowControllers[0] = new RowController(bottomRow());
		rowControllers[1] = new RowController(centerRow());
		rowControllers[2] = new RowController(topRow());
	}
	
	private void delayEvent(TweenCallback callback, float delay) {
		Tween.call(callback).delay(delay).start(game().tweenManager());
	}
}