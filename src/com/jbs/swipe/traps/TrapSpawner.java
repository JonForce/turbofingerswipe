package com.jbs.swipe.traps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.Font;

public abstract class TrapSpawner extends Button {
	
	private static final String SOURCE = "assets/GUI/Font/Digits2.png";
	protected static final int
		SPAWNER_START_X = 100,
		SPAWNER_MARGIN_X = 50,
		SPAWNER_MARGIN_Y = 10;
	
	private final Trap<?> trap;
	private final Font font;
	
	protected final Game game;
	
	public TrapSpawner(Game game, Vector2 center, Trap<?> trap) {
		super(center, trap.icon());
		this.game = game;
		this.trap = trap;
		this.font = new Font(game.getTexture(SOURCE));
		font.setAlignment(Font.ALIGNMENT_RIGHT);
	}
	
	@Override
	public final void onPress() {
		if (stock() > 0) {
			trap.decreaseStockBy(1);
			spawnTrap();
		}
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		if (stock() > 0)
			super.renderTo(batch);
		else {
			batch.setColor(Color.GRAY);
			super.renderTo(batch);
			batch.setColor(Color.WHITE);
		}
		
		font.renderIntegerTo(batch, trap.stock(), super.x() - width()/4, super.y() + height()/4);
	}
	
	/** Increase the stock of the TrapSpawner's Trap by the specified amount. */
	public final void increaseStockBy(int amount) {
		trap.increaseStockBy(amount);
	}
	
	/** Set the stock of Traps to the specified amount. */
	public final void setStock(int newStock) {
		trap.setStock(newStock);
	}
	
	/** @return the stock of Traps. */
	public int stock() {
		return trap.stock();
	}
	
	public abstract void spawnTrap();
}