package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;

public class PlaceholderFragment extends QueueFragment{


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceholderFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlaceholderFragment newInstance() {
        PlaceholderFragment fragment = new PlaceholderFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  log("Queue placeholder created");

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

                recyclerView.setLayoutManager(new LinearLayoutManager(context));

        }
        ( (DrawerActivity) getActivity()).placeholderCreated();

        return view;
    }


}
