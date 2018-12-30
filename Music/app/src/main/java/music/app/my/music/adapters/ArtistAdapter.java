package music.app.my.music.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import music.app.my.music.R;
import music.app.my.music.types.Artist;
import music.app.my.music.types.Playlist;
import music.app.my.music.ui.baseListFragment;

public class ArtistAdapter extends  RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private final List<Artist> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;

    public ArtistAdapter(ArrayList<Artist> items, baseListFragment.OnListFragmentInteractionListener activity) {

        mValues = items;
        mListener = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getArtist());

        Artist temp = mValues.get(position);
        String t = temp.getSongs() + " song";
        if(temp.getSongs() > 1) t += "s";

        t += " ";

        t += temp.getAlbums() + " album";
        if(temp.getAlbums() > 1) t += "s";

        holder.mLine2.setText(t);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onArtistClicked(holder.mItem);
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
        public final TextView mLine2;
        public Artist mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLine2 = (TextView) view.findViewById(R.id.line2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
