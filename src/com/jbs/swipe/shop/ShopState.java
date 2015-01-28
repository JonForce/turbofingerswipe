package com.jbs.swipe.shop;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.CoinWindow;
import com.jbs.swipe.gui.Scroller;
import com.jbs.swipe.states.OverlayState;

public class ShopState extends OverlayState {
	
	private static final String
		WINDOW_SOURCE = "assets/GUI/Shop/ShopWindow.png";
	
	private final ApplicationState subState;
	private final Graphic window;
	private final CoinWindow coinWindow;
	private final ArrayList<ItemWindow> itemWindows;
	private final BillingAPI billingAPI;
	private final Scroller scroller;
	
	private final float
		// The CoinWindow's offset from the top-left corner of the screen (in pixels).
		coinWindowHorizontalMargin = 10,
		coinWindowVerticalMargin = 10,
		// Each ItemWindow should be spaced out by n pixels.
		itemWindowHorizontalMargin = 100;
	
	/** Create the Shop control structure.
	 * @param subState The state that the Shop should be rendered on top of. */
	public ShopState(final Game game, final BillingAPI billingAPI, ApplicationState subState) {
		super(game);
		this.subState = subState;
		this.billingAPI = billingAPI;
		
		this.window = new Graphic(game.screenCenter(), game.getTexture(WINDOW_SOURCE));
		window.setWidth(game.screenWidth());
		
		this.coinWindow = new CoinWindow(game) {
			@Override
			public float x() {
				return this.width()/2 + coinWindowHorizontalMargin;
			}
			
			@Override
			public float y() {
				return game.screenHeight() - this.height()/2 - coinWindowVerticalMargin;
			}
			
			@Override
			protected int coins() {
				return game.user().jbsCoins();
			}
		};
		
		this.itemWindows = new ArrayList<ItemWindow>(5);
		this.scroller = new Scroller() {
			@Override
			public float lowerBound() {
				return game.screenCenter().x - positionOf(0); // The position of the first item window.
			}
			@Override
			public float upperBound() {
				return game.screenCenter().x + positionOf(itemWindows.size() - 1); // The position of the last item window.
			}
		};
	}

	@Override
	public void updateApplication(Application app) {
		for (ItemWindow window : itemWindows)
			window.updateWith(app.input);
		
		scroller.updateWith(app.input);
	}
	
	@Override
	public void enterState() {
		System.out.println("Entering ShopState.");
		scroller.setPosition(scroller.upperBound());
	}
	@Override
	public void exitState() { System.out.println("Exiting ShopState."); }
	
	/** Add a Purchase to be available to the Shop. */
	public void addPurchase(Purchase purchase) {
		itemWindows.add(new ItemWindow(game, purchase));
	}
	
	@Override
	protected Renderable superScreen() {
		return new Renderable() {
			@Override
			public void renderTo(SpriteBatch batch) {
				window.renderTo(batch);
				
				for (int windowIndex = 0; windowIndex != itemWindows.size(); windowIndex ++)
					itemWindows.get(windowIndex).renderTo(batch, scroller.position() - positionOf(windowIndex), window.y());
				
				coinWindow.renderTo(batch);
			}
		};
	}
	
	@Override
	protected Renderable subScreen() {
		return subState;
	}
	
	private float positionOf(int itemWindow) {
		return itemWindow * (windowWidth() + itemWindowHorizontalMargin);
	}
	
	private float windowWidth() {
		return itemWindows.get(0).window().width();
	}
}