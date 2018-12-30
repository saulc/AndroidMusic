package music.app.my.music.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import music.app.my.music.R;
import music.app.my.music.helpers.QueueListener;
import music.app.my.music.types.Song;
import music.app.my.music.ui.baseListFragment;

import java.util.List;


public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final QueueListener mListener;
    private int current = 0;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_queueitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getArtist());

        String t = "    ";
        if( position == current) t =  "-> ";

       //         Log.d("QADAPTER", "onBindViewHolder: " + current + t + position);
        holder.mIView.setText(t + (position + 1) + " ~");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onQueueItemClicked(holder.mItem, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mIView;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mIView = (TextView) view.findViewById(R.id.i);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
