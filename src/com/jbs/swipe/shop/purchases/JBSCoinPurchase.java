package com.jbs.swipe.shop.purchases;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.User;
import com.jbs.swipe.shop.BillingAPI;
import com.jbs.swipe.shop.BillingCallback;

public abstract class JBSCoinPurchase implements Purchase {
	
	private final BillingAPI billingAPI;
	private final TextureRegion icon;
	private final int coins;
	
	public JBSCoinPurchase(Game game, BillingAPI billingAPI, String iconSource, int coins) {
		this.billingAPI = billingAPI;
		this.icon = Assets.getAtlasRegion(iconSource);
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
	public final TextureRegion icon() {
		return icon;
	}
	
	@Override
	public final int itemCount() {
		return coins;
	}
	
	protected abstract String sku();
}