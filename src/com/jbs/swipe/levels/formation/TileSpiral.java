package com.jbs.swipe.levels.formation;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

public class TileSpiral extends Formation {
	
	private int numberOfTiles;
	
	public TileSpiral(Game game, TileListener listener, int tiles) {
		super(game, listener);
		this.numberOfTiles = tiles;
	}
	
	@Override
	public SwipeTile[] create() {
		setTranslationDamping(.1f + Math.min(.2f, numberOfTiles * .003f));
		float
			spacing = 150f,
			baseTimeToSwipe = 13500,
			timeToSwipeIncrease = 500;
		
		SwipeTile[] tiles = new SwipeTile[numberOfTiles];
		
		tiles[0] = super.createTile(Direction.UP, baseTimeToSwipe);
		tiles[0].setPosition(-game.screenWidth() / 2, game.screenHeight() / 2);
		tiles[0].setTranslationTarget(game.screenCenter(), .35f);
		
		Direction direction = Direction.UP;
		Vector2 offset = new Vector2(0, spacing);
		int rowLength = 2, lengthIncrease = 0;
		for (int i = 1; i != tiles.length; i ++) {
			float timeToSwipe = baseTimeToSwipe + i*timeToSwipeIncrease;
			if (i % rowLength == 0) {
				direction =
					(direction == Direction.UP)? Direction.LEFT :
					(direction == Direction.LEFT)? Direction.DOWN :
					(direction == Direction.DOWN)? Direction.RIGHT :
					(direction == Direction.RIGHT)? Direction.UP :
						null;
				offset =
					(direction == Direction.UP)? new Vector2(0, spacing) :
					(direction == Direction.LEFT)? new Vector2(-spacing, 0) :
					(direction == Direction.DOWN)? new Vector2(0, -spacing) :
					(direction == Direction.RIGHT)? new Vector2(spacing, 0) :
						null;
				rowLength = i + (++lengthIncrease);
			}
			
			tiles[i] = super.createTrackingTile(tiles[i - 1], direction, timeToSwipe, offset.cpy());
		}
		
		return tiles;
	}
}