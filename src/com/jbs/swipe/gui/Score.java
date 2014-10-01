package com.jbs.swipe.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;

public class Score implements Renderable {
	
	private final FileHandle
		FONT_SOURCE;
	
	public static final int
		FONT_TOP_MARGIN = 10;
	
	protected int count;
	protected Font font;
	
	public Score(Game game) {
		FONT_SOURCE = Gdx.files.internal("assets/GUI/Font/Digits.png");
		
		font = new Font(game.getTexture(FONT_SOURCE));
		// Set the Font to render at the top-center of the Game's screen.
		font.setPosition((int) game.screenCenter().x, (int) game.screenHeight());
		// Translate the Font to be completely on-screen.
		font.translate(0, -font.digitHeight());
		// Translate the Font down by the desired top margin.
		font.translate(0, -FONT_TOP_MARGIN);
		// Set the Font to be Center-Aligned.
		font.setAlignment(Font.ALIGNMENT_CENTER);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		font.renderIntegerTo(batch, count());
	}
	
	public int count() {
		return count;
	}
	
	public final void increment() {
		count ++;
	}
	
	public final void decrement() {
		count --;
	}
	
	public void reset() {
		count = 0;
	}
}