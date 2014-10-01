package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class ResumeButton extends Button {
	
	public static final String
		TEXTURE_SOURCE = "assets/GUI/Icons/Play.png";
	
	private Game game;
	
	public ResumeButton(Game game, Vector2 buttonCenter) {
		super(buttonCenter, game.getTexture(TEXTURE_SOURCE));
		this.game = game;
	}
	
	@Override
	public void onRelease() {
		game.resume();
	}
}