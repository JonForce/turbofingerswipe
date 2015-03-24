package com.jbs.swipe.levels.formation;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.effects.Animator;
import com.jbs.swipe.levels.LevelState;
import com.jbs.swipe.levels.TutorialState;
import com.jbs.swipe.levels.arcade.ArcadeTutorialState;
import com.jbs.swipe.levels.formation.TileLines.Dimension;
import com.jbs.swipe.tiles.SwipeTile.TileState;
import com.jbs.swipe.tiles.TileListener;
import com.jbs.swipe.tiles.SwipeTile;

public final class FormationMode extends LevelState implements TileListener {
	
	private ArrayList<SwipeTile> tiles;
	private Formation[] formations;
	private int difficulty, currentFormation;
	
	/** Formation Mode is a game mode where tiles are generated in combo-swipable formations,
	 * making it easy for the player to rack up big combos. */
	public FormationMode(Game game) {
		super(game);
	}
	
	@Override
	public void recieveTileStateChange(final SwipeTile tile, TileState oldState, TileState newState) {
		if (newState == TileState.CORRECTLY_SWIPED) {
			// Increment the correct swipe count.
			super.score().increment();
		} else if (newState == TileState.INCORRECTLY_SWIPED) {
			final float
				SHAKE_AMPLITUDE = 100f,
				SHAKE_DURATION = 300; // Milliseconds.
			final int
				SHAKES = 5;
			
			new Animator(game())
				// Shake the Tile in the opposite direction that it was pointing.
				.shakeTile(tile, SwipeTile.createSwipe(tile.direction(), SHAKE_AMPLITUDE).mul(-1), SHAKE_DURATION, SHAKES)
				.fadeTileAway(tile, SHAKE_DURATION);
			
			delayEvent(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					tile.setState(TileState.FINISHED);
				}
			}, SHAKE_DURATION);
			
			super.addScoreChange(new Vector2(tile.x(), tile.y()), -10);
		} else if (newState == TileState.EXPIRED) {
			final float
				SHAKE_AMPLITUDE = 100f,
				SHAKE_DURATION = 300; // Milliseconds.
			final int
				SHAKES = 5;
			
			new Animator(game())
				// Shake the Tile in the opposite direction that it was pointing.
				.shakeTile(tile, SwipeTile.createSwipe(tile.direction(), SHAKE_AMPLITUDE).mul(-1), SHAKE_DURATION, SHAKES)
				.fadeTileAway(tile, SHAKE_DURATION);
			
				delayEvent(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						fail();
					}
				}, SHAKE_DURATION * 1.5f);
		} else if (newState == TileState.FINISHED) {
			remove(tile);
		}
	}
	
	@Override
	protected void renderLevelTo(SpriteBatch batch) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.renderTo(batch);
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.updateWith(input);
		
		if (tiles.isEmpty()) {
			if (++currentFormation >= formations.length) {
				currentFormation = 0;
				formations = createFormations(++difficulty);
			}
			
			for (SwipeTile newTile : formations[currentFormation].create())
				tiles.add(newTile);
		}
	}
	
	@Override
	protected void create() {
		this.tiles = new ArrayList<SwipeTile>();
		this.difficulty = 1;
		this.currentFormation = 0;
		this.formations = createFormations(difficulty);
		
		for (SwipeTile newTile : formations[currentFormation].create())
			this.tiles.add(newTile);
	}
	
	@Override
	public SwipeTile[] tiles() {
		return tiles.toArray(new SwipeTile[tiles.size()]);
	}
	
	/** @return true if the Level contains the Tile. */
	public final boolean contains(SwipeTile tile) {
		return this.tiles.contains(tile);
	}
	
	/** Safely remove the specified Tile from the Level. */
	public final void remove(SwipeTile tile) {
		tiles.remove(tile);
	}
	
	@Override
	protected void reset() {
		// Clear the TouchManager.
		super.touchManager().clearListeners();
		tiles.clear();
	}
	
	@Override
	protected TutorialState createTutorial() {
		return new ArcadeTutorialState(game(), this);
	}
	
	@Override
	protected String levelName() {
		return "NormalMode";
	}
	
	/** @return a sequence of Tile formations based on the given difficulty. */
	protected Formation[] createFormations(int difficulty) {
		if (difficulty < 1)
			throw new RuntimeException("Difficulty must be greater than or equal to 1.");
		if (difficulty == 1)
			return new Formation[] {
				new TileLines(game(), this, 18, Dimension.VERTICAL),
				new TileLines(game(), this, 18, Dimension.HORIZONTAL),
				new TileSpiral(game(), this, 10),
				new TileSpiral(game(), this, 10),
			};
		else if (difficulty == 2)
			return new Formation[] {
				new TileLines(game(), this, 18, Dimension.HORIZONTAL),
				new TileSnake(game(), this, 10),
				new TileStorm(game(), this, 30),
			};
		else if (difficulty == 3)
			return new Formation[] {
				new TileSpiral(game(), this, 20),
				new TileLines(game(), this, 20, Dimension.VERTICAL),
				new TileSnake(game(), this, 20),
			};
		else if (difficulty == 4)
			return new Formation[] {
				new TileLines(game(), this, 50, Dimension.HORIZONTAL),
				new TileSpiral(game(), this, 40),
				new TileSnake(game(), this, 30),
				new TileBox(game(), this, 20),
			};
		else if (difficulty == 5)
			return new Formation[] {
				new TileStorm(game(), this, 60),
				new TileSpiral(game(), this, 50),
			};
		else
			return new Formation[] {
				new TileLines(game(), this, difficulty * 10, Dimension.HORIZONTAL),
				new TileLines(game(), this, difficulty * 10, Dimension.VERTICAL),
				new TileSpiral(game(), this, difficulty * 10),
				new TileStorm(game(), this, difficulty * 10),
				new TileSnake(game(), this, difficulty * 10),
			};
	}
	
	/** Delay the callback by the specified time in milliseconds (I think, idk). */
	private void delayEvent(TweenCallback callback, float delay) {
		Tween.call(callback).delay(delay).start(game().tweenManager());
	}
}