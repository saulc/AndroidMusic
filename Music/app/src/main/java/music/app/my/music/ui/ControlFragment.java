package music.app.my.music.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.types.Artist;
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
        void readyForInfo();
        void seekBarChanged(int progress);

        void controlIconClicked();

        void nowIconClicked(boolean isSwipe, boolean close);
        void nowIconLongClicked();

        void lineClicked(int i);

        void onNowViewCreated();

        void nowIconDoubleClicked();

        void onNowViewDestroyed();

        boolean onShuffleClicked();

        boolean onShuffleLongClicked();

        int onRepeatClicked();

        void onVolChanged(int i);

        int getMaxVol();

        int getVol();

        void onArtistLongClick(Artist a);

        void playPauseLongClicked();
    }
    private ControlFragmentListener mListener;
    private SeekBar sbar;
    private ImageButton pp;
    private TextView line1, line2;
    private  ImageView icon;

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
        log("Control fragment created");
        //   iniMsHelper();
        mListener = (ControlFragmentListener) getActivity();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_layout, container, false);
        log("Control fragment view created");
            Context context = view.getContext();

            
            icon = (ImageView) view.findViewById(R.id.currentIcon);
            
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.controlIconClicked();
                }
            });
            line1 = (TextView) view.findViewById(R.id.currentText);
            line2 = (TextView) view.findViewById(R.id.currentSubText);
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
    public  String findAlbumArt(String albumid){
        String[] cols = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM_ART
        };
        ContentValues values = new ContentValues();
        if(getContext()==null) return null;
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String selection =  MediaStore.Audio.Albums._ID + "=?";
        String[] arg = { albumid };
        Cursor cur = resolver.query(uri, cols, selection, arg, null);
        cur.moveToFirst();
        int id = cur.getInt(0);
        String base = cur.getString(1);
        Log.d("Now Fragment", "Found album art: " + id + " " + base);

        return base;
    }


    private boolean infoset = false;

    public void updateProgressBar(MusicPlayer player){
        if(sbar != null)
            if(player.isPlaying()) {
                sbar.setProgress(player.getProgress());

                if (infoset) return;
                else infoset = true;
                log("Control fragment updating song info.");
                //avoid doing this every second, it doesn't change
                Song s = player.getCurrentSong();

                line1.setText(s.getTitle());
                line2.setText(s.getArtist() + " : " + s.getAlbum());
                if (s.getAlbumId() != null) {
                    String p = findAlbumArt(s.getAlbumId());

                    log("Now fragment updating albumart: " + p);
                    Drawable d = Drawable.createFromPath(p);
                    if (d != null) {
//                        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
//                        // Scale it to 50 x 50
//
//                        d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 512, 512, true));

                        icon.setImageDrawable(d);
                    } else icon.setImageResource(R.drawable.android_icon32a5);
                }
            }

       // log("Control fragment updating");
    }

    public void updateSongInfo(Song s){
        log("Now fragment updating song info.");
        //avoid doing this every second, it doesn't change


        line1.setText(s.getTitle());
        line2.setText(s.getArtist() );
      //  line3.setText(s.getAlbum() );
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
