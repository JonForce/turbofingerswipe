package com.jbs.swipe.shop.purchases;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jbs.swipe.User;

public interface Purchase {
	
	/** Retrieve payment from the user. If payment could not be tendered, return false.
	 * @return true if the user successfully paid. */
	public void attemptPurchase(User user);
	
	/** @return the icon that denotes the item that may be bought. */
	public TextureRegion icon();
	
	/** @return the name of the item to be bought. */
	public String name();
	
	/** @return the number of items that are given per purchase. */
	public int itemCount();
	
	/** @return the cost of the Purchase. */
	public String cost();

	public String desc();
}