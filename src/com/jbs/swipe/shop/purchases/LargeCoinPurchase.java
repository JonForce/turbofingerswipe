package com.jbs.swipe.shop.purchases;

import com.jbs.swipe.Game;
import com.jbs.swipe.shop.BillingAPI;

public class LargeCoinPurchase extends JBSCoinPurchase {
	
	public LargeCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "Shop/Coins/ABoatLoadOfCoins", 15000);
	}
	
	@Override
	public String name() {
		return "A Ton Of Coins";
	}
	
	@Override
	public String cost() {
		return "$4.99";
	}

	@Override
	protected String sku() {
		return "large_coin_purchase.cx1v5eqse3";
	}
}