package com.jbs.swipe.levels.puzzle;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.tiles.SwipeListener;
import com.jbs.swipe.tiles.SwipeTile;

public abstract class PuzzleMode extends LevelState implements SwipeListener {
	
	private enum Position {
		right, top, left, bottom
	}
	
	private ArrayList<SwipeTile> tiles;
	
	public PuzzleMode(Game game) {
		super(game);
	}
	
	@Override
	public void recieveEvent(SwipeTile tile, Event event) {
		if (event == Event.TILE_FINISHED) {
			
		} else if (event == Event.TILE_CORRECTLY_SWIPED) {
			// Increase the score.
			super.incrementScore();
			// Cease notifying the correctly-swiped tile of touch events.
			super.touchManager().removeListener(tile);
			// We dont want to hear shit anymore from this Tile.
			tile.setSwipeListener(null);
			
			// Define the direction to animate the Tile.
			final Vector2 direction = SwipeTile.createSwipe(tile.direction(), 100f);
			
			new Animator(game())
				// Animate the Tile in the direction it was swiped.
				.swipeTileAway(tile, direction, tile.arrowGreenTime())
				
				// Fade the Tile to a 0% opacity.
				.fadeTileAway(tile, tile.arrowGreenTime()).get().setCallback(new TweenCallback() {
					// Once the Tile is finished animating, and it is no longer visible,
					// remove it from the list of Objects to be rendered.
					public void onEvent(int type, BaseTween<?> source) {
						
					}
				});
			
		} else if (event == Event.TILE_INCORRECTLY_SWIPED || event == Event.TILE_EXPIRED) {
			System.out.println("Tile was swiped incorrectly swiped or expired.");
			super.touchManager().removeListener(tile);
			super.fail();
		}
	}
	
	@Override
	public SwipeTile[] tiles() {
		return null;
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
	}
	
	@Override
	protected void create() {
		tiles = new ArrayList<SwipeTile>();
		
		tiles.add(createTile(Position.right));
		tiles.add(createTile(Position.top));
		tiles.add(createTile(Position.left));
		tiles.add(createTile(Position.bottom));
	}
	
	@Override
	protected void start() {
		
	}
	
	@Override
	protected void reset() {
		
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game());
	}
	
	@Override
	protected String levelName() {
		return "PuzzleMode";
	}
	
	/** @return true if the User has paid to unlock PuzzleMode. */
	protected abstract boolean hasBoughtPuzzleMode();
	
	private SwipeTile createTile(Position position) {
		final float TIME_TO_SWIPE = 5000f;
		final Vector2 POSITION = positionAsVector(position);
		
		final SwipeTile tile = new SwipeTile(game(), TIME_TO_SWIPE);
		tile.setScale(.5f, .5f);
		tile.setPosition(POSITION.x, POSITION.y);
		tile.setSwipeListener(this);
		
		super.touchManager().addListener(tile);
		
		return tile;
	}
	
	private Vector2 positionAsVector(Position position) {
		final int MAGNITUDE = 150;
		if (position == Position.top)
			return game().screenCenter().add(0, MAGNITUDE);
		else if (position == Position.left)
			return game().screenCenter().add(-MAGNITUDE, 0);
		else if (position == Position.bottom)
			return game().screenCenter().add(0, -MAGNITUDE);
		else if (position == Position.right)
			return game().screenCenter().add(MAGNITUDE, 0);
		else
			return null;
	}
}