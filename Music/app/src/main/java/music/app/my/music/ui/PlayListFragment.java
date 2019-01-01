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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.PlaylistAdapter;
import music.app.my.music.adapters.SongAdapter;
import music.app.my.music.helpers.MediaHelperListener;
import music.app.my.music.helpers.MediaStoreHelper;
import music.app.my.music.types.Playlist;

import music.app.my.music.types.Song;
import music.app.my.music.ui.dummy.DummyContent;
import music.app.my.music.ui.dummy.DummyContent.DummyItem;

/**
 * Created by saul on 7/26/16.
 */
public class PlayListFragment extends baseListFragment {


    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    //private MediaStoreHelper msHelper;
    //private RecyclerView.Adapter mAdapter;
   // private ArrayList<Playlist> items;
   // private RecyclerView recyclerView;

    private String pid = null;
    private String pname = null;

    public static PlayListFragment newInstance() {
        PlayListFragment fragment = new PlayListFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Playlist fragment qFragCreated");
     //   iniMsHelper();


    }


    private void headerClicked(){
        mListener.createNewPlaylist(false);
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
             recyclerView.setLayoutManager(new LinearLayoutManager(context));
            View header =  view.findViewById(R.id.header);
            TextView t = (TextView) header.findViewById(R.id.content);
            t.setText("Header");

            ImageButton next = (ImageButton) view.findViewById(R.id.nextupbtn);
            ImageButton op = (ImageButton) view.findViewById(R.id.optionbtn);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a) {

                    log("Playlist next up clicked");

                }
            });

            op.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View a){

                        log("Playlist op clicked");

                    }
            });

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a) {

                    log("Playlist header clicked");
                   headerClicked();

                }
            });

          updateAdapter();
        }
        return view;
    }


    @Override
    public void updateAdapter(){
        mAdapter = new PlaylistAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
    if(recyclerView == null) return;
        recyclerView.setAdapter(mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }


    @Override
    public void helperReady(){
        log("Helper ready, loading playlists");


        msHelper.loadPlaylists();
    }
    @Override
    public void playlistLoaderFinished(ArrayList<Playlist> p){
        log("Playlist Loaded");
        log("found " + p.size() + " playlist(s)");
        items = p;
        updateAdapter();


    }

    @Override
    public void playlistItemLoaderFinished(ArrayList<Song> s) {

    }

}
