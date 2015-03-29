package com.jbs.swipe.shop.purchases;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jbs.swipe.User;
import com.jbs.swipe.traps.Trap;

public class TrapPurchase implements Purchase {
	
	private final Trap<?> trap;
	
	public TrapPurchase(Trap<?> trap) {
		this.trap = trap;
	}
	
	public void giveTrapsTo(User user) {
		trap.increaseStockBy(trap.trapsPerPurchase());
	}
	
	@Override
	public void attemptPurchase(User user) {
		if (user.jbsCoins() < trap.cost())
			// User is too damn poor to afford the Trap.
			return;
		else
			user.removeCoins(trap.cost());
		// Payment was successful. Give the user the items.
		giveTrapsTo(user);
	}
	
	@Override
	public TextureRegion icon() {
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