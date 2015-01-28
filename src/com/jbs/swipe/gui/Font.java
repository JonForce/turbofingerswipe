package com.jbs.swipe.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Font {
	
	public static final int
		ALIGNMENT_LEFT = 0,
		ALIGNMENT_CENTER = 1,
		ALIGNMENT_RIGHT = 2;
	
	private static final int NUMBER_OF_DIGITS = 10;
	
	private Texture source;
	private int sourceDigitWidth, sourceDigitHeight, alignment = ALIGNMENT_LEFT;
	private Vector2 position;
	
	private float scale = 1;
	
	/*
	 * Assumes there are 10 digits in sequential order in 1 row on the Source texture.
	 */
	public Font(Texture source) {
		this.source = source;
		// Digit width is equal to the (totalSourceWidth)/(numberOfDigitsPerRow)
		this.sourceDigitWidth = source.getWidth()/NUMBER_OF_DIGITS;
		// Digit height is equal to the (totalSourceHeight)/(numberOfDigitsPerColumn)
		this.sourceDigitHeight = source.getHeight();
	}
	
	/* Set the default rendering position of the Font to (x, y). */
	public void setPosition(float x, float y) {
		// Initialize the position if it has not yet been.
		if (position == null)
			position = new Vector2();
		// Set the position of the Font to (x, y).
		position.set(x, y);
	}
	
	public void translate(float x, float y) {
		position.add(x, y);
	}
	
	/* @return the default scale to render the font at. */
	public float scale() {
		return scale;
	}
	
	/* Set the default scale to render the font with to newScale. */
	public void setScale(float newScale) {
		this.scale = newScale;
	}
	
	public final void setAlignment(int newAlignment) {
		if (newAlignment < 0 || newAlignment > 2)
			throw new RuntimeException("Cannot setAlignment to " + newAlignment + ", alignment ID not recognized.");
		else
			this.alignment = newAlignment;
	}
	
	/* @return the width of each rendered digit. */
	public float digitWidth() {
		return sourceDigitWidth * scale();
	}
	
	/* @return the height of each rendered digit. */
	public float digitHeight() {
		return sourceDigitHeight * scale();
	}
	
	public void renderDigitTo(SpriteBatch batch, int digit, float x, float y) {
		assertIntegerIsDigit(digit);
		batch.draw(getDigitTexture(digit), x, y, digitWidth(), digitHeight());
	}
	
	public void renderIntegerTo(SpriteBatch batch, int integer, float x, float y) {
		String integerString = integer + "";
		
		int numberOfDigits = integerString.length() + 1;
		if (this.alignment == ALIGNMENT_CENTER)
			x -= ((numberOfDigits + 1) * digitWidth())/2;
		else if (this.alignment == ALIGNMENT_RIGHT)
			x -= numberOfDigits * digitWidth();
		
		for (int index = 0; index != numberOfDigits - 1; index ++) {
			x += digitWidth();
			String digitString = integerString.substring(index, index + 1);
			renderDigitTo(batch, Integer.parseInt(digitString), x, y);
		}
	}
	
	/* Renders the specified digit to the SpriteBatch with the Font's default rendering position,
	 * throws a RuntimeException if the Font's default rendering position has not been set with Font.setPosition(x, y). */
	public void renderDigitTo(SpriteBatch batch, int digit) {
		if (this.position == null)
			throw new RuntimeException("Rendering position must be specified with Font.setPosition to call this method.");
		renderDigitTo(batch, digit, (int) this.position.x, (int) this.position.y);
	}
	
	/* Renders the specified integer to the SpriteBatch with the Font's default rendering position,
	 * throws a RuntimeException if the Font's default rendering position has not been set with Font.setPosition(x, y). */
	public void renderIntegerTo(SpriteBatch batch, int integer) {
		if (this.position == null)
			throw new RuntimeException("Rendering position must be specified with Font.setPosition to call this method.");
		renderIntegerTo(batch, integer, (int) this.position.x, (int) this.position.y);
	}
	
	/* @return a TextureRegion of the specified digit using the Font's source Texture. */
	public TextureRegion getDigitTexture(int i) {
		assertIntegerIsDigit(i);
		int x = i * sourceDigitWidth;
		int y = 0;
		return slice(x, y, sourceDigitWidth, sourceDigitHeight);
	}
	
	/* @return a new TextureRegion using the Font's source Texture and the
	 * specified region data. */
	private TextureRegion slice(int x, int y, int width, int height) {
		// Create the TextureRegion from the Font's source Texture.
		TextureRegion region = new TextureRegion(source);
		// Set the TextureRegion to the specified Region data.
		region.setRegion(x, y, width, height);
		return region;
	}
	
	/*
	 * Throw a RuntimeException if possibleDigit is < 0 or > 9.
	 */
	private void assertIntegerIsDigit(int possibleDigit) {
		// A mathematical digit must be greater than or equal to 0 and less than or equal to 9.
		if (possibleDigit < 0 || possibleDigit > 9)
			throw new RuntimeException("Error : "+possibleDigit+" is not a digit, must be within 0 to 9 (inclusive)");
	}
}