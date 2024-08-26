package music.app.my.music.ui.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.adapters.PlaylistAdapter;
import music.app.my.music.helpers.Logger;
import music.app.my.music.helpers.PlaylistHelper;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

/**
 * Created by saul on 8/26/24
 */
public class PlaylistFileFragment extends baseListFragment implements DrawerActivity.mFabListener {


    public void onMove(float x, float y) {
        log("move called:" + x + " " + y);
//            int sy = (int) (y/10);
        log("scroll by:" + y);
        recyclerView.scrollBy(0, (int) y);
    }
    private final String TAG = getClass().getSimpleName();
    private void log(String s){
//        Log.d(TAG, s);
        Logger.log(getClass().getSimpleName(), s);
    }

    private TextView countline;
    //private MediaStoreHelper msHelper;
    //private RecyclerView.Adapter mAdapter;
   // private ArrayList<Playlist> items;
   // private RecyclerView recyclerView;

    private String pid = null;
    private String pname = null;

    public static PlaylistFileFragment newInstance() {
        PlaylistFileFragment fragment = new PlaylistFileFragment();

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
        if (v instanceof FastScrollRecyclerView) {
            Context context = view.getContext();
             recyclerView = (FastScrollRecyclerView) v;
             recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setFastScrollEnabled(true);
            View header =  view.findViewById(R.id.header);
            TextView t = (TextView) header.findViewById(R.id.content);
            t.setText("   + Add Playlist + ");
            countline = (TextView) header.findViewById(R.id.line2);
            String sl = items.size() + " playlist" +  ( (items.size()==1) ? "" : "s");
            countline.setText(sl);

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

    public void exportPlaylist(String name, String id){
        log("playlist export, opening playlist: " + name);
        int s = items.size();
        log("checking playlists... found: " + s);
//        PlaylistFilemaker pf = new PlaylistFilemaker();
//        pf.exportPlaylist(name, id, items);
    }

    @Override
    public void updateAdapter(){
        mAdapter = new PlaylistAdapter(items
                , ( OnListFragmentInteractionListener) getActivity() );
    if(recyclerView == null) return;
        recyclerView.setAdapter(mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());
        String sl = items.size() + " playlist" +  ( (items.size()==1) ? "" : "s");
        countline.setText(sl);
    }


    @Override
    public void helperReady(){
        log("Helper ready, loading playlists");

        items = PlaylistHelper.readPlaylist(getContext());
        updateAdapter();
//        msHelper.loadPlaylists();
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
