package music.app.my.music.player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

public class myPlayer extends MediaPlayer
		implements MediaPlayer.OnErrorListener {

	private Handler mHandler = new Handler();
	private float volumeValue = 0f;
	private boolean paused = true;
	private int fadeInDuration = 1000, fadeOutDuration = 2000, fadeOutGap = 4000, startGap = 0;
	private int mCurrentStep = 1;
    private float duckVolume = .1f;
    private boolean isPrepared = false;
	private int id = -1;

    public myPlayer(){
    	super();
    	
    }
 
    public myPlayer(int in, int out, int outg, int startg){
    	super();
    	fadeInDuration = in;
    	fadeOutDuration =out;
    	fadeOutGap = outg;
    	startGap = startg;
    }

    public void setId(int i){
    	id = i;
	}

	public int getId(){
    	return id;
	}

	public void playAndFadeIn(){
		paused = false;
		start();
		setVol(0);
		mHandler.post(fadeInVolume);
		
	}
	public void playAndFadeIn(int ms){
		fadeInDuration = ms;
		playAndFadeIn();
		
	}
	public void stopAndFadeOut(){
		mHandler.post(fadeOutVolume);
		mHandler.postDelayed(stopAndRelease, fadeOutDuration + 1500);
	}
	public void pausePlayback(int ms){
		fadeOutDuration = ms;
		pausePlayback();
	}
	public void pausePlayback(){
    	if(paused) return;
		mHandler.post(fadeOutVolume);
		mHandler.postDelayed(pause, fadeOutDuration + 1000);
		paused = true;
	}

	public void pausePlaybackNow(){
		if(paused) return;
		mHandler.post(fadeOutVolume);
		mHandler.postDelayed(pause, 200);
		paused = true;
	}
	
	public void resumePlayback(){
		if(paused){
			paused = false;
			start();
		}
	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public void prepared(){
		isPrepared = true;
	}
    public boolean isPrepared(){
    	return isPrepared;
    }

    public float getVolumeValue(){ return volumeValue; }


    private void setVol(float v ){
    	try {

			if(isPlaying()){
			volumeValue = v;
			setVolume(v, v);
			//Log.i("MyPlayer", "volume at " + volumeValue);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			//removeCallbacks();
			mHandler.removeCallbacks(fadeOutVolume);
			mHandler.removeCallbacks(fadeInVolume);
		}
    	
    }
  
	  public Runnable fadeInVolume = new Runnable(){
	    	
	    	@Override
	    public void run()
	    {
	    	
	    		//setVol( (float) ( Math.log( mCurrentStep + 1) / (Math.log( (fadeInDuration/20)) )) );
	    		setVol( (float) ( Math.pow(mCurrentStep, 2) / Math.pow((fadeInDuration / 50), 2) ) );
	    		//Log.d("Myplayer", mCurrentStep + " fading in: " + volumeValue);
	    		//setVol( (float) ( Math.pow( ( mCurrentStep - (fadeOutDuration/20)), 2) / Math.pow( mCurrentStep + (fadeOutDuration/20), 2) ) );

	    		mCurrentStep++;
	      // if (mCurrentStep++ > (fadeInDuration/20)) {
	    		if(volumeValue >= .98){
	        	setVol(1);
	        	mHandler.removeCallbacks(fadeInVolume);
	        	mCurrentStep = 1;
	        } else mHandler.postDelayed(fadeInVolume, 50);
	    }
	    };
	    
	    
	    public Runnable fadeOutVolume = new Runnable(){
	    	
	    	@Override
	    public void run()
	    {
	    		
	    		//setVol((float) ( Math.log( (fadeOutDuration/20) - mCurrentStep + 1) / (Math.log( (fadeOutDuration/20) ) ) ) );
//	    		setVol( (float) ( ( ( (fadeOutDuration/50) - mCurrentStep) * ( (fadeOutDuration/50) - mCurrentStep) ) 
//	    				/( (fadeOutDuration/50) * (fadeOutDuration/50) ) ) );
	    		setVol( (float) ( Math.pow((fadeOutDuration / 50) - mCurrentStep, 2) / Math.pow(fadeOutDuration / 50, 2) ) );
	    		//Log.d("Myplayer", mCurrentStep + " fading out: " + volumeValue);

	    		mCurrentStep++;
	    //    if (mCurrentStep++ > (fadeOutDuration/20)) {  || (getCurrentPosition() >= getDuration()) 
	    		if((volumeValue <= 0.15) ){
	        	setVol(0f);
	        	mHandler.removeCallbacks(fadeOutVolume);
	        	mCurrentStep = 1;
	        } else 	mHandler.postDelayed(fadeOutVolume, 50);
	    }
	    };
	    
	    public Runnable stopAndRelease = new Runnable(){
	    	
	    	@Override
	    	public void run(){
	    		stop();
	    		//reset();
	    		//release();
	    		//this = null;
	    	}
	    };

	    public Runnable pause = new Runnable(){
	    	
	    	@Override
	    	public void run(){
	    		pause();
	    		paused = true;
	    		//seekTo(getCurrentPosition() - fadeOutDuration - 1);
	    	//	reset();
	    		//release();
	    	}
	    };

	    public void removeCallbacks(){
	    	mHandler.removeCallbacks(fadeInVolume);
	    	mHandler.removeCallbacks(fadeOutVolume);
	    	mHandler.removeCallbacks(pause);
	    	
	    }
		public void setDuckVolume() {
			if(!paused)
				setVol(duckVolume);
			// TODO Auto-generated method stub
			
		}
		public void setNormalVolume() {
			// TODO Auto-generated method stub
			if(!paused)
				setVol(1f);
		}
		


		public int getFadeInDuration() {
				return fadeInDuration;
			}
	public int getFadeOutDuration() {
		return fadeOutDuration;
	}
	public int getFadeOutGap() {
		return fadeOutGap;
	}
	public int getStartGap() { return  startGap; }

			public void setFadeInDuration(int fadeInDurationSecs) {
	    		if(fadeInDurationSecs < 1) fadeInDurationSecs = 1;
				this.fadeInDuration = fadeInDurationSecs *1000;
			}

			public void setFadeOutDuration(int fadeOutDurationSecs) {
	    	if(fadeOutDurationSecs < 1) fadeOutDurationSecs = 1;
				this.fadeOutDuration = fadeOutDurationSecs*1000;
			}

			public void setFadeOutGap(int fadeOutGapSecs) {
	    	if(fadeOutGapSecs < 1) fadeOutGapSecs = 1;
				this.fadeOutGap = fadeOutGapSecs * 1000;
			}

			public void setStartGap(int s){ if(s < 1) s = 1; startGap = s*1000; }

	/*
    boolean	True if the method handled the error, false if it didn't.
    Returning false, or not having an OnErrorListener at all,
    will cause the OnCompletionListener to be called.
     */
	@Override
	public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
		Log.d(getClass().getSimpleName().toString(), "onError: " + i + " i1: " + i1);


		return true;
	}
}
