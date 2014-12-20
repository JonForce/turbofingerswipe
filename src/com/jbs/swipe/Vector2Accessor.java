package com.jbs.swipe;

import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

public class Vector2Accessor implements TweenAccessor<Vector2> {
	public static final int
		POSITION_TWEEN = 0x1337;
	
	@Override
	public int getValues(Vector2 target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				returnValues[0] = target.x;
				returnValues[1] = target.y;
				return 2;
			default:
				throw new RuntimeException("Tween Type not recognized.");
		}
	}

	@Override
	public void setValues(Vector2 target, int tweenType, float[] newValues) {
		if (tweenType == POSITION_TWEEN)
			target.set(newValues[0], newValues[1]);
		else
			throw new RuntimeException("Tween Type not recognized.");
	}
	
}