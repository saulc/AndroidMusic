package music.app.my.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.service.dreams.DreamService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import music.app.my.music.helpers.FabDoubleTapGS;
import music.app.my.music.player.MusicService;

public class Dream extends DreamService implements FabDoubleTapGS.DoubleTapListener {
    private void log(String s){
        Log.d(getClass().getSimpleName(), s);
    }

    public Dream() {
    }

    private ViewGroup bg;
    private String msg;
    private TextView dreamText, subText;
    private Handler mhandler;
    private int delay = 1000; //every second?
    private int width, height;
    private Intent snoozeIntent, wakeIntent;
    public static final String ACTION_UPDATE_MEDIA_INFO = "music.app.my.music.dream.ACTION_UPDATE_INFO";
    //public static final String ACTION_BLANK = "com.app.m6.action.ACTION_BLANK";

    private Runnable updateDream = new Runnable() {
        @Override
        public void run() {
            log("Update Dream.");
            updateTime();
            mhandler.postDelayed(updateDream, delay);

        }
    };

    @Override
    public void onDoubleTap() {
        log("Double Clicked!! waking up.");

        finish();
    }

   private int getColor(){
        switch (cc){
            case 0: return Color.BLACK;
            case 1: return Color.RED;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            case 4: return Color.BLUE;
            case 5: return Color.CYAN;
            case 6: return Color.MAGENTA;
            case 7: return Color.TRANSPARENT;
            case 8: return Color.WHITE;
        }
        return Color.argb(100, 100, 100, 0);
   }
    private int mcolors = 9, cc;

    private void clicked(){
        log("Clicked!!");

        if(++cc > mcolors) cc = 0;

        int color = getColor();
        bg.setBackgroundColor(color);
    }


    private void saveBG(){
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.last_dream), cc);
        editor.commit();

    }
        private void getLastBG(){
        log("Setting default Theme.");
            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int defaultValue = 0; //black
        cc = sharedPref.getInt(getString(R.string.saved_theme), defaultValue);


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("Got command");
        String action = intent.getAction();
        if (action.equals(ACTION_UPDATE_MEDIA_INFO)){
            log("GOt update intent.!");
            Bundle b = intent.getExtras();
            String t = b.getString("TITLE");
            String a = b.getString("ARTIST");
            String ab = b.getString("ALBUM");

            msg = "Now playing: " + t + System.lineSeparator()
                     + a + " : " + ab;
            if(b == null) msg = "";
            log(msg);
            //if(subText != null) subText.setText(msg);

        } //else if( ... )

        return START_NOT_STICKY;
    }


    @Override
    public void onDreamingStarted(){
        //Your dream has started, so you should begin animations or other behaviors here.
        //try to get window size for bounds

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        //v = Math.round(height/10f);
        log("Dreaming window size: " + width + " h: " + height);

        getLastBG();
        mhandler = new Handler();
        mhandler.postDelayed(updateDream, 2000);

        //sent the intent to music service, it will send back data intent
        snoozeIntent = new Intent(this, MusicService.class);
        snoozeIntent.setAction(MusicService.ACTION_SNOOZE);
        startService(snoozeIntent);

    }

    @Override
    public void onDreamingStopped(){
        saveBG();
        mhandler.removeCallbacks(updateDream);
        wakeIntent = new Intent(this, MusicService.class);
        wakeIntent.setAction(MusicService.ACTION_WAKE);
        startService(snoozeIntent);
    }


    //Day dream turned on! ;)  zzzz
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch if false
        setInteractive(true);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.dream);
        bg = findViewById(R.id.dreamBG);

        msg = "";
        dreamText = findViewById(R.id.dreamText);
        subText = findViewById(R.id.dreamSubText);
        dreamText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked();
            }
        });

        FabDoubleTapGS dt =  new FabDoubleTapGS();
        dt.setDoubleTapListener(this);
        final GestureDetector gestureDetector = new GestureDetector( dt );
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    log("Touch event!!");
                    return true;
                }
                return false;
            }
        };
        dreamText.setOnTouchListener(gestureListener);

    }

    //-------------------------------------------------------------

    private void updateTime(){
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat(" h:");
        String s = dateFormat.format(d) + System.lineSeparator();
        dateFormat = new SimpleDateFormat("mm");
        s += dateFormat.format(d) + System.lineSeparator();
        dateFormat = new SimpleDateFormat("ssa");
        s += dateFormat.format(d);

        dateFormat = new SimpleDateFormat("EEEE MMMM d yyyy ");
        String s2 = dateFormat.format(d);

        dreamText.setText(s.toLowerCase());
        subText.setText(s2 + System.lineSeparator() +  msg);

        updateAnimation();
    }


    private  float v =  1.0f, vx = 10f;  //update to v = height/10f; onDreamStarted()
    private float y = 0;
    private float x = 0;
    private int pad = (int)(height*.8);

    private  void updateAnimation(){
        //a simple bounce animation.
        y += v;
        log("Y: " + y + "  Height: " + height);
        if(y > pad || y < 0 - pad){
            v *= -1f;
        }
        dreamText.animate().translationY( y );
        subText.animate().translationY( y );

        x += vx;
        if(x > width/2 || x < 0 ){
            vx *= -1f;
        }
        subText.animate().translationX(x);


    }


}