package com.jbs.swipe.shop;

public class DefaultBillingAPI extends BillingAPI {
	
	
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void requestPurchase(String sku) {
		System.out.println("Default Billing API recieved purchase request : " + sku);
	}
}