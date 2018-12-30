package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.QueueAdapter;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.types.Song;
import music.app.my.music.types.plist;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link QueueListener}
 * interface.
 */
public class QueueFragment extends baseListFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private QueueListener qListener;

    private ArrayList<Song> items;
    //private MediaStoreHelper msHelper;
    //private RecyclerView.Adapter mAdapter;

  //  private RecyclerView recyclerView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QueueFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static QueueFragment newInstance() {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Queue fragment qFragCreated");


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

      //  iniMsHelper();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queueitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
           // recyclerView.setAdapter(new QueueAdapter(items, mListener));
            qListener.qFragCreated();

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QueueListener) {
            qListener = (QueueListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement QueueListener interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        qListener = null;
    }

    @Override
    public void helperReady(){
        log("Helper ready, loading Queue");


      //  msHelper.loadQueue();
    }

    @Override
    public void queueitemLoaderFinished(ArrayList<Song> s) {
        log("queue Loaded");
        log("found " + s.size() + " song(s)");
        items = s;
        updateAdapter();
    }

    private void log(String s){
        Log.d(getClass().getSimpleName(), s);
    }


    @Override
    public void updateAdapter(){
        if(items == null) {
            items = new ArrayList<>();
        }
            mAdapter = new QueueAdapter(items, (QueueListener) getActivity());
        ((QueueAdapter)  mAdapter).setCurrent(current);
        recyclerView.setAdapter(mAdapter);

        log("Updating adapter");
        mAdapter.notifyDataSetChanged();

        log("items:" + items.size());
    }

    private  int current = 0;

    public void setQList(plist p){
        log("Queue fragment updating list");

        items =   p.getArray();
        current = p.getIndex();
        updateAdapter();
    }

}
