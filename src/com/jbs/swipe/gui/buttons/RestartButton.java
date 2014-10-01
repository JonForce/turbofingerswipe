package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class RestartButton extends Button {
	
	public static final String
		TEXTURE_SOURCE = "assets/GUI/Icons/Replay.png";
	
	private Game game;
	
	public RestartButton(Game game, Vector2 buttonCenter) {
		super(buttonCenter, game.getTexture(TEXTURE_SOURCE));
		this.game = game;
	}
	
	@Override
	public void onRelease() {
		game.resetLevel();
		game.resume();
	}
}