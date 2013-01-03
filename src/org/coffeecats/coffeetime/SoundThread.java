/** SoundThread is the application Thread that is responsible for loading and playing music
 * and sound effects in TacoTime. It runs asynchronously (for the most part...) and 
 * depends on receiving messages through MessageRouter to play sound effects and
 * music.
 * 
 */

package org.coffeecats.coffeetime;

import java.util.HashMap;

import org.coffeecats.coffeetime.gamelogic.GameInfo;

import org.coffeecats.coffeetime.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SoundThread extends Thread {
	private final String activitynametag = "SoundThread";
	 
	//Enum all music & sfx
	private final int MUSIC_NOTHING = 0;
	private final int MUSIC_LEVEL_1 = 1;
	private final int MUSIC_LEVEL_2 = 2;
	private final int MUSIC_LEVEL_3 = 3;
	private final int MUSIC_LEVEL_4 = 4;
	private final int MUSIC_LEVEL_5 = 5;
	private final int MUSIC_LEVEL_6 = 6;
	private final int MUSIC_LEVELS_IMPLEMENTED = 6;
	
	private final int MUSIC_LEVEL_END = GameInfo.MAX_GAME_LEVEL + 1;
	
	//Count the total number of sounds in the sound pool
	private final int SFX_COUNT = 7;
	
	//Enum message types
	public static final int MESSAGE_PLAY_LEVEL_MUSIC = 1;
	public static final int MESSAGE_PLAY_LEVEL_END = 2;
	public static final int MESSAGE_PLAY_NOTHING = 3;
	public static final int MESSAGE_SUSPEND = 4;
	public static final int MESSAGE_UNSUSPEND = 5;
	public static final int MESSAGE_LOAD_LEVEL_MUSIC = 6;
	public static final int MESSAGE_TOGGLE_PAUSED = 7;
	
	//How long between SoundThread wake-ups
	private static final int THREAD_DELAY_MS = 250;
	private static final int THREAD_DELAY_MS_SLOW = 1000;
	
	//Handler for receiving messages
	public static Handler handler;
	
	//Objects to play music & sounds - utility & datapath, not state
	private Context caller;
	//Map from level number to level music enum (identifier)
	private HashMap<Integer, Integer> levelMusicMap;
	//Map from level music enum -> music resource identifier 
	private HashMap<Integer, Integer> levelMusicResourceMap;
	//Map from sound effect enum -> MediaPlayer resource for that sound effect
	private HashMap<Integer, MediaPlayer> sfxMap;
	
	//Keeps track of state - what is playing and whether it has been changed
	private boolean currently_playing_changed;
	//Whether or not to loop the currently playing sound sample
	private boolean do_loop;
	//What sound sample is currently playing
	private int currently_playing;
	//The MediaPlayer object corresponding to what's currently playing
	private MediaPlayer current_mstream;
	//Whether this mstream is paused
	private boolean current_mstream_paused = false;
	//Whether this SoundThread is suspended
	private boolean suspended;
	//Set to true when we create the thread; it gets set to false in destroy() allowing us to
	//drop out of the busy loop and end the Thread's execution
	private boolean running;
	
	/** Create a new SoundThread. Instantiates the Hander (which handles messages received via
	 * MessageRouter) and also loads sound effects, setting up data structures 
	 * @param caller The calling Context, used to load MediaPlayer
	 */
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
				//Handle message to play level music
				if(msg.what == MESSAGE_PLAY_LEVEL_MUSIC) {
					if( levelMusicMap != null && levelMusicMap.containsKey(adjustLevelNumber(msg.arg1)) )
						setMusicPlaying(levelMusicMap.get(adjustLevelNumber(msg.arg1)), true);
				}
				//Handle message to *load* level music for a particular level, prepare to play it
				else if(msg.what == MESSAGE_LOAD_LEVEL_MUSIC) {
					if(levelMusicMap != null)
						loadLevelMusic(adjustLevelNumber(msg.arg1));
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
				
				else if(msg.what == MESSAGE_TOGGLE_PAUSED) {
					toggleMusicPaused();
				}
			}
		};
	}
	
	private int adjustLevelNumber(int level_number) {
		if(level_number > MUSIC_LEVELS_IMPLEMENTED)
			return ( ((level_number-1) % MUSIC_LEVELS_IMPLEMENTED) + 1 );
		else
			return level_number;
	}
	
	/** Called when the SoundThread is to be wound down. Releases all associated resources, mostly the
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
	
	/** Set whether this SoundThread is suspended or not */
	private synchronized void setSuspended(boolean n_suspended) { suspended = n_suspended; }
	/** Returns true if this SoundThread is suspended, else false */
	private synchronized boolean getSuspended() { return suspended; }
	
	/** Sets up the sound maps, i.e. the Map data structures defined in the header of this class.
	 * It loads all of the sound effect MediaPlayer instances, but does NOT load the level music - that
	 * is to be done by loadLevelMusic().
	 */
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
		levelMusicMap.put(3, MUSIC_LEVEL_3);
		levelMusicResourceMap.put(MUSIC_LEVEL_3, R.raw.music_level_3_4);
		levelMusicMap.put(4, MUSIC_LEVEL_4);
		levelMusicResourceMap.put(MUSIC_LEVEL_4, R.raw.music_level_3_4);
		levelMusicMap.put(5, MUSIC_LEVEL_5);
		levelMusicResourceMap.put(MUSIC_LEVEL_5, R.raw.music_level_5_6);
		levelMusicMap.put(6, MUSIC_LEVEL_6);
		levelMusicResourceMap.put(MUSIC_LEVEL_6, R.raw.music_level_5_6);
		
		//Mapping from sound effects to actual effect indices, and their durations	
		sfxMap.put(MUSIC_LEVEL_END, MediaPlayer.create(caller, R.raw.sfx_level_end));
	}
	
	/** Loads the music for a particular level. Unloads music for the previous level (if it was loaded). It
	 * is optimized so that if the music is already loaded we don't re-load it.
	 * @param level_number The level # to load & prepare the music for
	 */
	private void loadLevelMusic(int level_number) {		
		Log.d(activitynametag, "Going to load music for level " + level_number);
		
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
	
	/** Sets the currently playing music
	 * 
	 * @param new_music The enum sound effect identifier for the sound/music to play
	 * @param do_loop Whether we should loop this sound/music indefinitely
	 */
	private synchronized void setMusicPlaying(int new_music, boolean do_loop) {
		currently_playing_changed = true;
		this.currently_playing = new_music;
		this.do_loop = do_loop;
	}
	
	/** Returns the enum for the music we're playing */
	private synchronized int getMusicPlaying() {
		return this.currently_playing;
	}
	
	/** Returns true if we are to loop the currrently-playing music */
	private synchronized boolean getMusicLoop() {
		return this.do_loop;
	}
	
	/** Returns true if the music has been changed. Sets currently_playing_changed to false regardless,
	 * so we only indicate changed once (via this method) after the music playing has been changed.
	 * @return
	 */
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
			current_mstream_paused = false;
		}
	}
	
	/** Called to pause / unpause the music */
	private synchronized void toggleMusicPaused() {
		if(current_mstream != null) {
			if(current_mstream_paused) 
				//If we are NOT looping the mstream AND we are within 10ms of the end, don't play it
				if(!getMusicLoop() && current_mstream.getCurrentPosition() >= (current_mstream.getDuration()-10));
				//otherwise, do play it
				else current_mstream.start();
			else
				current_mstream.pause();
				
			current_mstream_paused = !current_mstream_paused;
		}
	}
	
	/** Run this thread. It's a poll loop that checks if the music has been changed and, if so,
	 * plays the new music. Uses Thread.sleep() to allow Thread to idle between music change polls.
	 */
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
