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
import music.app.my.music.adapters.GenreAdapter;
import music.app.my.music.types.Genre;

/*
 * Created by saul on 7/26/16.
 */
public class GenreFragment extends baseListFragment {

    protected ArrayList<Genre> items;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }


    private String pid = null;
    private String pname = null;

    public static GenreFragment newInstance() {
        GenreFragment fragment = new GenreFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Genre fragment Created");
        //   iniMsHelper();
        items = new ArrayList<Genre>();

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
        mAdapter = new GenreAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        if(recyclerView== null) return;
        recyclerView.setAdapter(mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }

    @Override
    public void helperReady(){
        log("Helper ready, loading Genres");


        msHelper.loadGenres();
    }

    @Override
    public void genreLoaderFinished(ArrayList<Genre> p) {
        log("Genre Loaded");
        log("found " + p.size() + " Genre(s)");
        items = p;
        updateAdapter();

    }


}
