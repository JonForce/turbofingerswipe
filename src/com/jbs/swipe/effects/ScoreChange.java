package com.jbs.swipe.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.Score;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.shop.ShopFont;

public abstract class ScoreChange implements Renderable {
	
	private LevelState level;
	private int change;
	private ShopFont font;
	
	private long startTime, nextScoreChangeTime, scoreChangeInterval = 100;
	private Vector2 startPosition, endPosition;
	private float animationDuration;
	
	public ScoreChange(LevelState level, Vector2 position, int change) {
		this.level = level;
		this.startPosition = position.cpy();
		this.endPosition = position.cpy();
		this.change = change;
		this.font = new ShopFont();
		font.setColor((change > 0)? Color.GREEN : Color.RED);
		this.startTime = System.currentTimeMillis();
		
		nextScoreChangeTime = System.currentTimeMillis() + scoreChangeInterval;
	}
	
	public ScoreChange fling(Vector2 direction, float speed) {
		endPosition.set(startPosition.cpy().add(direction.cpy().mul(speed)));
		animationDuration = startPosition.dst(endPosition) / speed;
		return this;
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		font.draw(batch, "" + Math.abs(change), startPosition.cpy().add((deltaTime() / animationDuration)*(endPosition.x - startPosition.x), (deltaTime() / animationDuration)*(endPosition.y - startPosition.y)));
		
		if (System.currentTimeMillis() > nextScoreChangeTime) {
			nextScoreChangeTime = System.currentTimeMillis() + scoreChangeInterval;
			if (change < 0) {
				level.score().decrement();
				if (level.score().count() == 0)
					change = 0;
				else
					change ++;
			} else if (change > 0) {
				level.score().increment();
				change --;
			} else
				remove();
		}
	}
	
	public abstract void remove();
	
	protected final float deltaTime() {
		return (System.currentTimeMillis() - startTime);
	}
}