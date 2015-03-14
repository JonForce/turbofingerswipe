package com.jbs.swipe.tiles;

import com.jbs.swipe.tiles.SwipeTile.TileState;

public interface TileListener {
	
	void recieveTileStateChange(SwipeTile tile, TileState oldState, TileState newState);
	
}