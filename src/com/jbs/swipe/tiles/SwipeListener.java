package com.jbs.swipe.tiles;

public interface SwipeListener {
	
	void onCorrectSwipe(SwipeTile tile);
	void onIncorrectSwipe(SwipeTile tile);
	void onExpire(SwipeTile tile);
	
}