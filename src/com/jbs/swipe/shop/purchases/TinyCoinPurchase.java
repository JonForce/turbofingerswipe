package com.jbs.swipe.shop.purchases;

import com.jbs.swipe.Game;
import com.jbs.swipe.shop.BillingAPI;

public class TinyCoinPurchase extends JBSCoinPurchase {
	
	public TinyCoinPurchase(Game game, BillingAPI billingAPI) {
		super(game, billingAPI, "Shop/Coins/ACoupleCoins", 1000);
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
		return "tiny_coin_purchase.2li2h77nnczxuhh";
	}

	@Override
	public String desc() { return "[Item desc]"; }
}