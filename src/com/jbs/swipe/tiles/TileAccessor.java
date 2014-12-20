package com.jbs.swipe.tiles;

import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

public class TileAccessor implements TweenAccessor<SwipeTile> {
	
	public static final int
		OPACITY_TWEEN = 0xF00,
		SCALE_TWEEN = 0xBEEF,
		POSITION_TWEEN = 0xC0FF1E,
		ROTATION_TWEEN = 0xB00B1E;
	
	@Override
	public int getValues(SwipeTile target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case OPACITY_TWEEN:
				returnValues[0] = target.opacity();
				return 2;
			case SCALE_TWEEN:
				final Vector2 scale = target.scale();
				returnValues[0] = scale.x;
				returnValues[1] = scale.y;
				return 2;
			case POSITION_TWEEN:
				returnValues[0] = target.x();
				returnValues[1] = target.y();
				return 2;
			case ROTATION_TWEEN:
				returnValues[0] = target.rotation();
				return 1;
			default: assert false; return -1;
		}
	}
	
	@Override
	public void setValues(SwipeTile target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case OPACITY_TWEEN:
				target.setOpacity(newValues[0]);
				break;
			case SCALE_TWEEN:
				target.setScale(newValues[0], newValues[1]);
				break;
			case POSITION_TWEEN:
				target.setPosition(newValues[0], newValues[1]);
				break;
			case ROTATION_TWEEN:
				target.setRotation(newValues[0]);
				break;
			default: assert false;
		}
	}
}