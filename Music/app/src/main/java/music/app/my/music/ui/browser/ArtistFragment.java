package music.app.my.music.ui.browser;

import android.content.Context;
import android.os.Bundle;
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

import music.app.my.music.R;
import music.app.my.music.adapters.ArtistAdapter;
import music.app.my.music.helpers.Logger;
import music.app.my.music.types.Artist;

/**
 * Created by saul on 7/26/16.
 */
public class ArtistFragment extends baseListFragment {

    protected ArrayList<Artist> items;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
//        Log.d(TAG, s);
        Logger.log(getClass().getSimpleName(), s);
    }

    //private MediaStoreHelper msHelper;
    //private RecyclerView.Adapter mAdapter;
   // private ArrayList<Playlist> items;
   // private RecyclerView recyclerView;

    private String pid = null;
    private String pname = null;

    public static ArtistFragment newInstance() {
        ArtistFragment fragment = new ArtistFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Artist fragment qFragCreated");
     //   iniMsHelper();
    items = new ArrayList<Artist>();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselist, container, false);
        View header =  view.findViewById(R.id.header);
        TextView t = (TextView) header.findViewById(R.id.content);
        infoText = (TextView) header.findViewById(R.id.line2);
        infoText.setText("...");

        String a = "All Artists";
        t.setText(a);

        updateHeader( );

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (FastScrollRecyclerView) v;
//                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            updateAdapter();
            recyclerView.setFastScrollEnabled(true);
        }
        return view;
    }

    private  TextView infoText;

    private void updateHeader( ){
        if(infoText == null) return;
        String s = items.size() + " Artist" +  ( (items.size()==1) ? "" : "s");
        infoText.setText(s);
    }


    public void updateAdapter(){
        mAdapter = new ArtistAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFastScrollStateChangeListener((OnFastScrollStateChangeListener)mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }

    @Override
    public void helperReady(){
        log("Helper ready, loading Artists");


        msHelper.loadArtists();
    }

    @Override
    public void artistLoaderFinished(ArrayList<Artist> p) {
        log("Artists Loaded");
        log("found " + p.size() + " Artist(s)");
        items = p;
        if(mAdapter != null)
        updateAdapter();
        updateHeader();

    }


}
