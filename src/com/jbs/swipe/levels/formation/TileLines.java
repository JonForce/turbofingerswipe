package com.jbs.swipe.levels.formation;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

public class TileLines extends Formation {
	
	public static enum Dimension {
		HORIZONTAL, VERTICAL
	}
	
	private int numberOfTiles;
	private Dimension dimension;
	
	public TileLines(Game game, TileListener listener, int tiles, Dimension dimension) {
		super(game, listener);
		this.numberOfTiles = tiles;
		this.dimension = dimension;
	}
	
	@Override
	public SwipeTile[] create() {
		float
			timeToSwipeBase = 4000,
			timeToSwipeIncrease = 700,
			smallMargin = 150,
			largeMargin = 500;
		int lineHeight = 3 + (int)(numberOfTiles / 20);
		super.setTranslationDamping(.1f + (numberOfTiles / 1000));
		
		SwipeTile[] tiles = new SwipeTile[numberOfTiles];
		
		tiles[0] = super.createTile(Direction.UP, timeToSwipeBase);
		if (dimension == Dimension.VERTICAL)
			tiles[0].setTranslationTarget(new Vector2(100, game.screenHeight()/2), .3f);
		else
			tiles[0].setTranslationTarget(new Vector2(game.screenWidth() / 2, 100), .3f);
		
		for (int i = 1; i != tiles.length; i ++) {
			if (i % lineHeight == 0) {
				largeMargin = 500;
				smallMargin *= -1;
			}
			
			Direction direction;
			Vector2 offset = new Vector2(largeMargin, smallMargin);;
			if (this.dimension == Dimension.VERTICAL) {
				direction = (i % (lineHeight * 2) < lineHeight)? Direction.UP : Direction.DOWN;
			} else {
				direction = (i % (lineHeight * 2) < lineHeight)? Direction.RIGHT : Direction.LEFT;
				offset.set(smallMargin, largeMargin);
			}
			
			tiles[i] = super.createTrackingTile(tiles[i - 1], direction, timeToSwipeBase + (i * timeToSwipeIncrease), offset);
			largeMargin = 0;
		}
		
		return tiles;
	}
}