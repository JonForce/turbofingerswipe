package com.jbs.swipe.levels.arcade;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.Pattern;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.Row;
import com.jbs.swipe.tiles.RowController;
import com.jbs.swipe.tiles.SwipeListener;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.traps.Bomb;
import com.jbs.swipe.traps.BombSpawner;
import com.jbs.swipe.traps.DarkHole;
import com.jbs.swipe.traps.DarkHoleSpawner;

public abstract class ArcadeMode extends LevelState implements SwipeListener {
	
	public final int
		NUMBER_OF_ROWS = 3,
		DEFAULT_PATTERN_LENGTH = 2;
	
	/* The Rows to be rendered and updated */
	protected Row[] rows;
	/* The Controllers to dictate the automatic expansion and contraction of the Rows. */
	protected RowController[] rowControllers;
	
	private BombSpawner bombSpawner;
	private DarkHoleSpawner darkHoleSpawner;
	
	public ArcadeMode(Game game) {
		super(game);
	}
	
	@Override
	public void recieveEvent(final SwipeTile tile, Event event) {
		if (event == Event.TILE_CORRECTLY_SWIPED) {
			// Increment the correct swipe count.
			super.incrementScore();
			// Remove the swiped Tile from the Touch notification list.
			super.touchManager().removeListener(tile);
		} else if (event == Event.TILE_INCORRECTLY_SWIPED || event == Event.TILE_EXPIRED) {
			System.out.println("Tile was swiped incorrectly swiped or expired.");
			
			final float
				SHAKE_AMPLITUDE = 50f,
				SHAKE_DURATION = 150; // Milliseconds.
			final int
				SHAKES = 3;
			
			new Animator(game())
				// Shake the Tile in the opposite direction that it was pointing.
				.shakeTile(tile, SwipeTile.createSwipe(tile.direction(), SHAKE_AMPLITUDE).mul(-1), SHAKE_DURATION, SHAKES)
				.fadeTileAway(tile, SHAKE_DURATION);
			
			delayEvent(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					for (Row row : rows)
						if (row != null && row.contains(tile))
							row.collapseTile(tile);
				}
			}, SHAKE_DURATION);
			
			if (!topRow().isVisible())
				revealTopRow();
			else if (!bottomRow().isVisible())
				revealBottomRow();
			else {
				super.touchManager().removeListener(tile);
				super.fail();
			}
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
	
	@Override
	protected void renderLevelTo(SpriteBatch batch) {
		for (Row row : this.rows)
			if (row != null && row.isVisible())
				row.renderTo(batch);
		
		bombSpawner.renderTo(batch);
		darkHoleSpawner.renderTo(batch);
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		// For all non-null Rows in the Level,
		for (Row row : this.rows)
			if (row != null && row.isVisible())
				row.updateWith(input);
		
		bombSpawner.updateWith(input);
		darkHoleSpawner.updateWith(input);
	}
	
	@Override
	protected void create() {
		initializeRows();
		initializeControllers();
		initializeDifficulty();
		
		bombSpawner = new BombSpawner(game(), this, 1);
		bombSpawner.setStock(game().user().stockOf(new Bomb(game())));
		
		darkHoleSpawner = new DarkHoleSpawner(game(), this, 2);
		darkHoleSpawner.setStock(game().user().stockOf(new DarkHole(game())));
	}
	
	@Override
	protected void start() {
		
	}
	
	@Override
	protected void reset() {
		// Reset all the Level's Tiles.
		resetTiles();
		// Reinitialize the RowControllers, a lazy way to reset them.
		//initializeControllers();
		hideTopRow();
		hideBottomRow();
		// Update the RowControllers.
		updateRowControllers();
		
		// Clear the TouchManager.
		super.touchManager().clearListeners();
		// Add all remaining Tiles to the TouchManager's notification list.
		for (Row row : rows)
			if (row != null)
				row.setTouchManager(super.touchManager());
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game());
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
		topRowController().update(this.score());
		bottomRowController().update(this.score());
		centerRowController().update(this.score());
	}
	
	protected final void setPatternLengths(int newLength) {
		for (Row row : rows) {
			final Pattern<Direction> pattern = new Pattern<Direction>(newLength, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.DOWN);
			pattern.scramble(super.game().random());
			row.setPattern(pattern);
		}
	}
	
	/* @return the Level's top Row. */
	protected final Row topRow() {
		return rows[2];
	}
	
	/* @return the Level's center Row. */
	protected final Row centerRow() {
		return rows[1];
	}
	
	/* @return the Level's bottom Row. */
	protected final Row bottomRow() {
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
		
		// Only the center Row is visible by default.
		centerRow().setVisible(true);
		// Other Rows must be revealed.
		bottomRow().setVisible(false);
		topRow().setVisible(false);
		
		for (Row row : this.rows) {
			row.setSwipeListener(this);
			row.setTouchManager(touchManager());
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