package com.jbs.swipe.traps;

import com.badlogic.gdx.graphics.Texture;
import com.jbs.swipe.Game;

public abstract class Trap<Target> {
	
	protected final Game game;
	private Target[] targets;
	
	public Trap(Game game) {
		this.game = game;
	}
	
	/**
	 * Use a Trap on the specified target. Throws a RuntimeException if you have no
	 * traps remaining.
	 * @param targets The targets of the Trap.
	 */
	public final void useTrapOn(Target... targets) {
		if (stock() <= 0)
			throw new RuntimeException("Cannot use trap, "+stock()+" traps in stock.");
		
		decreaseStockBy(1);
		
		activate();
	}
	
	/** Set the Objects that can be potentially affected by the Trap. */
	public final void setTargets(Target... targets) {
		this.targets = targets;
	}
	
	/** Decrease the number of Traps available to use by the specified amount. */
	public final void decreaseStockBy(int amount) {
		setStock(stock() - amount);
	}
	
	/** Increase the number of Traps available to use by the specified amount. */
	public final void increaseStockBy(int amount) {
		setStock(stock() + amount);
	}
	
	/** Set the number of Traps available to use to newStock. */
	public final void setStock(int newStock) {
		game.user().setStock(this, newStock);
	}
	
	/** @return the number of Traps available to used. */
	public final int stock() {
		return game.user().stockOf(this);
	}
	
	/** @return the default Texture based on the Trap's name. */
	public final Texture texture() {
		return game.getTexture("assets/Traps/" + trapName() + ".png");
	}
	
	/** @return the Trap's desired icon. */
	public Texture icon() {
		return texture();
	}
	
	/** @return the Objects that can potentially be affected by the Trap. */
	protected Target[] targets() {
		return this.targets;
	}
	
	/** @return the name of the Trap. This should remain constant. */
	public abstract String trapName();
	/** @return the cost of the Trap in JBS coins. */
	public abstract int cost();
	/** @return the number of Traps to get per purchase. */
	public abstract int trapsPerPurchase();
	protected abstract void activate();
}