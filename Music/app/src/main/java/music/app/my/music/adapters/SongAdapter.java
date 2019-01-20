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
import music.app.my.music.types.Song;
import music.app.my.music.ui.browser.baseListFragment;

/**
 * Created by saul on 7/27/16.
 */





public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        OnFastScrollStateChangeListener {

    private boolean isScrolling = false;
    @Override public void onFastScrollStart() {
        isScrolling = true;

    }
    @Override public void onFastScrollStop() {
        isScrolling = false;
    }

    private final List<Song> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;
    private boolean isPlaylist;
    private String pid, pname;

    public SongAdapter(boolean isPlaylist, String id, String name,  List<Song> items, baseListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.isPlaylist = isPlaylist;
        pid = id;
        pname = name;
        context = (Context) listener;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mValues.get(position).getTitle().charAt(0)+"";
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

        String temp = "";
        if(isPlaylist) temp += mValues.get(position).getPlayOrder() + " : ";
        temp += position + " " + mValues.get(position).getTitle();
        holder.mContentView.setText( temp);

        holder.nextupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSongNextupClicked(position,mValues.get(position) );
            }
        });

        holder.optionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOptionClicked(position, mValues.get(position) );
            }
        });

        holder.optionbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    mListener.onOptionLongClicked(mValues.get(position));
                return false;
            }
        });
        holder.nextupbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onNextLongClicked(mValues.get(position));
                return false;
            }
        });


                //set details on line 2/subtext
                Song s = mValues.get(position);
        holder.mLine2View.setText( s.getArtist() + " : " + s.getAlbum());


        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(isPlaylist)
                mListener.onPlaylistSongLongClicked(holder.mItem, pid, pname, position);
                return true;
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    mListener.onSongClicked(holder.mItem);
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
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slidein_right);
            viewToAnimate.startAnimation(animation);
          //  lastPosition = position;  //with this items are only animated the first time you scroll.
            //now it always animates. up and down. no animation on fast scroll.
        }
    }

    @Override
    public int getItemCount() {
        if(mValues == null) return 0;
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mLine2View;
        public final ImageButton nextupbtn;
        public final ImageButton optionbtn;

        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLine2View = (TextView) view.findViewById(R.id.line2);
            nextupbtn = (ImageButton) view.findViewById(R.id.nextupbtn);
            optionbtn = (ImageButton) view.findViewById(R.id.optionbtn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
