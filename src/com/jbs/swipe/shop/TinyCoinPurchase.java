package com.jbs.swipe.shop;

import com.jbs.swipe.Game;

public class TinyCoinPurchase extends JBSCoinPurchase {
	
	public TinyCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "assets/GUI/Shop/Coins/ACoupleCoins.png", 1000);
	}

	@Override
	public String name() {
		return "A Couple Coins";
	}

	@Override
	public String cost() {
		return "$0.99";
	}

	@Override
	protected String sku() {
		return "android.test.purchased";
	}
}