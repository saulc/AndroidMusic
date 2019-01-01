package music.app.my.music.player;

import android.media.MediaPlayer;
import android.os.Handler;

public class myPlayer extends MediaPlayer {

	private Handler mHandler = new Handler();
	private float volumeValue = 0f;
	private boolean paused = true;
	public int fadeInDuration = 1000, fadeOutDuration = 2000, fadeOutGap = 4000;
	private int mCurrentStep = 1;
    private float duckVolume = .1f;
    private boolean isPrepared = false;
	
    public myPlayer(){
    	super();
    	
    }
 
    public myPlayer(int in, int out, int outg){
    	super();
    	fadeInDuration = in;
    	fadeOutDuration =out;
    	fadeOutGap = outg;
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
		//mHandler.postDelayed(stopAndRelease, fadeOutDuration + 1500);
	}
	public void pausePlayback(int ms){
		fadeOutDuration = ms;
		pausePlayback();
	}
	public void pausePlayback(){
		mHandler.post(fadeOutVolume);
		mHandler.postDelayed(pause, fadeOutDuration + 1000);
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
  
    private void setVol(float v ){
    	try {
			if(isPlaying()){
			volumeValue = v;
			setVolume(v, v);
			//Log.i("MyPlayer", "volume at " + volumeValue);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			removeCallbacks();
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
	    		mHandler.postDelayed(fadeInVolume, 50);
	    		mCurrentStep++;
	      // if (mCurrentStep++ > (fadeInDuration/20)) {
	    		if(volumeValue >= .98){
	        	setVol(1);
	        	mHandler.removeCallbacks(fadeInVolume);
	        	mCurrentStep = 1;
	        }
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
	    		mHandler.postDelayed(fadeOutVolume, 50);
	    		mCurrentStep++;
	    //    if (mCurrentStep++ > (fadeOutDuration/20)) {  || (getCurrentPosition() >= getDuration()) 
	    		if((volumeValue <= 0.05) ){
	        	setVol(0);
	        	mHandler.removeCallbacks(fadeOutVolume);
	        	mCurrentStep = 1;
	        }
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
	    		seekTo(getCurrentPosition() - fadeOutDuration - 1);
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
			public void setFadeInDuration(int fadeInDuration) {
				this.fadeInDuration = fadeInDuration *1000;
			}
			public int getFadeOutDuration() {
				return fadeOutDuration*1000;
			}
			public void setFadeOutDuration(int fadeOutDuration) {
				this.fadeOutDuration = fadeOutDuration*1000;
			}
			public int getFadeOutGap() {
				return fadeOutGap;
			}
			public void setFadeOutGap(int fadeOutGap) {
				this.fadeOutGap = fadeOutGap;
			}
	
}
