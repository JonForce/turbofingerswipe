package com.jbs.swipe.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;

public class Explosion implements Renderable, Updatable {
	
	private static final String
		SOURCE = "assets/ParticleEffects/Explosion.fx",
		IMAGES = "assets/ParticleEffects/Images";
	
	protected final ParticleEffect particleEffect;
	
	private final float
		DEFAULT_UPDATE_DELTA = 0.1f;
	
	private boolean
		/** True if we are exploding. */
		exploding = false;
	/** The amount to update the particle effect each update. */
	private float
		updateDelta = DEFAULT_UPDATE_DELTA;
	
	/** Create an explosion.
	 * @param source The Explosion's particle effect file.
	 * @param autoStart True if the explosion should begin immediately. */
	public Explosion(String source) {
		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal(source), Gdx.files.internal(IMAGES));
		particleEffect.start();
	}
	
	/** Create and automatically start an explosion. */
	public Explosion() {
		this(SOURCE);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		if (exploding())
			particleEffect.draw(batch);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		particleEffect.update(updateDelta);
		
		if (particleEffect.isComplete())
			exploding = false;
	}
	
	public float duration() {
		return particleEffect.getEmitters().get(0).duration;
	}
	
	/** @return the x-component of the position of the Explosion. */
	public float x() {
		return particleEffect.getEmitters().get(0).getX();
	}
	
	/** @return the y-component of the position of the Explosion. */
	public float y() {
		return particleEffect.getEmitters().get(0).getY();
	}
	
	/** @return true if the Explosion is exploding. */
	public boolean exploding() {
		return exploding;
	}
	
	/** Reset the Explosion. Does not reset the Explosion's position */
	public Explosion reset() {
		exploding = false;
		return this;
	}
	
	/** Begin the explosion. */
	public Explosion explode() {
		exploding = true;
		particleEffect.reset();
		return this;
	}
	
	/** Explode at the specified position. */
	public Explosion explode(float x, float y) {
		setPosition(x, y);
		explode();
		return this;
	}
	
	/** Scale the rate at which to update the Explosion.
	 * @param speed The normalized value representing the rate at which
	 * to update the Explosion. */
	public Explosion scaleUpdateSpeed(float speed) {
		this.updateDelta = speed * DEFAULT_UPDATE_DELTA;
		return this;
	}
	
	/** Set the position of the Explosion. */
	public Explosion setPosition(float x, float y) {
		particleEffect.setPosition(x, y);
		return this;
	}
}