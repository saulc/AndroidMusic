package music.app.my.music;

import android.content.Context;
import android.os.Handler;
import android.service.dreams.DreamService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class Dream extends DreamService {
    private void log(String s){
        Log.d(getClass().getSimpleName(), s);
    }


    public Dream() {
    }

    private TextView dreamText;
    private Handler mhandler;
    private int delay = 1000; //every second?
    private int width, height;

    private Runnable updateDream = new Runnable() {
        @Override
        public void run() {
            log("Update Dream.");
            updateTime();
            mhandler.postDelayed(updateDream, delay);

        }
    };

    private  float v =  10f;  //update to v = height/10f; onDreamStarted()
    private float y = 0;
    private int pad = height/3;
    private void updateTime(){
        Date d = new Date();
        //Calendar c = Calendar.getInstance();
        String s =  d.toString();
        dreamText.setText(s);

        //a simple bounce animation.
        y += v;
        log("Y: " + y + "  Height: " + height);
        if(y+pad > height || y < 0 - pad){
            v *= -1;
        }
        dreamText.animate().translationY( y );
    }

    @Override
    public void onDreamingStarted(){
        //Your dream has started, so you should begin animations or other behaviors here.
        //try to get window size for bounds

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        v = Math.round(height/10f);
        log("Dreaming window size: " + width + " h: " + height);

        mhandler = new Handler();
        mhandler.postDelayed(updateDream, 2000);

    }

    @Override
    public void onDreamingStopped(){
        mhandler.removeCallbacks(updateDream);

    }


    //Day dream turned on! ;)  zzzz
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.dream);
        dreamText = findViewById(R.id.dreamText);

    }
}
