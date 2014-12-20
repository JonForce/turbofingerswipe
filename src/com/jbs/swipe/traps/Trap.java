package com.jbs.swipe.traps;

import com.badlogic.gdx.graphics.Texture;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.SwipeTile;

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
		game.preferences().putInteger(trapName() + ":stock", newStock);
	}
	
	/** @return the number of Traps available to used. */
	public final int stock() {
		return game.preferences().getInteger(trapName() + ":stock");
	}
	
	/** @return the Objects that can potentially be affected by the Trap. */
	protected Target[] targets() {
		return this.targets;
	}
	
	public abstract Texture icon();
	protected abstract void activate();
	protected abstract String trapName();
}