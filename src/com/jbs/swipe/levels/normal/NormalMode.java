package com.jbs.swipe.levels.normal;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.tiles.SwipeListener;
import com.jbs.swipe.tiles.SwipeTile;

public final class NormalMode extends LevelState implements SwipeListener {
	
	private ArrayList<SwipeTile> tiles;
	
	private final float
		swipesTilMaxDifficulty = 20,
		maximumTimeToSwipe = 10000f,
		minimumTimeToSwipe = 2000f;
	
	public NormalMode(Game game) {
		super(game);
	}
	
	@Override
	public void onCorrectSwipe(SwipeTile tile) {
		// Increase the score.
		super.incrementScore();
		// Cease notifying the correctly-swiped tile of touch events.
		super.touchManager().removeListener(tile);
		// We dont want to hear shit anymore from this Tile.
		tile.setSwipeListener(null);
		
		tiles.add(createTile());
	}
	
	@Override
	public void onIncorrectSwipe(SwipeTile tile) {
		System.out.println("Tile was swiped incorrectly");
		super.touchManager().removeListener(tile);
		super.fail();
	}
	
	@Override
	public void onExpire(SwipeTile tile) {
		System.out.println("Tile expired");
		super.touchManager().removeListener(tile);
		super.fail();
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
		
		tiles.add(createTile());
	}
	
	@Override
	protected void start() {
		
	}
	
	@Override
	protected void reset() {
		for (SwipeTile tile : tiles)
			super.touchManager().removeListener(tile);
		tiles.clear();
		
		tiles.add(createTile());
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game());
	}
	
	@Override
	protected String levelName() {
		return "NormalMode";
	}
	
	private SwipeTile createTile() {
		final float TIME_TO_SWIPE =
				Math.max(minimumTimeToSwipe, maximumTimeToSwipe - (score() / swipesTilMaxDifficulty)*(maximumTimeToSwipe - minimumTimeToSwipe));
		
		final SwipeTile tile = new SwipeTile(game(), TIME_TO_SWIPE);
		tile.setSwipeListener(this);
		
		super.touchManager().addListener(tile);
		
		return tile;
	}
}