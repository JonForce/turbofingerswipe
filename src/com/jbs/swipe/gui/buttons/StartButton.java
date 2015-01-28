package com.jbs.swipe.gui.buttons;


import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;

public class StartButton extends SpinningButton {
	
	public final static String
		BACKLIGHT_SOURCE = "assets/GUI/Play/Light.png",
		PLAY_BUTTON_SOURCE = "assets/GUI/Play/Button.png",
		TEXT_SOURCE = "assets/GUI/Play/Text.png";
	
	public StartButton(Game game, Vector2 center, float scale) {
		super(game, center, PLAY_BUTTON_SOURCE);
		super.addSpinnerBelowButton(BACKLIGHT_SOURCE, .1f);
		super.addSpinnerAboveButton(TEXT_SOURCE, .05f);
		super.scale(scale);
	}
}