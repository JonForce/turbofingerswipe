package com.jbs.swipe.levels.formation;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

public class TileBox extends Formation {
	
	private int numberOfTiles;
	
	public TileBox(Game game, TileListener listener, int tiles) {
		super(game, listener);
		this.numberOfTiles = tiles;
	}

	@Override
	public SwipeTile[] create() {
		SwipeTile[] tiles = new SwipeTile[numberOfTiles];
		
		final float timeToSwipe = 8000, timeIncrease = 500;
		
		tiles[0] = super.createTile(Direction.UP, timeToSwipe);
		tiles[0].setPosition(game.screenWidth() * 2, game.screenHeight()/2);
		tiles[0].setTranslationTarget(new Vector2(game.screenWidth() - 100, game.screenHeight()/2), .3f);
		
		for (int i = 1; i != tiles.length; i ++)
			if (i < 3)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.DOWN, timeToSwipe + (timeIncrease * i), new Vector2(0, -130));
			else if (i < 8)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.LEFT, timeToSwipe + (timeIncrease * i), new Vector2(-130, 0));
			else if (i < 11)
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.UP, timeToSwipe + (timeIncrease * i), new Vector2(0, 130));
			else
				tiles[i] = super.createTrackingTile(tiles[i - 1], Direction.RIGHT, timeToSwipe + (timeIncrease * i), new Vector2(130, 0));
		
		return tiles;
	}
	
}