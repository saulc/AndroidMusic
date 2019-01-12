package music.app.my.music.ui.popup;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;

public  class VisualizerDialogFragment extends Fragment implements Visualizer.OnDataCaptureListener {

    public static VisualizerDialogFragment newInstance() {
        VisualizerDialogFragment fragment = new VisualizerDialogFragment();
        return fragment;
    }

    public static VisualizerDialogFragment newInstance(int width, int height) {
        VisualizerDialogFragment fragment = new VisualizerDialogFragment();
        Bundle b = new Bundle();
        b.putInt("Width", width);
        b.putInt("Height", height);
        fragment.setArguments(b);
        return fragment;
    }


    public VisualizerDialogFragment(){ }


    @Override
    public void onStop(){

        log("Visualizer stopped.");
//        try {
//            vis.setEnabled(false);
//            vis.release();
//            vis = null;
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }


        super.onStop();

    }

    /*
    Fragment stuff ----------------.....................--------------------
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DrawerActivity) getActivity()).visualizerCreated();
        if (getArguments() != null) {

        }
    }

    public void setImageView(ImageView i){ iv = i; }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.visualizer_dialog, container, false);
//
//
//        iv = v.findViewById(R.id.visImage);
//        //iv.setAlpha(.5f);
//        iv2 = v.findViewById(R.id.visImage2); //not  used....
//
//        iv2.setAlpha(.5f);
//        iv2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clicked();
//            }
//        });
//
//        iv2.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                longClick();
//                return true;
//            }
//        });
//
//        ((DrawerActivity) getActivity()).visualizerCreated();
//        return v;
//
//    }


    private  void longClick(){
        log("Visualizer long clicked");

        ((DrawerActivity) getActivity()).visualizerLongClicked();
    }





    private  final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    private int aid;
    private  boolean enabled = true;
    private Visualizer vis = null;

    private ImageView iv, iv2;
    public void setWidth(int w){ width = w; }
    public int getWidth(){ return width; }
    public void setHeight(int h){ height = h; }
    public int getHeight(){ return height; }
    private int width, height;
    private Bitmap oldbit;
    private  int mode = 0, modes = 6;

    public void clicked(){
        if(++mode >= modes) mode = 0;
        log("VIs clicked. mode: " + mode);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        vis.setEnabled(enabled);
    }

    public void setAid(int id){
        log("Set Vis aid: " + id + " aid: " + aid);
        if(id != aid){
            if (vis != null)
            setEnabled(false);
            aid = id;
            vis = new Visualizer(aid);
            if(enabled) setEnabled(false);
            iniVis();

            setEnabled(true);

         }
    }


    private void iniVis() {
        log("ini Vis");

      //  log("Capture size: " + Visualizer.getCaptureSizeRange());

        vis.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        vis.setDataCaptureListener(this,
                Visualizer.getMaxCaptureRate() / 8, true, false);
        log("Capture size: " + vis.getCaptureSize());
        log("Capture rate: " + vis.getSamplingRate());

        // vis.setDataCaptureListener(this, vis., true, true);
        //vis.setEnabled(enabled);


        Bundle b = getArguments();
        if (b != null) {
            width = b.getInt("Width");
            height = b.getInt("Height");
        } else {
        width = 600;
        height = 600;
        }

        log("Starting Vis! width: " + width + " height: " + height);
    }



    private void updateIV(byte[] waves){
     //   log("Updating waves...");


       // log("Updating waves..." + width + " " + height);

        final Bitmap rc = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas((rc));
        Paint p = new Paint();
      //  p.setColor(Color.TRANSPARENT);
        //cc.drawColor(Color.TRANSPARENT);
        cc.drawARGB(0, 0, 0, 0);
        p.setStrokeWidth(3.0f);
        p.setColor(Color.GREEN);
        float space = 4.5f;

        float angle =  1.4f;  // 180 /128 + overlap?
        float amp = 90f; //inner space
        int af = 2; //length

        for(int j=0; j<waves.length; j++) {
            //log(j + " Wave: " + waves[j]);
            float w = (float) waves[j];
            float ww = Math.abs(w);
            if(ww > 110f) p.setColor(Color.RED);
            else if(ww > 88f) p.setColor(Color.MAGENTA);
            else if(ww > 66f) p.setColor(Color.CYAN);
            else if(ww > 48f) p.setColor(Color.BLUE);
            else if(ww > 20f) p.setColor(Color.YELLOW);
            else p.setColor(Color.GREEN);
//
            float x =  (float) Math.cos(angle*j);
            float y = (float) Math.sin(angle*j);
           // log("x: " + x +" y: " + y);

            p.setAlpha(255);

            switch (mode) {
                case 5:
                    cc.drawLine(width / 2 + x * amp, height / 2 + y * amp,
                            width / 2 + x * w * af, height / 2 + y * w * af, p);
                    break;

                case 4:
                    p.setAlpha(150);
                    cc.drawCircle(j * space, height / 2, w, p);

//                    p.setColor(Color.WHITE);
//                    cc.drawCircle( j*space, height / 2  , w-5 , p);
                    break;

                case 3:
                    cc.drawLine(width / 2 + x * amp, height / 2 + y * amp,
                            width / 2 + x * w * af, height / 2 + y * w * af, p);
                case 2:
                    cc.drawCircle(width / 2 + x * w * af, height / 2 + y * w * af, w / 3, p);
                    p.setColor(Color.BLACK);
                    cc.drawCircle(width / 2 + x * w * af, height / 2 + y * w * af, w / 3 - 3, p);
                    break;

                case 1:
                    cc.drawLine(j * space, height / 2, j * space, height / 2 + w, p);
                    break;


                case 0:
                    cc.drawLine(width / 2 * (1 + x), height / 2 * (1 + y),
                            width / 2 + x * w * af, height / 2 + y * w * af, p);
                    break;
            }

        }
//        iv2.setImageBitmap(oldbit);
//        oldbit = rc;
        p.setAlpha(150);
        if(oldbit != null)
        cc.drawBitmap(oldbit, 0, 0, p);
        oldbit = rc;
        iv.setImageBitmap(rc);

    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

       // log("Visualizer Data capture Wave: " + bytes.length);
        updateIV(bytes);


    }

    private void updateFTT(byte[] ftt){
        log("Updating ftt...");

//        width = getDialog().getWindow().getAttributes().width;
//        height = getDialog().getWindow().getAttributes().height;
//
        width = 600;
        height = 100;

        // log("Updating waves..." + width + " " + height);

        final Bitmap rc = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas cc = new Canvas((rc));
        Paint p = new Paint();
        p.setColor(Color.CYAN);
        int space = 4; //width / ftt.length;
        for(int j=0; j<ftt.length; j++) {
            //log(j + " Wave: " + waves[j]);

            float w = (float) ftt[j];
            cc.drawLine(j*space, 100f, j*space, 100f-w, p);

        }
        iv2.setImageBitmap(rc);

    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        log("Visualizer Data capture FFT:");
        updateFTT(bytes);
//        for(int j=0; j<bytes.length; j++)
//            log(j + " FFT: " + bytes[j]);

    }



}