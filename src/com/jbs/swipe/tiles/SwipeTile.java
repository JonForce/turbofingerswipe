package com.jbs.swipe.tiles;

import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.Swipe;
import com.jbs.swipe.effects.Animator;

public class SwipeTile implements Renderable {
	
	public final String
		INCORRECT_SWIPE_SOUND_SOURCE = "assets/SFX/Incorrect.wav",
		CORRECT_SWIPE_SOUND_SOURCE = "assets/SFX/Correct.wav",
		BLUE_TILE_SOURCE = "3second",
		YELLOW_TILE_SOURCE = "2second",
		ORANGE_TILE_SOURCE = "1second",
		RED_TILE_SOURCE = "halfsecond";
	
	public static final float
		DEFAULT_SWIPE_MAGNITUDE = 40f, // The default required distance of a correct swipe.
		DEFAULT_SCALE = .5f, // The default scale of the SwipeTile.
		MAXIMUM_GREEN_TIME = 500f; // The maximum time the SwipeTile will remain green before triggering the onCorrectSwipe() event.
	
	private static float
		defaultVolume = .5f; // The default volume to play the SwipeTile's Sounds at.
	
	public static enum TileState {
		BLUE, YELLOW, ORANGE, RED, CORRECTLY_SWIPED, INCORRECTLY_SWIPED, EXPIRED, FINISHED
	}
	
	protected TileState defaultTileState = TileState.BLUE;
	// The Stages for the SwipeTile to progress through.
	protected TileState[] lifecycle = new TileState[] {
			TileState.BLUE,
			TileState.YELLOW,
			TileState.ORANGE,
			TileState.RED,
			TileState.EXPIRED
		};
	
	private Graphic tile, arrow;
	private TextureRegion arrowGreen, arrowGray;
	
	private Vector2
		originalTileSize, // The width and height of the Tile graphic initially.
		center, // The center of the SwipeTile.
		target; // The position to translate the SwipeTile to.
	
	private float
		opacity = 1f,
		translationDamping, // The damping value to use when translating the SwipeTile to the target position.
		volume = defaultVolume, // The volume to play the SwipeTile's sounds at.
		startTime, // The nano time that the SwipeTile was constructed
		timeToSwipe, // The time until the SwipeTile expires
		requiredSwipeDirection, // The required swipe direction in degrees
		swipeAngleTolerance = 70, // The tolerated inaccuracy for a swipe
		requiredSwipeMagnitude; // The required swipe magnitude;
	
	private Swipe currentSwipe;
	private int maxInputs = 2;
	private boolean[] storedTouchStates;
	
	private Game game;
	private TileState tileState;
	private TileListener listener;
	
	public SwipeTile(final Game game, Vector2 swipeRequirement, TextureRegion arrowGreen, TextureRegion arrowGray, float timeToSwipe) {
		this.game = game;
		this.arrowGreen = arrowGreen;
		this.arrowGray = arrowGray;
		this.tileState = defaultTileState;
		
		this.storedTouchStates = new boolean[maxInputs];
		
		// Default center of the SwipeTile is the center of the Game's Screen.
		this.center = game.screenCenter();
		
		final SwipeTile swipeTile = this;
		this.tile = new Graphic(new Vector2(x(), y()), Assets.getAtlasRegion(BLUE_TILE_SOURCE)) {
			// Store the last Texture returned by the texture() method.
			TextureRegion lastTextureUsed;
			// Texture to use if all else fails.
			TextureRegion defaultTexture = Assets.getAtlasRegion(BLUE_TILE_SOURCE);
			
			// The center of the tile Graphic is the center of the SwipeTile.
			@Override
			public float x() {
				return swipeTile.x();
			}
			@Override
			public float y() {
				return swipeTile.y();
			}
			@Override
			public TextureRegion texture() {
				final TextureRegion textureToBeUsed;
				if (tileState() == TileState.BLUE)
					textureToBeUsed = Assets.getAtlasRegion(BLUE_TILE_SOURCE);
				else if (tileState() == TileState.YELLOW)
					textureToBeUsed = Assets.getAtlasRegion(YELLOW_TILE_SOURCE);
				else if (tileState() == TileState.ORANGE)
					textureToBeUsed = Assets.getAtlasRegion(ORANGE_TILE_SOURCE);
				else if (tileState() == TileState.RED)
					textureToBeUsed = Assets.getAtlasRegion(RED_TILE_SOURCE);
				// If the correct Texture to use cannot be determined, use the last Texture used.
				else
					if (lastTextureUsed == null)
						textureToBeUsed = defaultTexture;
					else
						textureToBeUsed = lastTextureUsed;
				
				this.lastTextureUsed = textureToBeUsed;
				return textureToBeUsed;
			}
			
			@Override
			public void setTexture(TextureRegion newTexture) {
				throw new RuntimeException("Cannot set the Texture of the SwipeTile's tile. Change the SwipeTile's Texture by changing it's state.");
			}
			
		};
		this.arrow = new Graphic(new Vector2(x(), y()), arrowGray) {
			// The center of the arrow is the center of the SwipeTile.
			@Override
			public float x() {
				return swipeTile.x();
			}
			@Override
			public float y() {
				return swipeTile.y();
			}
		};
		
		this.setAngle(swipeRequirement.angle());
		this.timeToSwipe = timeToSwipe;
		
		this.scale(DEFAULT_SCALE);
		
		startTime = System.nanoTime();
		
		originalTileSize = new Vector2(tile.height(), tile.height());
	}
	
	public SwipeTile(Game game, float swipeTime, Direction direction) {
		this(
				game,
				SwipeTile.createSwipe(direction, DEFAULT_SWIPE_MAGNITUDE),
				SwipeTile.getArrow(game, direction, true),
				SwipeTile.getArrow(game, direction, false),
				swipeTime);
	}
	
	public SwipeTile(Game game, float swipeTime) {
		this(game, swipeTime, randomDirection());
	}
	
	/**
	 * Pretty much the only method you need to call to set the arrows angle and requirement
	 * note: we don't have to super impose silly restrictions on the arrow directions.
	 * @param angle in degrees
	 */
	void setAngle(float angle) {
		arrow.setRotation(angle);
		requiredSwipeDirection = angle;
		requiredSwipeMagnitude = DEFAULT_SWIPE_MAGNITUDE;
	}

	@Override
	public void renderTo(SpriteBatch batch) {
		Color oldBatchColor = batch.getColor();
		batch.setColor(oldBatchColor.r, oldBatchColor.g, oldBatchColor.b, this.opacity());
		
		// First render the background tile to the batch.
		tile.renderTo(batch);
		
		// Next, render the arrow to the batch.
		arrow.renderTo(batch);
		
		batch.setColor(oldBatchColor);
	}
	
	public void updateWith(InputProxy input) {
		// Update the SwipeTile's translation animation.
		updateTranslationAnimation();

		if (!this.isCorrectlySwiped()) {
			// Set the State of the SwipeTile to be the current stage in it's lifecycle.
			if (tileState != lifecycle[lifecycleStage()] && tileState != TileState.INCORRECTLY_SWIPED)
				setState(lifecycle[lifecycleStage()]);
			
			if (currentSwipe == null) {
				for (int i = 0; i != 2; i ++) {
					if (this.contains(positionOf(input, i)) && input.isTouched(i))
						currentSwipe = new Swipe(input, i, !input.justTouched(), requiredSwipeMagnitude) {
							@Override
							public void onExpire() {
								if (currentSwipe.checkAngle(requiredSwipeDirection, swipeAngleTolerance)) {
									if (canChangeStateTo(TileState.CORRECTLY_SWIPED))
										// Set the SwipeTile to it's correctly-swiped State.
										setState(TileState.CORRECTLY_SWIPED);
								} else if (!currentSwipe.isComboSwipe()) {
									if (canChangeStateTo(TileState.INCORRECTLY_SWIPED))
										// Enter the Incorrectly swiped State.
										setState(TileState.INCORRECTLY_SWIPED);
								}
								currentSwipe = null;
							}
						};
				}
			} else
				currentSwipe.updateExpiration();
		}
		
		for (int i = 0; i != maxInputs; i ++)
			storedTouchStates[i] = input.isTouched(i);
	}
	
	/** Reset the SwipeTile to it's state when it was constructed (Excluding the volume). */
	public void reset() {
		// Reset the SwipeTile's state data.
		startTime = System.nanoTime();
		// Reset the Tile's State.
		tileState = defaultTileState;
		// Reset the opacity
		opacity = 1;
		// Refresh the arrow's Texture.
		refreshArrow();
	}
	
	/** Set the position to translate the SwipeTile to. */
	public void setTranslationTarget(float targetX, float targetY, float damping) {
		if (this.target == null)
			target = new Vector2();
		
		this.translationDamping = damping;
		this.target.set(targetX, targetY);
	}
	public final void setTranslationTarget(Vector2 target, float damping) {
		if (target == null)
			this.target = null;
		else
			setTranslationTarget(target.x, target.y, damping);
	}
	
	/* Set the volume to play the SwipeTile's Sounds at. */
	public void setVolume(float newVolume) {
		this.volume = newVolume;
	}
	
	/** Reset the SwipeTile's Volume to it's default value. */
	public void resetVolume() {
		this.volume = defaultVolume;
	}
	
	/** Translate the SwipeTile by the specified amount. */
	public void translate(float amountX, float amountY) {
		this.center.add(amountX, amountY);
	}
	
	/** Set the opacity to render the Tile at. */
	public void setOpacity(float newOpacity) {
		this.opacity = newOpacity;
	}
	
	/** Scale the SwipeTile's arrow and background by the specified scalar. */
	public void scale(float scalarX, float scalarY) {
		tile.scale(scalarX, scalarY);
		arrow.scale(scalarX, scalarY);
	}
	
	/**
	 * Scale the SwipeTile's arrow and background around it's center.
	 */
	public void scale(float scalar) {
		this.scale(scalar, scalar);
	}
	
	/**
	 * Scale the SwipeTile's arrow around it's center.
	 */
	public void scaleArrow(float scalar) {
		arrow.scale(scalar);
	}
	
	/** Set the SwipeListener to notify of SwipeTile events. */
	public void setSwipeListener(TileListener newListener) {
		this.listener = newListener;
	}
	
	/** Set the SwipeTile's arrow textures and swipe requirements to suite the
	 * specified direction. */
	public void setDirection(Direction direction) {
		// Set the arrow textures to the new direction's corresponding arrow textures.
		this.arrowGreen = SwipeTile.getArrow(game, direction, true);
		this.arrowGray = SwipeTile.getArrow(game, direction, false);
		// Set the required swipe direction to the new direction.
		setSwipeRequirement(SwipeTile.createSwipe(direction, requiredSwipeMagnitude));
		// Refresh the arrow so it uses the new Textures.
		refreshArrow();
	}
	
	/** Set the required swipe direction and magnitude. */
	public void setSwipeRequirement(Vector2 newRequirement) {
		this.requiredSwipeDirection = newRequirement.angle();
		this.requiredSwipeMagnitude = newRequirement.len();
	}
	
	/** Set the SwipeTile to use a different direction than it currently is using. */
	public void changeDirection() {
		// Define the direction we want to change from as our current direction.
		final float oldDirection = this.requiredSwipeDirection;
		// While our swipe direction is the direction we want to change from,
		while (this.requiredSwipeDirection == oldDirection)
			// Set our SwipeTile's direction to a random direction.
			setDirection(SwipeTile.randomDirection());
	}
	
	/** @return the tolerated deviation in the swipe's direction from the required
	 * swipe direction in degrees. */
	public float swipeInaccuracyTolerance() {
		return swipeAngleTolerance;
	}
	
	/**
	 * @return the opacity to render at.
	 */
	public float opacity() {
		return this.opacity;
	}
	
	/** @return the volume the SwipeTile will play it's Sounds at. */
	public float volume() {
		return this.volume;
	}
	
	/** @return the percent that the SwipeTile is expired in decimal form. */
	public float percentExpired() {
		return deltaTime() / timeToSwipe;
	}
	
	/** @return true when the SwipeTile's arrow is green. */
	public boolean isCorrectlySwiped() {
		return this.tileState() == TileState.CORRECTLY_SWIPED;
	}
	
	/**
	 * @return true if the SwipeTile has been created for longer
	 * then the maximum time to swipe the tile and has not been correctly swiped.
	 */
	public boolean expired() {
		return this.tileState() == TileState.EXPIRED;
	}
	
	/** @return true if the point is within the SwipeTile's bounds. */
	public boolean contains(Vector2 point) {
		return boundingBox().contains(point.x, point.y);
	}
	
	/** @return the listener of this Tile's events. */
	public final TileListener swipeListener() {
		return this.listener;
	}
	
	/**
	 * @return The Direction that the SwipeTile faces, throws RuntimeException if the Tile's required swipe
	 * direction isnt a multiple of 90 degrees.
	 */
	public final Direction direction() {
		if (this.requiredSwipeDirection % 360 == 0)
			return Direction.RIGHT;
		else if (this.requiredSwipeDirection % 360 == 90)
			return Direction.UP;
		else if (this.requiredSwipeDirection % 360 == 180)
			return Direction.LEFT;
		else if (this.requiredSwipeDirection % 360 == 270)
			return Direction.DOWN;
		else
			throw new RuntimeException("Tile's direction is not a multiple of 90 degrees : " + (this.requiredSwipeDirection % 360) );
	}
	
	/**
	 * Scale the SwipeTile to the specified scalar.
	 * @param scalarX The x-Component of the new scale.
	 * @param scalarY The y-Component of the new scale.
	 */
	public final void setScale(float scalarX, float scalarY) {
		final Vector2 oldScale = this.scale();
		this.scale(scalarX/oldScale.x, scalarY/oldScale.y);
	}
	
	/** Set the rotation of the SwipeTile.
	 * @param newRotation The new rotation in degrees. */
	public final void setRotation(float newRotation) {
		tile.setRotation(newRotation);
		arrow.setRotation(newRotation);
	}
	
	/** Translate the tile so it's center is at the specified coordinates. */
	public final void setPosition(float x, float y) {
		translate(x - this.x(), y - this.y());
	}
	
	/** Translate the tile so it's center is at the specified coordinates. */
	public final void setPosition(Vector2 newPosition) {
		setPosition(newPosition.x, newPosition.y);
	}
	
	/** Update the SwipeTile's translation animation. */
	public void updateTranslationAnimation() {
		if (target == null)
			return;
		
		final float
			// Declare the distance to the target position.
			deltaX = target.x - this.x(),
			deltaY = target.y - this.y(),
			// Declare the new coordinates for the SwipeTile to use.
			newX = this.x() + (deltaX * translationDamping),
			newY = this.y() + (deltaY * translationDamping);
		
		// Set the SwipeTile to the new-coordinates.
		setPosition(newX, newY);
	}
	
	/** Add to the total amount of time that the Tile was paused for. This will delay
	 * expiration of the Tile.
	 * @param timePaused the amount of time the Tile was paused in milliseconds. */
	public final void addPausedTime(float timePaused) {
		startTime += timePaused * 1E6f;
	}
	
	/** @return the x-Coordinate of the center of the SwipeTile. */
	public final float x() {
		return center.x;
	}
	
	/** @return the y-Coordinate of the center of the SwipeTile. */
	public final float y() {
		return center.y;
	}
	
	/** @return the rotation of the SwipeTile in degrees. */
	public final float rotation() {
		// Throw an exception if we encounter unexpected behavior in STRICT mode.
		//who cares if the tiles rotation is not the same as the back button.. the back button doesn't rotate
		//so this would always be true
		//if (game.IS_STRICT && tile.rotation() != arrow.rotation())
		//	throw new RuntimeException("Tile's rotation != arrow's rotation!");
		
		return this.tile.rotation();
	}
	
	/**
	 * @return The scale of the Tile relative to it's initial size.
	 */
	public final Vector2 scale() {
		return new Vector2(tile.width() / originalTileSize.x, tile.height() / originalTileSize.y);
	}
	
	/** @return the time the SwipeTile's arrow should remain green before
	 * triggering the onCorrectSwipe() event. */
	public final float arrowGreenTime() {
		return Math.min(timeUntilExpiration(), MAXIMUM_GREEN_TIME);
	}
	
	/** @return true if the Tile can be set to the new state without conflict. */
	public boolean canChangeStateTo(TileState newState) {
		return !((tileState == TileState.CORRECTLY_SWIPED && newState != TileState.FINISHED) ||
				(tileState == TileState.INCORRECTLY_SWIPED && newState != TileState.FINISHED) ||
				tileState == newState);
	}
	
	/** Set the State of the SwipeTile.
	 * @param react Set to true if the Tile should react to its change in state. */
	public void setState(TileState newState, boolean react) {
		if (tileState == newState) {
			System.err.println("Cannot set a Tile to the state it is already in : " + newState);
			return;
		}
		if (!canChangeStateTo(newState)){
			System.err.println("Cannot set a Tile to " + newState + " when it is in it's " + tileState + " state.");
			return;
		}
		TileState oldState = this.tileState;
		this.tileState = newState;
		refreshArrow();
		
		if (react)
			if (newState == TileState.CORRECTLY_SWIPED) {
				// Play the correct-swipe sound.
				game.audio().playSound(Gdx.files.internal(CORRECT_SWIPE_SOUND_SOURCE), volume);
				
				new Animator(game)
					.shrinkTile(this, arrowGreenTime())
					.getTween(0).setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							if (type == TweenCallback.COMPLETE)
								if (listener != null)
									setState(TileState.FINISHED);
						}
					});
			} else if (newState == TileState.INCORRECTLY_SWIPED) {
				// Play the incorrect-swipe sound.
				game.audio().playSound(Gdx.files.internal(INCORRECT_SWIPE_SOUND_SOURCE), volume);
				
			}
		
		if (this.listener != null)
			this.listener.recieveTileStateChange(this, oldState, newState);
	}
	/** Set the State of the SwipeTile.*/
	public void setState(TileState newState) {
		this.setState(newState, true);
	}
	
	/** @return the State of the SwipeTile. */
	public TileState tileState() {
		return this.tileState;
	}
	
	/** @return the Rectangle that surrounds the SwipeTile. */
	protected Rectangle boundingBox() {
		Vector2 bottomLeft = new Vector2(this.x() - tile.width()/2, this.y() - tile.height()/2);
		return new Rectangle(bottomLeft.x, bottomLeft.y, tile.width(), tile.height());
	}
	
	/** Refresh the SwipeTile's arrow's Texture. */
	protected void refreshArrow() {
		if (this.isCorrectlySwiped())
			arrow.setTexture(arrowGreen);
		else
			arrow.setTexture(arrowGray);
	}
	
	/** @return the stage of the lifecycle that the SwipeTile should use if it is in
	 * its normal lifecycle. */
	protected int lifecycleStage() {
		// Define the stage the Tile should be at.
		final float uncappedStage = this.percentExpired() * this.lifecycle.length;
		// Cap the stage at the maximum lifecycle index to prevent a OutOfBoundsException.
		final float cappedStage = Math.min(this.lifecycle.length - 1, uncappedStage);
		
		return (int) (cappedStage);
	}
	
	/**
	 * @return the time since the construction of StartButton.
	 */
	protected final float deltaTime() {
		// Return the difference in time since the start time converted to milliseconds.
		return (System.nanoTime() - startTime) * 1E-6f;
	}
	
	/** @return the time when the SwipeTile will expire. */
	protected final float expirationTime() {
		return startTime + timeToSwipe;
	}
	
	/** @return the time until the SwipeTile expires. */
	protected final float timeUntilExpiration() {
		return expirationTime() - deltaTime();
	}
	
	/**
	 * @return a new Vector2 constructed with the input's x and y screen coordinates.
	 */
	protected final Vector2 positionOf(InputProxy input, int touchID) {
		return new Vector2(input.getX(touchID), input.getY(touchID));
	}
	
	/**
	 * @return a normalized Vector2 that represents a swipe with a magnitude of
	 * 'magnitude' and direction of either up, left, right, or down.
	 */
	public static Vector2 createSwipe(Direction direction, float magnitude) {
		if (direction == Direction.RIGHT)
			return new Vector2(magnitude, 0);
		else if (direction == Direction.UP)
			return new Vector2(0, magnitude);
		else if (direction == Direction.LEFT)
			return new Vector2(-magnitude, 0);
		else if (direction == Direction.DOWN)
			return new Vector2(0, -magnitude);
		else
			throw new RuntimeException("Error in SwipeTile.createSwipe : Unknown direction " + direction);
	}
	
	public static TextureRegion getArrow(Game game, Direction direction, boolean green) {
		// The head of the arrow's source is the same regardless of direction.
		String header = "Arrows/";
		// The footer of the file depends on whether we are retrieving the green or grey arrow.
		String footer = !green? "arrow" : "arrowcorrect";
		
		return Assets.getAtlasRegion(header+footer);
	}
	
	/** @return a random Direction object. */
	public static Direction randomDirection() {
		int i = new Random().nextInt(5);
		
		if (i == 0)
			return Direction.RIGHT;
		else if (i == 1)
			return Direction.UP;
		else if (i == 2)
			return Direction.LEFT;
		else
			return Direction.DOWN;
	}
	
	/** @return the default SwipeTile volume. */
	public static float defaultVolume() {
		return defaultVolume;
	}
	
	/** Set the default SwipeTile volume to the specified value. */
	public static void setDefaultVolume(float newDefault) {
		defaultVolume = newDefault;
	}
}