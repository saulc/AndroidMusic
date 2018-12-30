package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.SongAdapter;
import music.app.my.music.helpers.MediaHelperListener;
import music.app.my.music.helpers.MediaStoreHelper;
import music.app.my.music.types.Playlist;

import music.app.my.music.types.Song;

/**
 * Created by saul on 7/26/16.
 */
public class SongFragment extends baseListFragment implements MediaHelperListener {

    public enum SF_TYPE {QUEUE, PLAYLISTITEMS, SONGS, ALBUMS, ARTISTS, GENRE };
    private SF_TYPE myType = SF_TYPE.SONGS;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

   // private MediaStoreHelper msHelper;
   // private RecyclerView.Adapter mAdapter;
    private ArrayList<Song> items;
  //  private RecyclerView recyclerView;

    private String pid = null;
    private String pname = null;

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
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselistitem_list, container, false);

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) v;

            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

            updateAdapter();
        }
        return view;
    }

    @Override
    public void updateAdapter(){
        mAdapter = new SongAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        recyclerView.setAdapter(mAdapter);
        log("Updating song list adapter");
        mAdapter.notifyDataSetChanged();
        if(items == null)
            items = new ArrayList<>();

        log("items:" + items.size());
    }


    @Override
    public void helperReady(){
        log("Helper ready, loading songs");

            switch(myType) {

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
