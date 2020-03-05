package music.app.my.music.ui.browser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import music.app.my.music.R;
import music.app.my.music.adapters.SongAdapter;
import music.app.my.music.helpers.MediaHelperListener;
import music.app.my.music.types.Playlist;

import music.app.my.music.types.Song;
import music.app.my.music.ui.ControlFragment;

/**
 * Created by saul on 7/26/16.
 */
public class SongFragment extends baseListFragment implements MediaHelperListener {

    public enum SF_TYPE {QUEUE, PLAYLISTITEMS, SONGS, ALBUMS, ARTISTS, GENRE, QUERY, BUBBLE };
    private SF_TYPE myType = SF_TYPE.SONGS;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

   // private MediaStoreHelper msHelper;
   // private RecyclerView.Adapter mAdapter;
    private ArrayList<Song> items;
  //  private RecyclerView recyclerView;

    private   ImageView bigIcon;

    private  TextView infoText;
    private String pid = null;
    private String pname = null;

    private ProgressBar songbar;
    private int songprogress = 0;
    protected Handler mhandler;

    public static SongFragment newInstance() {
        SongFragment fragment = new SongFragment();

        Bundle b = new Bundle();
        b.putString("SFTYPE", SF_TYPE.SONGS.toString());
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Song fragment qFragCreated");

        Bundle b = getArguments();
        if(b != null) {
            String s = b.getString("SFTYPE");
            if(s == null) return; //default to songs

            if(s.compareTo(SF_TYPE.PLAYLISTITEMS.toString())==0) {
                myType = SF_TYPE.PLAYLISTITEMS;

                pid = b.getString("PlaylistID");
                pname = b.getString("PlaylistName");
            }

            else if(s.compareTo(SF_TYPE.ALBUMS.toString())==0) {
                myType = SF_TYPE.ALBUMS;

                pid = b.getString("AlbumID");
                pname = b.getString("AlbumName");

            }
            else if(s.compareTo(SF_TYPE.ARTISTS.toString())==0) {
                myType = SF_TYPE.ARTISTS;

                pid = b.getString("ArtistID");
                pname = b.getString("ArtistName");

            }
            else if(s.compareTo(SF_TYPE.GENRE.toString())==0) {
                myType = SF_TYPE.GENRE;

                pid = b.getString("GenreID");
                pname = b.getString("GenreName");

            }
            else if(s.compareTo(SF_TYPE.QUERY.toString())==0) {
                myType = SF_TYPE.QUERY;

                pid = b.getString("QueryID");
                pname = b.getString("Query");

            }else if(s.compareTo(SF_TYPE.BUBBLE.toString())==0) {
                myType = SF_TYPE.BUBBLE;

                pid = b.getString("QueryID");
                pname = b.getString("Query");

            }
        }



    }
    private void nextLongClicked() {
        mListener.addSongsToPlaylist(items, true);
    }
    private void opLongClicked() {
        mListener.addSongsToPlaylist(items, false);
    }
    private void headerLongClicked(){
        Collections.shuffle(items);
        mListener.addSongsToQueue(items, true);
    }
    private void headerClicked(){
        mListener.addSongsToQueue(items, true);
    }
    private void nextClicked(){
        mListener.addSongsNextToQueue(items);
    }
    private void opClicked(){
        mListener.addSongsToQueue(items, false);
    }

    @Override
    public void onDestroy(){
        log("Song frag destroy");
        if(myType == SF_TYPE.QUERY)  mListener.onSearchDestroyed();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(myType == SF_TYPE.ALBUMS)
            view = inflater.inflate(R.layout.fragment_album, container, false);
        else
            view = inflater.inflate(R.layout.fragment_baselistitem_list, container, false);


        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof FastScrollRecyclerView) {
            Context context = view.getContext();
            recyclerView = (FastScrollRecyclerView) v;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setFastScrollEnabled(true);
              //  recyclerView.setAutoHideEnabled(false);

//            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

           // recyclerView.setVerticalScrollBarEnabled(true);
            songbar = view.findViewById(R.id.songbar);
            songbar.setProgress(songprogress);
            songbar.setMax(100);


            mhandler = new Handler();
            mhandler.postDelayed(updatesongbar, 10);
            //songbar.setOnClickListener();

            if(myType == SF_TYPE.ALBUMS) {
                if (pid != null) {

                    ImageView bigIcon = (ImageView) view.findViewById(R.id.bigIcon);
                    String p =  getArguments().getString("AlbumArt"); //findAlbumArt(pid);

                    log(" fragment updating albumart: " + p);
                    Drawable d = Drawable.createFromPath(p);
                    if (d != null) {
                        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                        d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 600, 600, true));

                        bigIcon.setImageDrawable(d);

                    }//else icon.setImageResource(R.drawable.android_robot_icon_2);
                }
            }

            View header =  view.findViewById(R.id.header);
            TextView t = (TextView) header.findViewById(R.id.content);
            infoText = (TextView) header.findViewById(R.id.line2);
            infoText.setText("type?");

            String a = "";
            if(myType == SF_TYPE.QUERY)  a += "Search results: ";

            if(pname == null) a = "All Songs";
            else a += pname;
            t.setText(a);

            ImageButton next = (ImageButton) view.findViewById(R.id.nextupbtn);
            ImageButton op = (ImageButton) view.findViewById(R.id.optionbtn);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a) {

                    log("Song Group next up clicked");
                        nextClicked();
                }
            });

            op.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a){

                    log("Song Group op clicked");
                    opClicked();
                }
            });

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a) {

                    log("Song Group header clicked");
                    headerClicked();

                }
            });

            header.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    log("Song Group header Long clicked");
                    headerLongClicked();
                    return true;
                }
            });
            next.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    log("Song Group next long clicked");
                    nextLongClicked();

                    return false;
                }
            });

            op.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    log("Song Group op long clicked");
                    opLongClicked();

                    return false;
                }
            });

                    updateAdapter();
        }
        return view;
    }


    private Runnable updatesongbar = new Runnable() {
        @Override
        public void run() {
            progress(0);
            if(songprogress < 101)
            mhandler.postDelayed(updatesongbar, 5);
        }
    };


    private void progress(int i){
        if(i!=0)
        songbar.setProgress(i);
        else songbar.setProgress(songprogress+=2);

        if(songprogress>=100) {
            songbar.setProgress(100);
            mhandler.removeCallbacks(updatesongbar);
        }

     //   log("progress: " + songbar.getProgress());

    }
    @Override
    public void updateAdapter(){


     //   mhandler.removeCallbacks(updatesongbar);


        mAdapter = new SongAdapter(myType == SF_TYPE.PLAYLISTITEMS, pid, pname, items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );

        if(recyclerView == null) return;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFastScrollStateChangeListener((OnFastScrollStateChangeListener)mAdapter);
        log("Updating song list adapter");
        mAdapter.notifyDataSetChanged();
        if(items == null)
            items = new ArrayList<>();

        String s = items.size() + " song";
        if(items.size() > 1) s += "s";
        if(infoText != null) infoText.setText(s);

        log("items:" + s );


    }


    @Override
    public void helperReady(){
        log("Helper ready, loading songs");

            switch(myType) {

                case QUERY: msHelper.search(pname);
                break;

                case BUBBLE:
                case SONGS:
                    msHelper.loadSongs();
                    break;
                case ARTISTS:
                    msHelper.loadArtistItems(pid, pname);
                    break;
                case ALBUMS:
                    msHelper.loadAlbumItems(pid, pname);
                    break;
                case GENRE:
                    msHelper.loadGenreItems(pid, pname);
                    break;
                case PLAYLISTITEMS:
                    msHelper.loadPlaylistItems(pid, pname);
                    break;
            }

    }


    public void updateQuery(String s){
        log("Update Query: " + s + " old: " + pname);
        pname = s;

        items = new ArrayList<>();
        updateQuery();
    }

    //secondary list for fast query results.
    private ArrayList<Song> slist;
    @Override
    public void queryLoaderFinished(ArrayList<Song> songs) {

        log("Query items Loaded " + songs.size() + " song(s)");

        slist = songs;
        //do matching here. so i dont' need to do multiple where queries

        updateQuery();
    }


    private void updateQuery(){


        updateAdapter();
        infoText.setText("Searching songs...");

        CharSequence q = new StringBuilder(pname.toLowerCase());
        int i = 0;
        for(Song s: slist) {
            if (s.getAlbum().toLowerCase().contains(q)) items.add(s);
            else if (s.getTitle().toLowerCase().contains(q)) items.add(s);
            else if (s.getArtist().toLowerCase().contains(q)) items.add(s);
            //else if(s.getGenre().contains(pname)) results.add(s);

//            if( (i++ % 100) == 0){
//                log("Returning ----------->>>>>>");
//             infoText.setText(i + " songs found");
//            }
        }

        infoText.setText(items.size() + " songs found");
        log("Returning Query results (" + items.size() + ") to activity");

    }

    @Override
    public void artistItemLoaderFinished(ArrayList<Song> songs) {
        log("Artist items Loaded " + songs.size() + " song(s)");
        items = songs;
        updateAdapter();
    }

    @Override
    public void albumItemLoaderFinished(ArrayList<Song> songs) {
        log("Album items Loaded " + songs.size() + " song(s)");
        items = songs;
        updateAdapter();
    }
    @Override
    public void genreItemLoaderFinished(ArrayList<Song> songs) {
        log("Genre items Loaded " + songs.size() + " song(s)");
        items = songs;
        updateAdapter();
    }


    @Override
    public void songLoadedFinished(ArrayList<Song> songs) {
        log("SONGS Loaded " + songs.size() + " song(s)");
        items = songs;
        updateAdapter();
    }

    @Override
    public void playlistLoaderFinished(ArrayList<Playlist> p) {

    }

    @Override
    public void playlistItemLoaderFinished(ArrayList<Song> s) {
        log("Playlist Loaded");
        log("found " + s.size() + " playlist(s)");
        items = s;
        updateAdapter();
//        for(Song a: s) {
//            log("name: " + a.getTitle() + "  id: " + a.getArtist());
//
//        }

    }
}
