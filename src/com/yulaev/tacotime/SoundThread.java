package com.yulaev.tacotime;

import java.io.IOException;
import java.util.HashMap;

import com.yulaev.tacotime.gamelogic.GameInfo;
import com.yulaev.tacotime.gamelogic.Interaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class SoundThread extends Thread {
	private final String activitynametag = "SoundThread";
	 
	//Enum all music & sfx
	private final int MUSIC_NOTHING = 0;
	private final int MUSIC_LEVEL_1 = 1;
	private final int MUSIC_LEVEL_2 = 2;
	
	private final int MUSIC_LEVEL_END = GameInfo.MAX_GAME_LEVEL + 1;
	
	//Count the total number of sounds in the sound pool
	private final int SFX_COUNT = 2;
	
	//Enum message types
	public static final int MESSAGE_PLAY_LEVEL_MUSIC = 1;
	public static final int MESSAGE_PLAY_LEVEL_END = 2;
	public static final int MESSAGE_PLAY_NOTHING = 3;
	public static final int MESSAGE_SUSPEND = 4;
	public static final int MESSAGE_UNSUSPEND = 5;
	public static final int MESSAGE_LOAD_LEVEL_MUSIC = 6;
	
	//How long between SoundThread wake-ups
	private static final int THREAD_DELAY_MS = 250;
	private static final int THREAD_DELAY_MS_SLOW = 1000;
	
	//Handler for receiving messages
	public static Handler handler;
	
	//Objects to play music & sounds - utility & datapath, not state
	private Context caller;
	private HashMap<Integer, Integer> levelMusicMap;
	private HashMap<Integer, Integer> levelMusicResourceMap;
	private HashMap<Integer, MediaPlayer> sfxMap;
	
	//Keeps track of state - what is playing and whether it has been changed
	private boolean currently_playing_changed;
	private boolean do_loop;
	private int currently_playing;
	private MediaPlayer current_mstream;
	private boolean suspended;
	private boolean running;
	
	
	public SoundThread(Context caller) {
		this.caller = caller;
		
		running = true;
		
		setupSoundMap();
		currently_playing = MUSIC_NOTHING;
		currently_playing_changed = false;
		current_mstream = null;
		do_loop=false;
		
		setSuspended(false);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Handle message to play level msuic
				if(msg.what == MESSAGE_PLAY_LEVEL_MUSIC) {
					if(levelMusicMap != null && levelMusicMap.containsKey(msg.arg1))
						setMusicPlaying(levelMusicMap.get(msg.arg1), true);
				}
				else if(msg.what == MESSAGE_LOAD_LEVEL_MUSIC) {
					if(levelMusicMap != null && levelMusicMap.containsKey(msg.arg1))
						loadLevelMusic(msg.arg1);
				}
				
				//Handle message to play the level end sfx
				else if(msg.what == MESSAGE_PLAY_LEVEL_END) {
					setMusicPlaying(MUSIC_LEVEL_END, false);
				}
				
				//Handle message to play nothing
				else if(msg.what == MESSAGE_PLAY_NOTHING) {
					setMusicPlaying(MUSIC_NOTHING, false);
				}
				
				else if(msg.what == MESSAGE_SUSPEND) {
					setSuspended(true);
				}
				
				else if(msg.what == MESSAGE_UNSUSPEND) {
					setSuspended(false);
				}
			}
		};
	}
	
	/** Called when the soundthread is to be wound down. Releases all associated resources, mostly the
	 * MediaPlayer objects that get loaded when we play the game */
	public synchronized void destroy() {
		running = false;
		
		for(int key : sfxMap.keySet()) {
			MediaPlayer mplayerIdx = sfxMap.get(key);
			
			if(mplayerIdx != null) {
				mplayerIdx.stop();					
				mplayerIdx.release();
			}
		}
	}
	
	private synchronized void setSuspended(boolean n_suspended) { suspended = n_suspended; }
	private synchronized boolean getSuspended() { return suspended; }
	
	@SuppressLint("UseSparseArrays")
	private void setupSoundMap() {
		levelMusicMap = new HashMap<Integer, Integer>(4*SFX_COUNT);
		levelMusicResourceMap = new HashMap<Integer, Integer>(4*SFX_COUNT);
		sfxMap = new HashMap<Integer, MediaPlayer>(4*SFX_COUNT);
		
		//Mapping from level numbers to level number enumerations
		levelMusicMap.put(1, MUSIC_LEVEL_1);
		levelMusicResourceMap.put(MUSIC_LEVEL_1, R.raw.music_level_1);
		levelMusicMap.put(2, MUSIC_LEVEL_2);
		levelMusicResourceMap.put(MUSIC_LEVEL_2, R.raw.music_level_2);
		
		//Mapping from sound effects to actual effect indices, and their durations	
		sfxMap.put(MUSIC_LEVEL_END, MediaPlayer.create(caller, R.raw.sfx_level_end));
	}
	
	private void loadLevelMusic(int level_number) {
		if(!levelMusicMap.containsKey(level_number)) return;
		
		int level_enum = levelMusicMap.get(level_number);
		int level_resource = levelMusicResourceMap.get(level_enum);
		
		//If the previous level has a enum & resource associated with it...
		if(levelMusicMap.containsKey(level_number-1)) {
			int prev_level_enum = levelMusicMap.get(level_number-1);
			int prev_level_resource = levelMusicResourceMap.get(prev_level_enum);
			
			//Check if the resource is the same, if so, reuse the same MediaPlayer (and return, since
			//we are done loading stuff)
			//Kind of a stupid local optimization, sorry
			if(prev_level_resource == level_resource && sfxMap.containsKey(prev_level_enum)) {
				sfxMap.put(level_enum, sfxMap.get(prev_level_enum));
				sfxMap.remove(prev_level_enum);
				return;
			}
			//If the resource is not the same, we should release the previous level's music
			else if(sfxMap.containsKey(prev_level_enum)) {
				MediaPlayer prevLevelMP = sfxMap.get(prev_level_enum);
				prevLevelMP.stop();
				prevLevelMP.release();
				sfxMap.remove(prev_level_enum);
			}
		}
		
		//Load the current level's music
		if(!sfxMap.containsKey(level_enum))		
			sfxMap.put(level_enum, MediaPlayer.create(caller, level_resource));
	}
	
	private synchronized void setMusicPlaying(int new_music, boolean do_loop) {
		currently_playing_changed = true;
		this.currently_playing = new_music;
		this.do_loop = do_loop;
	}
	
	private synchronized int getMusicPlaying() {
		return this.currently_playing;
	}
	
	private synchronized boolean getMusicLoop() {
		return this.do_loop;
	}
	
	private synchronized boolean getMusicChanged() {
		boolean retval = currently_playing_changed;
		currently_playing_changed = false;
		return retval;
	}
	
	
	/** Called to play a given sound effects enum. It will pause whatever is currently playing, and then
	 * start playing the music/sound effect that corresponds to sfx_idx (see the enums at the top of this
	 * class definition).
	 * @param sfx_idx Sound effect of music to play
	 * @param looping Whether we should loop the sound or not
	 */
	private void playSfx(int sfx_idx, boolean looping) {
		if(current_mstream != null) current_mstream.pause();
		
		if(sfx_idx != MUSIC_NOTHING && sfxMap != null && sfxMap.containsKey(sfx_idx)) {
			current_mstream = sfxMap.get(sfx_idx);
			current_mstream.setLooping(looping);
			current_mstream.start();
		}
	}
	
	public void run() {		
		while(running) {
			//If we aren't suspended, check if the music has been changed
			//If it has been changed then play the new music with the given looping setting
			if(!getSuspended()) {
				if(getMusicChanged()) {
					playSfx(getMusicPlaying(), getMusicLoop());
				}
			}
			
			try { Thread.sleep(getSuspended() ? THREAD_DELAY_MS_SLOW : THREAD_DELAY_MS); }
			catch(InterruptedException e) { ; }
		}
		
	}

}
