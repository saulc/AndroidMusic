package music.app.my.music;

import android.animation.LayoutTransition;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import music.app.my.music.ui.browser.BubbleFragment;
import music.app.my.music.ui.browser.HeaderFragment;
import music.app.my.music.ui.popup.ChoosePlaylistDialogFragment;
import music.app.my.music.ui.popup.ChooseThemeDialogFragment;
import music.app.my.music.ui.popup.ConfirmDeleteDialogFragment;
import music.app.my.music.ui.ControlFragment;
import music.app.my.music.ui.browser.GenreFragment;
import music.app.my.music.ui.popup.EQDialogFragment;
import music.app.my.music.ui.popup.NewPlaylistDialog;
import music.app.my.music.ui.NowFragment;
import music.app.my.music.ui.PlaceholderFragment;
import music.app.my.music.ui.browser.PlayListFragment;
import music.app.my.music.ui.QueueFragment;
import music.app.my.music.ui.browser.SongFragment;
import music.app.my.music.ui.browser.baseListFragment;
import music.app.my.music.ui.dummy.DummyContent;
import music.app.my.music.ui.popup.VisualizerDialogFragment;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        baseListFragment.OnListFragmentInteractionListener,
        HeaderFragment.OnFragmentInteractionListener,
        QueueListener, MixListener,
        ControlFragment.ControlFragmentListener,
        NewPlaylistDialog.OnDialogInteractionListener ,
        Toolbar.OnMenuItemClickListener{


    private VisualizerDialogFragment vf = null;

    private MixFragment mf = null;
    private NowFragment nf = null;
    private QueueFragment qf = null;
    private ControlFragment cf = null;
    private PlaceholderFragment pf = null;
    private SongFragment sf = null; //search fragment

    private FloatingActionButton fab3;
    private FloatingActionButton fab2;
    private FloatingActionButton fab1;
    private FloatingActionButton fab;

    private boolean showfmenu = false; //show/hide floating control buttons.
    private int showq = 0; //0 == hidden, 1 = miniplayer q, 2 = half, 3 = full screen, todo 4 edit plist

    private  final String TAG = getClass().getSimpleName();

    private void log(String s){
        Log.d(TAG, s);
    }
    private MusicService mService;
    private boolean mBound = false;
    private TextSwitcher nextText;
    private Intent startIntent, toggleIntent, pauseIntent, playIntent,
    nextIntent, previousIntent;

    private String actionSetTheme = "ACTION_SET_THEME";
    private int currentTheme = R.style.AppTheme_NoActionBar;

    private boolean controlsVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("on create");

        log("Setting app Theme.");
        handleThemeIntent();


        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add android default trasitions to main layout changes.
        // //smooth movement on queue/miniplayer
        ((ViewGroup) findViewById(R.id.llRoot)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        ((ViewGroup) findViewById(R.id.sidebar)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);


        //dim the systems  status and control bars
        // 0 == show
//        View decorView =  toolbar;
////        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN; // | View.SYSTEM_UI_FLAG_IMMERSIVE;
//        decorView.setSystemUiVisibility(uiOptions);
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

         fab = (FloatingActionButton) findViewById(R.id.fab);
         fab1 = (FloatingActionButton) findViewById(R.id.fab1);
         fab2 = (FloatingActionButton) findViewById(R.id.fab2);
         fab3 = (FloatingActionButton) findViewById(R.id.fab3);
         iniFM();
         moveFab(true);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //mService.duck();
                Log.d(TAG, "Fab Long Clicked: " + showfmenu);
                if(!showfmenu) showFM();
                else hideFM();

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

        log("Starting service");
        startService(startIntent);
//        if(controlsVisible)
//            showControls();

        showNow();
       // showBubbles();

        handleStartIntents(); //search media share....

    }

    private void showThemeDialog(){

        log("Showing Theme picker.");
        ChooseThemeDialogFragment ct =  ChooseThemeDialogFragment.newInstance(currentTheme);
        ct.show(getSupportFragmentManager(), "Choose Theme");
    }

    private  void handleThemeIntent(){
        Intent intent = getIntent();
        String action = intent.getAction();
        log("Theme Got intent! action: " + action);
        if(actionSetTheme.equals(action)) {
            int r = intent.getIntExtra("THEME", -22);
            if( r == -22){
                log("Error: no valid theme extra in intent.");
                return;
            }
            setTheme(r);
        }
    }

    public void themePicked(String name, int theme){
        log("Theme picked: "+ name + " id: " + theme + " cur: " + currentTheme);
        if(theme == currentTheme) return;


        Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
        i.setAction(actionSetTheme);
        i.putExtra("THEME", theme);
        startActivity(i);
    }

    private void iniFM(){
        //next
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab 1 Clicked: next");

                nextPressed();
            }
        });

        fab1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Fab 1 Long Clicked: next" );

                //close q
                showq = 2;
                showQ();
                showControls();
                return true;
            }
        });
        //play/pause

        fab2.setImageResource(android.R.drawable.ic_media_play);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab 2 Clicked: play/pause");
                playPausePressed();
            }
        });

        fab2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Fab 2 Long Clicked: play/pause" );

                //mini q
                showq = 0;
                showQ();
                showNow();
                return true;
            }
        });

        //previous
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab 3 Clicked: next");
                 prevPressed();
            }
        });

        fab3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Fab 3 Long Clicked:previous " );

                showq = 0;
                showQ();
                hideFM();

                return true;
            }
        });

    }


    //bottom line
//    private void showFM(){
//        showfmenu = true;
//        float r = -getResources().getDimension(R.dimen.fd);
//        fab1.animate().translationX(r);
//        fab2.animate().translationX(r*2);
//        fab3.animate().translationX(r*3);
//
//    }
//    private void hideFM(){
//        showfmenu = false;
//        fab1.animate().translationX(0);
//        fab2.animate().translationX(0);
//        fab3.animate().translationX(0);
//    }

   // circle?
   private void moveFab(boolean up){


       if(up) {
           float r = -getResources().getDimension(R.dimen.fab_marginvert);
           fab1.animate().translationY(r);
           fab2.animate().translationY(r);
           fab3.animate().translationY(r);
           fab.animate().translationY(r);
       }else {

           float r = 0f;
           fab1.animate().translationY(r);
           fab2.animate().translationY(r);
           fab3.animate().translationY(r);
           fab.animate().translationY(r);
       }


   }

    private void showFM(){
        showfmenu = true;
        moveFab(false);
        //if r == c its a square.
        float r = -getResources().getDimension(R.dimen.fd);
        float c = -getResources().getDimension(R.dimen.fc);

        fab1.animate().translationY(c);
        fab2.animate().translationY(r);
        fab2.animate().translationX(r);
        fab3.animate().translationX(c);

    }
    private void hideFM(){
        showfmenu = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab2.animate().translationX(0);
        fab3.animate().translationX(0);

        moveFab(showq == 0); //move menu back up


    }


    private void handleStartIntents(){

        //handle share intents if we need to...
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        log("Got intent! action: " + action);

        log("Got intent! action: " + action);
        if(Intent.ACTION_SEND.equals(action) && type != null) {

            log("GOt Action Send!!");
            log("Looking for data in extras!!");
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

            log("Type: ===>> "+ type);
            log("Oh its audio!!"  + uri.toString());

            String id = uri.toString().substring(uri.toString().lastIndexOf('/'), uri.toString().length());
             //shared = new Song("Shared Song", id );

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
    protected void onPause(){
        log("Pause activity.");
        super.onPause();

    }
    @Override
    protected void onResume(){
        super.onResume();

        log("Resume activity.");
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        final Intent intent = new Intent("music.app.my.music.player.MUSICSERVICE");  //Intent(DrawerActivity.this, music.app.my.music.player.MusicService.class);
        //startService(intent);
        Log.d("Main Activity", "binding service");
        boolean r = getApplicationContext()
                        .bindService(new Intent(this, MusicService.class),
                        mConnection
                        , Context.BIND_AUTO_CREATE);

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




    //app bar menu. top Icons!
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer, menu);

        // Get the SearchView and set the searchable configuration
       // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                log("Query Text Submit: "+ query);
                if(sf != null)
                sf.updateQuery(query);
                searchView.clearFocus();  //close keyboard
                 MenuItem m =   menu.findItem(R.id.menu_search);
                 m.collapseActionView();    //minimize search bar

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                log("Query Text Change: "+ newText);
                if(sf != null)
                sf.updateQuery(newText);
                return true;
            }
        });

        return true;
    }


    public void setEQ(int selection, String eqname){
        log("Set eq: " + eqname);
        if(mService !=null) mService.getPlayer().setEQ(selection);
    }

    public void showEQTab(){
        log("Showing eq Tab");
        if(mService== null || mService.getPlayer()==null) return;

        ArrayList<String> pre = mService.getPlayer().getEQPresets();
        if(pre == null) return;
        DialogFragment c = EQDialogFragment.newInstance(pre);
        c.show(getSupportFragmentManager(), "EQ");


    }

    public void visualizerLongClicked(){
        log("Vis long clicked.");

        showNow();
    }


    public void visualizerCreated(){
        log("Vis created.");
        if(mService!=null && mService.getPlayer() !=null && mService.getPlayer().isPlaying()) {
            vf.setAid(mService.getPlayer().getAID());
            //vf.setEnabled(true);
        }

    }
    public void visualizerClosed(){
        log("Vis closed.");

    }

    private void showVisualizer(){
        log("Showing visualizer!.");
        vf = VisualizerDialogFragment.newInstance();
        vf.show(getSupportFragmentManager(), "Visualizer");

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
        } else if (id == R.id.menu_search) {
            log("Search clicked");

           // item.getActionView().setSelected(true);
            doMySearch("");
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.shuffle) {
            return true;
        } else if (id == R.id.repeat) {
            return true;
        } else if (id == R.id.mix) {
            return true;
        }
        else if(id == R.id.menu_search){
           // onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void doMySearch(String q){
        log("Searching for: " + q);

        if(sf == null)
        sf =  (SongFragment) SongFragment.newInstance();

        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.QUERY.toString());
        b.putString("QueryID", "0" );
        b.putString("Query", q);
        sf.setArguments(b);
        showFragment(R.id.frame, sf, true);

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
                    (float)( (showq>2) ? showq*2 : showq)
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
                    (float)(2*showq)
            );
            findViewById(R.id.sidebar).setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    0,
                    (showq<3) ? 5.0f : 0.0f
            );
            findViewById(R.id.frame).setLayoutParams(param);


        }
    }
    public int hideQ(){
        if(qf != null) {
            getSupportFragmentManager().beginTransaction().remove(qf).commit();
            qf = null;
            showq = 0;
        }

        return  showq;
    }

    public int showQ(){
        log("Show Q:" + ++showq);
       // if(showq >0 && qf != null && qf.isVisible()) return showq;

        if(showq == 1) {
            //expandSidebar();
            showControls();
           // return showq;
        }
        if(showq > 3){
            showq = 0;
            if(showfmenu) hideFM();
            moveFab(true); //move menu back up
            hideQ();
            closeSidebar();
            return showq;
        }
            if (qf == null)  qf = QueueFragment.newInstance();
            expandSidebar();
            showFragment(R.id.qframe, qf, false);

        return  showq;
    }


    public void hideControls(boolean showPlaceholder){

        if(!controlsVisible) return;

        Log.d(TAG, "Hiding control fragement");
        controlsVisible = false;
      //  updateNextSongInfo();
        getSupportFragmentManager().beginTransaction().remove(cf).commit();

    }

    public void showControls(){
        if(controlsVisible && cf != null && cf.isVisible()) return;

        Log.d(TAG, "Showing control fragement");
        //cf =  (ControlFragment) ControlFragment.newInstance();
        cf =   NowFragment.newInstance(true);
        controlsVisible = true;
        showFragment(R.id.controlFrame, cf, false);
    }

    private void showFragment(int r, Fragment f, boolean addTobs){

        if(r == R.id.frame){
            //its the main frame.
            //if its the now fragment, minimize the queue, hide the controls
            //any other fragment set the queue to half.
            if(f instanceof NowFragment){
                showq = 0;
                showQ();
                hideControls(false);
            } else if(showq > 2){
                showq = 1;
                showQ();
                showControls();
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right, R.anim.slidein_right, R.anim.slideout_left);
        if(f instanceof SongFragment)
        transaction.setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left, R.anim.slidein_left, R.anim.slideout_right);
        else if(f instanceof NowFragment)
            transaction.setCustomAnimations(R.anim.slidein_up, R.anim.slideout_left, R.anim.slidein_left, R.anim.slideout_down);
        else if(f instanceof VisualizerDialogFragment)
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.slideout_up, R.anim.slidein_up, R.anim.slideout_down);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(r, f);
        if(addTobs) transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

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
    public void nowIconLongClicked(){

        Log.d(TAG, "Now icon clicked ");
        vf = VisualizerDialogFragment.newInstance();
        showFragment(R.id.frame, vf, true);
    }


    @Override
    public void nowIconClicked(boolean close) {

        Log.d(TAG, "Now icon clicked " + close);


//        if(close) {
//            showq = 3; //reset
//            hideFM();
//
//        } else showq=2; //open full
//
//
//            showQ();
    }

    @Override
    public void lineClicked(int i) {

        Log.d(TAG, "Now line clicked: " + i);

        if(mService.getCurrentSong() == null) return;
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

        showFragment(R.id.frame, mf, true);

    }


    public void showNow(){
        Log.d(TAG, "Showing NOW fragment");

        //always show controls in landscape.
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        hideControls(false);

        //opens the queue when its fully closed.
//        if(showq > 2) {
//            showq = 1;
//            showQ();
//        }

        if(nf != null && nf.isVisible() ) return;

        nf =  NowFragment.newInstance(false);
        showFragment(R.id.frame, nf, true);


//        updateCurrentInfo(new Song("", ""));
    }

    public void showBluetoothSettings(){
         //shortcut direct to bluetooth setting page
        Intent intentBluetooth = new Intent();
        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentBluetooth);

    }

    private  void showBubbles(){

        log("Showing Bubble fragment");
        Fragment b = BubbleFragment.newInstance();
        showFragment(R.id.frame, b, false);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_now) {

            showNow();

        } else if( id == R.id.nav_bubble) {
            log("Nav bubble clicked!");
            showEQTab();
            //showBubbles();

        }  else if( id == R.id.nav_theme) {
            log("Nav theme clicked!");
            showThemeDialog();

        } else if( id == R.id.exit) {
            log("Nav exit clicked!");
            mService.pauseRequest();
            finish();


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
            showFragment(R.id.frame, f, true);


        } else if (id == R.id.nav_artists) {
            Fragment f = ArtistFragment.newInstance();
            showFragment(R.id.frame, f, true);

        } else if (id == R.id.nav_albums) {
            Fragment f = AlbumFragment.newInstance();
            showFragment(R.id.frame, f, true);

        } else if (id == R.id.nav_genres) {
            Fragment f = GenreFragment.newInstance();
            showFragment(R.id.frame, f, true);

        } else if (id == R.id.nav_songs) {
            Fragment f = SongFragment.newInstance();
            showFragment(R.id.frame, f, true);


        }  else if (id == R.id.nav_settings) {
            log("Settings clicked.");

            showMix();
//            Fragment f = (Fragment) HeaderFragment.newInstance("", "");
//            showFragment(R.id.frame, f, true);
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
        showFragment(R.id.frame, f, true);

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
        showFragment(R.id.frame, f, true);
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
        showFragment(R.id.frame, f, true);
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
        long[] ids = new long[items.size()];
        for(int a = 0; a <items.size(); a++) ids[a] = Long.parseLong( items.get(a).getId() );

        DialogFragment c = ChoosePlaylistDialogFragment.newInstance("", "", top , true, ids );
        c.show(getSupportFragmentManager(), items.get(0).getId());
    }

    public void addPlaylistPicked(long[] items, boolean top, String pid){
        //happens after dialog name entered.

            ArrayList<Long> i = new ArrayList<>();
            for(int j = 0; j < items.length; j++) i.add(items[j]);

                addListToPlaylist(Long.parseLong(pid), i, top);

    }


    /*
      end of add group to playlist
    */

    //calls above method when a playlist is seltected
    @Override
    public void onOptionLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), song.getId(), false, false, null);
        c.show(getSupportFragmentManager(), song.getId());
    }

    @Override
    public void onNextLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to top of playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), song.getId(), false, true, null);
        c.show(getSupportFragmentManager(), song.getId());
    }

    @Override
    public void onBubblesReady() {
        log("Bubbles ready. locking screen.");
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
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
        showFragment(R.id.frame, f, true);
    }





    @Override
    public void onSongNextupClicked(int position, Song item) {
        Log.d(TAG, "Song nextup clicked" + item.getTitle() + " : " + item.getArtist());

        View contextView = findViewById(R.id.controlFrame);
        Snackbar.make(contextView, item.getTitle() + " added next to queue.", Snackbar.LENGTH_LONG).show();

        //Toast.makeText(this, item.getTitle() + " added next.", Toast.LENGTH_SHORT).show();
        plist p = mService.getQueue(); //new plist();
        p.addNextSong(item);

        updateQueueFrag(p);
        updateNextSongInfo();
    }

    @Override
    public void onOptionClicked(int position, Song item) {
        Log.d(TAG, "Song option clicked" + item.getTitle() + " : " + item.getArtist());
        // TODO: 12/29/18 just add it to the list dont play. maybe open a menu later?

        View contextView = findViewById(R.id.controlFrame);
        Snackbar.make(contextView, item.getTitle() + " added to queue.", Snackbar.LENGTH_LONG).show();

        //Toast.makeText(this, item.getTitle() + " added to queue.", Toast.LENGTH_SHORT).show();
        plist p = mService.getQueue();
        p.addSong(item);

        updateQueueFrag(p);
    }


    @Override
    public void onSongClicked(Song item) {
        Log.d(TAG, "Song item clicked" + item.getTitle() + " : " + item.getArtist());

        View contextView = findViewById(R.id.controlFrame);
        Snackbar.make(contextView, "Now Playing: "+ item.getTitle(), Snackbar.LENGTH_LONG).show();

       // Toast.makeText(this, "Now Playing: "+ item.getTitle(), Toast.LENGTH_SHORT).show();
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
        if(cf != null && controlsVisible) cf.setPlayPause(isPlaying);
        int r = !isPlaying ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause;
        fab2.setImageResource(r);
    }

    public void updateProgress(MusicPlayer player){
        if(mf != null && mf.isVisible()) mf.updateProgressBar(player);
        if(nf != null && nf.isVisible()) nf.updateProgressBar(player);
        if(controlsVisible)
        {
                cf.updateProgressBar(player);
        }

    }

    public void updateCurrentInfo(Song s){

        if(vf != null && vf.isVisible()) vf.setAid(mService.getPlayer().getAID());
        if(nf != null) nf.updateSongInfo(s);
        if(cf != null) cf.updateSongInfo(s);

        updateNextSongInfo();

        updateQueueFrag(mService.getQueue());

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
                    //this happens at 1hz, don't leave active
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

        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        int base = 0;
         if(!top &&  cur.moveToLast()) {

             base = cur.getInt(0);
             base += 1;
             String id = cur.getString(1);
         }
        cur.close();
        log("adding item --->>>>>base: " + base + " to " + pname);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, sid);
        resolver.insert(uri, values);


    }

    public void addListToPlaylist(Long pid, ArrayList<Long> ids, boolean top) {

       log("adding songs to playlist Ids: " + ids.size());

        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER, MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentValues values = new ContentValues();
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        Cursor cur = resolver.query(uri, cols, null, null, null);

        //ids already has our new items at the top, just add the old ones to it
        // if adding to the end, just insert the old items. at 0
            cur.moveToFirst();
            while(cur.moveToNext()){
                long l = Long.parseLong(cur.getString(1));
                if(top) ids.add(0, l);
                else ids.add( l );

            }
            cur.close();
            log("songs added to playlist Ids: " + ids.size());

            //todo delete old items
        //add the all the items in the new order


        for(int i=0; i<ids.size(); i++) {
            values = new ContentValues();
            // Log.d("Music service", i +" saving song: " + t.getTitle() + songid);
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
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

    @Override
    public  void onPlaylistSongLongClicked(Song s, String pid, String pname, int pos) {

        log("Playlist song long clicked! deleting item: " + s.getTitle() + "from playlist " + pname);

        DialogFragment c = ConfirmDeleteDialogFragment.newInstance(pname, pid, s.getId(), pos);
        c.show(getSupportFragmentManager(), "REMOVESONG");

    }


        public void deletedSong(String pname, String pid, String sid, int pos) {

        View contextView = findViewById(R.id.controlFrame);
        Snackbar.make(contextView, "deleting item: " +sid + " from playlist " + pname, Snackbar.LENGTH_LONG).show();

        deleleFromPlaylist(Long.parseLong(pid), pname, sid , pos);

    }

    public void deleleFromPlaylist( Long pid, String pname,  String sid, int pos) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        };
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);

        String[] arg = { sid, pos+"" };
        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID+"=? AND " +
                MediaStore.Audio.Playlists.Members.PLAY_ORDER+"=?";
        resolver.delete(uri, where, arg );

        log(sid + " deleted from playlist: " + pname + " pos: "+ pos);
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
            mf.updateProgressBar(mService.getPlayer());
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
