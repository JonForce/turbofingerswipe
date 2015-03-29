package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;

public class ShopButton extends SpinningButton {

	private static final String
		//BACKLIGHT_SOURCE = "assets/GUI/Play/Light.png",
		BUTTON_SOURCE = "Shop/ShopIcon";
		//TEXT_SOURCE = "assets/GUI/Shop/ShopText.png";
	
	//private final float
		//backlightRotationDamping = .1f,
		//textRotationDamping = .05f;
	
	public ShopButton(Game game, Vector2 center) {
		super(game, center, BUTTON_SOURCE);
		//super.addSpinnerAboveButton(TEXT_SOURCE, textRotationDamping);
		//super.addSpinnerBelowButton(BACKLIGHT_SOURCE, backlightRotationDamping);
	}
	public ShopButton(Game game, Vector2 center, String texture ) {
		super(game,center,texture);
	}
	
	@Override
	public void onTrigger() {
		game.openShop();
	}
}