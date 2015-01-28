package com.jbs.swipe.shop;

import com.jbs.swipe.Game;

public class LargeCoinPurchase extends JBSCoinPurchase {
	
	public LargeCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "assets/GUI/Shop/Coins/ABoatLoadOfCoins.png", 20000);
	}
	
	@Override
	public String name() {
		return "A Shitload Of Coins";
	}
	
	@Override
	public String cost() {
		return "$4.99";
	}

	@Override
	protected String sku() {
		return "0";
	}
}