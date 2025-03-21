package music.app.my.music.player;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Equalizer;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;

import music.app.my.music.helpers.Logger;
import music.app.my.music.types.Song;
import music.app.my.music.types.plist;


public class MusicPlayer implements OnPreparedListener, OnCompletionListener {

	private Equalizer eq;
	private Handler mHandler;
	
	private ArrayList<myPlayer> player;
	 private int currentPlayer = 0;	//the one actually palying
	 private int nextPlayer = 1;	//fade out
	 private int auxPlayer = 2;	//fade out fast skip?

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
	private int fadeInDuration = 7000, fadeOutDuration = 12000, fadeOutGap = 4000, fadeInGap = 1000, crossFade = 3000;

	private void log(String s){
//        Logger.log(TAG, s);
		Log.i(getClass().getSimpleName(), s);
	}

	public void setVolStep(boolean active){
		for(int i=0; i <player.size(); i++)
			player.get(i).setVolStep(active);
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
			fadeInGap = 500;
		}
		queue = new plist();
		index = 0;

		mHandler = new Handler();
	}

	//3 myplayers only! never add. never remove. only reset.
	private void iniPlayer(){
		if(player == null){
			player = new ArrayList<myPlayer>();
			currentPlayer = 0;
			nextPlayer = 1;
			auxPlayer = 2;

			myPlayer temp = new myPlayer(0);
//			temp.setId(0);
			player.add(temp);
			temp = new myPlayer(1);
//			temp.setId(1);
			player.add(temp);
			temp = new myPlayer(2);
//			temp.setId(2);
			player.add(temp);


		}

	}

	//update the new music player's fader settings ..
	public void updateFader(){
		setFadeInDuration(fadeInDuration);
		setFadeInGap(fadeInGap);
		setFadeOutDuration(fadeOutDuration);
		setFadeOutGap(fadeOutGap);
		setCrossFade(crossFade);
	}
	public void setFadeOutGap(int g){
		fadeOutGap = g;
		for(myPlayer p: player)
			//myPlayer p = player.get(currentPlayer);
			p.setFadeOutGap(g);
	}
	public void setFadeInGap(int g){
		fadeInGap = g;
		for(myPlayer p: player)
			p.setStartGap(g);
	}
	public void setFadeOutDuration(int g){
		fadeOutDuration = g;
		for(myPlayer p: player)
			p.setFadeOutDuration(g);
	}

	public void setFadeInDuration(int g){
		fadeInDuration = g;
		for(myPlayer p: player)
			p.setFadeInDuration(g);
	}

	public void setCrossFade(int g){
		crossFade = g;
		for(myPlayer p: player)
			p.setCrossFade(g);
	}

	public int getFadeInGap(){ return player.get(currentPlayer).getStartGap(); }
	public int getFadeInDuration(){ return player.get(currentPlayer).getFadeInDuration(); }
	public int getFadeOutDuration(){ return player.get(currentPlayer).getFadeOutDuration(); }
	public int getFadeOutGap(){ return player.get(currentPlayer).getFadeOutGap(); }
	public int getCrossFade(){ return player.get(currentPlayer).getCrossFade(); }


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
		 if(mState == MUSICPLAYER_STATE.PLAYING || mState == MUSICPLAYER_STATE.PLAYING_DUCKING) {
			 player.get(currentPlayer).pausePlayback();

			 setState(MUSICPLAYER_STATE.PAUSED_USER);
		 }
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
			
//			fadeOutOldPlayer();

			incrementPlayers();
			mHandler.postDelayed(setOldPlayerFadeout, crossFade);
			queue.nextSong();
			playWhenPrepared = true;
			prepareSong();
		
		}
	}
	private Runnable setOldPlayerFadeout = new Runnable() {
		@Override
		public void run() {
			log("Fading out older player...");
			fadeOutOldPlayer();
			
		}
	};

	public void playSongFromQueue(int index) {
		if( index < queue.getSize() ){

			queue.setIndex(index);
			incrementPlayers();
			mHandler.postDelayed(setOldPlayerFadeout, crossFade);
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
	//	if(!isPlaying()) return; //if not playing no need to fade it out.

		//other wise stop the ui updates.
//		sListener.stopUiCallbacks();

		//new 3 player loop. simple.
		player.get(auxPlayer).pausePlayback();  //pause and fade out current.

		//setup the new player, aux should be 2 songs previous. already faded out and done with.
		//aux represents the player after next.
		//we reset now. the next player will be reset when it becomes aux ie. after it has faded out.
//		player.get(auxPlayer).stop();
//		player.get(auxPlayer).reset();


//
//		incrementPlayers(); //move each player role to the next step. cur -> next - > aux -> cur *

		//first try 3 player loop. wtf was i thinking? lol
//		int temp = nextPlayer;		//save
//
//		nextPlayer = currentPlayer;
//		player.get(nextPlayer).pausePlayback(); //fade out.
////		player.get(nextPlayer).stopAndFadeOut();
//
//		//aux player should have been siting for one song
//		//or at least a few seconds
//		currentPlayer = auxPlayer;
//
//		player.get(currentPlayer).stop();  //stop incase its not?
//		player.get(currentPlayer).reset();
//
//		auxPlayer = temp;
		log("Fadeout Old end: " + nextPlayer + " cp: " + currentPlayer + " aux: " + auxPlayer);

	}

	private void incrementPlayers(){
		//current = 0, next = 1, aux = 2

		//even worse.
		int temp = currentPlayer;
		currentPlayer = nextPlayer;
		nextPlayer = auxPlayer;
		auxPlayer = temp;

		//it should be logically eq. but with less risk
//		if(++currentPlayer >= player.size()) currentPlayer = 0;
//		if(++nextPlayer >= player.size()) nextPlayer = 0;
//		if(++auxPlayer >= player.size()) auxPlayer = 0;
	}



	//this should never be called!!
	@Override
	public void onCompletion(MediaPlayer mp) {
		log( mp + " player completed! wft!!! <<------");
		((myPlayer)mp).removeCallbacks();

		int i = currentPlayer; //((myPlayer)mp).getId();
		log( "PLayer completed: " + i);
		player.remove(i);
		myPlayer rp = new myPlayer(i);
//		rp.setId(i);
		player.add(i,  rp);
		updateFader();


//		if(player.contains(mp)){
//			player.remove(mp);
//			player.add(new myPlayer());
//		}
		mp.release();
		mp = null;

		log( mp + "Get ready...Crunch? --->>>");
	}



	public void playRequest() {

//		updateFader();
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

//	    	mHandler.postDelayed(volCheck, getFadeInDuration()*2);
	    	//fix annoying problem where current player is faded out

			//try to catch it early, hopefully before its audible.
//			mHandler.postDelayed(volCheck2,  getFadeInDuration()+320);

	    }
	
	}

	public void fadeIn(){
		player.get(currentPlayer).fadeIn();
	}

	private Runnable volCheck = new Runnable() {
		@Override
		public void run() {
			log("Checking volume levels.");
			myPlayer p = player.get(currentPlayer);
			if(!p.isPaused() && p.isPlaying()){
				float v = p.getVolumeValue();
				for(int i=0; i<1000; i++);

				if(v < p.getVolumeValue() || v < .05f){
					log("Using volume fix: " + v + " Fading in...");
					p.fadeIn();
				}else if(v < .75f){
					log("Volume ok. " + v +" " + p.getVolumeValue());

					mHandler.postDelayed(volCheck, 100);
				}else
					log("Volume ok. " + v +" " + p.getVolumeValue());

			}

		}
	};

	private Runnable volCheck2 = new Runnable() {
		@Override
		public void run() {
			log("Checking fade in...");
			myPlayer p = player.get(currentPlayer);
			if(!p.isPaused() && p.isPlaying()){
				float v = p.getVolumeValue();
				if(v < .7f){
					log("Fade out caught. " + v + " applying fix...");
					p.fadeIn();
				}else log("fade in ok. " + v);
			}

		}
	};



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

		if(playWhenPrepared == true){
			//apply fade in gap if its been set. skip song intro.
			if(player.get(currentPlayer).getStartGap() > 0)
				player.get(currentPlayer).seekTo(player.get(currentPlayer).getStartGap());

			playRequest();
		} //else player.get(currentPlayer).pause();
		if(seekMe != 0) {
			player.get(currentPlayer).seekTo(seekMe);
			seekMe=0;
		}
	}

	private void prepareSong() {
		if(queue.getSize() > 0){
			try {

//			 player.get(currentPlayer).stop();
			 player.get(currentPlayer).reset();
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
	public int getEndspace(){
		if(player.get(currentPlayer).isPrepared() && !player.get(currentPlayer).isPaused())
			return player.get(currentPlayer).getEndspace();
		return 0;
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

	public int shuffle() {

		return queue.shuffle();
	}
	public boolean reshuffle() {

		return queue.shuf();
	}


	public int repeat() {
		
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
