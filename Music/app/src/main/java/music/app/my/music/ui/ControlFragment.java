package music.app.my.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.types.Playlist;
import music.app.my.music.types.Song;

/**
 * Created by saul on 3/15/17.
 *
 * Control for music playback.
 * collapseable floating buttons?
 */

public class ControlFragment extends Fragment {


    public interface ControlFragmentListener{
        void playPausePressed();
        void nextPressed();
        void prevPressed();
    }
    private ControlFragmentListener mListener;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }



    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Control fragment qFragCreated");
        //   iniMsHelper();
        mListener = (ControlFragmentListener) getActivity();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_layout, container, false);

            Context context = view.getContext();

           ImageButton pp = (ImageButton) view.findViewById(R.id.playpauseButton);
            pp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.playPausePressed();
                    }
                }
            });
        ImageButton nb = (ImageButton) view.findViewById(R.id.nextButton);
        nb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.nextPressed();
                }
            }
        });
        ImageButton pb = (ImageButton) view.findViewById(R.id.prevButton);
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.prevPressed();
                }
            }
        });

        return view;
    }




}
