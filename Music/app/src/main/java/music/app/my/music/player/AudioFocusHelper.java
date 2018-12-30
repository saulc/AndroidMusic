package music.app.my.music.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;


public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
	AudioManager mAudioManager;
	Context mContext;

	// other fields here, you'll probably hold a reference to an interface
	// that you can use to communicate the focus changes to your Service

	public AudioFocusHelper(Context ctx) {
		mContext = ctx;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		// ...
		//mContext
		Log.i("M6", "Listening for Audio focus!");
	}

	public boolean requestFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
				mAudioManager.requestAudioFocus( this, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN);
	}

	public boolean abandonFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
				mAudioManager.abandonAudioFocus(this);
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
			mContext.startService(new Intent(MusicService.ACTION_PAUSE));

			// Pause playback
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			mContext.startService(new Intent(MusicService.ACTION_BLANK).putExtra("ToDuckOrNotToDuck", "NO_DUCK"));

			Log.i("M6", "Got focus!");
			//am.registerMediaButtonEventReceiver(myEventReceiver);
			// Resume playback 
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			mContext.startService(new Intent(MusicService.ACTION_PAUSE));
			//  am.unregisterMediaButtonEventReceiver(myEventReceiver);
			mAudioManager.abandonAudioFocus(this);
			Log.i("M6", "lost focus!");
			// Stop playback
		} 
		else  if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
			mContext.startService(new Intent(MusicService.ACTION_BLANK).putExtra("ToDuckOrNotToDuck", "DUCK"));
			Log.i("M6", "ducking audio");
		} 


	}



}
