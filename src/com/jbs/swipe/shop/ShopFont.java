package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.swipe.Game;

public class ShopFont {
	
	private static final String
		FONT_FILE = "assets/GUI/Shop/Font/font0.fnt";
	BitmapFont font;
	Vector2 scale = new Vector2(1,1);
	
	/** Create a utility to render text. */
	public ShopFont() {
		font = Game.getFont(FONT_FILE);
		//super(Gdx.files.internal(FONT_FILE), Gdx.files.internal(TEXTURE), false);
		//super(game.getFont(FONT_FILE));
		
	}
	
	public void scaleToWidth( float maxWidth, String text ) {
		setScale(maxWidth / font.getBounds(text).width);
	}
	public void scaleToBounds( float maxWidth, float maxHeight, String text ) {
		TextBounds bounds = font.getBounds(text);
		setScale(
			Math.min(maxWidth / bounds.width, maxHeight / bounds.height)
		);
	}
	public void setScale( float scale ) {
		setScale( scale, scale );
	}
	public void setScale( float xscale, float yscale) {
		scale.set(xscale,yscale);
	}
	
	public void setColor(Color color) {
		font.setColor(color);
	}
	
	public TextBounds getBounds(String str) {
		return font.getBounds(str);
	}
	
	/** Draw the text to the Batch around the specified center.
	 * @param batch The Batch to render to.
	 * @param text The text to render.
	 * @param center The center of the text. */
	public void draw(SpriteBatch batch, String text, Vector2 center) {
		font.setScale(scale.x,scale.y);
		final float
			textWidth = font.getBounds(text).width,
			textHeight = font.getBounds(text).height;
		//font.draw(batch, text, center.x - textWidth/2, center.y + textHeight/2);
		drawAt(batch, text, center.x-textWidth/2,center.y+textHeight/2);
	}
	/** Draw the text to the Batch bottom left.
	 * @param batch The Batch to render to.
	 * @param text The text to render.
	 * @param center The center of the text. */
	public void drawAt(SpriteBatch batch, String text, Vector2 position) {
		drawAt(batch, text, position.x, position.y);
	}
	
	public void drawAt(SpriteBatch batch, String text, float x, float y ) {
		font.setScale(scale.x,scale.y);
		font.draw(batch,text,x,y);
	}
}