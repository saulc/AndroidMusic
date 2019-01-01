package music.app.my.music.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import music.app.my.music.R;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.types.Song;

/**
 * Created by saul on 3/15/17.
 *
 * Control for music playback.
 * collapseable floating buttons?
 */

public class NowFragment extends ControlFragment {


//    public interface ControlFragmentListener{
//        void playPausePressed();
//        void nextPressed();
//        void prevPressed();
//        void readyForInfo();
//        void seekBarChanged(int progress);
//    }
    private ControlFragment.ControlFragmentListener mListener;
    private SeekBar sbar;
    private ImageButton pp;
    private TextView line1, line2, line3;
    private  ImageView icon;

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }



    public static NowFragment newInstance() {
        NowFragment fragment = new NowFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Now fragment created");
        //   iniMsHelper();
        mListener = (ControlFragmentListener) getActivity();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.now_layout, container, false);
        log("Now fragment view created");
            Context context = view.getContext();

            icon = (ImageView) view.findViewById(R.id.currentIcon);
            line1 = (TextView) view.findViewById(R.id.currentText);
            line2 = (TextView) view.findViewById(R.id.currentSubText);
            line3 = (TextView) view.findViewById(R.id.currentSubText2);
            sbar = (SeekBar) view.findViewById(R.id.seekBar);
            sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser)
                    mListener.seekBarChanged(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            pp = (ImageButton) view.findViewById(R.id.playpauseButton);
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


    private boolean infoset = false;

    public void updateInfo(MusicPlayer player) {
        if (sbar != null)
            if (player.isPlaying()) {
                sbar.setProgress(player.getProgress());

            }

       // log("Control fragment updating");
    }

    public void updateInfo(Song s){
        log("Now fragment updating song info.");
        //avoid doing this every second, it doesn't change


        line1.setText(s.getTitle());
        line2.setText(s.getArtist() );
        line3.setText(s.getAlbum() );
        if (s.getAlbumArt() != null) {
            Drawable d = Drawable.createFromPath(s.getAlbumArt());
            icon.setImageDrawable(d);
        }
    }

    public void setPlayPause(Boolean isPlaying){
        if(isPlaying)
        pp.setImageResource(android.R.drawable.ic_media_pause);
     else  pp.setImageResource(android.R.drawable.ic_media_play);
    }
}
