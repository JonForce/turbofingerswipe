package com.jbs.swipe.shop;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jbs.framework.control.Application;
import com.jbs.framework.control.ApplicationState;
import com.jbs.framework.rendering.Graphic;
import com.jbs.swipe.Assets;
import com.jbs.swipe.Game;
import com.jbs.swipe.gui.CoinWindow;
import com.jbs.swipe.gui.Scroller;
import com.jbs.swipe.gui.buttons.BackButton;
import com.jbs.swipe.shop.purchases.Purchase;
import com.jbs.swipe.states.GameState;

public class ShopState extends GameState {
	
	private static final String
		WINDOW_SOURCE = "Shop/ShopWindow",
		BUBBLE0 = "Shop/bubble",
		BUBBLE1 = "Shop/bubble0";
	
	private final ApplicationState subState;
	private final Graphic window,bubble0,bubble1;
	private final CoinWindow coinWindow;
	private final ArrayList<ItemWindow> itemWindows;
	private final BillingAPI billingAPI;
	private final Scroller scroller;
	private final BackButton backButton;
	
	private final float
		// Each ItemWindow should be spaced out by n pixels.
		itemWindowHorizontalMargin = 1024;
	
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
				horizontalMargin = 20,
				verticalMargin = 5;
			@Override
			public float x() {
				return width()/2 + horizontalMargin;
			}
			
			@Override
			public float y() {
				return height()/2 + verticalMargin;
			}
		};
		
		this.coinWindow = new CoinWindow(game) {
			final float
				horizontalMargin = 10,
				verticalMargin = 10;
			@Override
			public float x() {
				return width()/2 + horizontalMargin;
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
				return game.screenCenter().x - positionOf(1) +500; // The position of the first item window.
			}
			@Override
			public float upperBound() {
				return game.screenCenter().x + positionOf(itemWindows.size()-1)-500; // The position of the last item window.
			}
		};
		
		bubble0 = new Graphic(0, 0, Assets.getAtlasRegion(BUBBLE0) );
		bubble1 = new Graphic(0, 0, Assets.getAtlasRegion(BUBBLE1) );
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
		super.enterState();
	}
	@Override
	public void exitState() { System.out.println("Exiting ShopState."); }
	
	/** Add a Purchase to be available to the Shop. */
	public void addPurchase(Purchase purchase) {
		itemWindows.add(new ItemWindow(game, purchase));
	}
	
	//@Override
	//protected Renderable superScreen() {
		//return new Renderable() {
	@Override
	public void renderTo(SpriteBatch batch) {
		super.renderTo(batch);
		window.renderTo(batch);
		
		for (int windowIndex = 0; windowIndex != itemWindows.size(); windowIndex ++) {
			itemWindows.get(windowIndex).renderTo(batch, 
				200+scroller.position() - positionOf(windowIndex), window.y());
			
			bubble1.setScale(0.5f, 0.5f);
			bubble1.setPosition(Game.game.screenCenter().add(
					new Vector2((-itemWindows.size()*15)+windowIndex*30,-200)));
			bubble1.renderTo(batch);
			
			if(windowIndex == itemWindows.size()-1-scroller.page()) {
				
				bubble0.setPosition(Game.game.screenCenter().add(
						new Vector2((-itemWindows.size()*15)+windowIndex*30,-200)));
				bubble0.renderTo(batch);
			}
		}
		
		coinWindow.renderTo(batch);
		
		backButton.renderTo(batch);
	}
		//};
	//}
	
	/*
	@Override
	protected Renderable subScreen() {
		return subState;
	}
	*/
	
	private float positionOf(int itemWindow) {
		return  itemWindow * (itemWindowHorizontalMargin);
	}
	
	private float windowWidth() {
		return itemWindows.get(0).window().width();
	}
}