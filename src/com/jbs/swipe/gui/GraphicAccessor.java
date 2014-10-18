package com.jbs.swipe.gui;

import aurelienribon.tweenengine.TweenAccessor;

import com.jbs.framework.rendering.Graphic;

public class GraphicAccessor implements TweenAccessor<Graphic> {
	
	public static final int
		POSITION_TWEEN = 0,
		ROTATION_TWEEN = 1;
	
	@Override
	public int getValues(Graphic graphic, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				returnValues[0] = graphic.x();
				returnValues[1] = graphic.y();
				return 2;
			case ROTATION_TWEEN:
				returnValues[0] = graphic.rotation();
				return 1;
			default: assert false; return -1;
		}
	}
	
	@Override
	public void setValues(Graphic graphic, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				graphic.setPosition((int) newValues[0], (int) newValues[1]);
				break;
			case ROTATION_TWEEN:
				graphic.setRotation(newValues[0]);
				break;
			default: assert false; break;
		}
	}
	
}