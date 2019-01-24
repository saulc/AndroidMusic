package music.app.my.music.ui.browser;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import music.app.my.music.R;
import music.app.my.music.adapters.SongAdapter;
import music.app.my.music.types.Song;

/**
 * A simple {@link SongFragment} subclass.
 * Use the {@link BubbleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BubbleFragment extends SongFragment {

    private  final String TAG = getClass().getSimpleName();

    private void log(String s){
        Log.d(TAG, s);
    }

    public BubbleFragment() {
        // Required empty public constructor
    }

    public static BubbleFragment newInstance() {
        BubbleFragment fragment = new BubbleFragment();
        Bundle b = new Bundle();
        b.putString("SFTYPE", SF_TYPE.BUBBLE.toString());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    private FloatingActionButton bubbleButton;
    private RelativeLayout bubbleFrame;
    private ArrayList<Song> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        log("Bubble frag view created");
        View view = inflater.inflate(R.layout.fragment_bubble, container, false);

        bubbleFrame = view.findViewById(R.id.bubbleframe);

        bubbleButton = view.findViewById(R.id.bubble);
        bubbleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("Bubble? oh kay.");
                makeBubbles();
            }
        });

        mhandler = new Handler();


        mListener.onBubblesReady();
        return view;
    }




    private Runnable updateBubbles = new Runnable() {
        @Override
        public void run() {
            //
            moveBubbles();
            if(bubbleFrame.getChildAt(3).getY() < 60)
            mhandler.postDelayed(updateBubbles, 250);
        }
    };

    @Override
    public void updateAdapter(){
        log("Updating Bubble 'adapter' ");
        if(items == null)
            items = new ArrayList<>();

        String s = items.size() + " song";
        log("items:" + s );

        makeBubbles();
      //  mhandler.postDelayed(updateBubbles, 1000);

    }

    private  void moveBubbles(){
        for(int i=0; i<items.size(); i++){
            bubbleFrame.getChildAt(i).animate().translationYBy(80f);
        }
//        log( bubbleFrame.getChildAt(3).getX() + " " + bubbleFrame.getChildAt(3).getY()
//                + " " + bubbleFrame.getChildAt(3).getTranslationY() );


    }

    private void expandBubble(int i){
        log("Expand Bubble: " + i  + " items: " + items.size());
        if(i < items.size()) {
                String s =items.get(i).getTitle() + " "
                        + items.get(i).getArtist() + " : "
                        +  items.get(i).getAlbum();

               //FloatingActionButton f = (FloatingActionButton) bubbleFrame.getChildAt(i);



        }

    }


    private void popBubble(int i){
        log("Popbubble: " + i  + " items: " + items.size());
        if(i < items.size()){
            String s =items.get(i).getTitle() + " "
                    + items.get(i).getArtist() + " : "
                    +  items.get(i).getAlbum();

            log("Pop! "+s);
            mListener.onSongClicked(items.get(i));
        }
    }

    private void makeBubbles(){
        if(items.size() > 0){
//            if(bubbleFrame.getChildCount() > 2)
                bubbleFrame.removeAllViews();
//            bubbleButton.animate().translationY(100);

            int s = items.size();
            int space = 80;
            int width = 470;
            int icon = 60; //icon size

            for(int i =0; i< items.size(); i++){
                //FloatingActionButton fab = new FloatingActionButton(getContext());
                ImageButton fab = new ImageButton(getContext());
                final int ii = i;
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        log("Fab clicked: " + ii);
                        expandBubble(ii);
                    }
                });
                fab.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        log("Bubble long Clicked: " + ii);
                        popBubble(ii);
                        return  true;
                    }
                });

                if (items.get(ii).getAlbumId() != null) {
                    String p = findAlbumArt(items.get(ii).getAlbumId());

                    log("Now fragment updating albumart: " + p);
                    Drawable d = Drawable.createFromPath(p);
                    if (d != null) {
                        items.get(ii).setAlbumArt(p);
                        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                        d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, icon, icon, true));

                        fab.setImageDrawable(d);
                    } else fab.setImageResource(R.drawable.android_icon32128);
                }
                fab.setTag(i);
                bubbleFrame.addView(fab, i);

                int x = i*space;
                fab.animate().translationX(x%width + 40f);
                fab.animate().translationY( (int)(x/width)*space );
            }
        }

    }

    @Override
    public void songLoadedFinished(ArrayList<Song> songs) {
        log("SONGS Loaded " + songs.size() + " song(s)");
        items = songs;
        updateAdapter();
    }

            private  String findAlbumArt(String albumid){
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
                int base = cur.getInt(0);
                String id = cur.getString(1);
                Log.d("Music service", "--->>>>>base: " + base + " " + id);

                return id;
            }


}
