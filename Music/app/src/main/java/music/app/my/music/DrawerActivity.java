package music.app.my.music;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.HashMap;

import music.app.my.music.helpers.QueueListener;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.player.MusicService;
import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.plist;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;
import music.app.my.music.ui.AlbumFragment;
import music.app.my.music.ui.ArtistFragment;
import music.app.my.music.ui.ControlFragment;
import music.app.my.music.ui.GenreFragment;
import music.app.my.music.ui.NewPlaylistDialog;
import music.app.my.music.ui.PlaceholderFragment;
import music.app.my.music.ui.PlayListFragment;
import music.app.my.music.ui.QueueFragment;
import music.app.my.music.ui.SongFragment;
import music.app.my.music.ui.baseListFragment;
import music.app.my.music.ui.dummy.DummyContent;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        baseListFragment.OnListFragmentInteractionListener, QueueListener,
        ControlFragment.ControlFragmentListener, NewPlaylistDialog.OnDialogInteractionListener {

    private  final String TAG = getClass().getSimpleName();

    private QueueFragment qf = null;
    private ControlFragment cf = null;
    private int showq = 0; //0 == hidden, 1 = mini q, 2 = half, 3 = full screen, 4 edit plist
    private void log(String s){
        Log.d(TAG, s);
    }
    private MusicService mService;
    private boolean mBound = false;
    private Intent startIntent, toggleIntent, pauseIntent, playIntent,
    nextIntent, previousIntent;

    private boolean controlsVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("on create");
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(controlsVisible)
                    hideControls();

                else showControls();

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        startIntent = new Intent(getApplicationContext(), MusicService.class);
        startIntent.setAction(MusicService.ACTION_BLANK);
        toggleIntent = new Intent(getApplicationContext(), MusicService.class);
        toggleIntent.setAction(MusicService.ACTION_TOGGLE_PLAYBACK);
        playIntent = new Intent(getApplicationContext(), MusicService.class);
        playIntent.setAction(MusicService.ACTION_PLAY);
        pauseIntent = new Intent(getApplicationContext(), MusicService.class);
        pauseIntent.setAction(MusicService.ACTION_PAUSE);
        nextIntent = new Intent(getApplicationContext(), MusicService.class);
        nextIntent.setAction(MusicService.ACTION_NEXT);
        previousIntent = new Intent(getApplicationContext(), MusicService.class);
        previousIntent.setAction(MusicService.ACTION_PREVIOUS);

        //log("Starting service");
        //startService(startIntent);


    }

    @Override
    public void finish(){
        //startService(new Intent(MusicService.ACTION_STOP));
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        final Intent intent = new Intent("music.app.my.music.player.MUSICSERVICE");  //Intent(DrawerActivity.this, music.app.my.music.player.MusicService.class);
        //startService(intent);
        Log.d("Main Activity", "binding service");
        boolean r = getApplicationContext().bindService(new Intent(this, MusicService.class), mConnection
                , Context.BIND_AUTO_CREATE);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if(r) log("Service should be bound");
        else log("Service binding failed.");

        //am.registerMediaButtonEventReceiver(myEventReceiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        log("Stopping Activity");
        // Unbind from the service
        // am.unregisterMediaButtonEventReceiver(myEventReceiver);
        //	saveQueue();
//        if (mBound) {
//            log("Unbinding Service");
//             unbindService(mConnection);
//            mBound = false;
//        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //adjust layout weights to hide/show queue and player
    public void placeholderCreated(){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0.0f
            );
            findViewById(R.id.sidebar).setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    2.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);
        } else {
            // In portrait

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
            );
            findViewById(R.id.qframe).setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    2.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);
        }
    }
    public void expandQframe(){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    2.0f
            );
            findViewById(R.id.sidebar).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    2.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);

        } else {
            // In portrait

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f
            );
            findViewById(R.id.qframe).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    2.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);
        }
    }
    public int hideQ(){
        if(qf != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.qframe, PlaceholderFragment.newInstance());
            //  transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

            qf = null;
            showq = 0;
        }

        return  showq;
    }

    public int showQ(){
        qf = QueueFragment.newInstance();
        expandQframe();
        showq = 1;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
        transaction.replace(R.id.qframe, qf);
      //  transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

        return  showq;
    }


    public void hideControls(){

        if(showq == 1){
            Log.d(TAG, "Hiding Q fragement");
            hideQ();

        }
        Log.d(TAG, "Hiding control fragement");
        controlsVisible = false;

       // getSupportFragmentManager().popBackStack("cf", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
        transaction.replace(R.id.controlFrame, PlaceholderFragment.newInstance());
        transaction.commit();

    }
    public void showControls(){
        if(showq == 0) {

            Log.d(TAG, "Showing Q fragement");
            showQ();
           // qf.setQList(mService.getQueue());
        }

        Log.d(TAG, "Showing control fragement");
        cf =  (ControlFragment) ControlFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.controlFrame, cf);
      //  transaction.addToBackStack("cf");

        // Commit the transaction
        transaction.commit();
        controlsVisible = true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_now) {
           if(showq == 1){

               //update queue for now.. now playing screen?
                 qf.setQList(mService.getQueue());
            }

        } else if (id == R.id.nav_playlists) {
            Fragment f = PlayListFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.frame, f);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_artists) {
            Fragment f = ArtistFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
           transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.frame, f);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_albums) {
            Fragment f = AlbumFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
           transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.frame, f);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_genres) {
            Fragment f = GenreFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
           transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.frame, f);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.nav_songs) {
            Fragment f = SongFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
           transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.frame, f);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "List item clicked");
    }

    @Override
    public void onArtistClicked(Artist mItem) {
        log("Artist Selected: " + mItem.getArtist());
        log("Looking for albums/songs");

        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.ARTISTS.toString());
        b.putString("ArtistID", mItem.getId());
        b.putString("ArtistName", mItem.getArtist());
        f.setArguments(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame, f);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onAlbumClicked(Album mItem) {
        log("Album Selected: " + mItem.getAlbum() + " by " + mItem.getArtist());
        log("Looking for album songs");

        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.ALBUMS.toString());
        b.putString("AlbumID", mItem.getId());
        b.putString("AlbumName", mItem.getAlbum());
        f.setArguments(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame, f);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onGenreClicked(Genre item) {
        Log.d(TAG, "Genre  clicked");
        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.GENRE.toString());
        b.putString("GenreID", item.getId());
        b.putString("GenreName", item.getGenre());
        f.setArguments(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

       transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame, f);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void nameEnted(String name) {
        Log.d(TAG, "Playlist  name entered: " + name);
        newPlaylist( name);


    }

    @Override
    public void cancelClicked() {
        Log.d(TAG, "Playlist  create terminated.");

    }

    @Override
    public void createNewPlaylist() {
        NewPlaylistDialog d =   NewPlaylistDialog.newInstance();

        d.show(getSupportFragmentManager(), "playlist_name_dialog");

        //do the actual work above if nameEnterd() is triggered

    }

    @Override
    public void onPlaylistClicked(Playlist item) {
        Log.d(TAG, "Playlist  clicked");
        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.PLAYLISTITEMS.toString());
        b.putString("PlaylistID", item.getId());
        b.putString("PlaylistName", item.getName());
        f.setArguments(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame, f);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onSongNextupClicked(int position, Song item) {
        Log.d(TAG, "Song nextup clicked" + item.getTitle() + " : " + item.getArtist());


        plist p = mService.getQueue(); //new plist();
        p.addNextSong(item);
        if(showq >0)
        qf.setQList(p);
    }

    @Override
    public void onOptionClicked(int position, Song item) {
        Log.d(TAG, "Song option clicked" + item.getTitle() + " : " + item.getArtist());
        // TODO: 12/29/18 just add it to the list dont play. maybe open a menu later?

        plist p = mService.getQueue();
        p.addSong(item);
        if(showq >0)
        qf.setQList(p);
    }


    @Override
    public void onSongClicked(Song item) {
        Log.d(TAG, "Song item clicked" + item.getTitle() + " : " + item.getArtist());

        plist p = mService.getQueue(); //new plist();
        if(p.getSize() > 0) {
            p.addNextSong(item);
            mService.nextRequest();
        }
        else{
            p.addSong(item);
            startService(playIntent);
        }
        if(showq != 0)
        qf.setQList(p);
    }



    @Override
    public void playPausePressed(){
        startService(toggleIntent);
    }

    @Override
    public void nextPressed() {
        startService(nextIntent);
    }

    @Override
    public void prevPressed() {
        startService(previousIntent);
    }

    @Override
    public void readyForInfo() {

    }

    @Override
    public void seekBarChanged(int progress) {
        mService.seekTo(progress);
    }


    // bound service


    public void setpp(Boolean isPlaying){
        if(controlsVisible) cf.setPlayPause(isPlaying);
    }
    public void updateInfo(MusicPlayer player){
        if(controlsVisible)
        {

                cf.updateInfo(player);

        }

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            log("Music service connected!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            log("Music service bound!");
            //	loadQueue();
            //System.out.println("Service Binded");
           // updateQueue();

            binder.setListener(new MusicService.BoundServiceListener() {

                @Override
                public void sendProgress(MusicPlayer player) {
                    //this happens alot, don't leave active
                  //  log("Activty got progress info. updating ui...");

                    //update seekbar.
                    updateInfo(player);

                }

                @Override
                public void setPlayPause(Boolean isPlaying) {
                   setpp(isPlaying);
                }

                @Override
                public void setAudioId(int aid) {

                }

                @Override
                public void setAlbumArt() {

                }

            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            log("Service Disconnected :(");
        }
    };


    @Override
    public void qFragCreated() {
        log("Q frag created");
        if(mService != null)
        qf.setQList(mService.getQueue());
    }

    @Override
    public void onQueueItemClicked(Song mItem, int position) {
        log(position + " Q item clicked " + mItem.getTitle());
       if( mService.getQueue().getIndex() != position)
            mService.playSongFromQueue(position);
        if(showq != 0)
            qf.setQList(mService.getQueue());
    }


    /*
     * Save/load queue when closing.
     */

    public void addToPlaylist(Long id, plist p){
        String[] cols = new String[] {
                "count(*)"
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        Log.d("Music service", "base: " + base);

        int i =0;
        for(Song t : p.getArray()){
            values = new ContentValues();
            int songid = Integer.parseInt(t.getId());
            Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,  i++ );
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songid);
            resolver.insert(uri, values);
        }
    }
    public void newPlaylist(String name){
        Log.i("m6", "Saving playlist "+ name);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        resolver.insert(uri, values);
        long id = findPlaylistId(name);
        if( id > 0){
            Log.i("m6", name + " Playlist saved sucessful id: "+ id);


        }

    }


    public long findPlaylistId(String name){
        Log.d("M6", "Looking for playlist: " + name);
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        String[] playlistProjection = { MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID};
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cur = resolver.query(uri, playlistProjection, null, null, null);

        long id = 0;
        while(cur.moveToNext()){
            if(cur.getString(0).equals(name)){
                id = Long.parseLong(cur.getString(1));
                Log.d("m6", "queue playlist id: " + id);
                return id;
            }
        }
        return id;
    }


}
