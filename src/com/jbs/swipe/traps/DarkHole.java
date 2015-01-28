package com.jbs.swipe.traps;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.gui.GraphicAccessor;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.SwipeListener.Event;

public class DarkHole extends Trap<LevelState> implements Renderable, Updatable {
	
	private final ArrayList<SwipeTile> targetedTiles;
	
	private final Graphic graphic;
	private final float
		initialScale = .6f,
		targetScale = 1 / initialScale,
		/** How long the DarkHole will exist for (in milliseconds). */
		lifetime = 2000f,
		rotationSpeed = 500f,
		/** How long the DarkHole will shrink for before disappearing. */
		shrinkDuration = lifetime * .15f,
		/** How long the DarkHole takes to grow when activated. */
		growDuration = shrinkDuration;
	private long
		startTime;
	private boolean
		/** True if the DarkHole is snapped to the Input. */
		grabbed = false;
	
	/** Create a DarkHole and translate it to the specified position. */
	public DarkHole(Game game, Vector2 position) {
		super(game);
		this.graphic = new Graphic(position, texture());
		graphic.scale(initialScale);
		this.targetedTiles = new ArrayList<SwipeTile>(20);
	}
	
	/** Create a DarkHole trap at the default position. */
	public DarkHole(Game game) {
		this(game, new Vector2());
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		graphic.renderTo(batch);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		// Release the DarkHole if it is no longer grabbed.
		if (grabbed() && !input.isTouched())
			release();
		if (grabbed())
			setPosition(input.getX(), input.getY());
		
		if (!expired() && !grabbed())
			for (SwipeTile tile : targetLevel().tiles()) {
				if (tile != null)
					suckIn(tile);
			}
		else if (this.expired())
			// Stop sucking in any tiles that the DarkHole is sucking in.
			for (SwipeTile tile : targetLevel().tiles())
				if (tile != null && isSuckingIn(tile))
					game.tweenManager().killTarget(tile);
	}
	
	/** Set the Bomb's center to the specified coordinates. */
	public void setPosition(int x, int y) {
		this.graphic.setPosition(x, y);
	}
	
	/** Snap the DarkHole to the Input. */
	public void grab() {
		this.grabbed = true;
	}
	
	/** Release the DarkHole. */
	public void release() {
		this.grabbed = false;
		grow();
	}
	
	/** @return the x-coordinate of the DarkHole's center. */
	public float x() {
		return graphic.x();
	}
	
	/** @return the y-coordinate of the DarkHole's center. */
	public float y() {
		return graphic.y();
	}
	
	/** @return true if the DarkHole is finished sucking stuff in. */
	public boolean expired() {
		return deltaTime() > lifetime && !animating();
	}
	
	/** @return true if the DarkHole is sucking in the specified Tile. */
	public boolean isSuckingIn(SwipeTile tile) {
		return targetedTiles.contains(tile);
	}
	
	/** @return true if the DarkHole is snapped to the Input. */
	public boolean grabbed() {
		return this.grabbed;
	}
	
	public boolean animating() {
		return game.tweenManager().containsTarget(graphic) || this.grabbed();
	}
	
	@Override
	protected void activate() {
		grab();
	}
	
	protected void grow() {
		Tween.to(graphic, GraphicAccessor.SCALE_TWEEN, growDuration)
			.ease(Quad.INOUT)
			.target(targetScale, targetScale)
			.setCallback(new TweenCallback() {
				public void onEvent(int type, BaseTween<?> source) {
					startSucking();
				}
			})
			.start(game.tweenManager());
	}
	
	protected void startSucking() {
		this.startTime = System.currentTimeMillis();
		
		beginRotating(rotationSpeed);
		
		Tween.to(graphic, GraphicAccessor.SCALE_TWEEN, shrinkDuration)
			.ease(Quad.IN)
			.target(0, 0)
			.delay(lifetime - shrinkDuration)
			.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					ceaseRotating();
				}
			})
			.start(game.tweenManager());
	}
	
	protected void suckIn(SwipeTile... tiles) {
		// For all the Tiles to suck in,
		for (final SwipeTile tile : tiles) {
			if (targetedTiles.contains(tile))
				continue;
			
			// Add the Tile to the list of Tiles getting sucked in.
			targetedTiles.add(tile);
			
			final float
				DISTANCE = (float)Math.sqrt(tile.x()*tile.x() + tile.y()*tile.y()),
				ANIMATION_DURATION = DISTANCE;
			
			new Animator(game)
				.attractTile(tile, new Vector2(x(), y()), ANIMATION_DURATION)
				.get().setCallback(new TweenCallback() {
					public void onEvent(int type, BaseTween<?> source) {
						// Called when the tile are all sucked into the DarkHole.
						
						// Notify the Tile's listener that the Tile was correctly swipe (so the player gets points)
						tile.swipeListener().recieveEvent(tile, Event.TILE_CORRECTLY_SWIPED);
						// and that the Tile is finished.
						tile.swipeListener().recieveEvent(tile, Event.TILE_FINISHED);
						
						// Remove the Tile from the list of Tiles getting sucked in.
						targetedTiles.remove(tile);
					}
				});
		}
	}
	
	/** @return the Level to suck Tiles in on. */
	protected LevelState targetLevel() {
		return targets()[0];
	}
	
	/** @return the number of milliseconds that have passed. */
	protected float deltaTime() {
		return (float)(System.currentTimeMillis() - startTime);
	}
	
	@Override
	public String trapName() {
		return "DarkHole";
	}
	
	@Override
	public int cost() {
		return 1500;
	}
	
	@Override
	public int trapsPerPurchase() {
		return 5;
	}
	
	@Override
	public Texture icon() {
		return game.getTexture("assets/Traps/"+trapName()+"Icon.png");
	}
	
	/** Begin rotating the BlackHole indefinitely. */
	protected void beginRotating(float speed) {
		new Animator(game)
			.rotateGraphicIndefinitely(graphic, speed);
	}
	
	/** Stop rotating the BlackHole. */
	protected void ceaseRotating() {
		game.tweenManager().killTarget(graphic, GraphicAccessor.ROTATION_TWEEN);
	}
}