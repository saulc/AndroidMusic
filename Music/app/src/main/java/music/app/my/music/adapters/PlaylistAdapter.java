package music.app.my.music.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import music.app.my.music.R;
import music.app.my.music.types.Playlist;
import music.app.my.music.ui.browser.baseListFragment;

/**
 * Created by saul on 7/27/16.
 */





public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        OnFastScrollStateChangeListener {

    private boolean isScrolling = false;
    @Override public void onFastScrollStart() {
        isScrolling = true;

    }
    @Override public void onFastScrollStop() {
        isScrolling = false;
    }

    private final List<Playlist> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;

    public PlaylistAdapter(List<Playlist> items, baseListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        context = (Context) listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_baselistitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getName());

        holder.line2.setText("");
        holder.opbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlaylistOptionClicked(position, mValues.get(position).getId(), mValues.get(position).getName() );
            }
        });
        holder.nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlaylistNextUpClicked(position, mValues.get(position).getId(), mValues.get(position).getName());
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPlaylistClicked(holder.mItem);
                }
            }
        });

        // Here you apply the animation when the view is bound
        if(!isScrolling){
            //  log("Setting animation. " + isScrolling);
            setAnimation(holder.itemView, position);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private  int lastPosition = -1;
    private Context context;
    private void setAnimation(View viewToAnimate, int position){
        if(context == null) return;

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slidenscale);
            viewToAnimate.startAnimation(animation);
            //lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mValues.get(position).getName().charAt(0)+"";
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView line2;
        public final ImageButton opbtn, nextbtn;
        public Playlist mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            opbtn = (ImageButton) view.findViewById(R.id.optionbtn);
            nextbtn = (ImageButton) view.findViewById(R.id.nextupbtn);
            line2 = (TextView) view.findViewById(R.id.line2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
