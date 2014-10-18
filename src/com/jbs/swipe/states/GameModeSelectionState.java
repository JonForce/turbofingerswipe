package com.jbs.swipe.states;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.GraphicAccessor;

public final class GameModeSelectionState implements ApplicationState {
	
	private static final String
		NORMAL_TEXT = "assets/GUI/GameModeSelect/Normal.png",
		NORMAL_CIRCLE = "assets/GUI/GameModeSelect/NormalCircle.png",
		ADVANCED_TEXT = "assets/GUI/GameModeSelect/Advanced.png",
		ADVANCED_CIRCLE = "assets/GUI/GameModeSelect/AdvancedCircle.png",
		PUZZLE_TEXT = "assets/GUI/GameModeSelect/Puzzle.png",
		PUZZLE_CIRCLE = "assets/GUI/GameModeSelect/PuzzleCircle.png";
	
	private final Game game;
	
	/** The background of the Screen. */
	private Renderable background;
	/** The Buttons used to select the game-mode. */
	private Button advancedButton, puzzleButton, normalButton;
	private Graphic advancedText, puzzleText, normalText;
	
	public GameModeSelectionState(Game game) {
		this.game = game;
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background().renderTo(batch);
		advancedButton.renderTo(batch);
		normalButton.renderTo(batch);
		puzzleButton.renderTo(batch);
	}
	
	@Override
	public void updateApplication(Application app) {
		advancedButton.updateWith(app.input);
		normalButton.updateWith(app.input);
		puzzleButton.updateWith(app.input);
	}
	
	@Override
	public void enterState() {
		final Vector2 center = game.screenCenter();
		advancedText = new Graphic(center.add(game.screenWidth(), 0), game.getTexture(ADVANCED_TEXT));
		normalText = new Graphic(center.add(game.screenWidth(), 0), game.getTexture(NORMAL_TEXT));
		puzzleText = new Graphic(center.add(game.screenWidth(), 0), game.getTexture(PUZZLE_TEXT));
		
		Tween.to(advancedText, GraphicAccessor.ROTATION_TWEEN, 1f/0f)
			.ease(Linear.INOUT)
			.target(1f/0f)
			.start(game.tweenManager());
	}
	
	@Override
	public void exitState() {
		
	}
	
	public Renderable background() {
		return background;
	}
	
	public void setBackground(Renderable background) {
		this.background = background;
	}
}