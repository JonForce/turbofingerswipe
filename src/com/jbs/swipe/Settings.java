package com.jbs.swipe;

import com.badlogic.gdx.Preferences;

public class Settings {
	
	protected final static String
		OPTED_OUT_OF_TUTORIAL = "OptedOutOfTutorial",
		IS_MUTED = "IsMuted";
	
	private final Preferences preferences;
	
	/** Create an interface for viewing and modifying the Game settings. */
	public Settings(Preferences preferences) {
		this.preferences = preferences;
	}
	
	/** @return true if the User has opted out of hearing sounds.*/
	public boolean isMuted() {
		return preferences().getBoolean(IS_MUTED);
	}
	
	/** @return true if the User has opted to not see the tutorial. */
	public boolean hasOptedOutOfTutorial() {
		return preferences().getBoolean(OPTED_OUT_OF_TUTORIAL);
	}
	
	/** Opt to not see the tutorial. */
	public void optOutOfTutorial() {
		preferences().putBoolean(OPTED_OUT_OF_TUTORIAL, true);
		save();
	}
	
	/** Opt to see the tutorial. */
	public void optIntoTutorial() {
		preferences().putBoolean(OPTED_OUT_OF_TUTORIAL, false);
		save();
	}
	
	/** Set whether or not the User wants to hear sounds. */
	public void setMuted(boolean flag) {
		preferences().putBoolean(IS_MUTED, flag);
		save();
	}
	
	/** Save the preferences to disk. */
	public void save() {
		preferences().flush();
	}
	
	/** @return the Preferences file to save settings to and read settings from. */
	protected Preferences preferences() {
		return preferences;
	}
}