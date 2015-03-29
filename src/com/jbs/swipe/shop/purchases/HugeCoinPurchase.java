package com.jbs.swipe.shop.purchases;

import com.jbs.swipe.Game;
import com.jbs.swipe.shop.BillingAPI;

public class HugeCoinPurchase extends JBSCoinPurchase {
	
	public HugeCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "Shop/Coins/WayTooManyCoins", 99999);
	}
	
	@Override
	public String name() {
		return "Too Many Coins!!!";
	}
	
	@Override
	public String cost() {
		return "$9.99";
	}
	
	@Override
	protected String sku() {
		return "huge_coin_purchase.he8dc620b2a";
	}
}