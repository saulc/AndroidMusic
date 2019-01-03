package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.QueueAdapter;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.types.Song;
import music.app.my.music.types.plist;
import music.app.my.music.ui.browser.baseListFragment;

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
    private QueueAdapter mAdapter;

    private ArrayList<Song> items;
    private DynamicListView mListView;
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
        View view = inflater.inflate(R.layout.qlist, container, false);

        mListView = (DynamicListView) view.findViewById(R.id.qlistview);

//        mListView.setOnItemLongClickListener(
//                new AdapterView.OnItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
//                                                   final int position, final long id) {
//                        log("Queue item long click ");
//                        mListView.startDragging(position);
//                        return true;
//                    }
//                }
//        );

        qListener.qFragCreated();


        return view;
    }

    @Override
    public void updateAdapter(){
        if(items == null) {
            items = new ArrayList<>();
        }

        mAdapter = new QueueAdapter(items, (QueueListener) getActivity());

        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(mAdapter);

       // mListView.insert(0, myItem); // myItem is of the type the adapter represents.

    //    animationAdapter.setAbsListView(mListView);
  //      mListView.setAdapter(animationAdapter);
        mListView.enableDragAndDrop();
        mListView.setDraggableManager(new TouchViewDraggableManager(R.id.content));

        SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(animationAdapter, getContext(),
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            mListener.onItemSwiped(items.get(position), position);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );


        swipeUndoAdapter.setAbsListView(mListView);
        mListView.setAdapter(swipeUndoAdapter);
        mListView.enableSimpleSwipeUndo();


        ((QueueAdapter)  mAdapter).setCurrent(current);
        mListView.setSelection(current) ;
        // mListView.setAdapter(mAdapter);

        log("Updating adapter");
        mAdapter.notifyDataSetChanged();

        log("items:" + items.size());
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


    private  int current = 0;

    public void setQList(plist p){
        log("Queue fragment updating list");

        items =   p.getArray();
        current = p.getIndex();
        updateAdapter();
    }

}
