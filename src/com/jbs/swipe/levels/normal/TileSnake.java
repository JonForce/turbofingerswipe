package com.jbs.swipe.levels.normal;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

public class TileSnake extends Formation {
	
	private int numberOfTiles;
	
	public TileSnake(Game game, TileListener listener, int tiles) {
		super(game, listener);
		this.numberOfTiles = tiles;
	}
	
	@Override
	public SwipeTile[] create() {
		final float
			timeToSwipeBase = 4000,
			timeToSwipeIncrease = 700,
			margin = 150;
		
		SwipeTile[] tiles = new SwipeTile[numberOfTiles];
		
		tiles[0] = super.createTile(Direction.UP, timeToSwipeBase);
		tiles[0].setPosition(game.screenWidth() * 2, game.screenHeight()/2 + 50);
		tiles[0].setTranslationTarget(new Vector2(100, game.screenHeight() / 3), .3f);
		
		for (int i = 1; i != tiles.length; i ++) {
			final float timeToSwipe = timeToSwipeBase + (i * timeToSwipeIncrease);
			System.out.println(i % 12);
			if (i % 12 < 3)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.UP, timeToSwipe, new Vector2(0, margin));
			else if (i % 12 > 6 && i % 12 < 9)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.DOWN, timeToSwipe, new Vector2(0, -margin));
			else if (i % 12 < 6 || i % 12 < 12)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.RIGHT, timeToSwipe, new Vector2(margin, 0));

//			else if (i % 10 < 12)
//				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction., timeToSwipe, offsetFromTarget)
		}
		
		return tiles;
	}
}