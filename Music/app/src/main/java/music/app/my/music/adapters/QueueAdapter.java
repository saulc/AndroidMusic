package music.app.my.music.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Insertable;
import com.nhaarman.listviewanimations.util.Swappable;

import music.app.my.music.R;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.types.Song;

import java.util.List;


public class QueueAdapter extends BaseAdapter implements UndoAdapter , Swappable, Insertable{ //RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final QueueListener mListener;
    private int current = 0;

    private  View mView;
    private  TextView mIdView;
    private  TextView mContentView;
    private  TextView mIView;

    public QueueAdapter(List<Song> items, QueueListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void setCurrent(int i){
        int old = current;
        current = i;
       // Log.d("QADAPTER", "SetCurrent: " + old + " - " + i + " " + current);

    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }
    @Override
    public long getItemId(int position) {
        long i = 0;

        try {
           i =  Long.parseLong(mValues.get(position).getId());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_queueitem, parent, false);

        final Song mItem = mValues.get(position);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mIView = (TextView) view.findViewById(R.id.i);

        String t = "    ";
        if( position == current) t =  "--> ";

        //         Log.d("QADAPTER", "onBindViewHolder: " + current + t + position);
        mIView.setText(t + (position + 1) + " ");
        mContentView.setText(mItem.getArtist());
        mIdView.setText(mItem.getTitle());

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onQueueItemClicked(mItem, position);
                }
            }
        });

        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                   // mListener.onQueueItemLongClicked(mItem, position);
//                    return true;
//                }

                return false;
            }
        });

        return view;
    }

    private View uv=null;

    @NonNull
    @Override
    public View getUndoView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        uv =  getView(i, view, viewGroup);

        mIdView.setText("Swipe again to remove.");
        mContentView.setText("Tap to undo.");
//        TextView v = new TextView();
//        v.setText("Swipe again to remove " + mValues.get(i).getTitle() + " from queue.");
        return uv;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
      //  Toast.makeText(view.getContext() , "Item removed from queue.", Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void swapItems(int i, int i1) {
        mListener.swapItems(i, i1);
    }

    @Override
    public void add(int i, @NonNull Object o) {
        mListener.addToQ(i, o);
    }
}
