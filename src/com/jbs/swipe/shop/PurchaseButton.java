package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;

public class PurchaseButton extends Button {
	
	private static final String
		UNPRESSED_SOURCE = "assets/GUI/Shop/Button.png",
		PRESSED_SOURCE = "assets/GUI/Shop/ButtonPressed.png";
	
	private final Purchase purchase;
	private final ShopFont font;
	
	public PurchaseButton(Game game, Purchase purchase, Vector2 center) {
		super(center, game.getTexture(UNPRESSED_SOURCE), game.getTexture(PRESSED_SOURCE));
		this.purchase = purchase;
		this.font = new ShopFont();
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		font.draw(batch, "" + purchase.cost(), new Vector2(x(), y()));
	}
}