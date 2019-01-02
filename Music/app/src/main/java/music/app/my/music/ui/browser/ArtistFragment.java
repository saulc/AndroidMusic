package music.app.my.music.ui.browser;

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
import music.app.my.music.adapters.ArtistAdapter;
import music.app.my.music.types.Artist;

/**
 * Created by saul on 7/26/16.
 */
public class ArtistFragment extends baseListFragment {

    protected ArrayList<Artist> items;

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

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) v;

                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

          updateAdapter();
        }
        return view;
    }

    public void updateAdapter(){
        mAdapter = new ArtistAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        recyclerView.setAdapter(mAdapter);
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

    }


}
