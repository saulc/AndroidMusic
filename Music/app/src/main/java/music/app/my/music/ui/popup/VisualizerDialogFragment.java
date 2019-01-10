package music.app.my.music.ui.popup;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.player.MusicPlayer;

public  class VisualizerDialogFragment extends DialogFragment implements Visualizer.OnDataCaptureListener,
        TextureView.SurfaceTextureListener  {

    public static VisualizerDialogFragment newInstance() {
        VisualizerDialogFragment fragment = new VisualizerDialogFragment();
//        Bundle b = new Bundle();
//        b.putStringArrayList("PRESETS", presents);
//        fragment.setArguments(b);
        return fragment;
    }

    public VisualizerDialogFragment(){

    }
    private  final String TAG = getClass().getSimpleName();

    private void log(String s){
        Log.d(TAG, s);
    }

    private int aid;
    private  boolean enabled = true;

    private MusicPlayer player;
    private Visualizer vis = null;

    private Camera mCamera;
    private TextureView mTextureView;


    public void setPlayer(MusicPlayer player){
        log("Visualizer Player set");
        this.player = player;

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) startVis();
        else vis.setEnabled(enabled);
    }

    private void startVis(){
        log("Starting Vis");
        if(vis == null) vis = new Visualizer(player.getAID());

        log("Capture size: " + vis.getCaptureSize());
        log("Capture rate: " + vis.getSamplingRate());
        vis.setDataCaptureListener(this, 10, true, true);

        vis.setEnabled(enabled);
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

        log("Visualizer Data capture Wave:");
        for(int j=0; j<bytes.length; j++)
            log(j + " Wave: " + bytes[j]);


    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        log("Visualizer Data capture FFT:");
        for(int j=0; j<bytes.length; j++)
            log(j + " FFT: " + bytes[j]);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.visualizer_dialog, container, false);


        mTextureView = v.findViewById(R.id.visTexture);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setAlpha(.5f);


        ((DrawerActivity) getActivity()).visualizerCreated();
        return v;

    }



    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        log("SurfaceTexture Available!");
        final Drawable picture = getActivity().getResources().getDrawable(R.drawable.android_robot_icon_2);
        Canvas canvas = mTextureView.lockCanvas();
        picture.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        picture.draw(canvas);
        Paint p = new Paint();
        canvas.drawCircle(300,200, 200f, p);
        mTextureView.unlockCanvasAndPost(canvas);


//        mCamera = Camera.open();
//
//        try {
//
//
//            mCamera.setDisplayOrientation(90);
//            mCamera.setPreviewTexture(surface);
//            mCamera.startPreview();
//        } catch (IOException ioe) {
//            // Something bad happened
//        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        mCamera.stopPreview();
//        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        builder.setTitle("Now Playing: ");
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                ((DrawerActivity) getActivity()).visualizerClosed();
//                dismiss();
//            }
//        });
//        ((DrawerActivity) getActivity()).visualizerCreated();
//        return builder.create();
//    }
}