package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public abstract class RestartButton extends Button {
	
	public static final String
		TEXTURE_SOURCE = "assets/GUI/Icons/Replay.png";
	
	public RestartButton(Game game, Vector2 buttonCenter) {
		super(buttonCenter, game.getTexture(TEXTURE_SOURCE));
	}
	
	@Override
	public final void onRelease() {
		resetLevel();
		resumeLevel();
	}
	
	public abstract void resetLevel();
	public abstract void resumeLevel();
}