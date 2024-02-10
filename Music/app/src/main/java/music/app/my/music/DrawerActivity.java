package music.app.my.music;

import android.animation.LayoutTransition;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import music.app.my.music.helpers.FabDoubleTapGS;
import music.app.my.music.helpers.FaderSettingListener;
import music.app.my.music.helpers.Logger;
import music.app.my.music.helpers.PlaylistFilemaker;
import music.app.my.music.helpers.PlaylistHelper;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.player.MediaControlReceiver;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.player.MusicService;
import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.plist;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;
import music.app.my.music.ui.LoggerFragment;
import music.app.my.music.ui.MixFragment;
import music.app.my.music.ui.MixxerFragment;
import music.app.my.music.ui.MovableFloatingActionButton;
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
        QueueListener ,
        ControlFragment.ControlFragmentListener,
        NewPlaylistDialog.OnDialogInteractionListener ,
        MixxerFragment.MixxerListener,
        FaderSettingListener,
        SecretFragment.SecretListener,
        FabDoubleTapGS.DoubleTapListener,
        Toolbar.OnMenuItemClickListener , Logger.LogCallback {


    private VisualizerDialogFragment vf = null;

    //fragments that need callbacks. send info/triggers to fragments
    private PlayListFragment playlistFrag = null;
    private FadeFragment fader = null;
    private MixxerFragment mf = null;
    private MixFragment mx = null;
    private NowFragment nf = null;
    private QueueFragment qf = null;
    private ControlFragment cf = null;
    private PlaceholderFragment pf = null;
    private SongFragment sf = null; //search fragment
    private SongFragment sFrag = null; //song fragment
    private LoggerFragment logFrag = null;

    private boolean searchActive = false;
    private FloatingActionButton fab3;
    private FloatingActionButton fab2;
    private FloatingActionButton fab1;
    private FloatingActionButton fab;
    private MovableFloatingActionButton mfab;

    private boolean autoCloseDrawer = true;
    private boolean showfmenu = false; //show/hide floating control buttons.
    private int showq = 0; //0 == hidden, 1 = miniplayer q, 2 = half, 3 = full screen, todo 4 edit plist

    private final String TAG = getClass().getSimpleName();

    private void log(String s) {
        Logger.log(TAG, s);
//        Log.i(TAG, s);
    }

    private MusicService mService;
    private boolean mBound = false;
    private TextSwitcher nextText;
    private Intent startIntent, toggleIntent, pauseIntent, playIntent,
            nextIntent, previousIntent;
    private ImageView navIcon;

    private String actionSetTheme = "ACTION_SET_THEME";
    private int currentTheme = R.style.DarkSide;    //R.style.AppTheme_NoActionBar;

    private boolean controlsVisible = false;
    //private TextureView bgTexture;

    private MediaControlReceiver myEventReceiver;
    private AudioManager am;

    private boolean playlistExport = false;

    private String pid = null;
    private String pname = null;


  //  private NotificationHelper noti;

    /* -----------------------------------   onCreate start.   ----------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("on create");

        log("Setting app Theme.");
        handleThemeIntent();


        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ini media events
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        myEventReceiver = new MediaControlReceiver();

        //add android default trasitions to main layout changes.
        // //smooth movement on queue/miniplayer
        ((ViewGroup) findViewById(R.id.llRoot)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        ((ViewGroup) findViewById(R.id.sidebar)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);


//        bgTexture = findViewById(R.id.bgTexture);

        toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) this);

        nextText = (TextSwitcher) findViewById(R.id.nextText);
        if (nextText != null) {
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

        mfab = (MovableFloatingActionButton) findViewById(R.id.mfab);
        mfab.setCallback(this);
//        mfab.setButtonStartPos();
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mFab Clicked: " + showq);
                showQ();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showLogs();
                return true;
            }
        });


//        fab.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                //mService.duck();
//                Log.d(TAG, "Fab Long Clicked: " + showfmenu);
//                if (!showfmenu) showFM();
//                else hideFM();
//
//                return true;
//            }
//        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab Clicked: " + showq);
                showQ();
            }
        });



        FabDoubleTapGS dt = new FabDoubleTapGS();
        dt.setDoubleTapListener(this);
        final GestureDetector gestureDetector = new GestureDetector(dt);
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    log("Touch event!!");
                    return true;
                }
                return false;
            }
        };
        fab.setOnTouchListener(gestureListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null)   navigationView.setNavigationItemSelectedListener(this);
        NavigationView menuView = (NavigationView) findViewById(R.id.menu_view);
        if(menuView != null)   menuView.setNavigationItemSelectedListener(this);
        //no nav view. for wide screens use persistent menu list

        int res  = getResources().getConfiguration().orientation;
        autoCloseDrawer = ( res != Configuration.ORIENTATION_LANDSCAPE );

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

       // noti = new NotificationHelper(this);
//        if(controlsVisible)
//            showControls();

        showNow(); //update queue when its loaded

        // showBubbles();

        handleStartIntents(); //search media share....

    }
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        log("onCreateView event");
//        return super.onCreateView(name, context, attrs);
//    }
//    @Override
//    public void showNoti(String msg){
//        int id = 0;
//        Notification.Builder nb = null;
//        nb = noti.getNotification1("Music", msg);
//
//        if (nb != null) {
//            noti.notify(id, nb);
//        }
//    }

    /* -----------------------------------   onCreate over.   ----------------------------------- */


    public void showLogs(){
        log("Showing Log Fragment");

        logFrag = LoggerFragment.Companion.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.qframe, logFrag).commit();
        Logger.setListener(this);
    }

    @Override
    public void updateLogs(ArrayList<String> log) {
        if(logFrag != null)
        logFrag.updateLog(log);
    }


    public interface mFabListener{
        void onMove(float x, float y);
    }

    public void fabMove(float x, float y){
        log("fab Moving " + x + " " + y + " sfrag " + (sFrag == null));
        if(sFrag != null)
            sFrag.onMove(x, y );
    }

    /* ---------- Main side drawer_actions navigation menu ------------- */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_now) {

            showNow();

        } else if (id == R.id.nav_eq) {
            log("Nav eq clicked!");
            showEQTab();
            //showBubbles();

        } else if (id == R.id.nav_dream) {
            log("Nav dream clicked!");
            showDaydream();

        } else if (id == R.id.nav_setdream) {
            log("Nav dream settings clicked!");
            showDaydreamSettings();

        } else if (id == R.id.nav_theme) {
            log("Nav theme clicked!");
            showThemeDialog();

        } else if (id == R.id.exit) {
            log("Nav exit clicked!");
            mService.pauseRequest();
            mService.removeFromForeground();
            finish();


        } else if (id == R.id.bluetooth) {
            log("Nav bluetooth clicked!");
            showBluetoothSettings();


        } else if (id == R.id.clear_queue) {
            clearQueueClicked();


        } else if (id == R.id.save_queue) {
            createNewPlaylist(true);


        } else if (id == R.id.new_playlist) {
            createNewPlaylist(false);

            //show playlist after we added a new one. no else
        }
        if (id == R.id.nav_playlists || id == R.id.new_playlist) {
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
//            Fragment f = SongFragment.newInstance();
//            showFragment(R.id.frame, f, true);
            log("Songs clicked. loading Query page.");
            doMySearch("");


        } else if (id == R.id.nav_settings) {
            log("Settings clicked.");

            showMix();
//            Fragment f = (Fragment) HeaderFragment.newInstance("", "");
//            showFragment(R.id.frame, f, true);
        } else if (id == R.id.nav_mix) {
            log("mix clicked.");
            showMixx();

        }
            if(autoCloseDrawer) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        return true;
    }


    /* ---------- Service Connection! UI Callbacks ------------- */

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
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

            binder.setListener(new MusicService.BoundServiceListener() {

                @Override
                public void sendProgress(MusicPlayer player) {
                    //this happens at 1hz, don't leave active
//                    log("Activty got progress info. updating ui...");
                    //update seekbar.
                    if (player != null)
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

            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            log("Service Disconnected :(");
        }
    };



    /* -----------------------------------  start    ----------------------------------- */

    private void handleStartIntents() {

        //handle share intents if we need to...
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        log("Got intent! action: " + action);

        log("Got intent! action: " + action);
        if (Intent.ACTION_SEND.equals(action) && type != null) {

            log("GOt Action Send!!");
            log("Looking for data in extras!!");
            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

            log("Type: ===>> " + type);

            String shortname = uri.getEncodedPath();
            log("Shared item Location?: " + shortname );
            try {
                File f = new File(shortname);

                Scanner myReader = new Scanner(f);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    log(data);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String id = uri.toString().substring(uri.toString().lastIndexOf('/'), uri.toString().length());
            //shared = new Song("Shared Song", id );

            //todo play the shared song.
//            onSongClicked(shared);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

        }

    }

    @Override
    public void finish() {
        //startService(new Intent(MusicService.ACTION_STOP));
        log("Finishing.");
        if (mBound) {
            log("Unbinding service.");
            stopService(new Intent(getApplicationContext(), MusicService.class));
//            unbindService(mConnection);
            mBound = false;
        }

        super.finish();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        log("Pause activity.");
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        mfab.setButtonStartPos();
        log("Resume activity.");
        if(playlistExport) {
            if(pname != null)
            log("Playlist save flag. " + pname);
            exportPlaylist();
            //do the work.
//            playlistExport = false;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Bind to LocalService

        final Intent intent = new Intent("music.app.my.music.player.MUSICSERVICE");  //Intent(DrawerActivity.this, music.app.my.music.player.MusicService.class);
        //startService(intent);
        Log.d("Main Activity", "binding service");
        boolean r = getApplicationContext().bindService(
                new Intent(this, MusicService.class), mConnection, Context.BIND_AUTO_CREATE);

        if (r) log("Service should be bound");
        else log("Service binding failed.");
        //am.registerMediaButtonEventReceiver(myEventReceiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        log("Stopping Activity");
        // am.unregisterMediaButtonEventReceiver(myEventReceiver);
        //	saveQueue();
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

    /* -----------------------------------   onCreateOptions   -----------------------------------
     *
     *                                app bar menu. top Icons
     *                                                                                        */

    @Override
    public boolean onShuffleClicked(){
        log("Suffle clicked");
        boolean b = mService.shuffleSongs();
        Toast.makeText(mService, "Shuffle: " + b, Toast.LENGTH_SHORT).show();
        updateQueueFrag(mService.getQueue());

        return b;
    }

    @Override
    public int onRepeatClicked(){
        log("Repeat clicked");
        int b = mService.repeatSongs();
        String bb = "";
        switch (b){
            case 0: bb = "Off"; break;
            case 1: bb = "Single"; break;
            case 2: bb = "All"; break;
        }
        Toast.makeText(mService, "Repeat: " + bb, Toast.LENGTH_SHORT).show();

        return b;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.shuffle) {
            onShuffleClicked();
            return true;
        } else if (id == R.id.repeat) {
            onRepeatClicked();
            return true;
        } else if (id == R.id.mixxer) {
            log("Show Fade Settings...");

            showMixx();
            return true;
        }else if (id == R.id.mix) {
            log("Mix clicked");
            Song s = PlaylistHelper.getRandomSong(getApplicationContext());
            String b = "Mix: " + s.getTitle() + " by " + s.getArtist() + " added to Queue.";
            Toast.makeText(mService, b, Toast.LENGTH_SHORT).show();

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

    //
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_actions, menu);
        // Get the SearchView and set the searchable configuration
        // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                log("SV closed");
                searchActive = false;
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                log("Query Text Submit: " + query);
                if (sf != null)
                    sf.updateQuery(query);
                searchView.clearFocus();  //close keyboard
                MenuItem m = menu.findItem(R.id.menu_search);
                m.collapseActionView();    //minimize search bar

                searchActive = false;  //reset the song fragment//
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                log("Query Text Change: " + newText);
                if (sf != null)
                    sf.updateQuery(newText);
                return true;
            }
        });

        return true;
    }


    @Override
    public void onSearchDestroyed() {
        log("Search Done.");
        searchActive = false;
    }

    //load all songs ready for search keyword. in title, artist, album.
    private void doMySearch(String q) {
        log("Searching for: " + q);

        if (!searchActive) {
            sf = (SongFragment) SongFragment.newInstance();

            Bundle b = new Bundle();
            b.putString("SFTYPE", SongFragment.SF_TYPE.QUERY.toString());
            b.putString("QueryID", "0");
            b.putString("Query", q);
            sf.setArguments(b);
            showFragment(R.id.frame, sf, true);
            searchActive = true;
        }

    }


    /* ---------- Daydream ------------- */
    private void showDaydream(){
        log("Sending Daydream intent.");
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
        startActivity(intent);

    }

    private void showDaydreamSettings() {

        final  Intent intent = new Intent(Settings.ACTION_DREAM_SETTINGS);
        startActivity(intent);
    }



    /* ---------- Themes ------------- */
    private void showThemeDialog() {

        log("Showing Theme picker.");
        ChooseThemeDialogFragment ct = ChooseThemeDialogFragment.newInstance(currentTheme);
        ct.show(getSupportFragmentManager(), "Choose Theme");
    }

    private void handleThemeIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        log("Theme Got intent! action: " + action);
        if (actionSetTheme.equals(action)) {
            int r = intent.getIntExtra("THEME", -22);
            if (r == -22) {  //if there was no extra,
                log("Error: no valid theme extra in intent.");
                return;
            }
            log("Found theme in extra: " + r);
            setTheme(r);
            currentTheme = r;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.saved_theme), currentTheme);
            editor.commit();

        } else if (Intent.ACTION_MAIN.equals(action)) {
            //regular start.
            log("Setting default Theme.");
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            int defaultValue = R.style.AppTheme;
            int currentTheme = sharedPref.getInt(getString(R.string.saved_theme), defaultValue);

            setTheme(currentTheme);
        }
    }

    public void themePicked(String name, int theme) {
        log("Theme picked: " + name + " id: " + theme + " cur: " + currentTheme);
        if (theme == currentTheme) return;


        Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
        i.setAction(actionSetTheme);
        i.putExtra("THEME", theme);
        startActivity(i);
        finish();
    }

    /* ---------- Floating button Menu ------------- */
    private void iniFM() {
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
                Log.d(TAG, "Fab 1 Long Clicked: next");

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
                Log.d(TAG, "Fab 2 Long Clicked: play/pause");

                playPauseLongClicked(); //trigger the fade in also.
                //mini q
                showq = 0;
                showQ();
//                showNow();
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
                Log.d(TAG, "Fab 3 Long Clicked:previous ");

                showq = 0;
                showQ();
                hideFM();

                return true;
            }
        });

    }

    private void moveFab(boolean up) {

        if (up) {
            float r = -getResources().getDimension(R.dimen.fab_marginvert);
            fab1.animate().translationY(r);
            fab2.animate().translationY(r);
            fab3.animate().translationY(r);
            fab.animate().translationY(r);
        } else {

            float r = 0f;
            fab1.animate().translationY(r);
            fab2.animate().translationY(r);
            fab3.animate().translationY(r);
            fab.animate().translationY(r);
        }
    }

    private void showFM() {
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

    private void hideFM() {
        showfmenu = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab2.animate().translationX(0);
        fab3.animate().translationX(0);

        moveFab(showq == 0); //move menu back up


    }
    /* ---------- Floating button Menu ------------- */


    /* ---------- Secret mode ------------- */
    @Override
    public void secretFragmentCreated() {
        log("Secret mode on");
        // secret.setTexture(bgTexture);
    }

    @Override
    public void secretFragmentDestroyed() {
        log("Secrets kill.");
    }

    @Override
    public void onDoubleTap() {
        log("Fab Double tap!");
        starkMode(!stark);
    }

    @Override
    public void nowIconDoubleClicked() {
        log("Now icon double tapped.");


    }

    private boolean stark = false;
    private SecretFragment secret = null;

    //enable stark mode when showimg Fab menu.
    private void starkMode(boolean enable) {

        if (stark && secret != null) {
            getSupportFragmentManager().beginTransaction().remove(secret).commit();
            stark = false;
        } else if (!stark) {
            secret = SecretFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.bgTexture, secret, "Itsasecret").commit();
            stark = true;

        }
    }

    /* ---------- Secret mode ------------- */

    /* ---------- Fader controls ------------- */
    private boolean faderActive = false;

    public void showFadeFrag(View v) {
        log("Nav icon clicked! secret shit going down! ^_- ");
        Toast.makeText(DrawerActivity.this,
                " -->  ^_^  <-- ", Toast.LENGTH_LONG).show();

        if (!faderActive) {
            log("Showing fadefragment.");
            fader = FadeFragment.newInstance();
            showFragment(R.id.frame, fader, false);
            faderActive = true;
        } else {
            log("Hiding Fader");
            faderActive = false;
            if (fader != null)
                getSupportFragmentManager().beginTransaction().remove(fader).commit();
        }
    }

    /* ---------- EQ mode ------------- */
    public void setEQ(int selection, String eqname) {
        log("Set eq: " + eqname);
        if (mService != null) mService.getPlayer().setEQ(selection);
    }

    public void showEQTab() {
        log("Showing eq Tab");
        if (mService == null || mService.getPlayer() == null) return;

        ArrayList<String> pre = mService.getPlayer().getEQPresets();
        if (pre == null) return;
        DialogFragment c = EQDialogFragment.newInstance(pre);
        c.show(getSupportFragmentManager(), "EQ");


    }



    //adjust layout weights to hide/show queue and player
    public void placeholderCreated() {

        log("Placeholder created, q frame");
        closeSidebar();
    }

    /* ---------- Hide/Show extra Fragments ------------- */
    public void closeSidebar() {
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

    public void expandSidebar() {
        log("Expanding q frame");
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    DrawerLayout.LayoutParams.MATCH_PARENT,
                    (float) ((showq > 2) ? showq * 2 : showq)
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
                    (float) ((showq == 2 ? 2:1)  * showq)
            );
            findViewById(R.id.sidebar).setLayoutParams(param);


                param = new LinearLayout.LayoutParams(
                        DrawerLayout.LayoutParams.MATCH_PARENT,
                        0,
                        (showq < 3) ? 6.0f : 0.0f
                );
                findViewById(R.id.frame).setLayoutParams(param);


        }
    }

    public int hideQ() {
        if (qf != null) {
            getSupportFragmentManager().beginTransaction().remove(qf).commit();
            qf = null;
            showq = 0;
        }

        return showq;
    }

    public int showQ() {
        log("Show Q:" + ++showq);
        // if(showq >0 && qf != null && qf.isVisible()) return showq;

        if (showq == 1) {
            //expandSidebar();
            showControls();
            // return showq;
        }
        if (showq > 3) {
            log("Hiding browser.");
            showq = 0;
            log("Hiding fm.");
            if (showfmenu) hideFM();
            moveFab(true); //move menu back up
            log("Hiding Queue.");
            hideQ();
            log("Hiding sidebar....");
            closeSidebar();
            return showq;
        }

        log("updating queue.");
        if (qf == null) {
            qf = QueueFragment.newInstance();
            showFragment(R.id.qframe, qf, false);
        }
        expandSidebar();
        return showq;
    }

    public void hideControls(boolean showPlaceholder) {

        if (!controlsVisible) return;

        Log.d(TAG, "Hiding control fragement");
        controlsVisible = false;
        //  updateNextSongInfo();
        getSupportFragmentManager().beginTransaction().remove(cf).commit();

    }

    public void showControls() {
        if (controlsVisible && cf != null && cf.isVisible()) return;

        Log.d(TAG, "Showing control fragement");
        //cf =  (ControlFragment) ControlFragment.newInstance();
        cf = NowFragment.newInstance(true);
        controlsVisible = true;
        showFragment(R.id.controlFrame, cf, false);
    }

    /* ---------- Hide/Show extra Fragments ------------- */

    /* ---------- Show  Fragments ------------- */

    private void showFragment(int r, Fragment f, boolean addTobs) {

        if (r == R.id.frame) {
            //its the main frame.
            //if its the now fragment, minimize the queue, hide the controls
            //any other fragment set the queue to half.
            if (f instanceof NowFragment) {
                showq = 0;
                showQ();
                hideControls(false);
            } else if(nowShowing && nf != null){
                //if the now frag was show. kill it. so it can show the actionbar.
                    nf.onDestroy();
            }else if(f instanceof PlayListFragment) playlistFrag = (PlayListFragment) f;
            else if( f instanceof SongFragment)  sFrag = (SongFragment) f;
            //other fragments. just make sure the mainframe is visible
            if (showq > 2) {
                showq = 1;
                showQ();
                showControls();
            }

        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right, R.anim.slidein_right, R.anim.slideout_left);
        if (f instanceof SongFragment)
            transaction.setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left, R.anim.slidein_left, R.anim.slideout_right);
        else if (f instanceof NowFragment)
            transaction.setCustomAnimations(R.anim.slidein_up, R.anim.slideout_left, R.anim.slidein_left, R.anim.slideout_down);
        else if (f instanceof VisualizerDialogFragment)
            transaction.setCustomAnimations(R.anim.anim_in, R.anim.slideout_up, R.anim.slidein_up, R.anim.slideout_down);

        else if (f instanceof FadeFragment) {
            //fade fragment is special.
            transaction.add(r, f, "Fader");
            transaction.commit();
            return;
        }
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(r, f);
        if (addTobs) transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }



    @Override
    public void controlIconClicked() {

        Log.d(TAG, "Control icon clicked");
        showNow();
    }



    public void visualizerLongClicked() {
        log("Vis long clicked.");
        nowIconLongClicked();
        // showNow();
    }


    public void visualizerCreated() {
        log("Vis created.");
        if (mService != null && mService.getPlayer() != null && mService.getPlayer().isPlaying()) {
            vf.setImageView(nf.getIcon());
            vf.setAid(mService.getPlayer().getAID());
            //vf.setEnabled(true);
        }

    }


    @Override
    public void nowIconLongClicked() {

        Log.d(TAG, "Now icon Long clicked " + (vf==null) );

        if (vf == null) {
            vf = VisualizerDialogFragment.newInstance();
            getSupportFragmentManager().beginTransaction().show(vf).commit();
        } else {
            vf.stop();
            getSupportFragmentManager().beginTransaction().remove(vf).commit();
            vf = null;
            //nowIconLongClicked();
        }

        //showFragment(R.id.frame, vf, true);
    }


    @Override
    public void nowIconClicked(boolean isSwipe, boolean close) {
        Log.d(TAG, "Now icon clicked " + close);
        if (!isSwipe) {
            if (vf != null) vf.clicked();
            return;
        }
        //else
        if (close) {
            showq = 3; //reset
            hideFM();

        } else showq = 2; //open full

        showQ();

    }

    private void updateNowButtons(plist q){
        log("Updateing now buttons.");
        if(nowShowing && nf!= null) nf.updateButtons(q);
    }

    /* ---------- Show artist/album clicked from now fragment ------------- */
    @Override
    public void lineClicked(int i) {

        Log.d(TAG, "Now line clicked: " + i);

        if (mService.getCurrentSong() == null) return;
        if (i == 1) {
            Log.d(TAG, "Current song clicked, doing nothing... ");
        } else if (i == 2) {
            Log.d(TAG, "Current Artist clicked");
            Song s = mService.getCurrentSong();
            Artist a = new Artist(s.getArtistId(), s.getArtist());
            onArtistClicked(a);
        } else if (i == 3) {
            Log.d(TAG, "Control Album clicked");
            Song s = mService.getCurrentSong();
            Album a = new Album(s.getAlbumId(), s.getAlbum());
            onAlbumClicked(a);
        }

    }

    /* ---------- set up an Update to now fragment ------------- */
    @Override
    public void onNowViewCreated() {
        log("Now Fragment created");

        if (nf == null) showNow();

        nowShowing = true;
        getSupportActionBar().hide();

        // if(controlsVisible && cf != null && cf.isVisible()) {
        log("Now Fragment setting updater");

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mService != null)
                    if (nf.isVisible() || cf.isVisible())
                        if (mService.getQueue().getSize() > 0) {
                            updateQueueFrag( mService.getQueue() );
                            updateCurrentInfo(mService.getCurrentSong());
                            updateNowButtons(mService.getQueue());
                            nf.setupVolbar(getMaxVol(), getVol());
                        }
                Log.d(TAG, "Now updating..");
                h.removeCallbacks(this);
            }
        }, 300);


    }


    @Override
    public void onNowViewDestroyed(){
        log("NOW fragment destroyed.");
        nowShowing = false;
        getSupportActionBar().show();

    }
    private boolean nowShowing = false;

    public void showNow(){
        log("Showing NOW fragment");

        //always show controls in landscape.
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        hideControls(false);

        if(nf != null && nf.isVisible() ) return;

        nf =  NowFragment.newInstance(false);
        showFragment(R.id.frame, nf, true);
    }

    /* ---------- Show mixxer fragment ------------- */
    public void showMix(){
        Log.d(TAG, "Showing MIX fragment");
        if(mf != null && mf.isVisible() ) return;
        mf =  MixxerFragment.newInstance();
        showFragment(R.id.frame, mf, true);

    }
    //show new kt mixer/fader
    public void showMixx(){
        Log.d(TAG, "Showing MIixX fragment");
        if(mx != null && mx.isVisible() ) return;
        mx =  MixFragment.Companion.newInstance();
        showFragment(R.id.frame, mx, true);
    }



    /* ---------- Show placeholder. for now its used for eq instead. ------------- */
    private  void showBubbles(){

        log("Showing Bubble fragment");
        Fragment b = BubbleFragment.newInstance();
        showFragment(R.id.frame, b, false);

    }

    public void showBluetoothSettings(){
        //shortcut direct to bluetooth setting page
        Intent intentBluetooth = new Intent();
        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentBluetooth);

    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "List item clicked");
    }


    //shows albums/ then all songs option.
    @Override
    public void onArtistClicked(Artist mItem) {
        log("Artist Selected: " + mItem.getArtist());
        log("Looking for songs");

        Fragment f = AlbumFragment.newInstance(mItem.getArtist(), mItem.getId());
        showFragment(R.id.frame, f, true);
    }

    //shows all songs by artist.
    @Override
    public void onArtistLongClicked(Artist mItem) {
        log("Artist long Clicked: " + mItem.getArtist());
        log("Looking for songs");

        Fragment f =  (Fragment) SongFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SongFragment.SF_TYPE.ARTISTS.toString());
        b.putString("ArtistID", mItem.getId());
        b.putString("ArtistName", mItem.getArtist());
        f.setArguments(b);
        showFragment(R.id.frame, f, true);

    }

    @Override
    public void onArtistLongClick(Artist a) {
        onArtistLongClicked(a);
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


    /* ---------- Playlist stuff ------------- */
    @Override
    public void cancelClicked() {
        Log.d(TAG, "Playlist  create terminated.");

    }



    @Override
    public void nameEnted(String name, boolean isQ) {
        Log.d(TAG, "Playlist  name entered: " + name);
        PlaylistHelper.newPlaylist(getApplicationContext(), name);
//         PlaylistFilemaker pl = new PlaylistFilemaker();
//         pl.newPlaylist(getApplicationContext(), name);
        Toast.makeText(mService, "Created Playlist: " + name, Toast.LENGTH_SHORT).show();
        if(isQ)  saveQueueAsPlaylist(name);
    }

    //called back fro nameEnted when playlist has been created.
    //add items and we're done.
    public void saveQueueAsPlaylist(String name){
        try {
            Long ii =  PlaylistHelper.findPlaylistId(getApplicationContext(), name);
            ArrayList<Long> ss = new ArrayList<>();

            if(mService != null) {
                ArrayList<Song> songs =  mService.getQueue().getArray();
                for(Song a: songs) ss.add( Long.parseLong( a.getId() ) );
                Toast.makeText(mService, "adding items Playlist" + name, Toast.LENGTH_SHORT).show();

            }
            PlaylistHelper.addListToPlaylist(getApplicationContext(), ii, ss, true);
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
                hideQ();
                mService.getQueue().clearQueue();
                updateQueueFrag(mService.getQueue());

                Toast.makeText(mService, "Cleared Now playing: " + name + " : " + pid, Toast.LENGTH_SHORT).show();
            }
        }else {

            PlaylistHelper.deletePlaylist(getApplicationContext(), pid);
            Toast.makeText(mService, "Deleted Playlist: " + name + " : " + pid, Toast.LENGTH_SHORT).show();
        }
    }

    /*
    add song to playlist  end
     */

    //playlist option add == delete playlist confirm dialog
    @Override
    public void onPlaylistOptionClicked(int position, String pid, String name) {
        //confirm first! important! double confirm?
        DialogFragment c = ConfirmDeleteDialogFragment.newInstance(name);
        c.show(getSupportFragmentManager(), pid);

    }

    private void exportPlaylist(){
        if(playlistFrag == null){
            log("null fragment...");

//            return;
        }
//        playlistFrag.exportPlaylist(pname, pid);
        log("Writing playlist data to file..");
        //fuck you android. now my moto g stylus saves m3u files in internal /music

//        PlaylistFilemaker pl = new PlaylistFilemaker();
//         pl.exportPlaylist(pname, pid);
//         log("playlist export complete.");
    }

    //save playlist to xml file
    @Override
    public void onPlaylistNextUpClicked(int position, String id, String name) {
        Log.d(TAG, "Playlist next up clicked");
        //play list to xml for real saving...

         PlaylistFilemaker pl = new PlaylistFilemaker();

         Intent i = pl.newPlaylist(getApplicationContext(), name);
          playlistExport = true;
          pid = id;
          pname = name;
          startActivity(i);
        Toast.makeText(mService, "Saving Playlist: " + name + ".m3u", Toast.LENGTH_SHORT).show();
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

            PlaylistHelper.addToPlaylist(getApplicationContext(), name, id, lsid, top);
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

            PlaylistHelper.addListToPlaylist(getApplicationContext(), Long.parseLong(pid), i, top);

    }


    /* ---------- remove song from playlist trigged. show comfirm dialog ------------- */
    @Override
    public  void onPlaylistSongLongClicked(Song s, String pid, String pname, int pos) {

        log("Playlist song long clicked! deleting item: " + s.getTitle() + "from playlist " + pname);

        DialogFragment c = ConfirmDeleteDialogFragment.newInstance(pname, pid, s.getId(), pos);
        c.show(getSupportFragmentManager(), "REMOVESONG");

    }

    /* ---------- remove song comfirmed, show snackbar and delete it. ------------- */
    public void deletedSong(String pname, String pid, String sid, int pos) {
        //TODO add an 'undo" to the snackbar.
        View contextView = findViewById(R.id.controlFrame);
        Snackbar.make(contextView, "deleting item: " +sid + " from playlist " + pname, Snackbar.LENGTH_LONG).show();
        PlaylistHelper.deleleFromPlaylist(getApplicationContext(), Long.parseLong(pid), pname, sid , pos);

    }

    /* ---------- playlist stuff over ------------- */


    //calls above method when a playlist is selected
    @Override
    public void onOptionLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), song.getId(), false, false, null);
        c.show(getSupportFragmentManager(), song.getId());
    }

    @Override
    public void onNextLongClicked(Song song) {
        Log.d(TAG, "Option long clicked, add to top of playlist");
        DialogFragment c = ChoosePlaylistDialogFragment.newInstance(song.getTitle(), song.getId(), true, false, null);
        c.show(getSupportFragmentManager(), song.getId());
    }

    /* ---------- lock screen orientation ------------- */
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
//        updateNextSongInfo();
//        updateCurrentSongInfo();
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

        if(play) showNow();     //on header clicked.
        updateQueueFrag(p);

//        updateNextSongInfo();
//        updateCurrentSongInfo();
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
//        updateNextSongInfo();
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
//        updateNextSongInfo();
//        updateCurrentSongInfo();
    }




    /* ---------- Queue Fragment ------------- */
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

//        updateCurrentSongInfo();
        updateQueueFrag(mService.getQueue());
//        updateNextSongInfo();
    }


    @Override
    public void onQueueItemLongClicked(Song mItem, int position) {
        //moves to next.
        log(position + " Q item Long clicked " + mItem.getTitle());
        if( mService.getQueue().getIndex() != position) {
            plist q =  mService.getQueue();
           int index = q.getIndex();
           Song clicked = q.removeSong(position);
           if(position < index) q.setIndex(index - 1);
           q.addNextSong(clicked);
        }
//        updateCurrentSongInfo();
        updateQueueFrag(mService.getQueue());
//        updateNextSongInfo();

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
    public void onItemSwiped(Song mItem, int position) {

        log(position + " Q item swiped " + mItem.getTitle());
      //  Toast.makeText(mService, "removed: " + mItem.getTitle() + " from queue.", Toast.LENGTH_SHORT).show();

        log(position + " Removing " + mItem.getTitle() + " from queue.");
        mService.removeSong(position);

        updateQueueFrag(mService.getQueue());
    }


    /* ---------- Queue Fragment end ------------- */


    /* ---------- MIxxer Fragment ------------- */


    private boolean mixxerReadyForUpdates = false;
    @Override
    public void onMixxerDestroyed() {
        mixxerReadyForUpdates = false;
    }

    @Override
    public void onMixxerCreated() {
        log("Mixxer Created.");
        mixxerReadyForUpdates = true;
    }
    /* ---------- Mixxer Fragment end ------------- */

    public void updateQueueFrag(plist p){
        if(qf != null && showq != 0 && p != null)
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

    /* ---------- User clicked seekbar ------------- */
    @Override
    public void seekBarChanged(int progress) {
        mService.seekTo(progress);
    }

    /* ---------- Music Serice Callbacks UpdateUI ------------- */
    public void setpp(Boolean isPlaying){
        if(nf != null && nowShowing) nf.setPlayPause(isPlaying);
        if(cf != null && controlsVisible) cf.setPlayPause(isPlaying);
        int r = !isPlaying ? //R.drawable.media_01 : R.drawable.media_02;
         android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause;
        fab2.setImageResource(r);
    }


    @Override
    public void playPauseLongClicked() {
        if(mService != null)
        mService.getPlayer().fadeIn();
    }

    //updates seekbar and fader cue at 1hz
    /* ---------- Music Serice Callbacks UpdateUI ------------- */
    public void updateProgress(MusicPlayer player){
        if(mixxerReadyForUpdates) mf.updateMixxerPlayer(player);
        if(nowShowing) nf.updateProgressBar(player);
        if(controlsVisible)
        {
            cf.updateProgressBar(player);
        }

    }

    //called on pause/play only
    /* ---------- Music Serice Callbacks UpdateUI ------------- */
    public void updateCurrentInfo(Song s){

        if(mixxerReadyForUpdates) mf.updateMP(mService.getPlayer());

        if(vf != null) vf.setAid(mService.getPlayer().getAID());
        if(nf != null && nowShowing) nf.updateSongInfo(s);
        if(cf != null && controlsVisible) cf.updateSongInfo(s);

//        updateNextSongInfo();

        updateQueueFrag(mService.getQueue());

    }

    @Override
    public void mixSwitched(boolean b) {
        log("Fader Mix switch: "+ b);
    }

    @Override
    public void fadeSwitched(boolean b) {
        log("Fader switch: "+ b);
    }


    @Override
    public void fadeInDurationChanged(int i) {
        log("Fader in Duration: "+ i);
        if(mService != null)  mService.setFadeInDuration(i);
    }

    @Override
    public void fadeOutDurationChanged(int i) {
        log("Fader out Duration: "+ i);
        if(mService != null)  mService.setFadeOutDuration(i);
    }

    @Override
    public void fadeOutGapChanged(int i) {
        log("Fader out gap: "+ i);
        if(mService != null)  mService.setFadeOutGap(i);

    }

    @Override
    public void fadeInGapChanged(int i) {
        log("Fader in gap: "+ i);
        if(mService != null)  mService.setFadeInGap(i);

    }

    @Override
    public int getMaxVol() {
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return max;
    }

    @Override
    public int getVol() {
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    @Override
    public void onVolChanged(int i) {
        log("Vol changed: "+ i);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if(i > max) i = max;
        else if(i < 0) i = 0;

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC, // Stream type
                i, // Index
                0 //AudioManager.FLAG_SHOW_UI // Flags
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //just let the system do it if nowfrag is not visble
        if(!nowShowing || nf == null) return super.onKeyDown(keyCode, event);


        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            log("Volume down button clicked.");
//            prevPressed();
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
        }else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
            log("Volume up button clicked.");
//            nextPressed();
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
        }

        //show the change on the now fragment if its showing.
        if(nowShowing && nf != null) nf.updateVol(getVol());

        return true;
    }

    /* ---------- not needed? ------------- */
//    public void updateCurrentSongInfo() {
//        if (mService != null) {
//            Song s = mService.getQueue().getCurrentSong();
//            String b = "";
//            if (s != null) {
//                b = s.getTitle() + " by " + s.getArtist();
//                if (pf != null)
//                    pf.setText(" Now Playing : " + b);
//
//
//            }
//        }
//    }

//    public void updateNextSongInfo() {
//        if (nextText == null) return;
//        plist q = mService.getQueue();
//
//
//        int m = mService.repeatMode();
//        if (m == 1) {    //repeat same song forever!
//            Song s = q.getCurrentSong();
//            String b = "";
//            if (s != null) {
//                b = s.getTitle() + " by " + s.getArtist();
//                //" Now Playing: "
//                nextText.setText(" Up Next: " + b);
//            }
//        } else if (m == 2) {
//            //repeat all songs
//            if (q.getIndex() == (q.getSize() - 1)) {
//                //last song, next is first
//                Song s = q.getSong(0);
//                String b = "";
//                if (s != null) {
//                    b = s.getTitle() + " by " + s.getArtist();
//                    //" Now Playing: "
//                    nextText.setText(" Up Next: " + b);
//                }
//            }
//
//        } else if (q.hasNext()) {
//            Song s = q.getSong(q.getIndex() + 1);
//            String b = "";
//            if (s != null) {
//                b = s.getTitle() + " by " + s.getArtist();
//                //" Now Playing: "
//                nextText.setText(" Up Next: " + b);
//            }
//        }
//
//
//    }


}
