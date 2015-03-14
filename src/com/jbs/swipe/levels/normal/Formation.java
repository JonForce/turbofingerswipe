package com.jbs.swipe.levels.normal;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileListener;

abstract class Formation {
	
	protected Game game;
	private TileListener listener;
	private float translationDamping = .1f;
	
	public Formation(Game game, TileListener listener) {
		this.game = game;
		this.listener = listener;
	}
	
	public abstract SwipeTile[] create();
	
	protected final void setTranslationDamping(float newDamping) {
		this.translationDamping = newDamping;
	}
	
	protected SwipeTile createTrackingTile(final SwipeTile tileToFollow, Direction direction, float timeToSwipe, final Vector2 offsetFromTarget) {
		final float
			SCALE = .55f;
				//Math.max(minimumTimeToSwipe, maximumTimeToSwipe - (score().count() / swipesTilMaxDifficulty)*(maximumTimeToSwipe - minimumTimeToSwipe));
		final Vector2 offset = (offsetFromTarget == null)? new Vector2() : offsetFromTarget;
		
		final SwipeTile tile = new SwipeTile(game, timeToSwipe, direction) {
			@Override
			public void updateTranslationAnimation() {
				//if (tileToFollow != null && (tileToFollow.tileState() == TileState.CORRECTLY_SWIPED || tileToFollow.tileState() == TileState.FINISHED)) {
				if (tileToFollow != null) {
					
					if (tileToFollow.tileState() == TileState.CORRECTLY_SWIPED || tileToFollow.tileState() == TileState.FINISHED)
						offset.set(0, 0);
					
					if (tileToFollow.tileState() == TileState.FINISHED)
						tileToFollow.updateTranslationAnimation();
					
					final float
						// Declare the distance to the target position.
						deltaX = tileToFollow.x() + offset.x - this.x(),
						deltaY = tileToFollow.y() + offset.y - this.y(),
						// Declare the new coordinates for the SwipeTile to use.
						newX = this.x() + (deltaX * translationDamping),
						newY = this.y() + (deltaY * translationDamping);
					
					setPosition(newX, newY);
				} else
					super.updateTranslationAnimation();
			}
		};
		if (tileToFollow != null)
			tile.setPosition(tileToFollow.x() + offset.x, tileToFollow.y() + offset.y);
		tile.scale(SCALE, SCALE);
		tile.setSwipeListener(listener);
		
		return tile;
	}
	protected SwipeTile createTrackingTile(final SwipeTile tileToFollow, Direction direction, float timeToSwipe) {
		return createTrackingTile(tileToFollow, direction, timeToSwipe, null);
	}
	protected SwipeTile createTile(Direction direction, float timeToSwipe) {
		return createTrackingTile(null, direction, timeToSwipe);
	}
}