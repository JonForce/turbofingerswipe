package com.jbs.swipe.levels.arcade;

import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Row;
import com.jbs.swipe.tiles.RowController;
import com.jbs.swipe.tiles.SwipeTile;

public class ArcadeModeEasy extends ArcadeMode {
	
	private static final int
		INITIAL_TIME_TO_SWIPE = 14000,
		MINIMUM_TIME_TO_SWIPE = 5000,
		TIME_TO_DECREMENT = 50,
		
		INITIAL_ROW_SIZE = 3,
		MAXIMUM_ROW_SIZE = 6,
		
		EXPANSION_INTERVAL = 25,
		EXPANSION_OFFSET =  5;
	
	private final int
		patternScramblePeriod = 10;
	private int
		previousScore;
	
	public ArcadeModeEasy(Game game) {
		super(game);
	}
	
	@Override
	protected int initialTilesPerRow() {
		return 3;
	}
	
	@Override
	protected void initializeDifficulty() {
		// Set the direction of all the Rows to be left.
		for (Row row : this.rows)
			row.setDirection(Row.DIRECTION_LEFT);
		
		// Configure the RowControllers to achieve an easy difficulty.
		for (RowController controller : this.rowControllers) {
			controller.setStartingTimeToSwipe(INITIAL_TIME_TO_SWIPE);
			controller.setMinimumTimeToSwipe(MINIMUM_TIME_TO_SWIPE);
			controller.setTimeToDecrement(TIME_TO_DECREMENT);
			
			controller.setStartingRowSize(INITIAL_ROW_SIZE);
			controller.setMaximumRowSize(MAXIMUM_ROW_SIZE);
			
			controller.setExpansionInerval(EXPANSION_INTERVAL);
			controller.setExpansionOffset(EXPANSION_OFFSET);
		}
	}
	
	@Override
	protected void updateLevelWith(InputProxy input) {
		super.updateLevelWith(input);
		int newScore = score().count();
		if (newScore != previousScore)
			onScoreChange(newScore);
		
		previousScore = score().count();
	}

	@Override
	protected String levelName() {
		return "ArcadeModeEasy";
	}
	
	@Override
	protected void reset() {
		super.reset();
		
		for (SwipeTile tile : tiles())
			if (tile != null)
				tile.reset();
		
		onScoreChange(0);
		
		System.out.println("ArcadeModeEasy.reset()");
	}
	
	protected void onScoreChange(int newScore) {
		if (newScore % patternScramblePeriod == 0)
			for (Row row : super.rows)
				row.scramblePattern();
		
		if (newScore < 7) {
			hideTopRow();
			hideBottomRow();
			
			topRow().setDirection(Row.DIRECTION_LEFT);
			centerRow().setDirection(Row.DIRECTION_LEFT);
			bottomRow().setDirection(Row.DIRECTION_LEFT);
			
			topRow().setSize(3);
			centerRow().setSize(3);
			bottomRow().setSize(3);
		} else if (newScore < 15) {
			if (!topRow().isVisible())
				revealTopRow();
			
			topRow().setSize(4);
			centerRow().setSize(4);
			bottomRow().setSize(4);
		} else if (newScore < 35) {
			if (!bottomRow().isVisible())
				revealBottomRow();
		} else if (newScore < 65) {
			centerRow().setDirection(Row.DIRECTION_RIGHT);
			
			topRow().setSize(2);
			centerRow().setSize(4);
			bottomRow().setSize(2);
		} else if (newScore < 95) {
			topRow().setDirection(Row.DIRECTION_RIGHT);
			centerRow().setDirection(Row.DIRECTION_LEFT);
			bottomRow().setDirection(Row.DIRECTION_RIGHT);
			
			topRow().setSize(5);
			centerRow().setSize(1);
			bottomRow().setSize(5);
		} else if (newScore < 130) {
			topRow().setDirection(Row.DIRECTION_RANDOM);
			centerRow().setDirection(Row.DIRECTION_RANDOM);
			bottomRow().setDirection(Row.DIRECTION_RANDOM);
			
			topRow().setSize(2);
			centerRow().setSize(4);
			bottomRow().setSize(2);
		} else if (newScore < 170) {
			topRow().setDirection(Row.DIRECTION_RANDOM);
			centerRow().setDirection(Row.DIRECTION_RANDOM);
			bottomRow().setDirection(Row.DIRECTION_RANDOM);
			
			topRow().setSize(4);
			centerRow().setSize(2);
			bottomRow().setSize(4);
		} else if (newScore < 240) {
			topRow().setDirection(Row.DIRECTION_RIGHT);
			centerRow().setDirection(Row.DIRECTION_LEFT);
			bottomRow().setDirection(Row.DIRECTION_RIGHT);
			
			topRow().setSize(3);
			centerRow().setSize(6);
			bottomRow().setSize(3);
		} else if (newScore < 300) {
			topRow().setDirection(Row.DIRECTION_RANDOM);
			centerRow().setDirection(Row.DIRECTION_RANDOM);
			bottomRow().setDirection(Row.DIRECTION_RANDOM);
			
			topRow().setSize(5);
			centerRow().setSize(5);
			bottomRow().setSize(5);
		}
	}
}