package music.app.my.music.ui.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.PlaylistAdapter;
import music.app.my.music.helpers.MediaHelperListener;
import music.app.my.music.helpers.MediaStoreHelper;
import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Genre;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;
import music.app.my.music.ui.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public  class baseListFragment extends Fragment  implements MediaHelperListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    protected OnListFragmentInteractionListener mListener;


    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    protected MediaStoreHelper msHelper;
    protected RecyclerView.Adapter mAdapter;
    protected ArrayList<Playlist> items;
    protected RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public baseListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static baseListFragment newInstance(int columnCount) {
        baseListFragment fragment = new baseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        log("Base fragment qFragCreated");
        iniMsHelper();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baselistitem_list, container, false);
        log("Base Fragment Ui Created.");
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            //            }
            updateAdapter();
        }
        return view;
    }

    public void iniMsHelper(){
        log("Setting up MediaStore Helper");
        msHelper = new MediaStoreHelper(getContext());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(msHelper, "MediaStoreHelper");
        fragmentTransaction.commit();
        msHelper.setListener(this);
        items = new ArrayList<>();
    }

    public void updateAdapter(){
        mAdapter = new PlaylistAdapter(items
                , ( baseListFragment.OnListFragmentInteractionListener) getActivity() );
        recyclerView.setAdapter(mAdapter);
        log("Updating adapter");
        mAdapter.notifyDataSetChanged();
        log("items:" + items.size());

    }



    @Override
    public void helperReady(){
        log("Helper ready, ");


    }


    @Override
    public void queryLoaderFinished(ArrayList<Song> songs) {

    }

    @Override
    public void songLoadedFinished(ArrayList<Song> songs) {

    }

    @Override
    public void queueitemLoaderFinished(ArrayList<Song> songs) {

    }


    @Override
    public void playlistLoaderFinished(ArrayList<Playlist> p){
        log("Playlist Loaded");


    }

    @Override
    public void artistLoaderFinished(ArrayList<Artist> a) {

    }

    @Override
    public void albumLoaderFinished(ArrayList<Album> a) {

    }

    @Override
    public void genreLoaderFinished(ArrayList<Genre> a) {

    }

    @Override
    public void playlistItemLoaderFinished(ArrayList<Song> s) {
        log("Playlist Items Loaded");
    }

    @Override
    public void artistItemLoaderFinished(ArrayList<Song> s) {

    }

    @Override
    public void albumItemLoaderFinished(ArrayList<Song> s) {

    }

    @Override
    public void genreItemLoaderFinished(ArrayList<Song> s) {

    }


    //attach to activity


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
        void onPlaylistClicked(Playlist item);
        void onSongClicked(Song item);

        void onArtistClicked(Artist mItem);

        void onAlbumClicked(Album mItem);

        void onSongNextupClicked(int position, Song id);

        void onOptionClicked(int position, Song song);

        void  onGenreClicked(Genre item);

        void createNewPlaylist(boolean isQ);

        void onPlaylistOptionClicked(int position, String pid, String name);

        void  onPlaylistNextUpClicked(int position, String pid);

        void onOptionLongClicked(Song song);

        void addSongsToQueue(ArrayList<Song> items, boolean play);

        void onItemSwiped(Song m, int position);

        void addSongsNextToQueue(ArrayList<Song> items);

        void addSongsToPlaylist(ArrayList<Song> items, boolean top);

        void onNextLongClicked(Song song);

        void onPlaylistSongLongClicked(Song s, String pid);

        void onBubblesReady();
    }
}
