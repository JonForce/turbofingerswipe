package com.jbs.swipe.traps;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.effects.Explosion;
import com.jbs.swipe.effects.SmallFlame;
import com.jbs.swipe.tiles.SwipeListener.Event;
import com.jbs.swipe.tiles.SwipeTile;

public class Bomb extends Trap<SwipeTile> implements Renderable, Updatable {
	
	private final Vector2
		// The position of the Bomb.
		position;
	
	private float
		// The magnitude of the Bomb's explosion.
		explosionMagnitude = 500f;
	
	private long
		// The duration of the Explosion animation in milliseconds.
		animationDuration = 200;
	
	private BombState
		/** The current state of the Bomb. */
		state;
	
	public Bomb(final Game game) {
		super(game);
		position = new Vector2(0, 0);
		state = new IdleState(this);
		state.enterState();
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		state.renderTo(batch);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		state.updateWith(input);
	}
	
	public void reset() {
		
	}
	
	/** Attach the Bomb to the input, as if the user has grabbed it. */
	public final void grab() {
		setState(new GrabbedState(this));
	}
	
	/** Detach the Bomb to the input, as if the user has released it. */
	public final void release() {
		setState(new IdleState(this));
	}
	
	/** Set the magnitude of the Bomb's explosion.
	 * @param newMagnitude The new magnitude of the Bomb's explosion. */
	public final void setExplosionMagnitude(float newMagnitude) {
		this.explosionMagnitude = newMagnitude;
	}
	
	/** Set the position of the Bomb. */
	public final void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public final float x() {
		return position.x;
	}
	
	public final float y() {
		return position.y;
	}
	
	/** Set the state of the Bomb. */
	public final void setState(BombState newState) {
		state.exitState();
		newState.enterState();
		state = newState;
	}
	
	/** @return true if the Bomb is finished detonating. */
	public final boolean expired() {
		return state instanceof ExplodingState && ((ExplodingState) state).finishedExploding();
	}
	
	/** @return true if the Bomb is lit. */
	public final boolean ignited() {
		return state instanceof LitState;
	}
	
	/** @return true if the Bomb is lit. */
	public final boolean grabbed() {
		return state instanceof GrabbedState;
	}
	
	@Override
	protected void activate() {
		if (!Gdx.input.isTouched())
			throw new RuntimeException("Cannot activate a Bomb if the input is not touched.");
		
		grab();
	}
	
	/** Blow the Tiles away from the Explosion.
	 * @param position The explosion to blow the targets away from.
	 * @param targets The Tiles to blow away.*/
	protected void blowTargetsAwayFrom(Explosion explosion, SwipeTile... targets) {
		// For each Tile to blow away,
		for (final SwipeTile target : targets) {
			final float
				// Establish the distance the Tile is from the explosion.
				DELTA_X = explosion.x() - target.x(),
				DELTA_Y = explosion.y() - target.y(),
				DELTA_MAGNITUDE = (float) Math.sqrt(Math.pow(DELTA_X,2) + Math.pow(DELTA_Y,2)),
				
				// Establish the direction that we are going to fling the Tile.
				DIRECTION_X = -(DELTA_X / DELTA_MAGNITUDE) * explosionMagnitude,
				DIRECTION_Y = -(DELTA_Y / DELTA_MAGNITUDE) * explosionMagnitude;
			final int
				ROTATIONS = 3;
			
			new Animator(game)
				.spinTile(target, ROTATIONS, animationDuration)
				// Fade the target away.
				.fadeTileAway(target, animationDuration)
				// Slide the target away from the explosion.
				.swipeTileAway(target, new Vector2(DIRECTION_X, DIRECTION_Y), animationDuration)
				// Create a callback function for when the animation is complete.
				.get().setCallback(new TweenCallback() {
					// When the animation is complete,
					public void onEvent(int type, BaseTween<?> source) {
						// Notify the Tile's listener that the Tile was correctly swipe (so the player gets points)
						target.swipeListener().recieveEvent(target, Event.TILE_CORRECTLY_SWIPED);
						// and that the Tile is finished.
						target.swipeListener().recieveEvent(target, Event.TILE_FINISHED);
					}
				});
		}
	}
	
	@Override
	public String trapName() {
		return "Bomb";
	}
	
	@Override
	public int cost() {
		return 1500;
	}
	
	@Override
	public int trapsPerPurchase() {
		return 5;
	}
}

abstract class BombState implements Renderable, Updatable {
	
	protected final Bomb bomb;
	
	public BombState(Bomb bomb) {
		this.bomb = bomb;
	}
	
	public void enterState() { }
	public void exitState() { }
	public abstract void updateWith(InputProxy input);
	public abstract void renderTo(SpriteBatch batch);
	
	protected void assertBombIsInScreen() {
		if (bomb.x() + bomb.texture().getWidth() > bomb.game.screenWidth())
			bomb.setPosition(bomb.game.screenWidth() - bomb.texture().getWidth(), bomb.y());
		if (bomb.x() < 0)
			bomb.setPosition(0, bomb.y());
		if (bomb.y() + bomb.texture().getHeight() > bomb.game.screenHeight())
			bomb.setPosition(bomb.x(), bomb.game.screenHeight() - bomb.texture().getHeight());
		if (bomb.y() < 0)
			bomb.setPosition(bomb.x(), 0);
	}
}

class IdleState extends BombState {
	
	private float rotation = 0;
	
	public IdleState(Bomb bomb) {
		super(bomb);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		// If the bomb isnt already grabbed and the input is touching,
		if (!bomb.grabbed() && input.isTouched())
			if (input.getX() > bomb.x() && input.getX() < bomb.x() + bomb.texture().getWidth())
				if (input.getY() > bomb.y() && input.getY() < bomb.y() + bomb.texture().getHeight())
					bomb.setState(new GrabbedState(bomb));
		assertBombIsInScreen();
	}
	
	/** @return the rotation of the Bomb in degrees. */
	public float rotation() {
		return rotation;
	}
	
	/** Set the Bomb's rotation to the specified angle in degrees. */
	public void setRotation(float degrees) {
		rotation = degrees;
	}
	
	/** Rotate the Bomb by the specified angle in degrees. */
	public final void rotate(float degrees) {
		setRotation(rotation() + degrees);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		Graphic.drawRotated(batch, bomb.texture(), new Vector2(bomb.x(), bomb.y()), rotation());
	}
}

class LitState extends IdleState {
	/** The flame from the fuse on the Bomb */
	private SmallFlame
		flame;
	
	public LitState(final Bomb bomb) {
		super(bomb);
		flame = new SmallFlame() {
			@Override
			protected float x() { return bomb.x() + (float)Math.cos(Math.toRadians(rotation() + 90)) * bomb.texture().getHeight()/2; }
			@Override
			protected float y() { return bomb.y() + (float)Math.sin(Math.toRadians(rotation() + 90)) * bomb.texture().getHeight()/2; }
		};
	}
	
	@Override
	public void updateWith(InputProxy input) {
		super.updateWith(input);
		flame.updateWith(input);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		flame.renderTo(batch);
	}
	
	@Override
	public void enterState() {
		ignite();
	}
	
	@Override
	public void exitState() {
		extinguish();
	}
	
	/** Light the Bomb. Makes the fuse burn. */
	public final void ignite() {
		flame.ignite();
	}
	
	/** Extinguish the Bomb. */
	public final void extinguish() {
		flame.extinguish();
	}
}

class GrabbedState extends LitState {
	
	private Vector2
		/** The speed at which the Bomb is being moved. */
		velocity;
	
	private float
		/** The damping rate of the Bomb's velocity. */
		velocityDamping = .5f;

	public GrabbedState(Bomb bomb) {
		super(bomb);
		velocity = new Vector2();
	}
	
	@Override
	public void updateWith(InputProxy input) {
		// If the Input is still touched,
		if (input.isTouched()) {
			// Update the Grabbed state.
			super.updateWith(input);
			bomb.setPosition(input.getX(), input.getY());
			velocity.add(input.getDeltaX(), input.getDeltaY());
			velocity.mul(velocityDamping);
		} else {
			// The player released the Bomb, enter the thrown-state.
			bomb.setState(new ThrownState(bomb, velocity, 10f));
		}
		
	}
}

class ThrownState extends LitState {
	
	private float
		damping = .95f,
		// The time until the Bomb enters its exploding-state.
		explosionDelay = 500f,
		torque;
	private Vector2 velocity;
	
	public ThrownState(Bomb bomb, Vector2 velocity, float torque) {
		super(bomb);
		this.velocity = velocity;
		this.torque = torque;
	}
	
	@Override
	public void updateWith(InputProxy input) {
		super.updateWith(input);
		// Move the bomb.
		bomb.setPosition(bomb.x() + velocity.x, bomb.y() + velocity.y);
		// Multiply the velocity by the damping.
		velocity.mul(damping);
		
		super.rotate(torque);
		torque *= damping;
		
		// Bounce the Bomb off the edge of the screen.
		if (bomb.x() + bomb.texture().getWidth() > bomb.game.screenWidth() || bomb.x() < 0)
			velocity.mul(-1, 1);
		if (bomb.y() + bomb.texture().getHeight() > bomb.game.screenHeight() || bomb.y() < 0)
			velocity.mul(1, -1);
	}
	
	@Override
	public void enterState() {
		super.enterState();
		// Set a timer, in x amount of time, the Bomb should explode.
		Tween.call(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				// Enter the exploding-state.
				bomb.setState(new ExplodingState(bomb));
			}
		}).delay(explosionDelay).start(bomb.game.tweenManager());
	}
	
	/** Set the velocity of the Bomb. The velocity is multiplied by the
	 * damping every update.
	 * @param x The new x-velocity.
	 * @param y The new y-velocity. */
	public final void setVelocity(float x, float y) {
		velocity.set(x, y);
	}
	
	/** Set the value to multiply the velocity by every frame. */
	public final void setDamping(float newDamping) {
		damping = newDamping;
	}
}

class ExplodingState extends BombState {
	
	/* The explosion from the Bomb. */
	private Explosion
		explosion;
	
	public ExplodingState(Bomb bomb) {
		super(bomb);
		explosion = new Explosion();
	}
	
	@Override
	public void enterState() {
		super.enterState();
		explosion.explode(bomb.x(), bomb.y());
		if (bomb.targets() != null)
			bomb.blowTargetsAwayFrom(explosion, bomb.targets());
	}
	
	@Override
	public void updateWith(InputProxy input) {
		explosion.updateWith(input);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		explosion.renderTo(batch);
	}
	
	/** @return true if the state is done exploding. */
	public boolean finishedExploding() {
		return !explosion.exploding();
	}
}