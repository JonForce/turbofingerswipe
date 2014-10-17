package com.jbs.swipe.tiles;

import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

public class TileAccessor implements TweenAccessor<SwipeTile> {
	
	public static final int
		POSITION_TWEEN = 666,
		SCALE_TWEEN = 667;
	
	@Override
	public int getValues(SwipeTile target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				returnValues[0] = target.x();
				returnValues[1] = target.y();
				return 2;
			case SCALE_TWEEN:
				final Vector2 scale = target.scale();
				returnValues[0] = scale.x;
				returnValues[1] = scale.y;
				return 2;
			default: assert false; return -1;
		}
	}
	
	@Override
	public void setValues(SwipeTile target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				target.setPosition(newValues[0], newValues[1]);
			case SCALE_TWEEN:
				target.setScale(newValues[0], newValues[1]);
			default: assert false;
		}
	}
}