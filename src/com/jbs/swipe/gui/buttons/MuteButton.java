package com.jbs.swipe.gui.buttons;

import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class MuteButton extends Button {
	
	public static final String
		UNMUTED_ICON_SOURCE = "assets/GUI/Icons/Mute/On.png",
		MUTED_ICON_SOURCE = "assets/GUI/Icons/Mute/Off.png";
	
	private Game game;
	
	public MuteButton(Game game, Vector2 center) {
		super(center, game.audio().isMuted()? game.getTexture(MUTED_ICON_SOURCE) : game.getTexture(UNMUTED_ICON_SOURCE));
		this.game = game;
	}
	
	@Override
	public void onRelease() {
		System.out.println("MuteButton released!");
		// If the Button displaying the muted icon,
		if (this.isUsingMutedIcon()) {
			// Unmute the audio.
			game.audio().unmute();
			// Use the unmuted-icon.
			this.useUnmutedIcon();
		} else {
			// Mute the audio.
			game.audio().mute();
			// Use the muted icon.
			this.useMutedIcon();
		}
	}
	
	/* Use the unmuted-icon Texture. Throws a RuntimeException if the Game is strict and
	 * the Texture is already the unmuted-icon Texture. */
	protected void useUnmutedIcon() {
		if (game.IS_STRICT && this.isUsingUnmutedIcon())
			throw new RuntimeException("Cannot useUnmutedIcon, already using unmuted icon.");
		else
			super.setTexture(game.getTexture(UNMUTED_ICON_SOURCE));
	}
	
	/* Use the muted-icon Texture. Throws a RuntimeException if the Game is strict and
	 * the Texture is already the muted-icon Texture. */
	protected void useMutedIcon() {
		if (game.IS_STRICT && this.isUsingMutedIcon())
			throw new RuntimeException("Cannot useMutedIcon, already using muted icon.");
		else
			super.setTexture(game.getTexture(MUTED_ICON_SOURCE));
	}
	
	/* @return true if the Button is using the muted-icon. */
	protected boolean isUsingMutedIcon() {
		// Return true when the Button's Texture equals the muted-icon Texture.
		return texture() == game.getTexture(MUTED_ICON_SOURCE);
	}
	
	/* @return true if the Button is using the unmuted-icon. */
	protected boolean isUsingUnmutedIcon() {
		// Return true when the Button's Texture equals the unmuted-icon Texture.
		return texture() == game.getTexture(UNMUTED_ICON_SOURCE);
	}
}