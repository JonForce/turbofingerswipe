package com.jbs.swipe.levels.arcade;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.GraphicAccessor;
import com.jbs.swipe.gui.TipWindow;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.tiles.Direction;
import com.jbs.swipe.tiles.SwipeTile;
import com.jbs.swipe.tiles.SwipeTile.TileState;
import com.jbs.swipe.tiles.TileAccessor;

public class ArcadeTutorialState extends TutorialState {
	
	private static final String[]
		TIP_SOURCES = new String[] {
			"assets/GUI/Tutorial/ArcadeMode/Tip0.png",
			"assets/GUI/Tutorial/ArcadeMode/Tip1.png",
			"assets/GUI/Tutorial/ArcadeMode/Tip2.png" };
	
	private static final String
		GOT_IT_PRESSED_SOURCE = "assets/GUI/Tutorial/GotItPressed.png",
		GOT_IT_UNPRESSED_SOURCE = "assets/GUI/Tutorial/GotItUnpressed.png",
		FINGER_SOURCE = "assets/GUI/Loading/Finger.png",
		GAME_OVER_LOGO_SOURCE = "assets/GUI/Font/GameOverMessage.png";
	
	private Graphic[] tips;
	private Button gotItButton;
	
	private SwipeTile[] demoTiles;
	private Graphic finger, gameOverLogo;
	
	private boolean
		demoTilesAreVisible = true,
		fingerIsVisible = true,
		gameOverIsVisible = false;
	
	private final float
		tilesYOffset = 150,
		infinity = 1/0f,
		swipeDistance = 200f,
		swipeDuration = 1000f;
	
	/**
	 * Create the TutorialState for the Arcade game-mode.
	 */
	public ArcadeTutorialState(Game game, ApplicationState exitState) {
		super(game);
		this.setExitState(exitState);
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering ArcadeTutorialState.");
		
		super.enterState();
		useTip(0);
	}
	
	@Override
	public void exitState() {
		super.exitState();
		super.reset();
		killAllTweens();
	}
	
	@Override
	public void updateApplication(Application app) {
		super.updateApplication(app);
		
		gotItButton.updateWith(app.input);
		bottomDemoTile().updateTranslationAnimation();
		topDemoTile().updateTranslationAnimation();
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		
		if (gameOverIsVisible) {
			game.beginIODChange(batch, 3);
				gameOverLogo.renderTo(batch);
			game.endIODChange(batch, 3);
		}
		
		if (demoTilesAreVisible) {
			game.beginIODChange(batch, 3.5f);
				bottomDemoTile().renderTo(batch);
				topDemoTile().renderTo(batch);
				demoTile().renderTo(batch);
			game.endIODChange(batch, 3.5f);
		}
		
		if (fingerIsVisible) {
			game.beginIODChange(batch, 4f);
				finger.renderTo(batch);
			game.endIODChange(batch, 4f);
		}
		
		gotItButton.renderTo(batch);
	}
	
	@Override
	public Graphic[] tips() {
		return tips;
	}
	
	@Override
	public String tutorialName() {
		return "arcadetutorial";
	}
	
	@Override
	public void useTip(int tip) {
		if (tip == 0) {
			buildFirstTipScene();
		} else if (tip == 1) {
			buildSecondTipScene(1000f);
		} else if (tip == 2) {
			buildThirdTipScene(130);
		} else if (tip == 3) {
			buildFourthTipScene();
		} else if (tip == 4) {
			buildFithTipScene();
		}
	}
	
	@Override
	protected void create() {
		// Initialize the Tips.
		initializeTips();
		// Initialize the GotIt Button.
		initializeGotItButton();
		
		// Initialize the finger.
		finger = new Graphic(new Vector2(0,0), game.getTexture(FINGER_SOURCE));
		// Initialize the demonstration Tiles.
		demoTiles = new SwipeTile[3];
		for (int i = 0; i != demoTiles.length; i ++) {
			demoTiles[i] = new SwipeTile(game, infinity, Direction.LEFT);
			demoTiles[i].translate(0, tilesYOffset);
		}
		// Initialize the game-over logo.
		gameOverLogo = new Graphic(game.screenCenter(), game.getTexture(GAME_OVER_LOGO_SOURCE));
		gameOverLogo.translate(0, 150);
	}
	
	private void buildFirstTipScene() {
		resetFinger();
		demoTilesAreVisible = true;
		
		final TweenCallback callback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				// Change the direction of the demo-Tile.
				demoTile().changeDirection();
				// Move the finger to the center of the demo-Tile.
				finger.setPosition((int) demoTile().x(), (int) demoTile().y());
				
				swipeTile(demoTile(), finger, swipeDistance, swipeDuration).setCallback(this);
			}
		};
		
		swipeTile(demoTile(), finger, swipeDistance, swipeDuration).setCallback(callback);
	}
	
	private void buildSecondTipScene(final float gameOverTime) {
		resetFinger();
		
		final TweenCallback callback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				// Display the game-over logo.
				gameOverIsVisible = true;
				// Hide the finger and demo-Tile.
				fingerIsVisible = false;
				demoTilesAreVisible = false;
				
				final TweenCallback callback = this;
				// After x amount of time, we need to hide the game-over logo and reset
				// the finger swiping animation.
				delayEvent(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						// Change the demo-Tile's Direction.
						demoTile().changeDirection();
						// Reset the finger.
						resetFinger();
						
						swipeTile(demoTile(), finger, -swipeDistance, swipeDuration).setCallback(callback).delay(500f);
						
						// Hide the game-over logo.
						gameOverIsVisible = false;
						demoTilesAreVisible = true;
						fingerIsVisible = true;
					}
				}, gameOverTime);
			}
		};
		
		// Change the Direction of the demoTile.
		demoTile().changeDirection();
		
		swipeTile(demoTile(), finger, -swipeDistance, swipeDuration).setCallback(callback);
	}
	
	private void buildThirdTipScene(final float tileSpacing) {
		final float
			EXPANSION_DURATION = 1000,
			DELAY = 1500f;
		
		gameOverIsVisible = false;
		demoTilesAreVisible = true;
		fingerIsVisible = true;
		resetFinger();
		game.tweenManager().killAll();
		
		final TweenCallback callback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				resetTiles();
				
				topDemoTile().setTranslationTarget(new Vector2(demoTile().x(), demoTile().y() + tileSpacing), .1f);
				bottomDemoTile().setTranslationTarget(new Vector2(demoTile().x(), demoTile().y() - tileSpacing), .1f);
				
				// Scale the tiles down to 50% their original size.
				for (int i = 0; i != demoTiles.length; i ++)
					scaleTile(demoTiles[i], .5f, EXPANSION_DURATION);
				
				final TweenCallback callback = this;
				delayEvent(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						resetTiles();
						resetFinger();
						
						swipeTile(demoTile(), finger, swipeDistance, swipeDuration)
							.setCallback(callback);
					}
				}, EXPANSION_DURATION + DELAY);
			}
		};
		
		swipeTile(demoTile(), finger, -swipeDistance, swipeDuration)
			.setCallback(callback);
	}
	
	private void buildFourthTipScene() {
		gameOverIsVisible = false;
		demoTilesAreVisible = true;
		fingerIsVisible = false;
		game.tweenManager().killAll();
		resetTiles();
		demoTile().changeDirection();
		
		final float
			swapStateInterval = 500f,
			gameOverDuration = 1500f;
		
		final TweenCallback advanceTileState = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (demoTile().tileState() == TileState.EXPIRED) {
					demoTile().setState(TileState.BLUE);
					gameOverIsVisible = false;
					demoTilesAreVisible = true;
				} else if (demoTile().tileState() == TileState.RED) {
					demoTile().setState(TileState.EXPIRED);
					gameOverIsVisible = true;
					demoTilesAreVisible = false;
					delayEvent(this, gameOverDuration);
					return;
				} else if (demoTile().tileState() == TileState.ORANGE)
					demoTile().setState(TileState.RED);
				else if (demoTile().tileState() == TileState.YELLOW)
					demoTile().setState(TileState.ORANGE);
				else if (demoTile().tileState() == TileState.BLUE)
					demoTile().setState(TileState.YELLOW);
				
				delayEvent(this, swapStateInterval);
			}
		};
		advanceTileState.onEvent(0, null);
	}
	
	private void buildFithTipScene() {
		gameOverIsVisible = false;
		demoTilesAreVisible = true;
		fingerIsVisible = false;
		game.tweenManager().killAll();
		resetTiles();
		
		final float
			expansionDuration = 1000f,
			comboSwipeDelay= 1000f,
			comboSwipeDuration = 750f;
		
		for (int i = 0; i != demoTiles.length; i++) {
			demoTiles[i].setDirection(Direction.LEFT);
			Tween.to(demoTiles[i], TileAccessor.POSITION_TWEEN, expansionDuration)
				.ease(Linear.INOUT)
				.target(demoTile().x() + (i - 1)*100, demoTile().y())
				.start(game.tweenManager());
		}
		
		final TweenCallback doComboSwipe = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				finger.setPosition(demoTiles[0].x() + 75, demoTiles[0].y());
				
//				swipeTile(demo)
				
				delayEvent(this, comboSwipeDelay);
			}
		};
		delayEvent(doComboSwipe, expansionDuration);
	}
	
	private void delayEvent(TweenCallback callback, float delay) {
		Tween.call(callback).delay(delay).start(game.tweenManager());
	}
	
	/**
	 * Initialize the Tutorial's tip Graphics.
	 */
	private void initializeTips() {
		tips = new Graphic[5];
		for (int i = 0; i != 3; i ++) {
			// Create the tip-Graphic.
			tips[i] = new Graphic(game.screenCenter(), game.getTexture(TIP_SOURCES[i]));
			// Translate the Tip down and to the left.
			tips[i].translate(-tips[i].width()/4, -tips[i].height()/2);
		}
		
		tips[3] = new TipWindow(game, game.screenCenter(), new String[] { "You only have", "a short time to", "swipe, so hurry!"});
		tips[3].translate(-tips[3].width()/4, -tips[3].height()/2);
		
		tips[4] = new TipWindow(game, game.screenCenter(), new String[] { "You can swipe", "multiple tiles", "at once!"});
		tips[4].translate(-tips[3].width()/4, -tips[3].height()/2);
	}
	
	/**
	 * Initialize the Tutorial's "Got it" button.
	 */
	private void initializeGotItButton() {
		// Initialize the Button with it's Unpressed and Pressed textures.
		gotItButton = new Button(new Vector2(0,0), game.getTexture(GOT_IT_UNPRESSED_SOURCE), game.getTexture(GOT_IT_PRESSED_SOURCE)) {
			// Override the position of the Button's center to be relative to the current-tip window.
			@Override
			public float x() {
				// The Center-x of the Button should be at the center of the current-tip's window.
				return currentTip().x() - texture().getHeight()/2;
			}
			@Override
			public float y() {
				// The Center-y of the Button should be at the,
				return
						currentTip().y() - currentTip().height()/2 // Bottom of the Current-tip's window,
						+ texture().getHeight() - 10; // With a positive y-offset.
			}
			// When the Button is released, use the next tip.
			@Override
			public void onRelease() {
				useNextTip();
			}
		};
	}
	
	/**
	 * Animate the "finger" Graphic to swipe the specified Tile.
	 * Only creates and starts animation, does not call any of the Tile's
	 * swipe-event methods.
	 * @param tile The Tile to be swiped.
	 * @param finger The Graphic to animate.
	 * @param distance The magnitude of the swipe. Can be negative to produce
	 * 	incorrect swipe animations.
	 * @param time The duration of the animation.
	 * @return the animation.
	 */
	private Tween swipeTile(SwipeTile tile, Graphic finger, float distance, float time) {
		final Vector2 target = getTileDirection(tile).mul(distance).add(tile.x(), tile.y());
		return Tween.to(finger, GraphicAccessor.POSITION_TWEEN, time)
			.target(target.x, target.y)
			.ease(Quad.OUT)
			.start(game.tweenManager());
	}
	
	/**
	 * Get the enumerated Direction of the specified Tile.
	 * @param tile The Tile to get the Direction of.
	 * @return the enumerated Direction of the Tile.
	 */
	private Vector2 getTileDirection(SwipeTile tile) {
		final Vector2 target;
		if (tile.direction() == Direction.RIGHT)
			target = new Vector2(1, 0);
		else if (tile.direction() == Direction.UP)
			target = new Vector2(0, 1);
		else if (tile.direction() == Direction.LEFT)
			target = new Vector2(-1, 0);
		else
			target = new Vector2(0, -1);
		return target;
	}
	
	/**
	 * Animate the scale of the Tile to the specified value over a period of time.
	 * @param tile The Tile to animate.
	 * @param scaleTo The target scale.
	 * @param time The duration of the animation.
	 * @return the animation.
	 */
	private Tween scaleTile(SwipeTile tile, float scaleTo, float time) {
		return 	Tween.to(tile, TileAccessor.SCALE_TWEEN, time)
				.ease(Linear.INOUT)
				.target(scaleTo, scaleTo)
				.start(game.tweenManager());
	}
	
	private void killAllTweens() {
		game.tweenManager().killAll();
	}
	
	/**
	 * Set the finger to the center of the screen and kill
	 * all running tweens on the finger.
	 */
	private void resetFinger() {
		fingerIsVisible = true;
		// Kill the finger animation.
		game.tweenManager().killTarget(finger);
		// Move the finger to the center of the demo-Tile.
		finger.setPosition((int) demoTile().x(), (int) demoTile().y());
	}
	
	/**
	 * Reset the position, scale, and translation-targets of all the Tiles.
	 * Also kills all animations associated with all of the Tiles.
	 */
	private void resetTiles() {
		for (int i = 0; i != demoTiles.length; i ++) {
			demoTiles[i].setPosition(game.screenCenter());
			demoTiles[i].translate(0, tilesYOffset);
			demoTiles[i].setScale(1, 1);
			demoTiles[i].setTranslationTarget(null, 0);
			game.tweenManager().killTarget(demoTiles[i]);
		}
	}
	
	private SwipeTile demoTile() {
		return demoTiles[1];
	}
	
	private SwipeTile topDemoTile() {
		return demoTiles[2];
	}
	
	private SwipeTile bottomDemoTile() {
		return demoTiles[0];
	}
}