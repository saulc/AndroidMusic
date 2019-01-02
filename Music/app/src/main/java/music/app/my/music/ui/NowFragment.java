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
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import music.app.my.music.DrawerActivity;
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
    private TextSwitcher line1, line2, line3;
    private  ImageSwitcher icon;

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
            final Context context = view.getContext();

            icon = (ImageSwitcher) view.findViewById(R.id.currentIcon);
            line1 = (TextSwitcher) view.findViewById(R.id.currentText);
            line2 = (TextSwitcher) view.findViewById(R.id.currentSubText);
            line3 = (TextSwitcher) view.findViewById(R.id.currentSubText2);
        icon.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView t = new ImageView(context);
                t.setImageResource(R.drawable.android_robot_icon_2);


                //image_view.requestLayout()
                return t;
            }
        });
        //vertical animation
        icon.setInAnimation(context, R.anim.anim_in);
        icon.setOutAnimation(context, R.anim.anim_out);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.nowIconClicked(); //show q only?
            }
        });
        //swipe
//        icon.setInAnimation(context, android.R.anim.slide_in_left);
//        icon.setOutAnimation(context, android.R.anim.slide_out_right);


            line1.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView t = new TextView(context);
                    t.setTextSize(24);
                    t.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
                    t.setGravity(Gravity.CENTER);
                    return t;
                }
            });
            line2.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView t = new TextView(context);
                    t.setTextSize(20);
                    t.setGravity(Gravity.CENTER);
                    t.setTypeface(Typeface.create("casual", Typeface.NORMAL));
                   // t.setTypeface(Typeface.MONOSPACE);
                    return t;
                }
            });
            line3.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView t = new TextView(context);
                    t.setGravity(Gravity.CENTER);
                    t.setTypeface(Typeface.create("casual", Typeface.BOLD));
                    return t;
                }
            });

            line1.setInAnimation(context, android.R.anim.slide_in_left);
            line1.setOutAnimation(context, android.R.anim.slide_out_right);
            line2.setInAnimation(context, android.R.anim.slide_in_left);
            line2.setOutAnimation(context, android.R.anim.slide_out_right);
            line3.setInAnimation(context, android.R.anim.slide_in_left);
            line3.setOutAnimation(context, android.R.anim.slide_out_right);


            line1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.lineClicked(1);
                }
            });
            line2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.lineClicked(2);
                }
            });
            line3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.lineClicked(3);
                }
            });

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
        if (s.getAlbumId() != null) {
            String p = findAlbumArt(s.getAlbumId());

            log("Now fragment updating albumart: " + p);
            Drawable d = Drawable.createFromPath(p);
            if(d != null) {
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                // Scale it to 50 x 50

                d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 512, 512, true));

                icon.setImageDrawable(d);
            }else icon.setImageResource(R.drawable.android_robot_icon_2);
        }
    }

    public void setPlayPause(Boolean isPlaying){
        if(isPlaying)
        pp.setImageResource(android.R.drawable.ic_media_pause);
     else  pp.setImageResource(android.R.drawable.ic_media_play);
    }
}
