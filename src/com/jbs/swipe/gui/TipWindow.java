package com.jbs.swipe.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.shop.ShopFont;

public class TipWindow extends Graphic {
	
	private static final String
		TIP_WINDOW = "Tutorial/tipWindow";
	
	private Game game;
	
	private ShopFont font;
	private String[] text;
	
	public TipWindow(Game game, Vector2 position, String[] text) {
		super(position, Assets.getAtlasRegion(TIP_WINDOW));
		
		this.game = game;
		
		this.font = new ShopFont();
		font.setScale(0.5f, 2/3f);
		
		this.text = text;
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		
		for (int i = 0; i != text.length; i ++)
			font.draw(batch, text[i], new Vector2(x() - width()/15, y() + height()/8 - i * height()/3/text.length));
	}
}