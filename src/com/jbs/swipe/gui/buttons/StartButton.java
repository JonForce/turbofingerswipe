package com.jbs.swipe.gui.buttons;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Animator;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Direction;

public class StartButton implements Renderable {
	
	public final String
		PLAY_BUTTON_SOURCE = "assets/GUI/Play/Button.png",
		BACKLIGHT_SOURCE = "assets/GUI/Play/Light.png",
		TEXT_SOURCE = "assets/GUI/Play/Text.png";
	
	// Determines the speed of rotation.
	private final float
		backlightDamping = .1f,
		textDamping = .05f;
	
	private final Game game;
	
	private Button startButton;
	private Graphic backlight, text;
	private long startTime;
	private boolean triggered = false;
	
	public StartButton(Game game, Vector2 center, float scale) {
		this.game = game;
		startButton = new Button(center, game.getTexture(PLAY_BUTTON_SOURCE)) {
			@Override
			public void onRelease() {
				if (!triggered())
					trigger();
			}
		};
		backlight = new Graphic(center, game.getTexture(BACKLIGHT_SOURCE));
		text = new Graphic(center, game.getTexture(TEXT_SOURCE));
		
		// Scale the StartButton's graphic components to the specified scale.
		startButton.scale(scale);
		backlight.scale(scale);
		text.scale(scale);
		
		// Set the time that the StartButton was created.
		startTime = System.currentTimeMillis();
	}
	
	public void onTrigger() { }
	public void onPress() { }
	
	/*
	 * Checks if the button has been pressed and can subsequently call trigger().
	 */
	public void updateWith(InputProxy input) {
		startButton.updateWith(input);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		// Interpolate the backlight's rotation with the deltaTime and damping value.
		backlight.setRotation(deltaTime() * backlightDamping);
		// Interpolate the text's rotation with the deltaTime and damping value.
		text.setRotation(deltaTime() * textDamping);
		
		backlight.renderTo(batch);
		startButton.renderTo(batch);
		text.renderTo(batch);
	}
	
	/* Translate all of the StartButton's components by (x, y). */
	public void translate(int x, int y) {
		backlight.translate(x, y);
		startButton.translate(x, y);
		text.translate(x, y);
	}
	
	/* Mark the StartButton as triggered and call the StartButton's onTrigger() event. */
	public final void trigger() {
		// Mark the StartButton as triggered so it is not triggered twice internally.
		triggered = true;
		
		final int
			baseAnimationDuration = 500,
			animationDurationVariation = 500;
		new Animator(game)
			.slideGraphicsOffscreen(
					baseAnimationDuration, // The base animation duration.
					animationDurationVariation, // The potential variance in animation duration between the animation targets.
					Direction.RIGHT, // The direction to animate in.
					startButton, text, backlight) // The Graphics to animate.
			.getTween(0).setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (type == TweenCallback.COMPLETE)
						onTrigger();
				}
			});
		
		onPress();
	}
	
	/* @return the width of the StartButton. */
	public final int width() {
		// The backlight is the largest part of the StartButton, therefore the width of the StartButton
		// is the width of the backlight Graphic.
		return backlight.width();
	}
	
	/* @return the height of the StartButton. */
	public final int height() {
		// The backlight is the largest part of the StartButton, therefore the height of the StartButton
		// is the height of the backlight Graphic.
		return backlight.height();
	}
	
	/*
	 * @return the time since the construction of StartButton.
	 */
	protected long deltaTime() {
		// Return the difference in time since the start time converted to milliseconds.
		return System.currentTimeMillis() - startTime;
	}
	
	/* @return true if the StartButton has been triggered. */
	private boolean triggered() {
		return triggered;
	}
}