package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.shop.purchases.Purchase;
import com.jbs.swipe.shop.purchases.TrapPurchase;
import com.jbs.swipe.traps.Trap;

public class ItemWindow implements Updatable, Renderable {
	
	protected final String
		WINDOW_SOURCE = "Shop/ItemWindow";
	
	protected final Purchase purchase;
	
	private final Graphic window, purchaseIcon;
	private final PurchaseButton button;
	private final ShopFont primaryFont, secondaryFont;
	private final float
		titleMargin = 15,
		iconMargin = 100;
	
	public ItemWindow(final Game game, final Purchase purchase, Vector2 center) {
		this.purchase = purchase;
		this.window = new Graphic(center, Assets.getAtlasRegion(WINDOW_SOURCE));
		
		// Create the Purchase Button at the center of the window.
		this.button = new PurchaseButton(game, purchase, new Vector2()) {
			@Override
			public float x() {
				return window.x();
			}
			@Override
			public float y() {
				// Move the Button up to have the Button's bottom edge on the bottom of the window.
				// Center the Button at the bottom of the window.
				return window.y() - window.height()/2 + button.height();
			}
			@Override
			public void onRelease() {
				System.out.println("Attempting to purchase " + purchase.name());
				purchase.attemptPurchase(game.user());
			}
		};
		
		this.purchaseIcon = new Graphic(new Vector2(window.x(), window.y()+500f), purchase.icon());
		final float
			MAX_ICON_WIDTH = window.width() - iconMargin*2,
			MAX_WIDTH = window.width() - titleMargin*2;
		if (purchaseIcon.width() > MAX_ICON_WIDTH)
			purchaseIcon.scale(MAX_ICON_WIDTH / purchaseIcon.width());
		
		this.primaryFont = new ShopFont();
		primaryFont.scaleToWidth(MAX_WIDTH, purchase.name());
		this.secondaryFont = new ShopFont();
		
		//secondaryFont.scaleToWidth(MAX_WIDTH, purchase.itemCount()+"");
		secondaryFont.setScale(0.5f);
	
	}
	
	public ItemWindow(Game game, Purchase purchase) {
		this(game, purchase, new Vector2());
	}
	
	public ItemWindow(Game game, Trap<?> trap, Vector2 center) {
		this(game, new TrapPurchase(trap), center);
	}
	
	/** Render the ItemWindow at the specified center. */
	public void renderTo(SpriteBatch batch, float x, float y) {
		window.setPosition(x, y);
		purchaseIcon.setPosition(x, y+40f);
		
		window.renderTo(batch);
		
		// Draw the name of the Purchase to the top of the window.
		primaryFont.draw(batch, purchase.name(), new Vector2(window.x(), window.y() + window.height()/2.5f));
		
		// Draw the Purchase Icon to the center of the window.
		purchaseIcon.renderTo(batch);
		// Render the item count at the bottom right corner of the Trap icon.
		secondaryFont.draw(batch, "x" + purchase.itemCount(), new Vector2(purchaseIcon.x(), purchaseIcon.y() - purchaseIcon.height()/2));
		
		// Draw the Button to the window.
		button.renderTo(batch);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		renderTo(batch, window.x(), window.y());
	}
	
	@Override
	public void updateWith(InputProxy input) {
		button.updateWith(input);
	}
	
	/** @return the window. */
	public Graphic window() {
		return this.window;
	}
}