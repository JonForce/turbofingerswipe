package com.jbs.swipe.shop;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.framework.rendering.Renderable;
import com.jbs.framework.rendering.ui.Button;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.CoinWindow;
import com.jbs.swipe.gui.Scroller;
import com.jbs.swipe.gui.buttons.BackButton;
import com.jbs.swipe.shop.purchases.Purchase;
import com.jbs.swipe.states.OverlayState;

public class ShopState extends OverlayState {
	
	private static final String
		WINDOW_SOURCE = "Shop/ShopWindow";
	
	private final ApplicationState subState;
	private final Graphic window;
	private final CoinWindow coinWindow;
	private final ArrayList<ItemWindow> itemWindows;
	private final BillingAPI billingAPI;
	private final Scroller scroller;
	private final BackButton backButton;
	
	private final float
		// Each ItemWindow should be spaced out by n pixels.
		itemWindowHorizontalMargin = 50;
	
	/** Create the Shop control structure.
	 * @param subState The state that the Shop should be rendered on top of. */
	public ShopState(final Game game, final BillingAPI billingAPI, ApplicationState subState) {
		super(game);
		this.subState = subState;
		this.billingAPI = billingAPI;
		
		this.window = new Graphic(game.screenCenter(), Assets.getAtlasRegion(WINDOW_SOURCE));
		window.setWidth(game.screenWidth());
		
		this.backButton = new BackButton(game, new Vector2()) {
			final float
				horizontalMargin = 10,
				verticalMargin = 10;
			@Override
			public float x() {
				return width()/2 + horizontalMargin;
			}
			
			@Override
			public float y() {
				return game.screenHeight() - height()/2 - verticalMargin;
			}
		};
		
		this.coinWindow = new CoinWindow(game) {
			final float
				horizontalMargin = 10,
				verticalMargin = 10;
			@Override
			public float x() {
				return backButton.x() + backButton.width()/2 + width()/2 + horizontalMargin;
			}
			
			@Override
			public float y() {
				return game.screenHeight() - (height()/2 + verticalMargin);
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
		backButton.updateWith(app.input);
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
				
				backButton.renderTo(batch);
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