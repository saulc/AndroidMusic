package music.app.my.music.player;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;

import music.app.my.music.types.Song;
import music.app.my.music.types.plist;


public class MusicPlayer implements OnPreparedListener, OnCompletionListener {

	
	private ArrayList<myPlayer> player;
	private int currentPlayer = 0;
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
	
	private void iniPlayer(){
		if(player == null){
			player = new ArrayList<myPlayer>();
			currentPlayer = 0;
		}
		
		myPlayer temp = new myPlayer();
		player.add(temp);
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
	
	private void fadeOutOldPlayer(){

		sListener.stopUiCallbacks();

		myPlayer temp = new myPlayer();
		int old = currentPlayer;
		player.add(temp);
		currentPlayer = player.size() - 1;
		player.get(old).stopAndFadeOut();

//		myPlayer temp = new myPlayer();
//		player.add(currentPlayer, temp); //add new player
//		int old = currentPlayer + 1;
//		player.get(old).stopAndFadeOut();  //fade out the old player
		//player.remove(old);
				
		
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
	

	@Override
	public void onPrepared(MediaPlayer arg0) {
		Log.d("Music Service", "Prepared!! :p");
		setState(MUSICPLAYER_STATE.PREPARED);
		player.get(currentPlayer).prepared();
		//mListener.setAudioId(player);
		if(seekMe != 0) {
			player.get(currentPlayer).seekTo(seekMe);
			seekMe=0;
		}
		if(playWhenPrepared == true){
			playRequest();
		} //else player.get(currentPlayer).pause();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i("Music Service", mp + " player completed");
		((myPlayer)mp).removeCallbacks();

		if(player.contains(mp)){
			player.remove(mp);
			player.add(currentPlayer, new myPlayer());
		}
		mp.release();
		mp = null;
	//	nextRequest();
//		if(player2 !=null){
//		player2.stop();
//		player2.release();
//		player2 = null;
//		}
		
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
	public int getFadeOutDuration(){ return player.get(currentPlayer).fadeOutDuration; }
	public int getFadeOutGap(){ return player.get(currentPlayer).fadeOutGap; }

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
