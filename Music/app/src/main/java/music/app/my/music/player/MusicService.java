package music.app.my.music.player;


import java.util.HashMap;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
//import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import music.app.my.music.R;
import music.app.my.music.types.Song;
import music.app.my.music.types.plist;

public class MusicService extends Service implements OnSharedPreferenceChangeListener, MusicPlayer.MusicPlayerStateListener{

	final static int myID = 1234;
	private static final int NOTIFICATION_ID = 1;
	private Handler mHandler = new Handler();;
	private static  MusicPlayer player;
	private boolean playWhenPrepared = false;

   private final IBinder mBinder = new LocalBinder();
   private BoundServiceListener mListener;
   private AudioManager am;
   private  ComponentName myEventReceiver;
  // private RemoteControlReceiver mRemoteControlReceiver;

   //intents
   public static final String ACTION_TOGGLE_PLAYBACK = "com.app.m6.action.TOGGLE_PLAYBACK";
   public static final String ACTION_PLAY = "com.app.m6.action.PLAY";
   public static final String ACTION_PAUSE = "com.app.m6.action.PAUSE";
   public static final String ACTION_STOP = "com.app.m6.action.STOP";
   public static final String ACTION_NEXT = "com.app.m6.action.NEXT";
   public static final String ACTION_PREVIOUS = "com.app.m6.action.PREVIOUS";
   public static final String ACTION_BLANK = "com.app.m6.action.ACTION_BLANK";
	public static final String ACTION_DUCK = "com.app.m6.action.ACTION_DUCK";
	public static final String ACTION_GOOSE = "com.app.m6.action.ACTION_GOOSE";

   private RemoteControlClientCompat mRemoteControlClientCompat;
   private MediaControlReceiver mediaReceiver;
   private NotificationCompat.Builder mBuilder;
   private NotificationManager mNotificationManager;
 //  MyNoisyAudioStreamReceiver mNoisyAudioReceiver;
   private Notification mNotification = null;
  // private Equalizer eq;
   private  AudioFocusHelper audioFocus;
   private String queuePlaylist = "m7 Now Playing";
   private long queuePlaylistId =  0;

	private void log(String s){
		Log.d(getClass().getSimpleName(), s);
	}

   @Override
 	public void onCreate() {

       log("Music service qFragCreated!");
   	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
		pref.registerOnSharedPreferenceChangeListener(this);

        player = new MusicPlayer(this);

         am = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
         audioFocus = new AudioFocusHelper(this);
         audioFocus.requestFocus();

         myEventReceiver = new ComponentName(getPackageName(),  MediaControlReceiver.class.getName());

         //register mediareceiver to get media button intents and "noisy" intent/headphone disconnected
         mediaReceiver = new MediaControlReceiver();
         IntentFilter noisyAudioIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
         noisyAudioIntentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
         registerReceiver(mediaReceiver,noisyAudioIntentFilter);

         am.registerMediaButtonEventReceiver(myEventReceiver);

         setupRemoteControls();

         loadQueue();

       //  eq = new Equalizer(100, player.getAudioSessionId());
       //  setupEq();
     Log.i("Music Service", "Service Created");
   }

   /**
    * Called when we receive an Intent. When we receive an intent sent to us via startService(),
    * this is the method that gets called. So here we react appropriately depending on the
    * Intent's action, which specifies what is being requested of us.
    */
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
       log("Got command");
       String action = intent.getAction();
       if (action.equals(ACTION_TOGGLE_PLAYBACK)) togglePlaybackRequest();
       else if (action.equals(ACTION_PLAY)) playRequest();
       else if (action.equals(ACTION_PAUSE)) pauseRequest();
       else if (action.equals(ACTION_NEXT)) nextRequest();
       else if (action.equals(ACTION_STOP)) stopRequest();
       else if (action.equals(ACTION_PREVIOUS)) previousRequest();
       else if(action.equals(ACTION_BLANK)) ;
	   else if(action.equals(ACTION_DUCK)) {
		  		 player.setDucking(true);
	   }
		else if(action.equals(ACTION_GOOSE)){
				player.setDucking(false);
				player.duck();
		   log("Music service Audio ducking: " + player.isDucking() );

	   }



       return START_NOT_STICKY; // Means we started the service, but don't want it to
                                // restart in case it's killed.
   }


   public void duck(){
   log("Music service Audio ducking: " + player.isDucking() );
	   player.setDucking(!player.isDucking());
	   player.duck();

	   log("Music service Audio ducking: " + player.isDucking() );
   }

	@Override
	public void stopUiCallbacks() {

   	mHandler.removeCallbacks(updateUi);
	}

	/**
    * Class used for the client Binder.  Because we know this service always
    * runs in the same process as its clients, we don't need to deal with IPC.
    */
   public class LocalBinder extends Binder {
       public MusicService getService() {
       	Log.i("Music Service", "Service binded");
           // Return this instance of LocalService so clients can call public methods
           return MusicService.this;
       }

       public void setListener(BoundServiceListener listener) {
           mListener = listener;
       }
   }

   public interface BoundServiceListener {

   	//public void sendProgress(HashMap<String, String> info);

	    public void sendProgress(MusicPlayer player);
		public void setPlayPause(Boolean isPlaying);

		public void setAudioId(int aid);

		public void setAlbumArt();


   	}
   @Override
	public IBinder onBind(Intent arg0) {
	   log("on bind called");
		return mBinder;
	}



   public void seekTo(int progress){
	   double secs = progress;
	   log("Seek playback to " +secs + "% requested");

	   int d = getDuration();
	   log("Duration " +d + " seconds");
	   secs = secs * d;
	   log("seekto " + secs + " m seconds");
	   secs = secs / 100;
	   log("seekto " + secs + " m seconds");

	   if(secs < getDuration() )
   		player.seekTo((int)(secs ));

   	else if(secs >= getDuration() ){
   		player.seekTo(player.getDuration() );
   		pauseRequest();
   	}
   }
   public void togglePlaybackRequest() {
	   log("Toggle playback requested");
	   player.togglePlaybackRequest();

	}
	private void stopRequest() {
		log("Stop playback requested");
		player.stopRequest();

	}

	@Override
	public void onDestroy(){
		saveQueue();
		unregisterReceiver(mediaReceiver);
		audioFocus.abandonFocus();
		mHandler.removeCallbacks(updateUi);
//		if(player2 != null)
//		try {
//			stopRequest();
//			player2.stop();
//			player2.reset();
//			player2.release();
//			player2 = null;
//		} catch (IllegalStateException e) {
//
//			e.printStackTrace();
//		}

	//	player.removeCallbacks();
		super.onDestroy();
	}
	public void pauseRequest() {
		player.pauseRequest();

	}
	public void previousRequest() {
		player.previousRequest();
	}
	public void nextRequest() {
		player.nextRequest();
	}

	public void playSongFromQueue(int index) {
		player.playSongFromQueue(index);
	}

	public void playRequest() {
		player.playRequest();
	}

	private void setupRemoteControls(){
		  // Use the remote control APIs (if available) to set the playback state

       if (mRemoteControlClientCompat == null) {
           Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
           intent.setComponent(myEventReceiver);
           mRemoteControlClientCompat = new RemoteControlClientCompat(
                   PendingIntent.getBroadcast(this /*context*/,
                           0 /*requestCode, ignored*/, intent /*intent*/, 0 /*flags*/));
           RemoteControlHelper.registerRemoteControlClient(am,
                   mRemoteControlClientCompat);
       }

       mRemoteControlClientCompat.setPlaybackState(
               RemoteControlClient.PLAYSTATE_PLAYING);

       mRemoteControlClientCompat.setTransportControlFlags(
               RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
               RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
               RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
               RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS);
             //  RemoteControlClient.FLAG_KEY_MEDIA_STOP);

	}
	private void updateRemoteControls(Song playingItem){

       // Update the remote controls
       mRemoteControlClientCompat.editMetadata(true)
               .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, playingItem.getArtist())
               .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, playingItem.getAlbum())
               .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, playingItem.getTitle())
               .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, playingItem.getLongDuration())
               .putBitmap(RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
               		BitmapFactory.decodeFile(playingItem.getAlbumArt()) )

               // TODO: fetch real item artwork
              // .putBitmap(
               //        RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
                //       mDummyAlbumArt)
               .apply();

	}

	   /**
    * Configures service as a foreground service. A foreground service is a service that's doing
    * something the user is actively aware of (such as playing music), and must appear to the
    * user as a notification. That's why we create the notification here.
    */
   private void setUpAsForeground(String text) {
	   Log.i("Music Service", "Setting service as Foreground");

			   mBuilder =  new NotificationCompat.Builder(this);
	   Song s = getQueue().getCurrentSong();

   		        mBuilder.setSmallIcon(R.drawable.android_robot_icon_128)
   		        .setContentTitle(s.getTitle())
   		        .setContentText(text + s.getArtist());
//   	 PendingIntent pauseIntent = PendingIntent.getService(getApplicationContext(), 0,
//   			 	new Intent(MusicService.ACTION_TOGGLE_PLAYBACK), PendingIntent.FLAG_UPDATE_CURRENT);
//   	 PendingIntent nextIntent = PendingIntent.getService(getApplicationContext(), 0,
//   			 	new Intent(MusicService.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
//	   PendingIntent preIntent = PendingIntent.getService(getApplicationContext(), 0,
//			   new Intent(MusicService.ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
//
	   PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
               0,  new Intent(MusicService.ACTION_BLANK), PendingIntent.FLAG_CANCEL_CURRENT);

	   MediaSessionCompat mediaSession = new MediaSessionCompat(getApplicationContext(), "Notification");
	   MediaControllerCompat controller = mediaSession.getController();
	   MediaMetadataCompat mediaMetadata = controller.getMetadata();
//	   MediaDescriptionCompat description = mediaMetadata.getDescription();

	    //mBuilder.setContentIntent(controller.getSessionActivity());

	   mBuilder.setContentIntent(resultPendingIntent);

       	 mBuilder.setOngoing(true);
       	 int pr = android.R.drawable.ic_media_play;
	   if(text.contains("Paused"))
		  pr = android.R.drawable.ic_media_pause;
	   // Add media control buttons that invoke intents in your media service


//        mBuilder.addAction(android.R.drawable.ic_media_previous, "Previous", preIntent) // #0
//			   .addAction(pr, "Pause", pauseIntent)  // #1
//			   .addAction(android.R.drawable.ic_media_next, "Next", nextIntent);

//	   RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.notification);
//	   mBuilder.setContent(rv);
//			rv.setOnClickPendingIntent(R.id.noti, resultPendingIntent);
//      // 	 mBuilder.addAction(R.drawable.pausebutton, "Pause", pauseIntent);
//       	 rv.setTextViewText(R.id.title, s.getTitle());
////
//       	 rv.setTextViewText(R.id.nowplaying, "("+text+ ") " + s.getArtist() );
////      	 rv.setImageViewResource(R.id.icon, R.drawable.android_robot_icon_128);
//       	 rv.setOnClickPendingIntent(R.id.next, nextIntent);
//       	 rv.setOnClickPendingIntent(R.id.playpause, pauseIntent);

//       	if(text.contains("Paused"))
//       	 rv.setImageViewResource(R.id.playpause, R.drawable.playbutton);
//       	else rv.setImageViewResource(R.id.playpause, R.drawable.pausebutton);

   		 mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   		 mNotification = mBuilder.build();
   		// mNotification.contentView = rv;
   		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
   		startForeground(NOTIFICATION_ID, mNotification);

   }


/*
* Sending info to ui
*/

//	public String getTimeElapsed(){
//		String temp = "";
//		if( (player.getCurrentPosition()/1000 / 60 /60) >= 1)
//			temp += (player.getCurrentPosition()/1000) / 60 /60;
//		    temp += (player.getCurrentPosition()/1000) / 60 + ":";
//		    if( (player.getCurrentPosition()/1000 % 60 ) < 10)
//		    	temp += "0";
//		    temp += (player.getCurrentPosition()/1000 % 60 );
//		return temp;
//	}
//	public String getTimeRemaining(){
//		String temp = "-";
//		if( ((player.getDuration() - player.getCurrentPosition() )/1000 / 60 /60) >= 1)
//			temp += ((player.getDuration() - player.getCurrentPosition() )/1000) / 60 /60;
//		    temp += ((player.getDuration() - player.getCurrentPosition() )/1000) / 60 + ":";
//		    if( ((player.getDuration() - player.getCurrentPosition() )/1000 % 60 ) < 10)
//		    	temp += "0";
//		    temp += ((player.getDuration() - player.getCurrentPosition() )/1000 % 60 );
//		return temp;
//
//	}
//
//	public int getProgress(){
//
//		return (player.getCurrentPosition()/1000);
//	}
	public int getDuration(){
		return (player.getDuration());
	}

    public Song getCurrentSong(){
   	return player.getCurrentSong();
   }
	public boolean shuffleSongs() {  return player.shuffle(); }
	public boolean repeatSongs(){
		return player.repeat();
	}
	public void setQueue(plist q){
		player.setQueue(q);
	}
    public plist getQueue(){
   	return player.getQueue();
   }

   public MusicPlayer getUiInfo(){
	   return player;
   }

	public Runnable updateUi = new Runnable(){
		@Override
		public void run()
		{
			 if(mListener != null){
		            mListener.sendProgress(getUiInfo());		//send call to begin updating UI
			 }

			if( (getDuration() - (player.getCurrentPosition()/1000) )
					<= ( player.getFadeOutDuration()/1000 + player.getFadeOutGap()/1000) ) {

				Log.i("Music Service", "Playing next song based on progress...");
				//mHandler.removeCallbacks(updateUi);  //must stop updating or well try to call on a dead player
				nextRequest();

				//if its the last song, wait till the end.
				if (!getQueue().hasNext() && (getDuration() - (player.getCurrentPosition() / 1000) < 1)){

					mHandler.removeCallbacks(updateUi);
					player.removeCallbacks();
					pauseRequest();
					return;
					}
			}
			 mHandler.postDelayed(updateUi, 1000);

		}
	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.i("Music Service", "Preferences changed");
//		   if (key.equals("pref_crossfade_in")) {
//			   fadeInDuration = Integer.parseInt(sharedPreferences.getString(key, "1"));
//			   player.setFadeInDuration(fadeInDuration);
//			   Log.i("Music Service", "Fade in duration set to: " + fadeInDuration);
//	        }
//		   else if (key.equals("pref_crossfade_out")) {
//			   fadeOutDuration =  Integer.parseInt(sharedPreferences.getString(key, "2"));
//			   player.setFadeOutDuration(fadeOutDuration);
//	        }
//		   else if (key.equals("pref_crossfade_out_gap")) {
//			   fadeOutGap =  Integer.parseInt(sharedPreferences.getString(key, "4"));
//			   player.setFadeOutGap(fadeOutGap);
//	        }

	}


/*
* Save/load queue when closing.
*/
	public void saveQueue(){
		Log.i("m6", "Saving Queue");
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.NAME, queuePlaylist);
		ContentResolver resolver = this.getApplicationContext().getContentResolver();
		resolver.insert(uri, values);

		if(findQueuePlaylist()){
			String[] cols = new String[] {
					"count(*)"
			};
			uri = MediaStore.Audio.Playlists.Members.getContentUri("external", queuePlaylistId);
			Cursor cur = resolver.query(uri, cols, null, null, null);
			cur.moveToFirst();
			final int base = cur.getInt(0);
			cur.close();
			Log.d("Music service", "base: " + base);

			int i =0;
			for(Song t : player.getQueue().getArray()){
				values = new ContentValues();
				int songid = Integer.parseInt(t.getId());
				Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
				values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,  i++ );
				values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songid);
				resolver.insert(uri, values);
			}

		}

	}

	public void loadQueue(){

		//if(queuePlaylistId == 0)
		if(findQueuePlaylist()){
			Log.d("M6", "Loading queue playlist");
			ContentResolver resolver = this.getApplicationContext().getContentResolver();
			String[] memberProjection = {
					MediaStore.Audio.Playlists.Members.TITLE,
					MediaStore.Audio.Playlists.Members.DATA,
					MediaStore.Audio.Playlists.Members.ARTIST,
					MediaStore.Audio.Playlists.Members.ALBUM,
					MediaStore.Audio.Playlists.Members.DURATION,
					MediaStore.Audio.Playlists.Members._ID,
					MediaStore.Audio.Playlists.Members.PLAY_ORDER

			};
			Uri memebersUri =  MediaStore.Audio.Playlists.Members.getContentUri("external", queuePlaylistId);
			String sort =  MediaStore.Audio.Playlists.Members.PLAY_ORDER + " COLLATE LOCALIZED ASC";
			Cursor cur = resolver.query(memebersUri, memberProjection, null, null, sort);
			//  ArrayList<Song> songs = new ArrayList<Song>();
			while(cur.moveToNext()){
				Log.d("Music service", "Adding song: " + cur.getString(0) + cur.getString(5));
				player.addSong(new Song(cur.getString(0), cur.getString(1), cur.getString(2),
						cur.getString(3), cur.getString(4), cur.getString(5) ));
			}

			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			this.getApplicationContext().getContentResolver().delete(uri, MediaStore.Audio.Playlists._ID +" = "+queuePlaylistId, null);
		} else Log.i("Music Service", "no saved queue found");
		// addGroupToQueue(songs);
	}

	public boolean findQueuePlaylist(){
		Log.d("M6", "seting up queue playlist");
		ContentResolver resolver = this.getApplicationContext().getContentResolver();
		String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,
				MediaStore.Audio.Playlists._ID};
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		Cursor cur = resolver.query(uri, playlistProjection, null, null, null);

		while(cur.moveToNext()){
			if(cur.getString(0).equals(queuePlaylist)){
				queuePlaylistId = Long.parseLong(cur.getString(1));
				Log.d("m6", "queue playlist id: " + cur.getString(1));
				return true;
			}
		}
		return false;
	}

	@Override
	public void onStateChanged(MusicPlayer.MUSICPLAYER_STATE s) {
		switch(s){

			case PLAYING_DUCKING:
				player.duck();

		case PLAYING :
			mHandler.post(updateUi);
			setUpAsForeground("Playing");;
			// Tell any remote controls that our playback state is 'paused'.
	        if (mRemoteControlClientCompat != null) {
	            mRemoteControlClientCompat
	                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
	        } break;
		case PAUSED : log("Music Player Pause");
		case PAUSED_USER :
			log("Music Player state Paused by User");
			mHandler.removeCallbacks(updateUi);
			 // Tell any remote controls that our playback state is 'paused'.
	        if (mRemoteControlClientCompat != null) {
	            mRemoteControlClientCompat
	                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
	            setUpAsForeground("Paused");
	        } break;
		case STOPPED :
			mHandler.post(updateUi);
			 // Tell any remote controls that our playback state is 'paused'.
	        if (mRemoteControlClientCompat != null) {
	            mRemoteControlClientCompat
	                    .setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
	        } break;
		case PREPARED :
		case PREPARING :  // Tell any remote controls that our playback state is 'paused'.
			mHandler.post(updateUi);
	        if (mRemoteControlClientCompat != null) {
	            mRemoteControlClientCompat
	                    .setPlaybackState(RemoteControlClient.PLAYSTATE_BUFFERING);
	        } break;
			default :
		}

		if(mListener != null)
		mListener.setPlayPause(player.isPlaying());
	}


	public Song removeSong(int i){
		log("Music Player removing song: " + i + " curr: " + player.getQueue().getIndex()+ " n: " + player.getQueue().getSize());

		Song s = player.removeSong(i);
		//if the current song was removed, play whats current now(next).
		int in = player.getQueue().getIndex();
		log("Music Player removed song: " + i + " curr: " + in + " n: " + player.getQueue().getSize());
		//if removing a song before the current
		if(i <= in) player.getQueue().setIndex(in-1);
		in = player.getQueue().getIndex();

		if(i >= in) {
			playSongFromQueue(i);
			log("Music Player playing current song");
		}
		return s;
	}

}







