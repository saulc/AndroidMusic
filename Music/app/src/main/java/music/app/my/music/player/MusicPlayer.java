package music.app.my.music.player;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Equalizer;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;

import music.app.my.music.types.Song;
import music.app.my.music.types.plist;


public class MusicPlayer implements OnPreparedListener, OnCompletionListener {

	private Equalizer eq;
	
	private ArrayList<myPlayer> player;
	private int currentPlayer = 0;	//the one actually palying
	private int nextPlayer = -1;	//fade out
	private int auxPlayer = -1;	//fade out fast skip?

	private MusicPlayerStateListener sListener;
	public enum MUSICPLAYER_STATE {PLAYING, PAUSED_USER, PAUSED, PREPARING, PREPARED, STOPPED, PLAYING_DUCKING};
	interface MusicPlayerStateListener{
		public void onStateChanged(MUSICPLAYER_STATE s);
		public void stopUiCallbacks();
	}

	private MUSICPLAYER_STATE mState;
	private plist queue;
	private int index = 0;
	
	private boolean CrossFadeEnabled = true;
	private boolean playWhenPrepared = true;
	private int fadeInDuration = 1000, fadeOutDuration = 2000, fadeOutGap = 4000;

	private void log(String s){
		Log.d(getClass().getSimpleName(), s);
	}
	
	public MusicPlayer(MusicPlayerStateListener l){
		iniPlayer();
		sListener = l;
		mState = MUSICPLAYER_STATE.STOPPED;

		if(!CrossFadeEnabled)
		{
			fadeInDuration = 500;
			fadeOutDuration = 500;
			fadeOutGap = 2000;
		}
		queue = new plist();
		index = 0;
		
	}

	//3 myplayers only! never add. never remove. only reset.
	private void iniPlayer(){
		if(player == null){
			player = new ArrayList<myPlayer>();
			currentPlayer = 0;
			auxPlayer = 1;
			nextPlayer = 2;

			myPlayer temp = new myPlayer();
			player.add(temp);
			temp = new myPlayer();
			player.add(temp);
			temp = new myPlayer();
			player.add(temp);

		}

	}

	public void setFadeOutGap(int g){
		for(myPlayer p: player)
			//myPlayer p = player.get(currentPlayer);
			p.setFadeOutGap(g);
	}

	private void setState(MUSICPLAYER_STATE s){
		mState = s;
		sListener.onStateChanged(mState); //update gui, remotes, ...
	}

	public void removeCallbacks(){


		player.get(currentPlayer).removeCallbacks();
	}
	
    public void togglePlaybackRequest() {
		  if (player.get(currentPlayer).isPaused()) {
	            playRequest();
	        } else {
	            pauseRequest();
	        }
	}
	public void stopRequest() {

			setState(MUSICPLAYER_STATE.STOPPED);
		try {
		 //   player.get(currentPlayer).removeCallbacks();
			player.get(currentPlayer).stop();
			//player.get(currentPlayer).reset();
//			player.get(currentPlayer).release();
//			player.remove(currentPlayer);
		} catch (IllegalStateException e) {
			throw e;
		}

       

	}
	
	public void pauseRequest() {
		 if(mState == MUSICPLAYER_STATE.PLAYING || mState == MUSICPLAYER_STATE.PLAYING_DUCKING)
			 player.get(currentPlayer).pausePlayback();
			 
			 
			 setState(MUSICPLAYER_STATE.PAUSED_USER);
		 
	}
	
	public void previousRequest() {
		if(getProgress() >= 10)
			playSongFromQueue(queue.getIndex());
		else if(queue.hasPrevious()){
			fadeOutOldPlayer();
			queue.previousSong();
			playWhenPrepared = true;
			prepareSong();
			
		}
	}
	public void nextRequest() {
		if(queue.getRepeatMode() == 1) playSongFromQueue(queue.getIndex());

		if(queue.hasNext()){
			
			fadeOutOldPlayer();
			queue.nextSong();
			playWhenPrepared = true;
			prepareSong();
		
		}
	}
	
	public void playSongFromQueue(int index) {
		if( index < queue.getSize() ){
			
			fadeOutOldPlayer();
			queue.setIndex(index);
			playWhenPrepared = true;
			prepareSong();
		
		}
	}

	
	public boolean isPlaying(){
		return !player.get(currentPlayer).isPaused();
	}

	public int getCurrentPlayer(){ return currentPlayer; }
	public int getNextPlayer(){ return nextPlayer; }
	public int getAuxPlayer(){ return auxPlayer; }

	//fade out whats playing
	//get the player (LIST) ready to play the next song.
	//never add or remove a player.
	//normal playback
	/*
	cp = playing
	op = fadeing out / none
	aux = fading out / none

	next();
	op = cp = fading out;
	cp = aux = reset()
	aux = temp (old)
	 */
	private void fadeOutOldPlayer(){

		log("Fadeout Old: old: " + nextPlayer + " cp: " + currentPlayer + " aux: " + auxPlayer);

		if(!isPlaying()) return; //if not playing no need to fade it out.

		//other wise stop the ui updates.
		sListener.stopUiCallbacks();

		int temp = nextPlayer;		//save

		nextPlayer = currentPlayer;
		player.get(nextPlayer).pausePlayback(); //fade out.
//		player.get(nextPlayer).stopAndFadeOut();

		//aux player should have been siting for one song
		//or at least a few seconds
		currentPlayer = auxPlayer;

		player.get(currentPlayer).stop();  //stop incase its not?
		player.get(currentPlayer).reset();

		auxPlayer = temp;
		log("Fadeout Old end: " + nextPlayer + " cp: " + currentPlayer + " aux: " + auxPlayer);

	}



	//this should never be called!!
	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i("Music Service", mp + " player completed! wft!!! <<------");
		((myPlayer)mp).removeCallbacks();

		if(player.contains(mp)){
			player.remove(mp);
			player.add(new myPlayer());
		}
		mp.release();
		mp = null;

		Log.i("Music Service", mp + "Get ready...Crunch? --->>>");
	}



	public void playRequest() {


		log("Play requested");
		if(queue.getSize() == 0) //play all songs
		{
			log("No songs in queue");
			return;	//for now do nothing
		}
		
		else if(!player.get(currentPlayer).isPrepared()){
			playWhenPrepared = true;
			prepareSong();
			log("player not ready..");
		}
	    else if(player.get(currentPlayer).isPaused()) {
			log("Playing...");
	            // If we're paused, just continue playback and restore the 'foreground service' state.
	    	player.get(currentPlayer).playAndFadeIn();
	    	setState(MUSICPLAYER_STATE.PLAYING);

	    }
	
	}

	public int getAID(){
		return player.get(currentPlayer).getAudioSessionId();
	}
	public String setEQ(int preset){
		if(eq ==null)  return "";

			short np = eq.getNumberOfPresets();
			if(preset < np) {
				eq.usePreset((short) preset);
				String n = eq.getPresetName((short) preset);
				log("Setting eq: " + n);
				n = eq.getPresetName(eq.getCurrentPreset());
				log("SET eq: " + n);
				return n;
			}
		return "";
	}

	public ArrayList<String> getEQPresets(){
		if(eq==null) return null;
		ArrayList<String> p = new ArrayList<String>();
		int np = eq.getNumberOfPresets();
		for(short i =0; i< np; i++ ) p.add(eq.getPresetName(i));


		return p;
	}


	private void iniAFX(int aid){
		log("Setting eq.");
		eq = new Equalizer(0, aid);

		short np = eq.getNumberOfPresets();
	//	for(short i =0; i< np; i++ ) log("Presents: " + i + " " + eq.getPresetName(i));

		if(np>0)
		eq.usePreset((short)0);

		log("Setting eq: " + eq.getPresetName((short)0));
	}


	@Override
	public void onPrepared(MediaPlayer arg0) {
		Log.d("Music Service", "Prepared!! :p");
		setState(MUSICPLAYER_STATE.PREPARED);
		player.get(currentPlayer).prepared();
		iniAFX(player.get(currentPlayer).getAudioSessionId());

		//mListener.setAudioId(player);
		if(seekMe != 0) {
			player.get(currentPlayer).seekTo(seekMe);
			seekMe=0;
		}
		if(playWhenPrepared == true){
			playRequest();
		} //else player.get(currentPlayer).pause();
	}

	private void prepareSong() {
		if(queue.getSize() > 0){
			try {
				
		
			 player.get(currentPlayer).setDataSource(queue.nowPlayingPath());
			 player.get(currentPlayer).setOnPreparedListener(this);
			 player.get(currentPlayer).setOnCompletionListener(this);
			 player.get(currentPlayer).prepareAsync();
			 setState(MUSICPLAYER_STATE.PREPARING);
			// player.get(currentPlayer).attachAuxEffect(eq.getId());
			 
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int getFadeOutDuration(){ return player.get(currentPlayer).getFadeOutDuration(); }
	public int getFadeOutGap(){ return player.get(currentPlayer).getFadeOutGap(); }

	public int getCurrentPosition(){
		if(!isPlaying()) return 0;
		return player.get(currentPlayer).getCurrentPosition();
	}

	public int getProgress(){


		if(!isPlaying()) return 0;

		double a = player.get(currentPlayer).getCurrentPosition();
		double d = player.get(currentPlayer).getDuration();
		//log(a + " time: " + d + " aa " + (a/d));
		//current/total == percent * 100.
		return (int)(a/d*100);
	}
	public int getDuration(){
		if(player.get(currentPlayer).isPrepared() && !player.get(currentPlayer).isPaused())
		return (player.get(currentPlayer).getDuration()/1000);
		return 110;
	}


    private int seekMe = 0;
    public void seekTo(int secs){
    	if(!player.get(currentPlayer).isPrepared()) seekMe = secs;
    	if(secs < getDuration() )
    	player.get(currentPlayer).seekTo(secs * 1000);
    	else if(secs >= getDuration() ){
    		player.get(currentPlayer).seekTo(player.get(currentPlayer).getDuration() );
    		player.get(currentPlayer).pause();
    	}
    }

    public void duck(){
    	if(isDucking())
    		player.get(currentPlayer).setDuckVolume();
    	else player.get(currentPlayer).setNormalVolume();
	}

    public boolean isDucking(){ return mState == MUSICPLAYER_STATE.PLAYING_DUCKING; }
	public void setDucking(boolean b){
	
			
			if(b && mState == MUSICPLAYER_STATE.PLAYING)
				mState = MUSICPLAYER_STATE.PLAYING_DUCKING;
			else if(!b && mState == MUSICPLAYER_STATE.PLAYING_DUCKING)
				mState = MUSICPLAYER_STATE.PLAYING;
			else if(!b && mState == MUSICPLAYER_STATE.PAUSED)
			mState = MUSICPLAYER_STATE.PAUSED;
			sListener.onStateChanged(mState);
	}
	
	

	public Song getCurrentSong() {
		return queue.getCurrentSong();
	}

	public boolean shuffle() {

		return queue.shuffle();
	}

	public boolean repeat() {
		
		return queue.repeat();
	}
	public int repeatMode() {

		return queue.getRepeatMode();
	}


	public void setQueue(plist q) {
		queue = q;
		index = 0;
	}

	public plist getQueue() {
		
		return queue;
	}

	public void addSong(Song song) {
		queue.addSong(song);
	}
	
	public Song removeSong(int index){
		return queue.removeSong(index);
	}

	public ArrayList<myPlayer> getmPlayers(){ return player; }
	
}
