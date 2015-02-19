package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class BackButton extends Button {
	
	private static final String SOURCE = "assets/GUI/Shop/BackButton.png";
	
	private Game game;
	
	public BackButton(Game game, Vector2 position) {
		super(position, game.getTexture(SOURCE));
		this.game = game;
	}
	
	@Override
	public void onRelease() {
		game.returnToMainMenu();
	}
}