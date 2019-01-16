package music.app.my.music;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;


public class SecretFragment extends Fragment implements TextureView.SurfaceTextureListener {
    private  final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    private Camera mCamera;
    private SecretListener mListener;
    private TextureView texture;



    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            releaseCamera();
            mCamera = Camera.open();
            qOpened = (mCamera != null);
            log("Camera opened: " + qOpened);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
//
//    private void iniStark(){
//        log("Initializing Stark Mode.");
//        texture.setTag("cam");
//        texture.setSurfaceTextureListener( this );
//        log("Initialization complete.");
//        // mTextureView.setAlpha(.5f);
//
//    }
//    public void setTexture(TextureView t){
//        if(t == null) return;
//
//            texture = t;
//
//        log("Initializing Stark Mode.");
//        t.setTag("cam");
////        t.setSurfaceTextureListener( this );
//        log("Initialization complete.");
//
//    }
    public SecretFragment() {
        // Required empty public constructor
    }


    public static SecretFragment newInstance() {
        SecretFragment fragment = new SecretFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new TextureView(getContext());

        texture = (TextureView) view;
        texture.setTag("CAM");
        texture.setSurfaceTextureListener(this);

        return view;
    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener.secretFragmentCreated();
    }

    @Override
    public void onDestroy(){
        log("We're done here.");
        mListener.secretFragmentDestroyed();
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SecretListener) {
            mListener = (SecretListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SecretListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SecretListener {
        void secretFragmentCreated();
        void secretFragmentDestroyed();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int i, int i1) {
        log("Camera surface Available!");

        // safeCameraOpen();
        mCamera = Camera.open();
        try {
            mCamera.setDisplayOrientation(90);
            //setCameraDisplayOrientation(1, mCamera);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mCamera.stopPreview();
       mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


}
