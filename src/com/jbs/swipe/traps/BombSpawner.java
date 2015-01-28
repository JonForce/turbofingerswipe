package com.jbs.swipe.traps;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;

public class BombSpawner extends TrapSpawner {
	
	protected final ArrayList<Bomb> bombs;
	
	public BombSpawner(Game game, int position) {
		super(game, position, new Bomb(game));
		this.bombs = new ArrayList<Bomb>(10);
	}
	
	@Override
	public final void spawnTrap() {
		Bomb bomb = new Bomb(game);
		bomb.setTargets(game.levelState().tiles());
		bomb.activate();
		bombs.add(bomb);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		super.updateWith(input);
		for (Bomb bomb : bombs)
			if (bomb != null)
				bomb.updateWith(input);
		clearExpiredBombsFrom(this.bombs);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		for (Bomb bomb : bombs)
			if (bomb != null)
				bomb.renderTo(batch);
	}
	
	/** Clear all Bombs from the list that are expired. */
	protected void clearExpiredBombsFrom(List<Bomb> list) {
		for (int i = bombs.size() - 1; i >= 0; i --)
			if (bombs.get(i) != null && bombs.get(i).expired())
				bombs.remove(i);
	}
}