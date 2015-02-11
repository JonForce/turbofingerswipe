package com.jbs.swipe.shop;

public abstract class BillingAPI {
	
	public abstract void destroy();
	
	public abstract void requestPurchase(String sku, BillingCallback callback);
	
}