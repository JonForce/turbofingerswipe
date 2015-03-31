package com.jbs.swipe.gui;

import com.jbs.framework.io.InputProxy;
import com.jbs.framework.util.Updatable;

public class Scroller implements Updatable {
	
	private float
		position = 0,
		damping = .95f,
		velocity = 0f,
		magnetStrength = 0.5f,
		pageSize = 1024,
		inputScale = .15f;
	
	int page = 5;
	boolean touched = false;
	
	private final float
		lowerBound, upperBound;
	
	/** Create a Scrolling utility that has a bounded position.
	 * @param lowerBound The minimum position.
	 * @param upperBound The maximum position. */
	public Scroller(float lowerBound, float upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	/** Create a Scrolling utility with no upper or lower bounds. */
	public Scroller() {
		this(Float.MIN_VALUE, Float.MAX_VALUE);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		//page = (int) Math.round(position()/pageSize);
		
		if (input.isTouched()) {
			//addVelocity(input.getDeltaX() * inputScale*2f);
			if(!touched) {
				if(input.getDeltaX()>10) { page++; touched = true; }
				if(input.getDeltaX()<-10) { page--; touched = true; }
				page = Math.max(0,  Math.min(5, page));
			}
		} else {
			
			touched = false;
			//magnet force
			float delta = (float)(page*pageSize)-position();
			setVelocity((delta)*0.1f);
			//System.out.println(page+" "+position());
		}
		
		//physics update
		setVelocity(velocity() * damping());
		translate(velocity());
		
		//always clamp after translation
		if (position() < lowerBound()) {
			setPosition(lowerBound());
		}
		if (position() > upperBound()) {
			setPosition(upperBound());
		}
	}
	
	/** Translate the Scroller's position by the specified amount. */
	public void translate(float amount) {
		setPosition(position() + amount);
	}
	
	/** Add velocity to the Scroller. */
	public final void addVelocity(float amount) {
		setVelocity(velocity() + amount);
	}
	
	/** Set the Scroller's position. */
	public final void setPosition(float position) {
		this.position = position;
	}
	
	/** Set the velocity. */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	
	/** Set the normal to multiply the velocity by every update. */
	public void setDamping(float newDamping) {
		this.damping = newDamping;
	}
	
	/** @return the Scroller's position. */
	public float position() {
		return position;
	}
	
	/** the current page the scroller is on */
	public int page() {
		return page;
	}
	
	/** @return the scrolling velocity. */
	public float velocity() {
		return velocity;
	}
	
	/** @return the normal to multiply the velocity by every update. */
	public float damping() {
		return damping;
	}
	
	/** @return the minimum position of the Scroller. */
	public float lowerBound() {
		return this.lowerBound;
	}
	
	/** @return the maximum position of the Scroller. */
	public float upperBound() {
		return this.upperBound;
	}
}