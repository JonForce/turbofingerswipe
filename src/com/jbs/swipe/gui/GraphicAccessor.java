package com.jbs.swipe.gui;

import aurelienribon.tweenengine.TweenAccessor;

import com.jbs.framework.rendering.Graphic;

public class GraphicAccessor implements TweenAccessor<Graphic> {
	
	public static final int
		POSITION_TWEEN = 0;
	
	@Override
	public int getValues(Graphic button, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				returnValues[0] = button.x();
				returnValues[1] = button.y();
				return 2;
			default: assert false; return -1;
		}
	}
	
	@Override
	public void setValues(Graphic button, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_TWEEN:
				button.setPosition((int) newValues[0], (int) newValues[1]);
			default: assert false; break;
		}
	}
	
}