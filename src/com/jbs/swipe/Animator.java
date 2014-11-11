package com.jbs.swipe;

import java.util.ArrayList;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.gui.GraphicAccessor;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.TileAccessor;

public class Animator {
	
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
	 * @param time The duration of the animation.
	 * @param tile The Tile to shrink.
	 */
	public Animator shrinkTile(float time, SwipeTile tile) {
		tweens.add(
				Tween.to(tile, TileAccessor.SCALE_TWEEN, time)
					.ease(Quad.IN)
					.target(0, 0)
					.start(game.tweenManager())
				);
		return this;
	}
	
	/**
	 * Rotate the specified Graphic FOREVER.
	 * @param target The Graphic to rotate FOREVER.
	 * @param speed The speed at which to rotate.
	 */
	public Animator rotateGraphicIndefinitely(Graphic target, float speed) {
		tweens.add(
				Tween.to(target, GraphicAccessor.ROTATION_TWEEN, speed)
					.target(360f)
					.ease(Linear.INOUT)
					.repeat(-1, 0)
					.start(game.tweenManager())
				);
		return this;
	}
	
	/**
	 * Create an animation sequence that shakes the Tile with the specified amplitude in the desired
	 * direction.
	 * @param tile The Tile to apply a shaking animation to.
	 * @param direction The direction to shake the Tile in.
	 * @param duration The duration of the animation (In Milliseconds).
	 * @param shakes The number of times to shake the Tile.
	 */
	public Animator shakeTile(SwipeTile tile, Vector2 direction, float duration, int shakes) {
		final float shakeDuration = duration/shakes;
		
		Timeline.createSequence()
			.push(Tween.to(tile, TileAccessor.POSITION_TWEEN, shakeDuration)
				.ease(Quad.OUT)
				.targetRelative(direction.x, direction.y))
			.push(Tween.to(tile, TileAccessor.POSITION_TWEEN, shakeDuration)
				.ease(Quad.OUT)
				.targetRelative(-direction.x, -direction.y))
			.repeat(shakes, 0f)
			.start(game.tweenManager());
		return this;
	}
	
	/**
	 * Animate the Tile to be swiped away.
	 * @param tile The Tile to animate.
	 * @param direction The direction to swipe the Tile.
	 * @param time The time the animation should take from start to finish in milliseconds.
	 */
	public Animator swipeTileAway(SwipeTile tile, Vector2 direction, float time) {
		tweens.add(Tween.to(tile, TileAccessor.POSITION_TWEEN, time)
			.targetRelative(direction.x, direction.y)
			.ease(Quad.OUT)
			.start(game.tweenManager()));
		return this;
	}
	
	/**
	 * Create a opacity Tween to fade the Tile to 0% opacity.
	 * @param tile The target of the animation.
	 * @param time The duration of the animation in milliseconds.
	 * @return the animation.
	 */
	public Animator fadeTileAway(SwipeTile tile, float time) {
		tweens.add(Tween.to(tile, TileAccessor.OPACITY_TWEEN, time)
			.target(0)
			.ease(Linear.INOUT)
			.start(game.tweenManager()));
		return this;
	}
	
	/**
	 * @param tween The index of the Tween to retrieve.
	 * @return The n'th Tween generated by the Animator.
	 */
	public Tween getTween(int tween) {
		return this.tweens.get(tween);
	}
	
	/**
	 * @return The most recent Tween added to the Animator.
	 */
	public Tween get() {
		return this.tweens.get(tweens.size() - 1);
	}
}