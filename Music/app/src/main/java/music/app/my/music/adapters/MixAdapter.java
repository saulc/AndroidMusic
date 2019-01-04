package music.app.my.music.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.helpers.MixListener;
import music.app.my.music.player.myPlayer;


public class MixAdapter extends BaseAdapter {
    private final List<String> mValues;
    private final MixListener mListener;

    private  View mView;
    private  TextView mIdView;
    private  TextView mContentView;
    private  TextView mIView;

    public MixAdapter(List<String> items, MixListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public String getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }
    @Override
    public long getItemId(int position) {
        int i = position;

        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ((DrawerActivity) mListener).getApplicationContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView =inflater.inflate(R.layout.fragment_listitem, container, false);
        }

       TextView t =  ((TextView) convertView.findViewById(R.id.content));
        t.setText(getItem(position));

        ((TextView) convertView.findViewById(R.id.line2)).setText(getItem(position));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMixItemClicked(getItem(position), position);
                }
            }
        });


        return convertView;
    }
//
//    @Override
//    public View getView(final int position, View view, ViewGroup parent) {
//        if (view == null) {
//            view = LayoutInflater.from( ((DrawerActivity)mListener).getApplicationContext() ).inflate(R.layout.fragment_queueitem, parent, false);
//        }
//
//        Log.d("Mix Adapter", "getView: " + position);
//       // final myPlayer mItem = mValues.get(position);
//            mView = view;
//            mIdView = (TextView) view.findViewById(R.id.id);
//            mContentView = (TextView) view.findViewById(R.id.content);
//            mIView = (TextView) view.findViewById(R.id.i);
////
//        mIView.setText(position);
//        if(position < mValues.size())
//        mContentView.setText( mValues.get(position) );
//       // mIdView.setText(mItem.getAudioSessionId());
//
//        mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                  //  mListener.onMixItemClicked(mItem, position);
//                }
//            }
//        });
//
//        mView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                   // mListener.onMixItemLongClicked(mItem, position);
//                    return true;
//                }
//
//                return false;
//            }
//        });
//
//        return view;
//    }



}
