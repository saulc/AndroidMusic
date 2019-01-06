package music.app.my.music;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuInflater;
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
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import music.app.my.music.helpers.MixListener;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.player.MusicService;
import music.app.my.music.player.myPlayer;
import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.plist;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;
import music.app.my.music.ui.MixFragment;
import music.app.my.music.ui.browser.AlbumFragment;
import music.app.my.music.ui.browser.ArtistFragment;
import music.app.my.music.ui.browser.HeaderFragment;
import music.app.my.music.ui.popup.ChoosePlaylistDialogFragment;
import music.app.my.music.ui.popup.ConfirmDeleteDialogFragment;
import music.app.my.music.ui.ControlFragment;
import music.app.my.music.ui.browser.GenreFragment;
import music.app.my.music.ui.popup.NewPlaylistDialog;
import music.app.my.music.ui.NowFragment;
import music.app.my.music.ui.PlaceholderFragment;
import music.app.my.music.ui.browser.PlayListFragment;
import music.app.my.music.ui.QueueFragment;
import music.app.my.music.ui.browser.SongFragment;
import music.app.my.music.ui.browser.baseListFragment;
import music.app.my.music.ui.dummy.DummyContent;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        baseListFragment.OnListFragmentInteractionListener,
        HeaderFragment.OnFragmentInteractionListener,
        QueueListener, MixListener,
        ControlFragment.ControlFragmentListener,
        NewPlaylistDialog.OnDialogInteractionListener ,
        Toolbar.OnMenuItemClickListener{

    private  final String TAG = getClass().getSimpleName();

    private MixFragment mf = null;
    private NowFragment nf = null;
    private QueueFragment qf = null;
    private ControlFragment cf = null;
    private PlaceholderFragment pf = null;

    private int showq = 0; //0 == hidden, 1 = miniplayer q, 2 = half, 3 = full screen, 4 edit plist

    private void log(String s){
        Log.d(TAG, s);
    }
    private MusicService mService;
    private boolean mBound = false;
    private TextView  currentText;
    private TextSwitcher nextText;
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

        //dim the systems  status and control bars
        // 0 == show
        View decorView =  toolbar;
//        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
//        decorView.setSystemUiVisibility(0);

        toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) this);
        nextText = (TextSwitcher) findViewById(R.id.nextText);
        if(nextText!=null) {
            nextText.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    TextView t = new TextView(getApplicationContext());
                    t.setTypeface(Typeface.MONOSPACE);
                    return t;
                }
            });
            nextText.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
            nextText.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
            nextText.setText("Nothing to play...");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mService.duck();
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab Clicked: " + showq);
                    showQ();
     }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView)  findViewById(R.id.search);
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setQueryRefinementEnabled(true);




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
        if(controlsVisible)
            showControls();


        handleStartIntents(); //search media share....

    }

    private Song shared = null;

    private void handleStartIntents(){

        //search if we need to...
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        log("Got intent! action: " + action);

        log("Got intent! action: " + action);
        if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
        else if(Intent.ACTION_SEND.equals(action) && type != null) {

            log("GOt Action Send!!");
            log("Looking for data in extras!!");
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

            log("Type: ===>> "+ type);
            log("Oh its audio!!"  + uri.toString());

            String id = uri.toString().substring(uri.toString().lastIndexOf('/'), uri.toString().length());
             shared = new Song("Shared Song", id );

            //todo play the shared song.
//            onSongClicked(shared);

        }  else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

        }

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
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shuffle) {
            return true;
        } else if (id == R.id.repeat) {
            return true;
        } else if (id == R.id.mix) {
            return true;
        }
//        else if(id == R.id.app_bar_search){
//            onSearchRequested();
//
//        }

        return super.onOptionsItemSelected(item);
    }


    private void doMySearch(String q){
        log("Searching for: " + q);

        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.QUERY.toString());
        b.putString("QueryID", "0" );
        b.putString("Query", q);
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


    //adjust layout weights to hide/show queue and player
    public void placeholderCreated() {

        log("Placeholder created, q frame");
        closeSidebar();
    }




    public void closeSidebar(){
        log("Closing q frame: ");
      //  mHandler.removeCallbacks(hideQframe);
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
            //queue frame ends at q == 0, f == 2
            findViewById(R.id.sidebar).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    5.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);
            //main frame
        }
    }
    public void expandSidebar(){
        log("Expanding q frame");
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    (float)showq
            );
            findViewById(R.id.sidebar).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    4.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);

        } else {
            // In portrait

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    (float)( (showq>2) ? showq*2 : showq)
            );
            findViewById(R.id.sidebar).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    4.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);


        }
    }
    public int hideQ(){
        if(qf != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.qframe, PlaceholderFragment.newInstance("something"));
            //  transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

            qf = null;
            showq = 0;
        }

        return  showq;
    }

    public int showQ(){
        log("Show Q:" + ++showq);
       // if(showq >0 && qf != null && qf.isVisible()) return showq;

        //showq = 1;
        if(showq == 1 ) {
            expandSidebar();
            showControls();
            return showq;
        }

        if(showq > 4){
            showq = 0;
            hideQ();
            hideControls(false);
            return showq;
        }

            if (qf == null)
                qf = QueueFragment.newInstance();
            expandSidebar();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
            transaction.replace(R.id.qframe, qf);
            //  transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        return  showq;
    }

    public  void expandControlFrame(){

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape

            showq = 1;
            showQ();
            //control frame
        }
        else {
            //Im portrait

//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    DrawerLayout.LayoutParams.MATCH_PARENT,
//                    0,
//                    1.0f
//            );
//            findViewById(R.id.controlFrame).setLayoutParams(param);
//            //control frame
//
//            param = new LinearLayout.LayoutParams(
//                    DrawerLayout.LayoutParams.MATCH_PARENT,
//                    0,
//                    (float) showq
//            );
//            findViewById(R.id.qframe).setLayoutParams(param);
//            //queue frame

        }
    }

    public  void closeControlFrame(){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape

            hideQ();
            //control frame
        }
        else {
            //Im portrait
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    DrawerLayout.LayoutParams.MATCH_PARENT,
//                    0,
//                    0.0f
//            );
//            findViewById(R.id.controlFrame).setLayoutParams(param);
//            //control frame
//
//            param = new LinearLayout.LayoutParams(
//                    DrawerLayout.LayoutParams.MATCH_PARENT,
//                    0,
//                    0.0f
//            );
//            findViewById(R.id.qframe).setLayoutParams(param);
            //queue frame

        }

    }
    public void hideControls(boolean showPlaceholder){

    closeControlFrame();
        Log.d(TAG, "Hiding control fragement");
        controlsVisible = false;

       // getSupportFragmentManager().popBackStack("cf", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        Song s = mService.getQueue().getCurrentSong();
        String a = "";
        if(s != null) a =  s.getTitle() + " by " + s.getArtist();

        updateNextSongInfo();

        pf = PlaceholderFragment.newInstance(a);
        transaction.replace(R.id.controlFrame, pf);
        transaction.commit();

        if(!showPlaceholder)
        getSupportFragmentManager().beginTransaction().remove(pf).commit();

    }

    public void showControls(){
        if(controlsVisible && cf != null && cf.isVisible()) return;

        Log.d(TAG, "Showing control fragement");
        //cf =  (ControlFragment) ControlFragment.newInstance();
        expandControlFrame();

        cf =   NowFragment.newInstance(true);

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

    public void updateCurrentSongInfo(){
        if(mService != null ) {
            Song s = mService.getQueue().getCurrentSong();
            String b = "";
            if (s != null) {
                b = s.getTitle() + " by " + s.getArtist();
                if(pf != null)
                    pf.setText(" Now Playing : " + b);


            }
        }
    }

    public void updateNextSongInfo(){
        if(nextText == null) return;
        plist q = mService.getQueue();


        int m = mService.repeatMode();
        if(m == 1) {    //repeat same song forever!
            Song s = q.getCurrentSong();
            String b = "";
            if (s != null) {
                b = s.getTitle() + " by " + s.getArtist();
                //" Now Playing: "
                nextText.setText( " Up Next: " + b);
            }
        } else if( m == 2){
            //repeat all songs
            if( q.getIndex() == (q.getSize() -1 ) ){
                //last song, next is first
                Song s = q.getSong(0);
                String b = "";
                if (s != null) {
                    b = s.getTitle() + " by " + s.getArtist();
                    //" Now Playing: "
                    nextText.setText( " Up Next: " + b);
                }
            }

        }else

        if(q.hasNext()) {
            Song s = q.getSong(q.getIndex() + 1);
            String b = "";
            if (s != null) {
                b = s.getTitle() + " by " + s.getArtist();
                //" Now Playing: "
                nextText.setText( " Up Next: " + b);
            }
        }


    }



    @Override
    public void controlIconClicked(){

        Log.d(TAG, "Control icon clicked");
        showNow();
    }

    @Override
    public void nowIconClicked() {

        Log.d(TAG, "Now icon clicked");

        if(showq == 0){
            showQ();
        } else hideQ();
    }

    @Override
    public void lineClicked(int i) {

        Log.d(TAG, "Now line clicked: " + i);

        if(i== 1) {
            Log.d(TAG, "Current song clicked, doing nothing... ");
        } else if(i==2){
            Log.d(TAG, "Current Artist clicked");
            Song s = mService.getCurrentSong();
            Artist a = new Artist( s.getArtistId(), s.getArtist());
            onArtistClicked(a);
        }else if(i==3){
            Log.d(TAG, "Control Album clicked");
            Song s = mService.getCurrentSong();
            Album a = new Album(s.getAlbumId(), s.getAlbum());
            onAlbumClicked(a);
        }

    }

    @Override
    public void onNowViewCreated() {
        log("Now Fragment created");

        if( nf == null)  showNow();


        // if(controlsVisible && cf != null && cf.isVisible()) {
             log("Now Fragment setting updater");

             final Handler h = new Handler();
             h.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     if (nf.isVisible() || cf.isVisible())
                         if (mService.getQueue().getSize() > 0)
                             updateCurrentInfo(mService.getCurrentSong());
                     Log.d(TAG, "Now updating..");
                     h.removeCallbacks(this);
                 }
             }, 1000);



    }


    public void showMix(){
        Log.d(TAG, "Showing MIX fragment");

        if(mf != null && mf.isVisible() ) return;

        mf =  MixFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        transaction.replace(R.id.frame, mf);
        transaction.addToBackStack(null);
        transaction.commit();


    }


    public void showNow(){
        Log.d(TAG, "Showing NOW fragment");

        if(nf != null && nf.isVisible() ) return;

        //hideControls(false);
        //todo
        //hideStatusLines(); //doesn't exist yet
        nf =  NowFragment.newInstance(false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        transaction.replace(R.id.frame, nf);
        transaction.addToBackStack(null);
        transaction.commit();


//        updateCurrentInfo(new Song("", ""));
    }

    public void showBluetoothSettings(){
         //shortcut direct to bluetooth setting page
        Intent intentBluetooth = new Intent();
        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentBluetooth);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_now) {

            showNow();

        } else if( id == R.id.search) {
            log("Nav search clicked!");
            onSearchRequested();


        } else if( id == R.id.bluetooth) {
            log("Nav bluetooth clicked!");
            showBluetoothSettings();


        } else if( id == R.id.clear_queue) {
           clearQueueClicked();


        } else if( id == R.id.save_queue) {
            createNewPlaylist(true);


        } else if( id == R.id.new_playlist){
            createNewPlaylist(false);

            //show playlist after we added a new one. no else
        }  if (id == R.id.nav_playlists || id == R.id.new_playlist) {
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


        }  else if (id == R.id.nav_settings) {
            log("Settings clicked.");

            showMix();
//            Fragment f = (Fragment) HeaderFragment.newInstance("", "");
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);
//            transaction.replace(R.id.frame, f);
//            transaction.addToBackStack(null);
//            // Commit the transaction
//            transaction.commit();
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
        b.putString("AlbumArt", mItem.getArt());
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
    public void cancelClicked() {
        Log.d(TAG, "Playlist  create terminated.");

    }



    @Override
    public void nameEnted(String name, boolean isQ) {
        Log.d(TAG, "Playlist  name entered: " + name);
        newPlaylist( name);

        Toast.makeText(mService, "Created Playlist: " + name, Toast.LENGTH_SHORT).show();
        if(isQ)
            saveQueueAsPlaylist(name);
    }

    //called back fro nameEnted when playlist has been created.
    //add items and we're done.
    public void saveQueueAsPlaylist(String name){
        try {
            Long ii =  findPlaylistId(name);
            ArrayList<Long> ss = new ArrayList<>();

            if(mService != null) {
                ArrayList<Song> songs =  mService.getQueue().getArray();
                for(Song a: songs) ss.add( Long.parseLong( a.getId() ) );
                Toast.makeText(mService, "adding items Playlist" + name, Toast.LENGTH_SHORT).show();

            }
            addListToPlaylist(ii, ss, true);
            onPlaylistClicked(new Playlist(name, ii.toString()));   //show the new playlist.

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void createNewPlaylist(boolean isQ) {
        NewPlaylistDialog d =   NewPlaylistDialog.newInstance(isQ);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out, R.anim.anim_in, R.anim.anim_out);

        d.show(transaction, "playlist_name_dialog");

        //do the actual work above if nameEnterd() is triggered

    }

    //called from clear queue nav menu item
    public void clearQueueClicked(){
        String name = "NOWPLAYING";
        DialogFragment c = ConfirmDeleteDialogFragment.newInstance(name);
        c.show(getSupportFragmentManager(), "QUEUE");

    }

    public void deleted(String pid, String name){
        if(pid.compareTo("QUEUE") == 0 ){

            if(mService!=null) {
                mService.pauseRequest();
                mService.getQueue().clearQueue();

                Toast.makeText(mService, "Cleared Now playing: " + name + " : " + pid, Toast.LENGTH_SHORT).show();
            }
        }else {

            deletePlaylist(pid);
            Toast.makeText(mService, "Deleted Playlist: " + name + " : " + pid, Toast.LENGTH_SHORT).show();
        }
    }

    /*
    add song to playlist
     */
    @Override
    public void onPlaylistOptionClicked(int position, String pid, String name) {
        //confirm first! important! double confirm?
        DialogFragment c = ConfirmDeleteDialogFragment.newInstance(name);
        c.show(getSupportFragmentManager(), pid);

    }

    @Override
    public void onPlaylistNextUpClicked(int position, String pid) {
        Log.d(TAG, "Playlist next up clicked");
        //play list?
    }

    public void addToPlaylistCanceled(){
        Log.d(TAG, "add to Playlist canceled.");
        //nothign to do.
    }

    public void addSongToPlaylist(String name, String sid, int pos, String pid, boolean top){
        Log.d(TAG, "Playlist picked: " + pos + " pid:" + pid);
        try {
            long id = Long.parseLong(pid);
            long lsid = Long.parseLong(sid);

            addToPlaylist(name, id, lsid, top);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /*
    end of add song to playlist
    */

    /*
   add group to playlist
    */

    //called for song option/next long click top is true for next
    @Override
    public void addSongsToPlaylist(ArrayList<Song> items, boolean top) {
        Log.d(TAG, "Group Option long clicked, add to playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance( items.get(0).getTitle(), true, top);
        c.show(getSupportFragmentManager(), items.get(0).getId());

        //happens after dialog name entered.
        try {
            ArrayList<Long> i = new ArrayList<>();
            for(Song s: items) i.add(Long.parseLong( s.getId() ));

            if(temptag != 0) {
                Log.d(TAG, "adding Group to playlist: " + temptag);
                addListToPlaylist(temptag, i, top);
                temptag = 0;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
    private long temptag = 0;

    public void addSongsToPlaylist(String name, String sid, int pos, String pid){
        Log.d(TAG, "Playlist picked: " + pos + " pid:" + pid);
        try {
            long id = Long.parseLong(pid);
            temptag = id;
            //then let the above method resume.
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }



    /*
      end of add group to playlist
    */

    //calls above method when a playlist is seltected
    @Override
    public void onOptionLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), false, false);
        c.show(getSupportFragmentManager(), song.getId());
    }

    @Override
    public void onNextLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to top of playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), false, true);
        c.show(getSupportFragmentManager(), song.getId());
    }

    @Override
    public void addSongsNextToQueue(ArrayList<Song> items) {
        log("Group header clicked. adding group to queue and playing...");

        plist p = mService.getQueue(); //new plist();
        if(p.getSize() > 0) {
            p.addToTop(items);
        }
        else{
            p.addTo(items);
        }

        updateQueueFrag(p);
        updateNextSongInfo();
        updateCurrentSongInfo();
    }



    @Override
    public void addSongsToQueue(ArrayList<Song> items, boolean play) {
    log("Group header clicked. adding group to queue and playing...");

        plist p = mService.getQueue(); //new plist();
        if(p.getSize() > 0) {
            p.addToTop(items);
            if(play)
            mService.nextRequest();
        }
        else{
            p.addTo(items);
            if(play)
            startService(playIntent);
        }

        updateQueueFrag(p);

        updateNextSongInfo();
        updateCurrentSongInfo();
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
        Toast.makeText(this, item.getTitle() + " added next.", Toast.LENGTH_SHORT).show();
        plist p = mService.getQueue(); //new plist();
        p.addNextSong(item);

        updateQueueFrag(p);
        updateNextSongInfo();
    }

    @Override
    public void onOptionClicked(int position, Song item) {
        Log.d(TAG, "Song option clicked" + item.getTitle() + " : " + item.getArtist());
        // TODO: 12/29/18 just add it to the list dont play. maybe open a menu later?

        Toast.makeText(this, item.getTitle() + " added to queue.", Toast.LENGTH_SHORT).show();
        plist p = mService.getQueue();
        p.addSong(item);

        updateQueueFrag(p);
    }


    @Override
    public void onSongClicked(Song item) {
        Log.d(TAG, "Song item clicked" + item.getTitle() + " : " + item.getArtist());

        Toast.makeText(this, "Now Playing: "+ item.getTitle(), Toast.LENGTH_SHORT).show();
        plist p = mService.getQueue(); //new plist();
        if(p.getSize() > 0) {
            p.addNextSong(item);
            //let this action take care of the rest ;)
            onQueueItemClicked(item, p.getIndex()+1);
           // mService.nextRequest();
        }
        else{
            p.addSong(item);
            startService(playIntent);
        }

        updateQueueFrag(p);
        updateNextSongInfo();
        updateCurrentSongInfo();
    }



    public void updateQueueFrag(plist p){
        if(qf != null && showq != 0)
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


    public void setpp(Boolean isPlaying){
        if(nf != null) nf.setPlayPause(isPlaying);
        if(controlsVisible) cf.setPlayPause(isPlaying);
    }

    public void updateProgress(MusicPlayer player){
        if(mf != null) mf.updateInfo(player);
        if(nf != null) nf.updateInfo(player);
        if(controlsVisible)
        {
                cf.updateInfo(player);
        }

    }

    public void updateCurrentInfo(Song s){
        if(nf != null) nf.updateInfo(s);
        if(cf != null) cf.updateInfo(s);

        updateNextSongInfo();
//
//        updateQueueFrag(mService.getQueue());

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
           // updateQueueFrag();

            binder.setListener(new MusicService.BoundServiceListener() {

                @Override
                public void sendProgress(MusicPlayer player) {
                    //this happens alot, don't leave active
                  //  log("Activty got progress info. updating ui...");

                    //update seekbar.
                    if(player != null)
                    updateProgress(player);

                }

                @Override
                public void setPlayPause(Boolean isPlaying) {
                   setpp(isPlaying);
                }

                @Override
                public void setCurrentInfo(Song s) {
                    updateCurrentInfo(s);
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
        if(mService != null  )
            updateQueueFrag(mService.getQueue());
    }

    @Override
    public void onQueueItemClicked(Song mItem, int position) {
        log(position + " Q item clicked " + mItem.getTitle());
       if( mService.getQueue().getIndex() != position)
            mService.playSongFromQueue(position);

        updateCurrentSongInfo();
        updateQueueFrag(mService.getQueue());
        updateNextSongInfo();
    }


    @Override
    public void onQueueItemLongClicked(Song mItem, int position) {

        log(position + " Q item Long clicked " + mItem.getTitle());

        if( mService.getQueue().getIndex() != position) {
            plist q =  mService.getQueue();
           int index = q.getIndex();
           Song clicked = q.removeSong(position);
           if(position < index) q.setIndex(index - 1);
           q.addNextSong(clicked);

        }

        updateCurrentSongInfo();
        updateQueueFrag(mService.getQueue());
        updateNextSongInfo();

    }

    @Override
    public void swapItems(int i, int i1) {
        log("Swapping queue items: " + i + " : " + i1);

        plist p = mService.getQueue();

        int a = i > i1 ? i : i1;    //return the larger
        int b = a == i ? i1: i;    // return the other

        Song s = p.getSong(b);
        Song f = p.removeSong(a);
        p.getArray().add(a, s);  //remove the larger, cope smaller to it
        p.getArray().remove(b);
        p.getArray().add(b, f);

        //todo "deal with moving the current song. adjust index or play new current?

      //  mService.setQueue(p);

    }

    @Override
    public void addToQ(int i, Object o) {
        log("Add to Q called: " + i);
    }

    @Override
    public void onItemSwiped(Song mItem, int position) {

        log(position + " Q item swiped " + mItem.getTitle());
      //  Toast.makeText(mService, "removed: " + mItem.getTitle() + " from queue.", Toast.LENGTH_SHORT).show();

        log(position + " Removing " + mItem.getTitle() + " from queue.");
        mService.removeSong(position);

        updateQueueFrag(mService.getQueue());
    }


    /*
     * Save/load queue when closing.
     */

    public void addToPlaylist(String pname, Long pid, Long sid, boolean top){
        String[] cols = new String[] {
                 MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        Cursor cur = resolver.query(uri, cols, null, null, null);
         cur.moveToFirst();
        int base = cur.getInt(0);
        String id = cur.getString(1);
        Log.d("Music service", "--->>>>>base: " + base + " " + id);
        ArrayList<String> temp = new ArrayList<>();

    while(cur.moveToNext()) {

        base = cur.getInt(0);
        id = cur.getString(1);
        temp.add(id);
        Log.d("Music service", "--->>>>>base: " + base + " " + id);
    }
    cur.close();

//    if( top ) temp.add(0, sid.toString());
//    else temp.add(sid.toString());
//
//    deletePlaylist(pid.toString());
//    newPlaylist(pname);
//    pid = findPlaylistId(pname);
//        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
//        cur = resolver.query(uri, cols, null, null, null);
//        Log.d("Music service", "--->>>>>base: " + base + " " + id);
//        while(cur.moveToNext()) {
//
//            base = cur.getInt(0);
//            id = cur.getString(1);
//            //temp.add(id);
//            Log.d("Music service", "--->>>>>base: " + base + " " + id);
//        }
//        cur.close();
//        resolver.query(uri, cols, null, null, null);
//
//        for(int i=0; i<temp.size(); i++) {
//            Log.d("Music service", "--->>>>>base: " + i + " " + temp.get(i));
//        values = new ContentValues();
//        // Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
//        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
//        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, temp.get(i));
//        resolver.insert(uri, values);
//    }

    }

    public void addListToPlaylist(Long pid, ArrayList<Long> ids, boolean top) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER, MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        int base = 0;
        if(!top){
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
            base = cur.getInt(0);
            cur.close();
            Log.d("Music service", "base: " + base);

        }

        for(int i=0; i<ids.size(); i++) {
            values = new ContentValues();
            // Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + i);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids.get(i));
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


    public void deletePlaylist(String id){
        Log.i("m6", "Deleting playlist "+ id);
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        String[] arg = { id};
        resolver.delete(uri, MediaStore.Audio.Playlists._ID+ "=?", arg);

            Log.i("m6", id + " Playlist delete sucessful id: "+ id);


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


    public Song getRandomSong(){
        Log.d("M6", "Looking for 'random' song..." );
        ContentResolver resolver = this.getApplicationContext().getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
         String defaultSort =  MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
         String defaultSelection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
         String[] defaultProjection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                 MediaStore.Audio.Media.ARTIST_ID

        };
        Cursor cursor = resolver.query(songUri, defaultProjection, defaultSelection, null, defaultSort);
        cursor.moveToFirst();
        ArrayList<Song> songs = new ArrayList<Song>();
        while(cursor.moveToNext()) {
            songs.add(new Song(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4)
                    , cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        int i = ((int) (Math.random()*100) );
        i = i % songs.size();

        Log.d("M6", " 'random' song: " + i );

        return songs.get(i);

    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.shuffle) {
             log("Suffle clicked");
            boolean b = mService.shuffleSongs();
            Toast.makeText(mService, "Shuffle: " + b, Toast.LENGTH_SHORT).show();
            updateQueueFrag(mService.getQueue());

            return true;
        } else if (id == R.id.repeat) {
            log("Repeat clicked");
            boolean b = mService.repeatSongs();
            Toast.makeText(mService, "Repeat: " + b, Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.mix) {
            log("Mix clicked");
            Song s = getRandomSong();
            String b = "Mix: " + s.getTitle() + " by " + s.getArtist() + " added to Queue.";
            Toast.makeText(mService,  b, Toast.LENGTH_SHORT).show();

            onSongClicked(s);
           // showNow();
            return true;
        } else if(id == R.id.now){
            Log.d(TAG, "Now clicked");

                showNow();

//            if(nf.isVisible())
//            if(mService.getQueue().getSize() > 0)
//            updateCurrentInfo(mService.getCurrentSong());

            return true;
        }
            return false;
    }

    @Override
    public void onHeaderFragmentInteraction(Uri uri) {
        log("Header fragment interaction");
    }

    @Override
    public void onMixItemClicked(String mItem, int position) {
        log("Mix item clicked: " + position);
        ArrayList<myPlayer> p = mService.getPlayer().getmPlayers();
        if(position < p.size()){
            if(p.get(position).isPlaying() || !p.get(position).isPaused()) p.get(position).pausePlayback();
            else  p.get(position).resumePlayback();
            mf.updateInfo(mService.getPlayer());
        }
    }

    @Override
    public void onMixItemLongClicked(myPlayer mItem, int position) {
        log("Mix item long clicked: " + position);
    }


    @Override
    public void onMixViewCreated() {
        log("Mix Fragment created");

        if(mf != null && mf.isVisible()) {
            log("Mix Fragment setting updater");

            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mf.isVisible()) {
                        if (mService != null)
                            updateProgress(mService.getPlayer());

                        mf.updateAdapter();
                        Log.d(TAG, "Mix updating..");
                        h.removeCallbacks(this);
                    }
                }
            }, 1000);
        }

    }

}
