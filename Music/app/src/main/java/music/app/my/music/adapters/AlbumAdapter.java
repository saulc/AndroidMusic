package music.app.my.music.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import music.app.my.music.R;
import music.app.my.music.types.Album;
import music.app.my.music.types.Artist;
import music.app.my.music.ui.baseListFragment;

public class AlbumAdapter   extends  RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final List<Album> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;

    public AlbumAdapter(ArrayList<Album> items, baseListFragment.OnListFragmentInteractionListener activity) {

        mValues = items;
        mListener = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.albumfragment_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getAlbum());

        Album temp = mValues.get(position);
        String t = temp.getArtist() + " : " + temp.getSongs() + " song";
        if(temp.getSongs() > 1) t += "s";

        holder.mLine2.setText(t);

        if(mValues.get(position).getArt() != null) {
            Drawable d = Drawable.createFromPath(mValues.get(position).getArt());
            holder.mIcon.setImageDrawable(d);

        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAlbumClicked(holder.mItem);
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
        public final TextView mLine2;
        public final TextView mContentView;
        public  final ImageView mIcon;


        public Album mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);

            mIcon = (ImageView) view.findViewById(R.id.albumImageView);
            mLine2 = (TextView) view.findViewById(R.id.line2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
