package com.jbs.swipe.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;

public abstract class SmallFlame implements Renderable, Updatable {
	
	private static final String
		SOURCE = "assets/ParticleEffects/SmallFlame.fx",
		IMAGES = "assets/ParticleEffects/Images";
	
	private ParticleEffect effect;
	
	/** True when the Flame is lit. */
	private boolean ignited = false;
	
	/** The time when the Flame began spawning particles. */
	private long startTime;
	
	/** The amount to update the particle effect each update. */
	private float updateDelta = .1f;
	
	public SmallFlame() {
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal(SOURCE), Gdx.files.internal(IMAGES));
		effect.start();
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		if (ignited()) {
			effect.setPosition(x(), y());
			effect.draw(batch);
		}
	}
	
	@Override
	public void updateWith(InputProxy input) {
		effect.update(updateDelta);
	}
	
	/** Extinguish the Flame. */
	public final void extinguish() {
		ignited = false;
	}
	
	/** Light the Flame. */
	public final void ignite() {
		ignited = true;
		startTime = System.currentTimeMillis();
	}
	
	/** @return true if the Flame is lit. */
	public final boolean ignited() {
		return ignited;
	}
	
	protected abstract float x();
	protected abstract float y();
	
	/** @return the number of seconds since the object was initialized. */
	protected final float deltaTime() {
		return (System.currentTimeMillis() - startTime) / 1000f;
	}
	
	/** @return the duration of the Effect in seconds. */
	protected final float duration() {
		return effect.getEmitters().get(0).duration / 1000f;
	}
}