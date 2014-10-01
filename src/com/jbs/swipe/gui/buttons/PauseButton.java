package com.jbs.swipe.gui.buttons;

import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public final class PauseButton extends Button {
	
	public static final String
		PRESSED_SOURCE = "assets/GUI/Icons/Pause/Pressed.png",
		RELEASED_SOURCE = "assets/GUI/Icons/Pause/Released.png";
	
	private Game game;
	
	public PauseButton(Game game) {
		// Create the Button centered around the top right hand corner of the Game's screen.
		super(game.screenSize(), game.getTexture(RELEASED_SOURCE), game.getTexture(PRESSED_SOURCE));
		
		this.game = game;
		// Translate the Button down and to the left, positioning it's top right
		// hand corner at the top right of the screen.
		this.translate(- this.width()/2, - this.height()/2);
	}
	
	@Override
	public void onRelease() {
		game.pause();
	}
}