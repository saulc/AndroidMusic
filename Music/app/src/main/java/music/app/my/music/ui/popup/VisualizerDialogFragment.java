package music.app.my.music.ui.popup;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;

public  class  VisualizerDialogFragment extends Fragment implements Visualizer.OnDataCaptureListener {

    public static VisualizerDialogFragment newInstance() {
        Log.d("Visualizer", "Creating visualizer fragment...");
        return new VisualizerDialogFragment();
    }

    public static VisualizerDialogFragment newInstance(int aid) {
        Log.d("Visualizer", "Creating visualizer fragment with aid: " + aid);
        VisualizerDialogFragment fragment = new VisualizerDialogFragment();
        Bundle b = new Bundle();
        b.putInt("Aid", aid);
        fragment.setArguments(b);
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


//    public VisualizerDialogFragment(){ }


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
        log("Visualizer created.");
        if (getArguments() != null) {
            aid = getArguments().getInt("Aid");
            log("Visualizer aid: " + aid);

            callBack.visualizerCreated();
        }
    }
    private DrawerActivity callBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        log("Visualizer attached.");
        callBack = (DrawerActivity) context;
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
    private Visualizer vis = null;

    private ImageView iv, iv2;
    public void setWidth(int w){ width = w; }
    public int getWidth(){ return width; }
    public void setHeight(int h){ height = h; }
    public int getHeight(){ return height; }
    private int width, height;
    private Bitmap oldbit;
    private  int mode = 4, modes = 7;

    public void clicked(){
        if(vis == null) startup();

        if(++mode >= modes) mode = 0;
        log("VIs clicked. mode: " + mode);
    }

    public void startup(){
        log("Visualizer starting.");
        callBack.visualizerCreated();
    }


    public void setEnabled(boolean enabled) {


        vis.setEnabled(enabled);
    }

    public void setAid(int id){
        log("Set Vis aid: " + id + " aid: " + aid);
        if(iv == null) return;

        if(id != aid){
            if (vis != null)
                setEnabled(false);
            aid = id;
            vis = new Visualizer(aid);
            if(vis.getEnabled()) setEnabled(false);
            iniVis();

            setEnabled(true);

         }
    }

    public void stop(){

        log("Visualizer stopped. " + vis);
        if(vis == null) return;
        try {
            vis.setEnabled(false);
            vis.release();
            vis = null;

            iv.setImageResource(R.drawable.gradientbox);
            iv = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
//ran out of memeory.
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
                case 6:
                    p.setAlpha(150);
//                    float r = 220.0f;
//                    cc.rotate(j*15);
//                    cc.drawRect(width / 2 + r, height / 2 + r,width / 2 - r, height / 2 - r, p);
                    cc.drawRect(j*space, 0, j*space*2, j*space, p);
                    break;

                case 5:
                    cc.drawLine(width / 2 + x * amp, height / 2 + y * amp,
                            width / 2 + x * w * af, height / 2 + y * w * af, p);
                    break;

                case 4:
                    p.setAlpha(150);
                    cc.drawCircle(j * space, height / 2, w, p);
//                    cc.drawRect(j*space, 0, j*space*2, j*space, p);
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
        if(oldbit != null) {
            cc.drawBitmap(oldbit, 0, 0, p);
            oldbit = rc;
        }
        iv.setImageBitmap(rc);

    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

       // log("Visualizer Data capture Wave: " + bytes.length);
        if(iv != null)
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