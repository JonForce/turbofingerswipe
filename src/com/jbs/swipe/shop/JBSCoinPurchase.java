package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.Texture;
import com.jbs.swipe.Game;
import com.jbs.swipe.User;

public abstract class JBSCoinPurchase implements Purchase {
	
	private final BillingAPI billingAPI;
	private final Texture icon;
	private final int coins;
	
	public JBSCoinPurchase(Game game, BillingAPI billingAPI, String iconSource, int coins) {
		this.billingAPI = billingAPI;
		this.icon = game.getTexture(iconSource);
		this.coins = coins;
	}
	
	@Override
	public void giveItemsTo(User user) {
		user.addCoins(coins);
	}
	
	@Override
	public boolean retrievePayment(User user) {
		billingAPI.requestPurchase(sku());
		return false;
	}
	
	@Override
	public final Texture icon() {
		return icon;
	}
	
	@Override
	public final int itemCount() {
		return coins;
	}
	
	protected abstract String sku();
}