package com.jbs.swipe.traps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.Font;

public abstract class TrapSpawner implements Renderable, Updatable {
	
	private static final String
		SOURCE = "assets/GUI/Font/Digits2.png",
		BG_SOURCE = "assets/Traps/IconBG.png";
	private static final int
		SPACING = 100,
		BOTTOM_MARGIN = 5,
		WIDTH = 100,
		HEIGHT = 100;
	private static final float
		DEFAULT_BG_SCALE = 1.75f;
	
	private final Trap<?> trap;
	private final Font font;
	private final Graphic bg;
	private final Button button;
	
	protected final Game game;
	
	public TrapSpawner(Game game, int position, final Trap<?> trap) {
		this.game = game;
		this.trap = trap;
		
		this.font = new Font(game.getTexture(SOURCE));
		font.setAlignment(Font.ALIGNMENT_RIGHT);
		
		this.button = new Button(new Vector2(position * (WIDTH + SPACING), HEIGHT + BOTTOM_MARGIN), trap.texture()) {
			@Override
			public final void onPress() {
				if (stock() > 0) {
					trap.decreaseStockBy(1);
					spawnTrap();
				}
			}
		};
		button.setSize(WIDTH, HEIGHT);
		
		this.bg = new Graphic(new Vector2(), game.getTexture(BG_SOURCE)) {
			@Override
			public float x() { return button.x(); }
			@Override
			public float y() { return button.y(); }
		};
		bg.setSize(button.width(), button.height());
		bg.scale(DEFAULT_BG_SCALE);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		renderBGTo(batch);
		
		if (stock() > 0)
			button.renderTo(batch);
		else {
			batch.setColor(Color.GRAY);
			button.renderTo(batch);
			batch.setColor(Color.WHITE);
		}
		
		font.renderIntegerTo(batch, trap.stock(), button.x() - width()/4, y() + height()/4);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		button.updateWith(input);
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
	
	/** @return the width of the TrapSpawner (in pixels). */
	public float width() {
		return WIDTH;
	}
	
	/** @return the height of the TrapSpawner (in pixels). */
	public float height() {
		return HEIGHT;
	}
	
	/** @return the TrapSpawner's x-coordinate. */
	public float x() {
		return button.x();
	}
	
	/** @return the TrapSpawner's y-coordinate. */
	public float y() {
		return button.y();
	}
	
	protected void renderBGTo(SpriteBatch batch) {
//		final float
//			scale = 1.75f,
//			width = width() * scale,
//			height = height() * scale,
//			x = x() - width/2,
//			y = y() - height/2;
//		batch.draw(game.getTexture(BG_SOURCE), x, y, width, height);
		bg.renderTo(batch);
	}
	
	public abstract void spawnTrap();
}