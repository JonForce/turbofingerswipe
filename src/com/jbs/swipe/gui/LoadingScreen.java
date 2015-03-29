package com.jbs.swipe.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;

public abstract class LoadingScreen implements Renderable {
	
	private final static int FONT_VERTICAL_OFFSET = 100;
	
	/* The source locations of the respective Textures */
	private FileHandle
		LOGO_SOURCE,
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
	private Texture logo;
	
	private Font font;
	private Vector2 fontRightBounds;
	private Game game;
	
	TextureRegion tex( FileHandle filehandle ) {
		return new TextureRegion(new Texture(filehandle));
	}
	
	public LoadingScreen(Game game, int width, int height) {
		this.game = game;
		
		initializeFileHandles();
		
		logo = new Texture(LOGO_SOURCE);
		font = new Font(tex(FONT_SOURCE));
		
		// The center of the specified width and height of our LoadingScreen.
		final Vector2 center = new Vector2(width/2, height/2);
		
		// Create the background Graphic at the center of our LoadingScreen with the background texture.
		background = new Graphic(center, new Vector2(width, height), tex(BACKGROUND_SOURCE));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		// Create the loading bar background Graphic at the center of our loadingScreen with the loading bar's bg texture.
		emptyBar = new Graphic(center, tex(EMPTY_BAR_SOURCE));
		emptyBar.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// Create the loading bar foreground Graphic at the center of our loadingScreen with the loading bar's foreground texture.
		fullBar = new Graphic(center, tex(FULL_BAR_SOURCE)) {
			// Override the width of the Graphic to be relative to the percent completeness of the loading process.
			@Override
			public float width() {
				return super.width() * percentComplete();
			}
			
			// Override the x position of the center of the Graphic to keep the full bar expanding from left to right.
			@Override
			public float x() {
				float sourceWidth = srcWidth();
				float offset = percentComplete() * (sourceWidth/2);
				return center.x + offset - sourceWidth/2;
			}
		};
		fullBar.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// Create the finger that swipes the loading bar.
		finger = new Graphic(center, tex(FINGER_SOURCE)) {
			@Override
			public float x() {
				float offset = -fullBar.srcWidth()/2 +  fullBar.width();
				return center.x + offset;
			}
		};
		finger.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		font.setAlignment(Font.ALIGNMENT_RIGHT);
		fontRightBounds = new Vector2(center.x, center.y + FONT_VERTICAL_OFFSET);
		
		// Define the percent symbol with it's texture at the right edge of the font.
		percent = new Graphic(fontRightBounds, tex(PERCENT_SOURCE));
		// Move the percent symbol right by it's half-width and up by it's half-height so it does not overlap the font.
		percent.translate(font.digitWidth(), percent.height()/2);
		percent.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		final float switchPercent = .5f;
		if (percentComplete() > switchPercent) {
			background.renderTo(batch);
			game.beginIODChange(batch, 1.5f);
				emptyBar.renderTo(batch);
				fullBar.renderTo(batch);
				
				font.renderIntegerTo(batch, (int)(percentComplete() * 100), (int) fontRightBounds.x, (int) fontRightBounds.y);
				percent.renderTo(batch);
			game.endIODChange(batch, 1.5f);
			
			game.beginIODChange(batch, 2.5f);
				finger.renderTo(batch);
			game.endIODChange(batch, 2.5f);
		} else {
			batch.draw(logo, 0, 0, game.screenWidth(), game.screenHeight());
		}
				
	}
	
	public abstract float percentComplete();
	
	protected void initializeFileHandles() {
		LOGO_SOURCE = Gdx.files.internal("assets/GUI/Loading/Intro.png");
		BACKGROUND_SOURCE = Gdx.files.internal("assets/GUI/Loading/Background.png");
		EMPTY_BAR_SOURCE = Gdx.files.internal("assets/GUI/Loading/Bar/Empty.png");
		FULL_BAR_SOURCE = Gdx.files.internal("assets/GUI/Loading/Bar/Full.png");
		FINGER_SOURCE = Gdx.files.internal("assets/GUI/Loading/Finger.png");
		FONT_SOURCE = Gdx.files.internal("assets/GUI/Font/Digits2.png");
		PERCENT_SOURCE = Gdx.files.internal("assets/GUI/Font/%.png");
	}
}