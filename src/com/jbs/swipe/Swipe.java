package com.jbs.swipe;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;

public class Swipe {
	
	private Vector2 origin;
	private InputProxy input;
	private boolean expired = false, comboSwipe;
	private float maxMagnitude;
	private final int swipeID;
	
	public Swipe(InputProxy input, int swipeID, boolean comboSwipe, float maxMagnitude) {
		//Really? do you really like crashing the game for no reason?
		//if (!input.isTouched())
		//	throw new RuntimeException("Input is not touched, Swipe cannot be constructed.");
		//if (maxMagnitude <= 0)
		//	throw new RuntimeException("maxMagnitude is <= 0, Swipe cannot be constructed.");
		
		this.input = input;
		this.swipeID = swipeID;
		this.comboSwipe = comboSwipe;
		this.origin = positionOf(input);
		this.maxMagnitude = maxMagnitude;
	}
	
	/**
	 * Expire the swipe if the input is no longer touched.
	 */
	public void updateExpiration() {
		if ((!input.isTouched(swipeID) || magnitude() >= maxMagnitude) && !expired())
			expire();
	}
	
	/**
	 * Invalidate the swipe.
	 */
	public final void expire() {
		expired = true;
		onExpire();
	}
	
	public void onExpire() { }
	
	/**
	 * @return true if the Swipe has expired.
	 */
	public boolean expired() {
		return expired;
	}
	
	/**
	 * @return true if the Swipe's angle is equal to the required angle with
	 * a tolerance of 'toleranceDegrees'. Check is inclusive.
	 */
	public boolean checkAngle(float requiredAngle, float toleranceDegrees) {
		// The minimum angle of the swipe required to return true.
		float minimumAngle = requiredAngle - toleranceDegrees;
		// The maximum angle of the swipe required to return true;
		float maximumAngle = requiredAngle + toleranceDegrees;
		float angle = swipeAngle();
		if (angle > requiredAngle + 180)
			angle -= (requiredAngle + 360);
		// Return true when the swipe's angle is greater than the minimum angle
		// and less than the maximum angle (Inclusively).
		return angle >= minimumAngle && angle <= maximumAngle;
	}
	
	/**
	 * @return the angle of the swipe relative to the positive x-axis
	 * (Counterclockwise) in degrees.
	 */
	public float swipeAngle() {
		return asVector().angle();
	}
	
	/**
	 * @return the Swipe's magnitude.
	 */
	public float magnitude() {
		return origin.dst(inputPosition());
	}
	
	/**
	 * @return the Swipe's origin, or start.
	 */
	public Vector2 origin() {
		return origin;
	}
	
	/** @return true if this is a combo swipe. */
	public boolean isComboSwipe() {
		return this.comboSwipe;
	}
	
	/**
	 * @return the position of the Swipe's input.
	 */
	public Vector2 inputPosition() {
		return positionOf(input);
	}
	
	protected final Vector2 asVector() {
		return differenceOf(origin, positionOf(input));
	}
	
	/**
	 * @return a new Vector2 constructed with the input's x and y screen coordinates.
	 */
	protected final Vector2 positionOf(InputProxy input) {
		return new Vector2(input.getX(swipeID), input.getY(swipeID));
	}
	
	/**
	 * @return vectorB - vectorA
	 */
	protected final Vector2 differenceOf(Vector2 vectorA, Vector2 vectorB) {
		return new Vector2(vectorB.x - vectorA.x, vectorB.y - vectorA.y);
	}
}