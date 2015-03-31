package com.jbs.swipe.shop.purchases;

import com.jbs.swipe.Game;
import com.jbs.swipe.shop.BillingAPI;

public class SmallCoinPurchase extends JBSCoinPurchase {
	
	public SmallCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "Shop/Coins/AFewCoins", 3500);
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

	@Override
	public String desc() { return "[Item desc]"; }
}