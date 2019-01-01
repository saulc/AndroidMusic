package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;

public class PlaceholderFragment extends baseListFragment{


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceholderFragment() {
    }

    private String arg = "";
    private  TextView textView;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlaceholderFragment newInstance(String a) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle b = new Bundle();
        b.putString("ARG", a);
        fragment.setArguments(b);
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

        textView = (TextView) view.findViewById(R.id.textView2);
        arg = getArguments().getString("ARG");
        textView.setText("Now Playing: " + arg);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

                recyclerView.setLayoutManager(new LinearLayoutManager(context));


        }
        ( (DrawerActivity) getActivity()).placeholderCreated();

        return view;
    }

    public void setText(String t){
        textView.setText(t);
    }

}
