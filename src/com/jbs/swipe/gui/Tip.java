package com.jbs.swipe.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;

public class Tip implements Renderable {
	
	private final Game game;
	private final String text;
	private final int width, height;
	
	private BitmapFont font;
	
	public Tip(Game game, String text, int width, int height) {
		this.game = game;
		this.text = text;
		this.width = width;
		this.height = height;
		
		
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		
	}
}