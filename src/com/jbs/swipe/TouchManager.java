package com.jbs.swipe;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;

public final class TouchManager {
	
	private ArrayList<TouchListener> touchListeners, touchListenersToRemove, touchListenersToAdd;
	private boolean[] previousTouchState;
	private final int MAX_TOUCHES;
	
	public TouchManager(int maxTouches) {
		this.MAX_TOUCHES = maxTouches;
		this.touchListeners = new ArrayList<TouchListener>();
		this.touchListenersToRemove = new ArrayList<TouchListener>();
		this.touchListenersToAdd = new ArrayList<TouchListener>();
		this.previousTouchState = new boolean[maxTouches];
	}
	
	/**
	 * Clear the TouchManager's TouchListener list.
	 */
	public void clearListeners() {
		this.touchListeners.clear();
	}
	
	/**
	 * @param listener The Listener to notify of touch events.
	 */
	public void addListener(TouchListener listener) {
		touchListenersToAdd.add(listener);
	}
	
	/**
	 * @param listener The Listener to cease notifying of touch events.
	 */
	public void removeListener(TouchListener listener) {
		touchListenersToRemove.add(listener);
	}
	
	/**
	 * Update the TouchManager with the Input. May call Listener's events.
	 * @param input 
	 */
	public void update(InputProxy input) {
		flushListenerQueue();
		
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
	
	private void notifyListenersOfTouch(Vector2 touchPosition, int touchID, InputProxy proxy) {
		for (TouchListener listener : touchListeners)
			listener.onTouch(touchPosition, touchID, proxy);
	}
	
	private void notifyListenersOfRelease(int touchID) {
		for (TouchListener listener : touchListeners)
			listener.onRelease(touchID);
	}
	
	private void flushListenerQueue() {
		for (TouchListener listener : this.touchListenersToAdd)
			touchListeners.add(listener);
		touchListenersToAdd.clear();
		
		for (TouchListener listener : this.touchListenersToRemove)
			touchListeners.remove(listener);
		touchListenersToRemove.clear();
	}
}