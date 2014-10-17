package com.jbs.swipe.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.NullState;

public abstract class TutorialState implements ApplicationState {
	
	protected final Game game;
	
	private ApplicationState exitState;
	private Renderable background;
	private int tipIndex = 0; // The index of the current tip.
	private boolean created = false; // True if the Tutorial has been initialized.
	
	/**
	 * The base class for a Tutorial.
	 */
	public TutorialState(Game game) {
		this.game = game;
		this.exitState = new NullState();
	}
	
	@Override
	public void renderTo(SpriteBatch batch) {
		background.renderTo(batch);
		tips()[tipIndex].renderTo(batch);
	}
	
	@Override
	public void updateApplication(Application app) {
		
	}
	
	@Override
	public void enterState() {
		if (!created)
			create();
		created = true;
	}
	@Override
	public void exitState() { }
	
	public final Graphic currentTip() {
		return tips()[tipIndex];
	}
	
	public final void reset() {
		tipIndex = 0;
	}
	
	public final void setExitState(ApplicationState newExitState) {
		this.exitState = newExitState;
	}
	
	public final void setBackground(Renderable newBackground) {
		this.background = newBackground;
	}
	
	protected final void useNextTip() {
		if (++tipIndex == tips().length)
			game.setState(exitState);
		else
			useTip(tipIndex);
	}
	
	protected void create() { }
	
	abstract void useTip(int tip);
	abstract Graphic[] tips();
	abstract String tutorialName();
	
}