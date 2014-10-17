package com.jbs.swipe.levels.arcade;

import com.jbs.framework.io.InputProxy;
import com.jbs.swipe.Game;
import com.jbs.swipe.tiles.Row;
import com.jbs.swipe.tiles.RowController;

public class ArcadeModeEasy extends ArcadeMode {
	
	private static final int
		INITIAL_TIME_TO_SWIPE = 17500,
		MINIMUM_TIME_TO_SWIPE = 10000,
		TIME_TO_DECREMENT = 40,
		
		INITIAL_ROW_SIZE = 4,
		MAXIMUM_ROW_SIZE = 6,
		
		SCORE_TO_REVEAL_ROWS_AT = 5,
		
		EXPANSION_INTERVAL = 15,
		EXPANSION_OFFSET =  5;
	
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
	public void updateWith(InputProxy input) {
		super.updateWith(input);
		if (score() >= SCORE_TO_REVEAL_ROWS_AT) {
			// Reveal Rows if they arnt already revealed.
			
			if (!bottomRow().visible())
				revealBottomRow();
			if (!topRow().visible())
				revealTopRow();
		} else if (score() < SCORE_TO_REVEAL_ROWS_AT) {
			// Hide Rows if they are visible.
			
			if (bottomRow().visible())
				hideBottomRow();
			if (topRow().visible())
				hideTopRow();
		}
		
	}
}