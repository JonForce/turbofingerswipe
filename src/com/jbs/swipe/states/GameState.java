package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Game;

public class GameState implements ApplicationState {

	public final String
	BACKGROUND_SOURCE = "assets/GUI/MainMenu/bg.png",
	BG_TOP_SOURCE = "assets/GUI/MainMenu/top.png",
	BG_BOT_SOURCE = "assets/GUI/MainMenu/bottom.png";
	
	protected Game game;
	protected Graphic background, title, bgTop, bgBot;
	
	protected Vector2
	menuCenter, // The center of the MainMenu.
	menuSize; // The width and height of the MainMenu.
	
	protected boolean initialized = false;
	
	public GameState(Game game) {
		this.game = game;
		this.menuCenter = game.screenCenter();
		this.menuSize = game.screenSize();
	}
	
	/** Create and position the MainMenu's background. */
	protected void initializeBackground() {
		background = new Graphic(menuCenter, menuSize, game.getTexture(BACKGROUND_SOURCE));
		bgTop = new Graphic(new Vector2(menuCenter.x,menuSize.y), menuSize, game.getTexture(BG_TOP_SOURCE));
		bgBot = new Graphic(new Vector2(menuCenter.x,0), menuSize, game.getTexture(BG_BOT_SOURCE));
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background.renderTo(batch);
		bgTop.renderTo(batch);
		bgBot.renderTo(batch);
	}

	@Override
	public void enterState() {
		if(!initialized) {
			initialized = true;
			initializeBackground();
		}
	}

	@Override
	public void updateApplication(Application app) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitState() {
		// TODO Auto-generated method stub
		
	}

}
