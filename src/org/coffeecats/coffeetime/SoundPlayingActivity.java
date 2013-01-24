package org.coffeecats.coffeetime;

import org.coffeecats.coffeetime.utility.SoundPlayer;

import android.app.Activity;
import android.os.Bundle;

public abstract class SoundPlayingActivity extends Activity {
	SoundPlayer mSoundPlayer;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSoundPlayer = new SoundPlayer(this);
	}
	
	protected void playQuickTap() {
		mSoundPlayer.playSound(SoundPlayer.SFX_TAP);
	}
	
	protected void playLongTap() {
		mSoundPlayer.playSound(SoundPlayer.SFX_MENU_TAP);
	}
}
