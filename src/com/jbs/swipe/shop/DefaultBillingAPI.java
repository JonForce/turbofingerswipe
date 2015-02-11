package com.jbs.swipe.shop;

public class DefaultBillingAPI extends BillingAPI {
	
	
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void requestPurchase(String sku, BillingCallback callback) {
		System.out.println("Default Billing API recieved purchase request : " + sku + ". CallingBack True.");
		callback.callback(true);
	}
}