package com.jbs.swipe.tiles;

public interface SwipeListener {
	
	public static enum Event {
		TILE_CORRECTLY_SWIPED,
		TILE_INCORRECTLY_SWIPED,
		TILE_EXPIRED,
		TILE_FINISHED
	}
	
	void recieveEvent(SwipeTile tile, Event event);
	
}