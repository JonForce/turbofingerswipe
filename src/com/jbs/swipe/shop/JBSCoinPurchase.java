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
	
	public void giveCoinsTo(User user) {
		user.addCoins(coins);
	}
	
	@Override
	public void attemptPurchase(final User user) {
		billingAPI.requestPurchase(sku(), new BillingCallback() {
			@Override
			public void callback(boolean purchaseSuccess) {
				System.out.println("BillingCallback : " + purchaseSuccess);
				// If the User paid us,
				if (purchaseSuccess)
					// Give him the coins.
					giveCoinsTo(user);
			}
		});
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