package com.jbs.swipe.states;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.gui.GraphicAccessor;
import com.jbs.swipe.levels.arcade.ArcadeModeEasy;
import com.jbs.swipe.levels.normal.NormalMode;
import com.jbs.swipe.levels.puzzle.PuzzleMode;

public final class GameModeSelectionState implements ApplicationState {
	
	private static final String
		NORMAL_TEXT = "assets/GUI/GameModeSelect/Normal.png",
		NORMAL_CIRCLE = "assets/GUI/GameModeSelect/NormalCircle.png",
		ADVANCED_TEXT = "assets/GUI/GameModeSelect/Advanced.png",
		ADVANCED_CIRCLE = "assets/GUI/GameModeSelect/AdvancedCircle.png",
		PUZZLE_TEXT = "assets/GUI/GameModeSelect/Puzzle.png",
		PUZZLE_CIRCLE = "assets/GUI/GameModeSelect/PuzzleCircle.png";
	
	private final Game game;
	private final float
		buttonMargin = 50, // The empty space in-between each button.
		rotationAnimationDuration = 4500f,
		translationAnimationDuration = 1000f;
	
	/** The Buttons used to select the game-mode. */
	private Button advancedButton, puzzleButton, normalButton;
	private Graphic advancedText, puzzleText, normalText;
	
	private final Vector2 center, offscreen;
	
	public GameModeSelectionState(Game game) {
		this.game = game;
		this.center = game.screenCenter();
		this.offscreen = center.cpy().add(game.screenSize().x, 0);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		game.background().renderTo(batch);
		
		game.beginIODChange(batch, 3);
			advancedButton.renderTo(batch);
			normalButton.renderTo(batch);
			puzzleButton.renderTo(batch);
			
			advancedText.renderTo(batch);
			normalText.renderTo(batch);
			puzzleText.renderTo(batch);
		game.endIODChange(batch, 3);
	}
	
	@Override
	public void updateApplication(Application app) {
		advancedButton.updateWith(app.input);
		normalButton.updateWith(app.input);
		puzzleButton.updateWith(app.input);
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering GameModeSelectionState.");
		
		initialize();
		animateIn();
	}
	
	@Override
	public void exitState() {
		System.out.println("Exiting GameModeSelectionState.");
	}
	
	/**
	 * Animate the SelectionState's Graphics into position.
	 */
	protected final void animateIn() {
		final Button[] buttons = allButtons();
		
		float targetX = // The position at which to animate our current Button to.
				center.x - ((buttons.length - 1) * buttonWidth())/2; // Start at the leftmost target-position.
		
		for (int i = 0; i != buttons.length; i ++) {
			// Tween the current button to the target position.
			Tween.to(buttons[i], GraphicAccessor.POSITION_TWEEN, translationAnimationDuration)
					.target(targetX + ((i - (buttons.length/2)) * buttonMargin), center.y)
					.ease(Quad.OUT)
					.start(game.tweenManager());
			
			// Move the target position over by the width of the Button.
			targetX += buttonWidth();
		}
	}
	
	/**
	 * Animate the SelectionState's Graphics off screen.
	 */
	protected final void animateOut() {
		
	}
	
	/**
	 * Initialize all the State's components.
	 */
	private void initialize() {
		final float ROTATION_ANIMATION_DURATION = 100f;
		
		// Initialize the Advanced GameMode Button.
		advancedButton = new Button(offscreen.cpy(), game.getTexture(ADVANCED_CIRCLE)) {
			// When the Button is pressed, rotate it very quickly indefinitely.
			public void onPress() {
				new Animator(game)
					.rotateGraphicIndefinitely(this, ROTATION_ANIMATION_DURATION);
			}
			// When the Button is released, bind the "Advanced" GameMode (Which is really just the Puzzle GameMode).
			public void onRelease() {
				game.setLevelState(new ArcadeModeEasy(game));
				game.setState(game.levelState());
			}
		};
		// Initialize the Normal GameMode Button.
		normalButton = new Button(offscreen.cpy(), game.getTexture(NORMAL_CIRCLE)) {
			// When the Button is pressed, rotate it very quickly indefinitely.
			public void onPress() {
				new Animator(game)
					.rotateGraphicIndefinitely(this, ROTATION_ANIMATION_DURATION);
			}
			// When the Button is released, bind the Normal GameMode.
			public void onRelease() {
				game.setLevelState(new NormalMode(game));
				game.setState(game.levelState());
			}
		};
		// Initialize the Puzzle GameMode Button.
		puzzleButton = new Button(offscreen.cpy(), game.getTexture(PUZZLE_CIRCLE)) {
			// When the Button is pressed, rotate it very quickly indefinitely.
			public void onPress() {
				new Animator(game)
					.rotateGraphicIndefinitely(this, ROTATION_ANIMATION_DURATION);
			}
			// When the Button is released, bind the Normal GameMode.
			public void onRelease() {
				game.setLevelState(new PuzzleMode(game) {
					@Override
					protected boolean hasBoughtPuzzleMode() {
						// For now, always assume that the User has bought PuzzleMode.
						return true;
					}
				});
				game.setState(game.levelState());
			}
		};
		
		advancedText = new Graphic(offscreen.cpy(), game.getTexture(ADVANCED_TEXT)) {
			public float x() { return advancedButton.x(); }
			public float y() { return advancedButton.y(); }
		};
		normalText = new Graphic(offscreen.cpy(), game.getTexture(NORMAL_TEXT)) {
			public float x() { return normalButton.x(); }
			public float y() { return normalButton.y(); }
		};
		puzzleText = new Graphic(offscreen.cpy(), game.getTexture(PUZZLE_TEXT)) {
			public float x() { return puzzleButton.x(); }
			public float y() { return puzzleButton.y(); }
		};
		
		new Animator(game)
			.rotateGraphicIndefinitely(advancedText, rotationAnimationDuration + game.random().nextInt(1500))
			.rotateGraphicIndefinitely(normalText, rotationAnimationDuration + game.random().nextInt(1500))
			.rotateGraphicIndefinitely(puzzleText, rotationAnimationDuration + game.random().nextInt(1500));
	}
	
	/**
	 * @return a new Array containing all the Buttons in the SelectionScreen.
	 */
	private Button[] allButtons() {
		return new Button[] {
			advancedButton, puzzleButton, normalButton
		};
	}
	
	/**
	 * @return The width of the average Button and it's text.
	 */
	private float buttonWidth() {
		final float
			textWidth = normalButton.width() - normalText.width();
		
		return normalButton.width() + textWidth*2;
	}
}