package com.jbs.swipe.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ShopFont extends BitmapFont {
	
	private static final String
		TEXTURE = "assets/GUI/Shop/Font/font0_0.png",
		FONT_FILE = "assets/GUI/Shop/Font/font0.fnt";
	
	/** Create a utility to render text. */
	public ShopFont() {
		super(Gdx.files.internal(FONT_FILE), Gdx.files.internal(TEXTURE), false);
	}
	
	/** Draw the text to the Batch around the specified center.
	 * @param batch The Batch to render to.
	 * @param text The text to render.
	 * @param center The center of the text. */
	public void draw(SpriteBatch batch, String text, Vector2 center) {
		final float
			textWidth = getBounds(text).width,
			textHeight = getBounds(text).height;
		draw(batch, text, center.x - textWidth/2, center.y + textHeight/2);
	}
}