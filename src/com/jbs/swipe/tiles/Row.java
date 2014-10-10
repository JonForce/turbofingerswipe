package com.jbs.swipe.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.io.InputProxy;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.util.Updatable;
import com.jbs.swipe.Game;
import com.jbs.swipe.TouchManager;

public class Row implements SwipeListener, Renderable, Updatable {
	
	public static final int
		DIRECTION_LEFT = -1,
		DIRECTION_RANDOM = 0,
		DIRECTION_RIGHT = 1;
	
	private static final float
		DEFAULT_SCALE = .5f;
	
	/* The SwipeListener to notify of all the Row's SwipeTile's events. */
	protected SwipeListener listener;
	
	protected final Game game;
	
	/* The positions on the Screen of the slots. */
	private Vector2[] slotPositions;
	private SwipeTile[] tiles;
	private TouchManager touchManager;
	
	// The center of the Row.
	private final Vector2 center;
	
	private float
		animationSpeed = .05f; // The speed of which to animate the Row when a Tile is removed.
	
	private int
		numberOfSlots,
		numberOfTiles,
		direction = DIRECTION_LEFT, // The direction to move the Row when a Tile is removed.
		timeToSwipe = 10000; // The number of milliseconds the player will have to swipe the Tile.
	
	public Row(Game game, Vector2 center, int numberOfTiles) {
		this.game = game;
		this.center = center;
		
		this.numberOfTiles = numberOfTiles;
		this.numberOfSlots = numberOfTiles + 2;
		this.slotPositions = new Vector2[numberOfSlots];
		this.tiles = new SwipeTile[numberOfTiles];
		
		// Initialize the slot positions.
		initializeSlotPositions(center, this.spacing());
		
		for (int i = 0; i != numberOfTiles; i ++)
			tiles[i] = createTileAt(i, timeToSwipe, DEFAULT_SCALE, DEFAULT_SCALE);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		renderTilesTo(batch);
	}
	
	@Override
	public void updateWith(InputProxy input) {
		updateTilesWith(input);
	}
	
	/* Render all the SwipeTiles in the Row to the SpriteBatch. */
	public void renderTilesTo(SpriteBatch batch) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.renderTo(batch);
	}
	
	/* Update all the SwipeTiles in the Row with the InputProxy. */
	public void updateTilesWith(InputProxy input) {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.updateWith(input);
	}
	
	/* Reset all the SwipeTiles in the Row. */
	public void resetTiles() {
		for (SwipeTile tile : tiles)
			if (tile != null)
				tile.reset();
	}
	
	/* Set the direction for the Row to move in when expanding and collapsing. */
	public void setDirection(int newDirection) {
		final String errorMessage = "Error in setDirection("+newDirection+") : ";
		// Assert that the direction is a valid direction.
		if (newDirection > 1)
			throw new RuntimeException(errorMessage + "NewDirection must be <= 1");
		else if (newDirection < -1)
			throw new RuntimeException(errorMessage + "NewDirection must be >= -1");
		
		// Set the direction of the Row to collapse in.
		this.direction = newDirection;
	}
	
	/* Set the time (in milliseconds) that the player has to swipe a SwipeTile created by the Row. */
	public void setTimeToSwipe(int newTimeToSwipe) {
		this.timeToSwipe = newTimeToSwipe;
	}
	
	/* Set the Listener to notify of all the Row's SwipeTile's events. */
	public void setSwipeListener(SwipeListener newListener) {
		this.listener = newListener;
	}
	
	/* Collapse the Row over it's n'th Tile. */
	public void collapseTile(int tileToDissolve, int direction) {
		String errorMessage = "Error in collapseTile("+tileToDissolve+", "+direction+") : ";
		
		// Assert that the specified tile-to-collapse is a tile.
		if (tileToDissolve < 0)
			throw new RuntimeException(errorMessage + "SlotIndex must be >= 0");
		else if (tileToDissolve > numberOfTiles - 1)
			throw new RuntimeException(errorMessage + "SlotIndex must be <= numberOfTiles - 1");
		
		// Assert that the direction is a valid direction.
		if (direction < -1)
			throw new RuntimeException(errorMessage + "Direction must be >= -1");
		else if (direction > 1)
			throw new RuntimeException(errorMessage + "Direction must be <= 1");
		
		// If the direction is set to 0,
		if (direction == 0)
			// Choose a random direction.
			direction = (game.random().nextBoolean())? -1 : 1;
		
		final int
			START_INDEX = tileToDissolve,
			// The Tile-index to insert a new Tile at.
			INDEX_TO_ADD_TILE_AT = (direction == DIRECTION_RIGHT)? 0 : numberOfTiles + direction;
		
		for (int tile = START_INDEX; tile != INDEX_TO_ADD_TILE_AT; tile -= direction) {
			
			this.tiles[tile] = this.tiles[tile - direction];
			
			this.tiles[tile].setTranslationTarget(getSlot(tile), animationSpeed);
		}
		
		this.tiles[INDEX_TO_ADD_TILE_AT] = this.createTileAt(INDEX_TO_ADD_TILE_AT - direction, timeToSwipe, DEFAULT_SCALE, DEFAULT_SCALE);
		// Animate the Tile to its slot's position.
		this.tiles[INDEX_TO_ADD_TILE_AT].setTranslationTarget(getSlot(INDEX_TO_ADD_TILE_AT), animationSpeed);
	}
	
	public final SwipeTile[] tiles() {
		return this.tiles;
	}
	
	public final void animateTilesIn() {
		for (int i = 0; i != this.numberOfTiles; i ++) {
			tiles[i].setTranslationTarget(getSlot(i), animationSpeed);
			tiles[i].setPosition(getSlot(numberOfTiles));
		}
	}
	
	/* Collapse the Row over it's n'th Tile. */
	public final void collapseTile(int tileToDissolve) {
		// Collapse the Row over the specified Tile in the Row's default direction.
		collapseTile(tileToDissolve, this.direction);
	}
	
	public final void setTouchManager(TouchManager manager) {
		this.touchManager = manager;
		for (SwipeTile tile : this.tiles)
			manager.addListener(tile);
	}
	
	/* Contract the Row in the specified direction to be have one less Tile. */
	public final void contract(int direction) {
		if (numberOfTiles <= 1)
			throw new RuntimeException("Cannot contract Row if it only has 1 Tile.");
		
		final int
			newNumberOfTiles = numberOfTiles - 1,
			startCopyingIndex = (direction == DIRECTION_RIGHT)? newNumberOfTiles - 1 : 0;
		
		redefineRowSize(newNumberOfTiles, startCopyingIndex);
		
		// Set each Tile to animate to its new slot.
		for (int index = 0; index != tiles.length; index ++)
			tiles[index].setTranslationTarget(getSlot(index), this.animationSpeed);
	}
	/* Contract the Row in its default direction to have one less Tile. */
	public final void contract() { contract(this.direction); }
	
	/* Expand the Row to use another SwipeTile. */
	public final void expand(int direction) {
		// If the direction is set to 0,
		if (direction == 0)
			// Choose a random direction.
			direction = (game.random().nextBoolean())? -1 : 1;
		
		final int newNumberOfTiles = numberOfTiles + 1;
		
		// If the Row is expanding right, the Tile array needs to be shifted over one index
		// to make room for the Tile to be added on the left side of the array.
		final int shift = (direction == DIRECTION_RIGHT)? 1 : 0;
		// Redefine the Row's Tile and Slot arrays to be of the new size, and copy over the old Tile
		// references into the new Tile-array starting at the 'shift' index.
		redefineRowSize(newNumberOfTiles, shift);
		
		// Define the index at which to add a new Tile at,
		final int indexToAddTileAt =
				// As either the first Tile or the last Tile (Depending on the direction we are expanding in)
				(direction == DIRECTION_RIGHT)? 0 : newNumberOfTiles - 1;
		// Set the Tile in the specified index to be a new Tile one
		// slot in the opposite direction we are expanding in.
		this.tiles[indexToAddTileAt] = createTileAt(indexToAddTileAt - direction, timeToSwipe, DEFAULT_SCALE, DEFAULT_SCALE);
		
		// Set each Tile to animate to its new slot.
		for (int index = 0; index != tiles.length; index ++)
			tiles[index].setTranslationTarget(getSlot(index), this.animationSpeed);
	}
	/* Expand the Row to use another SwipeTile. */
	public final void expand() { expand(this.direction); }
	
	@Override
	public final void onCorrectSwipe(SwipeTile tile) {
		if (this.listener != null)
			listener.onCorrectSwipe(tile);
		
		// Collapse the Row over the SwipeTile that was swiped.
		for (int i = 0; i != numberOfTiles; i ++)
			if (tiles[i].equals(tile))
				collapseTile(i, direction);
	}
	
	@Override
	public final void onIncorrectSwipe(SwipeTile tile) {
		if (this.listener != null)
			listener.onIncorrectSwipe(tile);
	}
	
	@Override
	public final void onExpire(SwipeTile tile) {
		if (this.listener != null)
			listener.onExpire(tile);
	}
	
	/* @return the amount of space between each of the Row's slots. */
	public final int spacing() {
		return game.screenWidth() / this.numberOfTiles;
	}
	
	/* @return the height of the Row. */
	public final int height() {
		return (int) this.tiles[0].boundingBox().height;
	}
	
	/* @return the number of SwipeTiles in the Row. */
	public final int numberOfTiles() {
		return this.numberOfTiles;
	}
	
	/* Create and return a SwipeTile in the specified slot. */
	protected SwipeTile createTileAt(int slot, float timeToSwipe, float scaleX, float scaleY) {
		if (scaleX <= 0 || scaleY <= 0)
			throw new RuntimeException("Scale must be > 0");
		else if (getSlot(slot) == null)
			throw new RuntimeException("Cannot create a tile at a non-existant slot.");
		
		// Create the Tile with the game and specify the amount of time the player
		// has to swipe the Tile.
		SwipeTile tile = new SwipeTile(game, timeToSwipe);
		// Translate the Tile to the specified slot position.
		tile.setPosition(getSlot(slot));
		// Set the Tile to use the specified scale.
		tile.scale(scaleX, scaleY);
		// Set the Tile to notify the Row of all Swipe events.
		tile.setSwipeListener(this);
		
		if (touchManager != null)
			touchManager.addListener(tile);
		
		return tile;
	}
	
	protected void initializeSlotPositions(Vector2 center, int spacing) {
		// Define the width of all the slot positions.
		final float span = numberOfSlots * spacing;
		
		float x = center.x - span/2 + spacing/2;
		for (int index = 0; index != numberOfSlots; index ++) {
			slotPositions[index] = new Vector2(x, center.y);
			x += spacing;
		}
	}
	
	/* Re-initialize the Tile and slot-position arrays with the specified size.
	 * Copies over references to Tiles from the current Tile-array into the new Tile-array starting at
	 * the specified index.
	 * @param startCopyingIndex the index to begin copying over Tile references into the new Tile-array. */
	protected final void redefineRowSize(int newSize, int startCopyingIndex) {
		// Store a reference to the current Row's SwipeTiles.
		final SwipeTile[] oldTileArray = this.tiles;
		
		// Re-define the number of Tiles and Slots in the Row.
		this.numberOfTiles = newSize;
		this.numberOfSlots = newSize + 2;
		
		// Reinitialize the Row's SwipeTile array with the new length.
		this.tiles = new SwipeTile[numberOfTiles];
		// Reinitialize the Row's array of slot-positions with the new length.
		this.slotPositions = new Vector2[numberOfSlots];
		
		// Re-define the slot positions.
		initializeSlotPositions(center, this.spacing());
		
		final int endCopyingIndex = (int) Math.min(tiles.length, oldTileArray.length);
		// Copy the oldTileArray into the new Tile array.
		for (int index = startCopyingIndex; index != endCopyingIndex; index ++)
			this.tiles[index] = oldTileArray[index];
	}
	
	/* @return the position of the n'th slot. */
	protected final Vector2 getSlot(int slotIndex) {
		// Return a position one to the right of the requested index to shift the slots into the
		// center of the slots.
		return this.slotPositions[slotIndex + 1];
	}
	
	/* Return a value that is 'normal' percent between 'start' and 'end'. */
	protected final Vector2 interpolate(Vector2 start, Vector2 end, float normal) {
		return new Vector2(interpolate(start.x, end.x, normal), interpolate(start.y, end.y, normal));
	}
	
	/* Return a value that is 'normal' percent between 'start' and 'end'. */
	protected final float interpolate(float start, float end, float normal) {
		return start + (end - start)*normal;
	}
}