package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class HomeButton extends Button {
	
	private static final String
		TEXTURE_SOURCE = "assets/GUI/Icons/Home.png";
	
	private Game game;
	
	public HomeButton(Game game, Vector2 buttonCenter) {
		super(buttonCenter, game.getTexture(TEXTURE_SOURCE));
		this.game = game;
	}
	
	@Override
	public void onRelease() {
		game.returnToMainMenu();
	}
}