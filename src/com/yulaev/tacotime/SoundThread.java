package com.yulaev.tacotime;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundThread extends Thread {
	 
	//Enum all music & sfx
	private final int MUSIC_NOTHING = 0;
	private final int MUSIC_LEVEL_1 = 1;
	private final int MUSIC_LEVEL_1_DURATION = 31;
	private final int MUSIC_LEVEL_END = 2;
	private final int MUSIC_LEVEL_END_DURATION = 3;
	
	//Count the total number of sounds in the sound pool
	private final int SFX_COUNT = 2;
	
	//Enum message types
	public static final int MESSAGE_PLAY_LEVEL_MUSIC = 1;
	public static final int MESSAGE_PLAY_LEVEL_END = 2;
	public static final int MESSAGE_PLAY_NOTHING = 3;
	
	//Objects to play music & sounds - utility & datapath, not state
	private Context caller;
	private SoundPool mSoundPool;
	private AudioManager  mAudioManager;
	private HashMap<Integer, Integer> levelMusicMap;
	private HashMap<Integer, Integer> sfxMap;
	
	//Keeps track of state - what is playing and whether it has been changed
	private boolean currently_playing_changed;
	private int currently_playing;
	private int current_mstream;
	
	public SoundThread(Context caller) {
		this.caller = caller;
		mSoundPool = new SoundPool(SFX_COUNT, AudioManager.STREAM_MUSIC, 0);
		mAudioManager = (AudioManager)caller.getSystemService(Context.AUDIO_SERVICE);
		
		setupSoundMap();
		currently_playing = 0;
		currently_playing_changed = false;
	}
	
	@SuppressLint("UseSparseArrays")
	private void setupSoundMap() {
		levelMusicMap = new HashMap<Integer, Integer>(4*SFX_COUNT);
		sfxMap = new HashMap<Integer, Integer>(4*SFX_COUNT);
		
		levelMusicMap.put(MUSIC_LEVEL_1, mSoundPool.load(caller, R.raw.music_level_1, 1));
		sfxMap.put(MUSIC_LEVEL_END, mSoundPool.load(caller, R.raw.sfx_level_end, 1));
	}
	
	private synchronized void setMusicPlaying(int new_music) {
		currently_playing_changed = true;
		this.currently_playing = new_music;
	}
	
	private synchronized int getMusicPlaying() {
		return this.currently_playing;
	}
	
	private synchronized boolean getMusicChanged() {
		boolean retval = currently_playing_changed;
		currently_playing_changed = false;
		return retval;
	}
	
	private void stopMusic() {
		mSoundPool.stop(current_mstream);
	}
	
	private void playLevelMusic(int level_number) {
		stopMusic();
		
		float streamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		current_mstream = mSoundPool.play(levelMusicMap.get(level_number), streamVolume, streamVolume, 1, 0, 1f);
	}
	
	private void playSfx(int sfx_idx) {
		stopMusic();
		
		float streamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		current_mstream = mSoundPool.play(sfxMap.get(sfx_idx), streamVolume, streamVolume, 1, 0, 1f);
	}
	
	private void run() {
		
	}

}
