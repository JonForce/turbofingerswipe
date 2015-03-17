package com.jbs.swipe.levels.normal;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.tiles.SwipeTile.TileState;
import com.jbs.swipe.tiles.TileListener;
import com.jbs.swipe.tiles.SwipeTile;

public final class NormalMode extends LevelState implements TileListener {
	
	private ArrayList<SwipeTile> tiles;
	
	public NormalMode(Game game) {
		super(game);
	}
	
	@Override
	public void recieveTileStateChange(final SwipeTile tile, TileState oldState, TileState newState) {
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
					tile.setState(TileState.FINISHED);
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
		} else if (newState == TileState.FINISHED) {
			remove(tile);
		}
	}
	
	@Override
	protected void renderLevelTo(SpriteBatch batch) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.renderTo(batch);
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.updateWith(input);
		
		if (tiles.isEmpty())
			for (SwipeTile tile : new TileSnake(game(), this, 50).create())
				tiles.add(tile);
	}
	
	@Override
	protected void create() {
		this.tiles = new ArrayList<SwipeTile>();
		start();
	}
	
	@Override
	public SwipeTile[] tiles() {
		return tiles.toArray(new SwipeTile[tiles.size()]);
	}
	
	/** @return true if the Level contains the Tile. */
	public final boolean contains(SwipeTile tile) {
		return this.tiles.contains(tile);
	}
	
	/** Safely remove the specified Tile from the Level. */
	public final void remove(SwipeTile tile) {
		if (tiles.contains(tile))
			tiles.remove(tile);
		else
			throw new RuntimeException("Couldnt remove the Tile.");
	}
	
	/** Start the Level. */
	protected void start() {
		for (SwipeTile tile : new TileBox(game(), this, 20).create())
			tiles.add(tile);
	}
	
	@Override
	protected void reset() {
		// Clear the TouchManager.
		super.touchManager().clearListeners();
		tiles.clear();
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game(), this);
	}
	
	@Override
	protected String levelName() {
		return "NormalMode";
	}
	
	/** Delay the callback by the specified time in milliseconds (I think, idk). */
	private void delayEvent(TweenCallback callback, float delay) {
		Tween.call(callback).delay(delay).start(game().tweenManager());
	}
}