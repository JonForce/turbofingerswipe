package com.jbs.swipe.levels.normal;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.tiles.SwipeListener;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.traps.Bomb;

public final class NormalMode extends LevelState implements SwipeListener {
	
	private ArrayList<SwipeTile> tiles;
	private ArrayList<Renderable> renderables;
	
	private final float
		swipesTilMaxDifficulty = 20,
		maximumTimeToSwipe = 10000f,
		minimumTimeToSwipe = 2000f;
	
	private int
		stackSize = 5,
		tileOffset = 20;
	
	private Bomb bomb;
	
	public NormalMode(Game game) {
		super(game);
	}
	
	@Override
	public void recieveEvent(SwipeTile tile, Event event) {
		System.out.println("Recieved event : " + event);
		if (event == Event.TILE_CORRECTLY_SWIPED) {
			final float
				POSITION_ANIMATION_SCALE = 200f;
			
			// Increase the score.
			incrementScore();
			
			// Define the direction to animate the Tile.
			final Vector2 direction = SwipeTile.createSwipe(tile.direction(), POSITION_ANIMATION_SCALE);
			
			new Animator(game())
				// Animate the Tile in the direction it was swiped.
				.swipeTileAway(tile, direction, tile.arrowGreenTime())
				
				// Fade the Tile to a 0% opacity.
				.fadeTileAway(tile, tile.arrowGreenTime()).get().setCallback(new TweenCallback() {
					// Once the Tile is finished animating, and it is no longer visible,
					// remove it from the list of Objects to be rendered.
					public void onEvent(int type, BaseTween<?> source) {
						renderables.remove(this);
						addTileToStack();
					}
				});
			
			// Cease notifying the correctly-swiped tile of touch events.
			touchManager().removeListener(tile);
			// We dont want to hear shit anymore from this Tile.
			tile.setSwipeListener(null);
			// Remove this from the list of Tiles to update.
			tiles.remove(tile);
			
			touchManager().addListener(topTile());
		} else if (event == Event.TILE_INCORRECTLY_SWIPED || event == Event.TILE_EXPIRED) {
			System.out.println("Tile was swiped incorrectly swiped or expired.");
			super.touchManager().removeListener(tile);
			super.fail();
		}
	}
	
	@Override
	protected void renderLevelTo(SpriteBatch batch) {
		// Iterate inversely.
		for (int i = renderables.size() - 1; i != -1; i --)
			renderables.get(i).renderTo(batch);
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		// Iterate inversely.
		for (int i = tiles.size() - 1; i != -1; i --)
			tiles.get(i).updateWith(input);
		
		bomb.updateWith(input);
		if (Gdx.input.isButtonPressed(Buttons.RIGHT) && bomb.stock() >= 50) {
			bomb.reset();
			bomb.useTrapOn(tiles());
			bomb.setStock(0);
		}
		bomb.increaseStockBy(1);
	}
	
	@Override
	protected void create() {
		tiles = new ArrayList<SwipeTile>();
		renderables = new ArrayList<Renderable>();
		
		createStackOfSize(stackSize);
		
		bomb = new Bomb(game()) {
			@Override
			public void reset() {
				super.reset();
				this.setPosition(0, game().screenHeight()/4f);
			}
		};
		bomb.setStock(50);
	}
	
	@Override
	public SwipeTile[] tiles() {
		SwipeTile[] tilesArray = new SwipeTile[tiles.size()];
		tiles.toArray(tilesArray);
		return tilesArray;
	}
	
	@Override
	protected void start() {
		
	}
	
	@Override
	protected void reset() {
		// Clear the TouchManager.
		super.touchManager().clearListeners();
		// Clear the list of Tiles.
		tiles.clear();
		// Clear the list of Objects to be rendered.
		renderables.clear();
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game());
	}
	
	@Override
	protected String levelName() {
		return "NormalMode";
	}
	
	private SwipeTile topTile() {
		for (int i = tiles.size() - 1; i != -1; i --)
			if (tiles.get(i) != null)
				return tiles.get(i);
		
		throw new RuntimeException("Cannot get top tile.");
	}
	
	/** Create n tiles on top of eachother. */
	private void createStackOfSize(int size) {
		for (int i = 0; i != size; i ++)
			addTileToStack();
		
		touchManager().addListener(topTile());
	}
	
	/** Add a Tile to the bottom of the Tile stack. */
	private void addTileToStack() {
		moveStackUp(tileOffset);
		tiles.add(0, createTile());
	}
	
	/** Shift the stack up by n pixels. Do not translate specified indices. */
	private void moveStackUp(int pixels, int... excludedIndices) {
		for (int tile = 0; tile != tiles.size(); tile ++) {
			boolean tileIsExcluded = false;
			for (int i = 0; i != excludedIndices.length; i ++)
				if (tile == i)
					tileIsExcluded = true;
			
			if (!tileIsExcluded)
				tiles.get(tile).translate(0, pixels);
		}
	}
	
	private SwipeTile createTile() {
		final float TIME_TO_SWIPE =
				Math.max(minimumTimeToSwipe, maximumTimeToSwipe - (score() / swipesTilMaxDifficulty)*(maximumTimeToSwipe - minimumTimeToSwipe));
		
		final SwipeTile tile = new SwipeTile(game(), TIME_TO_SWIPE);
		// Set the Tile to notify this class of swipe-events.
		tile.setSwipeListener(this);
		
		// Add the Tile to list of Objects to be rendered.
		renderables.add(tile);
		
		return tile;
	}
}