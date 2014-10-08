package com.jbs.swipe;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;

public interface TouchListener {
	
	/**
	 * Be notified of a touch.
	 * @param touchPosition The position of the touch.
	 * @param touchID The ID of the touch.
	 */
	void onTouch(Vector2 touchPosition, int touchID, InputProxy input);
	
	/**
	 * @param touchID The ID of the touch that was released.
	 */
	void onRelease(int touchID);
	
}