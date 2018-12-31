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
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
			Intent i = new Intent(MusicService.ACTION_DUCK);
			//i.putExtra("ToDuckOrNotToDuck", "DUCK");
			i.setPackage("music.app.my.music.player");
			mContext.startService(i);
			Log.i("M6", "ducking audio");
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
			Intent bi = new Intent(MusicService.ACTION_DUCK);
			bi.setPackage("music.app.my.music.player");
			mContext.startService(bi);
			Log.i("M6", "lost focus!");

			// Pause playback
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			Intent i = new Intent(MusicService.ACTION_GOOSE);
//			i.putExtra("ToDuckOrNotToDuck", "NO_DUCK");
			i.setPackage("music.app.my.music.player");
			mContext.startService(i);

			Log.i("M6", "Got focus!");
			//am.registerMediaButtonEventReceiver(myEventReceiver);
			// Resume playback 
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			Intent bi = new Intent(MusicService.ACTION_PAUSE);
			bi.setPackage("music.app.my.music.player");

			mAudioManager.abandonAudioFocus(this);
			Log.i("M6", "lost focus for good!");
			// Stop playback
		} 



	}



}
