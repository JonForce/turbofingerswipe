package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.shop.purchases.Purchase;

public class PurchaseButton extends Button {
	
	private static final String
		UNPRESSED_SOURCE = "Shop/Button",
		PRESSED_SOURCE = "Shop/ButtonPressed";
	
	final float FONT_PADDING = 25f;
	
	private final Purchase purchase;
	private final ShopFont font;
	
	public PurchaseButton(Game game, Purchase purchase, Vector2 center) {
		super(center, Assets.getAtlasRegion(UNPRESSED_SOURCE), Assets.getAtlasRegion(PRESSED_SOURCE));
		this.purchase = purchase;
		this.font = new ShopFont();
		
		//font.scaleToWidth(this.width()-FONT_PADDING*2f, purchase.cost());
		font.setScale(0.5f);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		
		font.draw(batch, "" + purchase.cost(), new Vector2(x(), y()+7f));
	}
}