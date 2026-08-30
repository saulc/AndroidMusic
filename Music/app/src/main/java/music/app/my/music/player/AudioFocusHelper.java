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
			Intent i = new Intent(mContext, MusicService.class);
			i.setAction(MusicService.ACTION_DUCK);
			mContext.startService(i);
			Log.i("M6", "ducking audio");
		}
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
			Intent bi = new Intent(mContext, MusicService.class);
			bi.setAction(MusicService.ACTION_PAUSE);
			mContext.startService(bi);
			Log.i("M6", "lost focus transiently!");
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			Intent i = new Intent(mContext, MusicService.class);
			i.setAction(MusicService.ACTION_PLAY);
			mContext.startService(i);
			Log.i("M6", "Got focus!");
		} 
		else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			Intent bi = new Intent(mContext, MusicService.class);
			bi.setAction(MusicService.ACTION_PAUSE);

			mContext.startService(bi);
			Log.i("M6", "pausing music - lost focus for good!");

			mAudioManager.abandonAudioFocus(this);
		} 



	}



}
