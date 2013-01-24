package org.coffeecats.coffeetime.utility;

import java.util.HashMap;

import org.coffeecats.coffeetime.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {
	public static final int SFX_NOTHING = -1;
	public static final int SFX_TAP = 4;
	public static final int SFX_MENU_TAP = 6;
	
	private int mShortSfxPlaying = -1;
	private SoundPool mSoundPool;
	private AudioManager mAudioManager;
	private HashMap<Integer, Integer> mShortSfxMap;
	
	public SoundPlayer(Context caller) {
		//Setup stuff to play short sound effects with SoundPool
		mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    	mAudioManager = (AudioManager)caller.getSystemService(Context.AUDIO_SERVICE);
    	mShortSfxMap = new HashMap<Integer, Integer>();
    	
    	mShortSfxMap.put(SFX_TAP, mSoundPool.load(caller, R.raw.sfx_tap, 1));
    	mShortSfxMap.put(SFX_MENU_TAP, mSoundPool.load(caller, R.raw.sfx_menu_tap, 1));
	}
	
	public synchronized void playSound(int sfx_number) {
		if(mSoundPool == null || mAudioManager == null || mShortSfxMap == null) return;
		
		//Make sure we're not to play nothing, or that sfx_number is a valid short sound effect
		if(sfx_number == SFX_NOTHING || !mShortSfxMap.containsKey(sfx_number)) mSoundPool.stop(mShortSfxPlaying);
		else {
			float streamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    	mShortSfxPlaying = mSoundPool.play(mShortSfxMap.get(sfx_number), streamVolume, streamVolume, 1, 0, 1f);
		}
	}
}
