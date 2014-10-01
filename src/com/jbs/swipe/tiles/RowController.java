package com.jbs.swipe.tiles;

public class RowController {
	
	private int
		/* Expand the Row every n'th point */
		expansionInterval = 3,
		/* The offset of when to expand the Row */
		expansionOffset = 0,
		/* The initial size of the Row */
		startingRowSize,
		/* The maximum number of Tiles in each of the Level's Rows */
		maximumRowSize = 7,
		/* The initial number of milliseconds it takes for Tiles to expire. */
		startingTimeToSwipe = 15000,
		/* The minimum number of milliseconds it takes for Tiles to expire. */
		minimumTimeToSwipe = 9000,
		/* The number of milliseconds to deplete the time it takes for
		 * Tiles to expire per point. */
		timeToDecrement = 50;
	
	private final Row row;
	
	public RowController(Row row) {
		this.row = row;
		setStartingRowSize(row().numberOfTiles());
	}
	
	/* Set the number of milliseconds to deplete the time it takes for
	 * Tiles to expire per point. */
	public void setTimeToDecrement(int timeToDecrement) {
		this.timeToDecrement = timeToDecrement;
	}
	
	/* Set the minimum number of milliseconds it takes for Tiles to expire. */
	public void setMinimumTimeToSwipe(int newMinimum) {
		this.minimumTimeToSwipe = newMinimum;
	}
	
	/* Set the initial number of milliseconds it takes for Tiles to expire. */
	public void setStartingTimeToSwipe(int newStartingTime) {
		this.startingTimeToSwipe = newStartingTime;
	}
	
	/* Set the number of tiles each row should have when the Controller is reset. */
	public void setStartingRowSize(int newStartingSize) {
		this.startingRowSize = newStartingSize;
	}
	
	/* Set the offset of when the Controller will begin to expand the
	 * controlled Row. */
	public void setExpansionOffset(int newOffset) {
		this.expansionOffset = newOffset;
	}
	
	/* Set the interval at which to expand the controlled Row. */
	public void setExpansionInerval(int newInterval) {
		this.expansionInterval = newInterval;
	}
	
	/* Set the maximum size of the Controlled Row. */
	public void setMaximumRowSize(int newMaximum) {
		this.maximumRowSize = newMaximum;
	}
	
	/* Expand or contract the controlled Row to use the RowController's intended settings. */
	public void update(int score) {
		// Set the controlled Row to be of the needed width.
		setRowSize(intendedRowWidth(score));
		
		// Set the controlled Row's timeToSwipe.
		row().setTimeToSwipe(intendedTimeToSwipe(score));
	}
	
	/* Expand/Contract the the Row to be of the specified width (in Tiles). */
	public final void setRowSize(Row row, int newSize) {
		// While the Row is not the desired size,
		while (row.numberOfTiles() != newSize)
			// If the Row is larger than the desired size,
			if (row.numberOfTiles() > newSize)
				// Contract the Row to be one Tile smaller.
				row.contract();
			// Else the Row is smaller than the desired size,
			else
				// Expand the Row to be one Tile larger.
				row.expand();
	}
	/* Expand/Contract the controlled Row to be of the specified width (in Tiles). */
	public final void setRowSize(int newSize) {	setRowSize(this.row(), newSize); }
	
	/* @return the controlled Row. */
	public final Row row() {
		return this.row;
	}
	
	/* Compute the width of the Row controlled by the RowController based on
	 * the specified score. */
	protected int intendedRowWidth(int score) {
		if (score < 0)
			throw new RuntimeException("Score must be >= 0");
		
		int width = startingRowSize;
		for (int iteration = 0; iteration != score; iteration ++)
			if (iteration % expansionInterval == expansionOffset)
				width ++;
		
		// If the width is larger than the maximum Row size,
		if (width > maximumRowSize)
			// Cap the width at its maximum size.
			width = maximumRowSize;
		
		return width;
	}
	
	/* Compute the amount of time the player should have to swipe each of the Row's tiles before
	 * they expire. */
	protected int intendedTimeToSwipe(int score) {
		if (score < 0)
			throw new RuntimeException("Score must be >= 0");
		else if (timeToDecrement < 0)
			throw new RuntimeException("timeToDecrement must be >= 0");
		
		final int
			/* The amount of time that the timeToSwipe has gone down by. */
			decrementation = timeToDecrement * score,
			/* The timeToSwipe, not taking into consideration the minimumTimeToSwipe. */
			time = startingTimeToSwipe - decrementation;
		
		// Cap the time-to-swipe at its minimum.
		return Math.max(time, minimumTimeToSwipe);
	}
}