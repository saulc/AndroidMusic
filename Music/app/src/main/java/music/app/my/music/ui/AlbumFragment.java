package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.AlbumAdapter;
import music.app.my.music.types.Album;

public class AlbumFragment  extends baseListFragment {

    protected ArrayList<Album> items;

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

    public static AlbumFragment newInstance() {
        AlbumFragment fragment = new AlbumFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Album fragment qFragCreated");
        //   iniMsHelper();
        items = new ArrayList<Album>();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselistitem_list, container, false);

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView)  v;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            //
            updateAdapter();
        }
        return view;
    }

    public void updateAdapter(){
        mAdapter = new AlbumAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );

        recyclerView.setAdapter(mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }

    @Override
    public void helperReady(){
        log("Helper ready, loading Albums");

        msHelper.loadAlbums();
    }

    @Override
    public void albumLoaderFinished(ArrayList<Album> p) {
        log("Albums Loaded");
        log("found " + p.size() + " album(s)");
        items = p;
        updateAdapter();

    }


}
