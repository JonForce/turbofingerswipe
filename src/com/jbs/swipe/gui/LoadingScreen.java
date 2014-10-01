package com.jbs.swipe.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;

public abstract class LoadingScreen implements Renderable {
	
	private final static int FONT_VERTICAL_OFFSET = 100;
	
	/* The source locations of the respective Textures */
	private FileHandle
		BACKGROUND_SOURCE,
		EMPTY_BAR_SOURCE,
		FULL_BAR_SOURCE,
		FINGER_SOURCE,
		FONT_SOURCE,
		PERCENT_SOURCE;
	
	private Graphic
		background, // The LoadingScreen's background.
		emptyBar, // The loading bar's background.
		fullBar, // The loading bar's foreground.
		finger,
		percent; // The finger that swipes the loading bar.
	
	private Font font;
	private Vector2 fontRightBounds;
	
	public LoadingScreen(int width, int height) {
		initializeFileHandles();
		
		font = new Font(new Texture(FONT_SOURCE));
		
		// The center of the specified width and height of our LoadingScreen.
		final Vector2 center = new Vector2(width/2, height/2);
		
		// Create the background Graphic at the center of our LoadingScreen with the background texture.
		background = new Graphic(center, new Vector2(width, height), new Texture(BACKGROUND_SOURCE));
		// Create the loading bar background Graphic at the center of our loadingScreen with the loading bar's bg texture.
		emptyBar = new Graphic(center, new Texture(EMPTY_BAR_SOURCE));
		
		// Create the loading bar foreground Graphic at the center of our loadingScreen with the loading bar's foreground texture.
		fullBar = new Graphic(center, new Texture(FULL_BAR_SOURCE)) {
			// Override the width of the Graphic to be relative to the percent completeness of the loading process.
			@Override
			public int width() {
				return (int)(super.width() * percentComplete());
			}
			
			// Override the x position of the center of the Graphic to keep the full bar expanding from left to right.
			@Override
			public int x() {
				float sourceWidth = texture().getWidth();
				float offset = percentComplete() * (sourceWidth/2);
				return (int) (center.x + offset - sourceWidth/2);
			}
		};
		
		// Create the finger that swipes the loading bar.
		finger = new Graphic(center, new Texture(FINGER_SOURCE)) {
			@Override
			public int x() {
				float sourceWidth = fullBar.texture().getWidth();
				float offset = percentComplete() * sourceWidth;
				return (int)(center.x + offset - sourceWidth/2);
			}
		};
		
		font.setAlignment(Font.ALIGNMENT_RIGHT);
		fontRightBounds = new Vector2(center.x, center.y + FONT_VERTICAL_OFFSET);
		
		// Define the percent symbol with it's texture at the right edge of the font.
		percent = new Graphic(fontRightBounds, new Texture(PERCENT_SOURCE));
		// Move the percent symbol right by it's half-width and up by it's half-height so it does not overlap the font.
		percent.translate(font.digitWidth(), percent.height()/2);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background.renderTo(batch);
		emptyBar.renderTo(batch);
		fullBar.renderTo(batch);
		finger.renderTo(batch);
		
		font.renderIntegerTo(batch, (int)(percentComplete() * 100), (int) fontRightBounds.x, (int) fontRightBounds.y);
		percent.renderTo(batch);
	}
	
	public abstract float percentComplete();
	
	protected void initializeFileHandles() {
		BACKGROUND_SOURCE = Gdx.files.internal("assets/GUI/Loading/Background.png");
		EMPTY_BAR_SOURCE = Gdx.files.internal("assets/GUI/Loading/Bar/Empty.png");
		FULL_BAR_SOURCE = Gdx.files.internal("assets/GUI/Loading/Bar/Full.png");
		FINGER_SOURCE = Gdx.files.internal("assets/GUI/Loading/Finger.png");
		FONT_SOURCE = Gdx.files.internal("assets/GUI/Font/Digits2.png");
		PERCENT_SOURCE = Gdx.files.internal("assets/GUI/Font/%.png");
	}
}