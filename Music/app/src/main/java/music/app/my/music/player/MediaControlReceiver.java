package music.app.my.music.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;


public class MediaControlReceiver extends BroadcastReceiver {
	
	
	//intent reciever for media control buttons
   @Override
   public void onReceive(Context context, Intent intent) {
   	System.out.println("Remote event received!");
    //   if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
        // Pause the playback
    	  Intent pauseIntent = new Intent(context, MusicService.class);
   	   	pauseIntent.setAction(MusicService.ACTION_PAUSE);
 			context.startService(pauseIntent);
    	  return;
    }
    
           KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
           if(event.getAction() ==  KeyEvent.ACTION_UP){
           if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
        		Intent playIntent = new Intent(context, MusicService.class);
        		playIntent.setAction(MusicService.ACTION_PLAY);
       			context.startService(playIntent);
           	
           } else if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE){
        	   Intent pauseIntent = new Intent(context, MusicService.class);
        	   pauseIntent.setAction(MusicService.ACTION_PAUSE);
      			context.startService(pauseIntent);
           }
           else if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE){
        	   Intent toggleIntent = new Intent(context, MusicService.class);
        	   toggleIntent.setAction(MusicService.ACTION_TOGGLE_PLAYBACK);
      			context.startService(toggleIntent);
           } else if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT){
        	   Intent nextIntent = new Intent(context, MusicService.class);
        	   nextIntent.setAction(MusicService.ACTION_NEXT);
      			context.startService(nextIntent);
              	
           } else if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS){
        	    Intent previousIntent = new Intent(context, MusicService.class);
       			previousIntent.setAction(MusicService.ACTION_PREVIOUS);
        	    context.startService(previousIntent);
              	
           }
       }
   }
   


	
}



