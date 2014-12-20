package com.jbs.swipe.traps;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.SwipeListener.Event;

public class DarkHole extends Trap<SwipeTile> implements Renderable, Updatable {
	
	private static final String SOURCE = "assets/Traps/DarkHole.png";
	
	private Vector2 position;
	private float
		/** How long the DarkHole will exist for (in milliseconds). */
		lifetime = 1000f;
	private long
		startTime;
	
	public DarkHole(Game game, Vector2 position) {
		super(game);
		// Set the DarkHole's position to the position defined in the constructor.
		this.position = new Vector2(position);
		
		this.startTime = System.currentTimeMillis();
	}
	
	public DarkHole(Game game) {
		this(game, new Vector2());
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		batch.draw(icon(), position.x - icon().getWidth()/2, position.y - icon().getHeight()/2);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		
	}
	
	/** Set the Bomb's center to the specified coordinates. */
	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}
	
	/** @return the x-coordinate of the DarkHole's center. */
	public float x() {
		return position.x;
	}
	
	/** @return the y-coordinate of the DarkHole's center. */
	public float y() {
		return position.y;
	}
	
	/** @return true if the DarkHole is finished sucking stuff in. */
	public boolean expired() {
		return deltaTime() > lifetime;
	}
	
	@Override
	public Texture icon() {
		return game.getTexture(SOURCE);
	}
	
	@Override
	protected void activate() {
		suckIn(targets());
	}
	
	protected void suckIn(SwipeTile... tiles) {
		
		for (final SwipeTile tile : tiles) {
			final float
				ANIMATION_DURATION = 1000f;
			
			new Animator(game)
				.attractTile(tile, new Vector2(x(), y()), ANIMATION_DURATION)
				.get().setCallback(new TweenCallback() {
					public void onEvent(int type, BaseTween<?> source) {
						// Called when the tile are all sucked into the DarkHole.
						
						// Notify the Tile's listener that the Tile was correctly swipe (so the player gets points)
						tile.swipeListener().recieveEvent(tile, Event.TILE_CORRECTLY_SWIPED);
						// and that the Tile is finished.
						tile.swipeListener().recieveEvent(tile, Event.TILE_FINISHED);
					}
				});
		}
	}
	
	/** @return the number of milliseconds that have passed. */
	protected float deltaTime() {
		return (float)(System.currentTimeMillis() - startTime);
	}
	
	@Override
	protected String trapName() {
		return "DarkHole";
	}
}