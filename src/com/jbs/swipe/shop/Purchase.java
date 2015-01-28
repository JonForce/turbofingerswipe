package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.Texture;
import com.jbs.swipe.User;

public interface Purchase {
	
	/** Fulfill the Purchase by giving the items to the user. */
	public void giveItemsTo(User user);
	
	/** Retrieve payment from the user. If payment could not be tendered, return false.
	 * @return true if the user successfully paid. */
	public boolean retrievePayment(User user);
	
	/** @return the icon that denotes the item that may be bought. */
	public Texture icon();
	
	/** @return the name of the item to be bought. */
	public String name();
	
	/** @return the number of items that are given per purchase. */
	public int itemCount();
	
	/** @return the cost of the Purchase. */
	public String cost();
}