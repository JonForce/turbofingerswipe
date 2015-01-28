package com.jbs.swipe.gui.buttons;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.tiles.Direction;

public class SpinningButton implements Renderable, Updatable {
	
	public static final int
		MAX_SPINNERS = 5,
		TOP_LAYER = 1,
		BOTTOM_LAYER = -1;
	
	protected final Game game;
	protected final Button button;
	protected final Spinner[] spinners;
	
	private boolean triggered = false;
	private int numberOfSpinners = 0;
	private long startTime;
	
	/** Create a Button capable of having spinning Graphics attached to it.
	 * @param center The center of the Button in screen coordinates.
	 * @param buttonSource The path to the file that contains the Button's primary Texture. */
	public SpinningButton(Game game, Vector2 center, String buttonSource) {
		this.game = game;
		this.button = new Button(center, game.getTexture(buttonSource)) {
			@Override
			public void onRelease() {
				if (!triggered())
					trigger();
			}
		};
		this.spinners = new Spinner[MAX_SPINNERS];
		
		// Set the time that the StartButton was created.
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public void updateWith(InputProxy input) {
		button.updateWith(input);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		// Render all the Spinners that are on the bottom layer.
		for (int i = 0; i != spinners.length; i ++)
			if (spinners[i] != null && spinners[i].layer == BOTTOM_LAYER)
				spinners[i].renderTo(batch, deltaTime());
		
		// Render the Button.
		button.renderTo(batch);
		
		// Render all the Spinners that are on the top layer.
		for (int i = 0; i != spinners.length; i ++)
			if (spinners[i] != null && spinners[i].layer == TOP_LAYER)
				spinners[i].renderTo(batch, deltaTime());
	}
	
	/** Called when the Button has finished disappearing. */
	public void onTrigger() { }
	/** Called when the Button immediately after the Button is pressed. */
	public void onPress() { }
	
	/** Set the position of the Button and its Graphics. */
	public void setPosition(float x, float y) {
		for (Spinner spinner : spinners)
			if (spinner != null)
				spinner.graphic.setPosition(x, y);
		button.setPosition(x, y);
	}
	
	/** Translate the Button and all of its Graphics. */
	public void translate(float x, float y) {
		for (Spinner spinner : spinners)
			if (spinner != null)
				spinner.graphic.translate(x, y);
		button.translate(x, y);
	}
	
	/** Scale the Button and all of its Graphics. */
	public void scale(float scalar) {
		for (Spinner spinner : spinners)
			if (spinner != null)
				spinner.graphic.scale(scalar);
		button.scale(scalar);
	}
	
	/** Attach a spinning Graphic to the SpinningButton.
	 * @param source The Graphic's source.
	 * @param damping The speed at which the Graphic should rotate.
	 * @param layer Determines whether the Spinner renders above or below the Button. */
	public void addSpinner(String source, float damping, int layer) {
		if (layer != TOP_LAYER && layer != BOTTOM_LAYER)
			throw new RuntimeException("Layer not recognized.");
		
		Graphic graphic = new Graphic(new Vector2(x(), y()), game.getTexture(source));
		graphic.setScale(button.scaleX(), button.scaleY());
		spinners[numberOfSpinners++] = new Spinner(graphic, damping, layer);
	}
	
	/** Attach a spinning Graphic to the SpinningButton. The graphic is rendered
	 * above the Button.
	 * @param source The Graphic's source.
	 * @param damping The speed at which the Graphic should rotate. */
	public void addSpinnerAboveButton(String source, float damping) {
		addSpinner(source, damping, TOP_LAYER);
	}
	
	/** Attach a spinning Graphic to the SpinningButton. The graphic is rendered
	 * below the Button.
	 * @param source The Graphic's source.
	 * @param damping The speed at which the Graphic should rotate. */
	public void addSpinnerBelowButton(String source, float damping) {
		addSpinner(source, damping, BOTTOM_LAYER);
	}
	
	/** Trigger the Button, this will cause the Button to behave as if it was pressed. */
	public final void trigger() {
		// Mark the StartButton as triggered so it is not triggered twice internally.
		triggered = true;
		
		// Create a function to be called when the Button is finished sliding offscreen.
		slideOffscreen().setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (type == TweenCallback.COMPLETE)
						onTrigger();
				}
			});
		
		onPress();
	}
	
	public Tween slideOffscreen() {
		final int
			baseAnimationDuration = 500;
		
		Animator animator = new Animator(game);
		for (Spinner spinner : spinners)
			if (spinner != null)
				animator.slideGraphicOffscreen(
						baseAnimationDuration,
						Direction.RIGHT,
						spinner.graphic);
		animator.slideGraphicOffscreen(
				baseAnimationDuration,
				Direction.RIGHT,
				button);
		// Return the first Tween of the sliding animations.
		return animator.get();
	}
	
	/** @return the x-coordinate of the center of the Button. */
	public final float x() {
		return button.x();
	}
	
	/** @return the y-coordinate of the center of the Button. */
	public final float y() {
		return button.y();
	}
	
	/** @return the width of the Button. */
	public final float width() {
		float largestWidth = 0;
		for (Graphic graphic : graphics())
			if (graphic != null && graphic.width() > largestWidth)
				largestWidth = graphic.width();
		return largestWidth;
	}
	
	/** @return the height of the Button. */
	public final float height() {
		float largestHeight = 0;
		for (Graphic graphic : graphics())
			if (graphic != null && graphic.height() > largestHeight)
				largestHeight = graphic.height();
		return largestHeight;
	}
	
	/** @return the change in time since the start time (in milliseconds). */
	protected long deltaTime() {
		// Return the difference in time since the start time converted to milliseconds.
		return System.currentTimeMillis() - startTime;
	}
	
	/** @return true if the Button has been triggered. */
	protected boolean triggered() {
		return triggered;
	}
	
	/** @return all the Graphics that compose the Button. */
	protected final Graphic[] graphics() {
		// An array large enough to contain all of the Graphics.
		Graphic[] graphics = new Graphic[numberOfSpinners + 1];
		// Add all the Spinners' Graphics.
		for (int i = 0; i != numberOfSpinners; i ++)
			graphics[i] = spinners[i].graphic;
		// The final graphic is the Button itself.
		graphics[numberOfSpinners] = button;
		return graphics;
	}
}

/** A container class to be used to aid the SpinningButton in keeping track of it's Spinners. */
class Spinner {
	
	public final Graphic graphic;
	public final float damping;
	public final int layer;
	
	public Spinner(Graphic graphic, float damping, int layer) {
		this.graphic = graphic;
		this.damping = damping;
		this.layer = layer;
	}
	
	/** Render the Spinner to the Batch after changing its rotation according
	 * to the current time. */
	public final void renderTo(SpriteBatch batch, float deltaTime) {
		graphic.setRotation(deltaTime * damping);
		graphic.renderTo(batch);
	}
}