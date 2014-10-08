package com.jbs.swipe;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;

public final class TouchManager {
	
	private ArrayList<TouchListener> touchListeners;
	private boolean[] previousTouchState;
	private final int MAX_TOUCHES;
	
	public TouchManager(int maxTouches) {
		this.MAX_TOUCHES = maxTouches;
		this.touchListeners = new ArrayList<TouchListener>();
		this.previousTouchState = new boolean[maxTouches];
	}
	
	/**
	 * Clear the TouchManager's TouchListener list.
	 */
	public void clearListeners() {
		System.out.println("List clear requested.");
		this.touchListeners.clear();
	}
	
	/**
	 * @param listener The Listener to notify of touch events.
	 */
	public void addListener(TouchListener listener) {
		System.out.println("List addition requested.");
		touchListeners.add(listener);
	}
	
	/**
	 * @param listener The Listener to cease notifying of touch events.
	 */
	public void removeListener(TouchListener listener) {
		touchListeners.remove(listener);
	}
	
	/**
	 * Update the TouchManager with the Input. May call Listener's events.
	 * @param input 
	 */
	public void update(InputProxy input) {
		System.out.println("TouchListeners : " + touchListeners.size());
		final boolean[] currentTouchState = getTouchStateOf(input);
		for (int i = 0; i != MAX_TOUCHES; i ++)
			// If input[i] is currently touched and was not previously touched,
			if (currentTouchState[i] && !previousTouchState[i])
				notifyListenersOfTouch(new Vector2(input.getX(i), input.getY(i)), i, input);
			// Else if input[i] is currently touched and was previously touched,
			else if (!currentTouchState[i] && previousTouchState[i])
				notifyListenersOfRelease(i);;
		previousTouchState = currentTouchState;
	}
	
	protected void notifyListenersOfTouch(Vector2 touchPosition, int touchID, InputProxy proxy) {
		Iterator<TouchListener> iterator = touchListeners.iterator();
		while (iterator.hasNext())
			iterator.next().onTouch(touchPosition, touchID, proxy);
	}
	
	protected void notifyListenersOfRelease(int touchID) {
		Iterator<TouchListener> iterator = touchListeners.iterator();
		while (iterator.hasNext())
			iterator.next().onRelease(touchID);
	}
	
	/**
	 * @param input The InputProxy to fetch the state of.
	 * @return An array containing the touched status of all the 
	 */
	protected boolean[] getTouchStateOf(InputProxy input) {
		boolean[] touchState = new boolean[MAX_TOUCHES];
		for (int i = 0; i != MAX_TOUCHES; i ++)
			touchState[i] = input.isTouched(i);
		return touchState;
	}
}