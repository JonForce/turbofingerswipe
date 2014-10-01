package com.jbs.swipe;

import com.jbs.framework.rendering.Graphic;

public class ScreenOverlay extends Graphic {
	
	private final static String
		TEXTURE_SOURCE = "assets/Windows/Overlay.png";
	
	public ScreenOverlay(Game game) {
		super(game.screenCenter(), game.screenSize(), game.getTexture(TEXTURE_SOURCE));
	}
}