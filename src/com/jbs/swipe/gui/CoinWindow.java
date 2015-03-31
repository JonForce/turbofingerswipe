package com.jbs.swipe.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.shop.ShopFont;

public abstract class CoinWindow implements Renderable {
	
	private static final String
		WINDOW_SOURCE = "Shop/CoinWindow";
	
	private Graphic window;
	private ShopFont font;
	
	/** Create a utility for notifying the user of how many jbs coins he or she has. */
	public CoinWindow(Game game) {
		this.window = new Graphic(new Vector2(), Assets.getAtlasRegion(WINDOW_SOURCE));
		window.setPosition(x(), y());
		
		this.font = new ShopFont();
		
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		window.renderTo(batch);
		
		final String
			text = coins() + "";
		final float
			// The horizontal offset of the text.
			leftMargin = -60f;
		font.scaleToBounds(100f, 30f, coins()+"");
		font.drawAt(batch, text, x() + leftMargin, y() + heightOf(text)/2 +10);
	}
	
	/** Scale the window around it's center. */
	public final void scale(float scalar) {
		window.scale(scalar);
		font.setScale(scalar);
	}
	
	/** @return the width of the window. */
	public final float width() {
		return window.width();
	}
	
	/** @return the height of the window. */
	public final float height() {
		return window.height();
	}
	
	/** @return the y-coordinate of the center of the window. */
	public abstract float x();
	/** @return the x-coordinate of the center of the window. */
	public abstract float y();
	
	/** @return the number of jbs coins that the user has. */
	protected abstract int coins();
	
	/** @return the width in pixels of the text when it's rendered with the Window's Font. */
	private float widthOf(String text) {
		return font.getBounds(text).width;
	}
	
	/** @return the height in pixels of the text when it's rendered with the Window's Font. */
	private float heightOf(String text) {
		return font.getBounds(text).height;
	}
}