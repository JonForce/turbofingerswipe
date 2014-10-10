package com.jbs.swipe.gui;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Game;

public class Animator {
	
	public static enum Direction { UP, DOWN, LEFT, RIGHT }
	
	private final Game game;
	private final ArrayList<Tween> tweens;
	
	/**
	 * Create an animation utility.
	 */
	public Animator(Game game) {
		this.game = game;
		this.tweens = new ArrayList<Tween>();
	}
	
	/**
	 * @param time The duration of the sliding animation.
	 * @param direction The Direction of the animation.
	 * @param graphic The Graphic to animate.
	 */
	public Animator slideGraphicOffscreen(float time, Direction direction, Graphic graphic) {
		final Vector2 translation;
		if (direction == Direction.RIGHT)
			translation = new Vector2(game.screenWidth(), 0);
		else if (direction == Direction.UP)
			translation = new Vector2(0, game.screenHeight());
		else if (direction == Direction.LEFT)
			translation = new Vector2(-game.screenWidth(), 0);
		else
			translation = new Vector2(0, -game.screenHeight());
		
		tweens.add(Tween.to(graphic, GraphicAccessor.POSITION_TWEEN, time)
				.target(graphic.x() + translation.x, graphic.y() + translation.y)
				.ease(Quad.OUT)
				.start(game.tweenManager()));
		return this;
	}
	
	/**
	 * @param time The duration of the sliding animations.
	 * @param timeVariation The variation in animation duration between the Graphics.
	 * @param direction The direction to slide the Graphics.
	 * @param graphics The Graphics to be animated.
	 */
	public Animator slideGraphicsOffscreen(float time, int timeVariation, Direction direction, Graphic... graphics) {
		for (Graphic graphic : graphics)
			slideGraphicOffscreen(time - (timeVariation/2) + game.random().nextInt(timeVariation), direction, graphic);
		return this;
	}
	
	/**
	 * @param time The duration of the sliding animation.
	 * @param direction The direction to slide the Graphics.
	 * @param graphics The Graphics to be animated.
	 */
	public Animator slideGraphicsOffscreen(float time, Direction direction, Graphic... graphics) {
		slideGraphicsOffscreen(time, 0, direction, graphics);
		return this;
	}
	
	/**
	 * @param tween The index of the Tween to retrieve.
	 * @return The n'th Tween generated by the Animator.
	 */
	public Tween getTween(int tween) {
		return this.tweens.get(tween);
	}
}