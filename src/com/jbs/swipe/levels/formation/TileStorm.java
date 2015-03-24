package com.jbs.swipe.levels.formation;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

public class TileStorm extends Formation {
	
	private int numberOfTiles;
	
	public TileStorm(Game game, TileListener listener, int tiles) {
		super(game, listener);
		this.numberOfTiles = tiles;
	}
	
	@Override
	public SwipeTile[] create() {
		final float
			timeToSwipeBase = 4000,
			timeToSwipeIncrease = 700,
			verticalSpacing = 75;
		
		setTranslationDamping(.1f + numberOfTiles * .0015f);
		
		SwipeTile[] tiles = new SwipeTile[numberOfTiles];
		
		tiles[0] = super.createTile(Direction.UP, timeToSwipeBase);
		tiles[0].setPosition(game.screenWidth() * 2, game.screenHeight()/4);
		tiles[0].setTranslationTarget(new Vector2(game.screenWidth()/2, game.screenHeight()/4), .3f);
		for (int i = 1; i != tiles.length; i ++)
			tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.UP, timeToSwipeBase + (i * timeToSwipeIncrease), new Vector2((((i % 3) - 1)*(i * 10)), verticalSpacing));
		
		return tiles;
	}
}