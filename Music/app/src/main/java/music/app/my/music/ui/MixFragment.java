package music.app.my.music.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.MixAdapter;
import music.app.my.music.adapters.QueueAdapter;
import music.app.my.music.helpers.MixListener;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.player.myPlayer;
import music.app.my.music.types.Song;

/**
 * Created by saul on 3/15/17.
 *
 * Control for music playback.
 * collapseable floating buttons?
 */

public class MixFragment extends ControlFragment {


//    public interface ControlFragmentListener{
//        void playPausePressed();
//        void nextPressed();
//        void prevPressed();
//        void readyForInfo();
//        void seekBarChanged(int progress);
//    }
    private MixListener mListener;
    private TextView mixText;
    private ListView mixList;

    private ArrayList<String> items;
    private MixAdapter mAdapter;
    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }



    public static MixFragment newInstance() {
        MixFragment fragment = new MixFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Mix fragment created");
        //   iniMsHelper();
        mListener = (MixListener) getActivity();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix, container, false);
        log("Mix fragment view created");

            mixList = view.findViewById(R.id.mixList);
            mixText = view.findViewById(R.id.mixText);

            mixText.setText("Mixxer");


            updateAdapter();

        mListener.onMixViewCreated();
        return view;
    }


    @Override
    public void updateProgressBar(MusicPlayer player) {

        log("MIX fragment updating");
        if(player != null){
           ArrayList<myPlayer>  p = player.getmPlayers();

            log("MIX fragment Players: "+ p.size());
           String info = "";

           items = new ArrayList<>();

            try {
                mixText.setText("Mixxer Player info: " + p.get(0).getAudioSessionId());

                for( int i = 0; i<p.size(); i++){

                    info =   " id: " + p.get(i).getAudioSessionId()
                            + " Playing: " + p.get(i).isPlaying()
                            + " Paused: " + p.get(i).isPaused()
                            + " Dur: " + p.get(i).getDuration()
                            + " pos: " + p.get(i).getCurrentPosition();

                    items.add(info);
                    log(i + info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            log("MIX fragment items: "+ items.size());
            updateAdapter();

        }
    }


    public void updateSongInfo(Song s){
        log("Mix fragment updating song info.");
        //avoid doing this every second, it doesn't change

    }

    public void setPlayPause(Boolean isPlaying){

    }

    public  void updateAdapter(){
        if(items == null) {
            items = new ArrayList<>();
        }

        mAdapter = new MixAdapter(items, (MixListener) getActivity());
        mixList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
}
