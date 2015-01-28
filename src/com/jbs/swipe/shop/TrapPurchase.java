package com.jbs.swipe.shop;

import com.badlogic.gdx.graphics.Texture;
import com.jbs.swipe.User;
import com.jbs.swipe.traps.Trap;

public class TrapPurchase implements Purchase {
	
	private final Trap<?> trap;
	
	public TrapPurchase(Trap<?> trap) {
		this.trap = trap;
	}
	
	@Override
	public void giveItemsTo(User user) {
		trap.increaseStockBy(trap.trapsPerPurchase());
	}

	@Override
	public boolean retrievePayment(User user) {
		if (user.jbsCoins() < trap.cost())
			// User is too damn poor to afford the Trap.
			return false;
		else
			user.removeCoins(trap.cost());
		// Payment was successful.
		return true;
	}

	@Override
	public Texture icon() {
		return trap.icon();
	}

	@Override
	public String name() {
		return trap.trapName();
	}

	@Override
	public int itemCount() {
		return trap.trapsPerPurchase();
	}
	
	@Override
	public String cost() {
		return "" + trap.cost();
	}
}