package music.app.my.music.ui.browser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.adapters.GenreAdapter;
import music.app.my.music.helpers.Logger;
import music.app.my.music.types.Genre;

/*
 * Created by saul on 7/26/16.
 */
public class GenreFragment extends baseListFragment implements DrawerActivity.mFabListener {

    public void onMove(float x, float y) {
        log("move called:" + x + " " + y);
//            int sy = (int) (y/10);
        log("scroll by:" + y);
//        recyclerView.scrollBy(0, (int) y);
    }
    protected ArrayList<Genre> items;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
//        Logger.log(getClass().getSimpleName(), s);
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

    private TextView countline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselist, container, false);

        View v = view.findViewById(R.id.list);
        // Set the adapter
        if (v instanceof FastScrollRecyclerView) {
            Context context = view.getContext();
            recyclerView = (FastScrollRecyclerView) v;
            recyclerView.setFastScrollEnabled(true);

//                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            updateAdapter();
            View header =  view.findViewById(R.id.header);
            TextView t = (TextView) header.findViewById(R.id.content);
            t.setText(" Genre ");
            countline = (TextView) header.findViewById(R.id.line2);
            updatecount();
        }
        return view;
    }
    private void updatecount(){
        if(countline == null) return;
        String sl = items.size() + " Genre" +  ( (items.size()==1) ? "" : "s");
        countline.setText(sl);
    }

    public void updateAdapter(){
        mAdapter = new GenreAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        if(recyclerView== null) return;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFastScrollStateChangeListener((OnFastScrollStateChangeListener)mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());
        updatecount();
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
