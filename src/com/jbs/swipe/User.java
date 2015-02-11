package com.jbs.swipe;

import com.badlogic.gdx.Preferences;
import com.jbs.swipe.traps.Trap;

public class User {
	
	private static final String
		// User account properties :
		COIN_COUNT = ":CoinCount",
		HIGH_SCORE = ":HighScore",
		TRAP_STOCK = ":TrapStock";
	
	private final int ID;
	private final Preferences preferences;
	
	/** Create an access point to save User data.
	 * @param ID The User's unique ID. */
	public User(int ID, Preferences preferences) {
		this.ID = ID;
		this.preferences = preferences;
	}
	
	/** @return the User's highest score. */
	public int highScore() {
		return preferences().getInteger(username() + HIGH_SCORE);
	}
	
	/** @return the number of jbs coins that the User has. */
	public int jbsCoins() {
		return preferences().getInteger(username() + COIN_COUNT);
	}
	
	/** @return the number of the specified trap that the User has.
	 * @param trap The Trap to check the User's stock of. */
	public int stockOf(Trap<?> trap) {
		return preferences().getInteger(username() + TRAP_STOCK + ":" + trap.trapName());
	}
	
	/** Set the User's stock of the specified Trap. */
	public void setStock(Trap<?> trap, int newStock) {
		preferences().putInteger(username() + TRAP_STOCK + ":" + trap.trapName(), newStock);
		save();
	}
	
	/** Set the User's High Score. */
	public void setHighScore(int newScore) {
		preferences().putInteger(username() + HIGH_SCORE, newScore);
		save();
	}
	
	/** Set the number of coins that User has. */
	public void setCoinCount(int newCount) {
		preferences().putInteger(username() + COIN_COUNT, newCount);
		save();
	}
	
	/** Add n number of coins to the User's account. */
	public final void addCoins(int coinsToAdd) {
		setCoinCount(jbsCoins() + coinsToAdd);
		
		if (jbsCoins() < 0)
			throw new RuntimeException("User cannot have a negative number of jbs coins.");
	}
	
	/** Remove n number of coins from the User's account. */
	public final void removeCoins(int coinsToRemove) {
		addCoins(-coinsToRemove);
	}
	
	/** Save the preferences to disk. */
	public void save() {
		preferences().flush();
	}
	
	/** @return the Preferences that User data is saved to. */
	protected Preferences preferences() {
		return preferences;
	}
	
	/** @return the User's name, a unique string that can be used to store data to. */
	protected String username() {
		return "User_" + ID;
	}
}