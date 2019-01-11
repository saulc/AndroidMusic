package music.app.my.music.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private ImageSwitcher icon;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;


    private final String TAG = getClass().getSimpleName();

    private void log(String s) {
        Log.d(TAG, s);
    }


    public static NowFragment newInstance(boolean mini) {
        NowFragment fragment = new NowFragment();

        Bundle b = new Bundle();
        b.putBoolean("ISMINI", mini);
        fragment.setArguments(b);
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
        final boolean isMini = getArguments().getBoolean("ISMINI");
        View view;
        if (isMini) view = inflater.inflate(R.layout.nowmini_layout, container, false);
        else view = inflater.inflate(R.layout.now_layout, container, false);

        //add android default trasitions to main layout changes.
        // //smooth movement on queue/miniplayer
        ((ViewGroup) view.findViewById(R.id.llnow)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        log("Now fragment view created; mini: " + isMini);
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


        // animation
//        icon.setInAnimation(context, R.anim.slidein_left);
//        icon.setOutAnimation(context, R.anim.slideout_right);

        icon.setInAnimation(getContext(), R.anim.slidein_up);
        icon.setOutAnimation(getContext(), R.anim.slideout_up);

        //mini player uses only simple click. for now.

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMini) mListener.controlIconClicked();
                    else mListener.nowIconClicked(true); //show q only?
                }
            });

            icon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.nowIconLongClicked();
                    return true;

                }
            });

        if (!isMini) {
            //swipe
            // Gesture detection
            gestureDetector = new GestureDetector(new MyGestureDetector());
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        log("Touch event!!");
                        return true;
                    }
                    return false;
                }
            };

            icon.setOnTouchListener(gestureListener);
        }

        line1.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView t = new TextView(context);
                t.setTextSize(24);
                if (isMini) t.setTextSize(14);
                t.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
                t.setGravity(Gravity.CENTER);
                t.setLines(1);
                return t;
            }
        });
        line2.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView t = new TextView(context);
                t.setTextSize(20);
                if (isMini) t.setTextSize(14);
                t.setGravity(Gravity.CENTER);
                t.setTypeface(Typeface.create("casual", Typeface.NORMAL));
                t.setLines(1);
                // t.setTypeface(Typeface.MONOSPACE);
                return t;
            }
        });
        line3.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView t = new TextView(context);
                t.setGravity(Gravity.CENTER);
                t.setTypeface(Typeface.create("casual", Typeface.BOLD));
                t.setLines(1);
                if (isMini) t.setTextSize(14);
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
                if (fromUser)
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
               playPressed();
            }
        });
        ImageButton nb = (ImageButton) view.findViewById(R.id.nextButton);
        nb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             nextPressed();
            }
        });
        ImageButton pb = (ImageButton) view.findViewById(R.id.prevButton);
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               prevPressed();
            }
        });



        mListener.onNowViewCreated();
        return view;
    }

    private  void iconClicked(boolean close){
        if(mListener != null) mListener.nowIconClicked(close);
    }
    private void playPressed(){
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.playPausePressed();
//            icon.setInAnimation(getContext(), R.anim.slidein_up);
//            icon.setOutAnimation(getContext(), R.anim.slideout_up);

        }
    }

    private void nextPressed(){
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.nextPressed();
//            icon.setInAnimation(getContext(), R.anim.slidein_left);
//            icon.setOutAnimation(getContext(), R.anim.slideout_right);

        }
    }
    private void prevPressed(){
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.prevPressed();
//            icon.setInAnimation(getContext(), R.anim.slidein_right);
//            icon.setOutAnimation(getContext(), R.anim.slideout_left);

        }
    }
    private boolean infoset = false;

    @Override
    public void updateProgressBar(MusicPlayer player) {
        if (sbar != null)
            if (player.isPlaying()) {
                sbar.setProgress(player.getProgress());

            }

        // log("Control fragment updating");
    }


    @Override
    public void updateSongInfo(Song s) {
        log("Now fragment updating song info.");
        //avoid doing this every second, it doesn't change

        log("Now Playing: "+ s.getTitle() + " : " + s.getArtist());

        line1.setText(s.getTitle());
        line2.setText(s.getArtist());
        line3.setText(s.getAlbum());
        if (s.getAlbumId() != null) {
            String p = findAlbumArt(s.getAlbumId());

            log("Now fragment updating albumart: " + p);

            Drawable d = Drawable.createFromPath(p);

            if (d != null) {
                log("Drawable created.");
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                // Scale it to 50 x 50
                log("Bitmap created.");
                d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 600, 600, true));
                log("Bitmap scaled");
                icon.setImageDrawable(d);
            } else icon.setImageResource(R.drawable.android_robot_icon_2);
        }
    }

    @Override
    public void setPlayPause(Boolean isPlaying) {
        if (isPlaying)
            pp.setImageResource(android.R.drawable.ic_media_pause);
        else pp.setImageResource(android.R.drawable.ic_media_play);
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        /**
         * Swipe min distance.
         */
        private static final int SWIPE_MIN_DISTANCE = 60;
        /**
         * Swipe max off path.
         */
        private static final int SWIPE_MAX_OFF_PATH = 250;
        /**
         * Swipe threshold velocity.
         */
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            log("single tap!");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            log("On down!");
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                log("on Fling Triggered");
//                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//                    return false;

                if ( e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                    log("Swiped up?!");
                    iconClicked(true);
                    return true;


                }else if(Math.abs( e1.getY() - e2.getY() ) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                    log("Swiped down!");
                   iconClicked(false);
                    return true;


                }else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    log("Swiped left!");
                    // right to left swipe
                       prevPressed();
                       return true;

                }else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    log("Swiped right!");
                    nextPressed();

                    return true;
                }

            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

}