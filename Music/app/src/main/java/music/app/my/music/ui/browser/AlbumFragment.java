package music.app.my.music.ui.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.adapters.AlbumAdapter;
import music.app.my.music.helpers.Logger;
import music.app.my.music.types.Album;

public class AlbumFragment  extends baseListFragment  implements DrawerActivity.mFabListener {

    public void onMove(float x, float y) {
        log("move called:" + x + " " + y);
//            int sy = (int) (y/10);
        log("scroll by:" + y);
        recyclerView.scrollBy(0, (int) y);
    }

    protected ArrayList<Album> items;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
//        Log.d(TAG, s);
        Logger.log(TAG, s);
    }


    //private MediaStoreHelper msHelper;
    //private RecyclerView.Adapter mAdapter;
    // private ArrayList<Playlist> items;
    // private RecyclerView recyclerView;

    private String id = null;
    private String name = null;
    private static final String arg1 = "ARTIST";
    private static final String arg2 = "ARTISTID";



    public static AlbumFragment newInstance(String artist, String artistId) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle b = new Bundle();
        b.putString(arg1, artist);
        b.putString(arg2, artistId);
        fragment.setArguments(b);
        return fragment;
    }


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

        Bundle b = getArguments();
        if(b != null){
            name = b.getString(arg1);
            id = b.getString(arg2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselist, container, false);

        View header =  view.findViewById(R.id.header);
        TextView t = (TextView) header.findViewById(R.id.content);
        infoText = (TextView) header.findViewById(R.id.line2);
        infoText.setText("...");

        String a = "All Albums";
        t.setText(a);

        updateHeader( );

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof FastScrollRecyclerView) {
            Context context = view.getContext();
            recyclerView = (FastScrollRecyclerView)  v;
            //recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setFastScrollEnabled(true);

//                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            updateAdapter();
        }
        return view;
    }

    public void updateAdapter(){
        mAdapter = new AlbumAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );

        if(recyclerView== null) return;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFastScrollStateChangeListener((OnFastScrollStateChangeListener)mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }

    private  TextView infoText;

    private void updateHeader( ){
        if(infoText == null) return;
        String s = items.size() + " Albums" +  ( (items.size()==1) ? "" : "s");
        infoText.setText(s);
    }

    @Override
    public void helperReady(){
        log("Helper ready, loading Albums");

        //get albums for only artist. or all albums
        if(name != null) msHelper.loadAlbums(name);
        else msHelper.loadAlbums();
    }

    @Override
    public void albumLoaderFinished(ArrayList<Album> p) {
        log("Albums Loaded");
        log("found " + p.size() + " album(s)");
        items = p;
        updateAdapter();
        updateHeader();
    }


}
