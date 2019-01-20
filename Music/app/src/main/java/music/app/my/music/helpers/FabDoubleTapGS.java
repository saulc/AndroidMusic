package music.app.my.music.helpers;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class FabDoubleTapGS extends GestureDetector.SimpleOnGestureListener {
    private  final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    private DoubleTapListener mListener = null;

    public interface DoubleTapListener {
        void onDoubleTap();
    }

    public void setDoubleTapListener(DoubleTapListener l){
        mListener = l;

    }
     public void doubleTap(){

        if(mListener != null)
        mListener.onDoubleTap();
     }

    @Override
    public boolean onDoubleTap(MotionEvent e){
        log("Double tap!");
        doubleTap();
        return true;
        //return super.onDoubleTap(e);
    }


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

}