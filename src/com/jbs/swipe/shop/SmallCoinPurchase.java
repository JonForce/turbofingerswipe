package com.jbs.swipe.shop;

import com.jbs.swipe.Game;

public class SmallCoinPurchase extends JBSCoinPurchase {
	
	public SmallCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "assets/GUI/Shop/Coins/AFewCoins.png", 3500);
	}
	
	@Override
	public String name() {
		return "A Few Coins";
	}
	
	@Override
	public String cost() {
		return "$1.99";
	}
	
	@Override
	protected String sku() {
		return "small_coin_purchase.y2x5lk9w15";
	}
}