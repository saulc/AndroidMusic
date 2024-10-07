package music.app.my.music.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import music.app.my.music.R;
import music.app.my.music.types.Genre;
import music.app.my.music.ui.browser.baseListFragment;


public class GenreAdapter extends  RecyclerView.Adapter<GenreAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        OnFastScrollStateChangeListener {

    private boolean isScrolling = false;
    @Override public void onFastScrollStart() {
        isScrolling = true;

    }
    @Override public void onFastScrollStop() {
        isScrolling = false;
    }

    private final List<Genre> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;

    public GenreAdapter(ArrayList<Genre> items, baseListFragment.OnListFragmentInteractionListener activity) {

        mValues = items;
        mListener = activity;
        context = (Context) activity;
    }
    @NonNull
    @Override
    public String getSectionName(int position) {
        return mValues.get(position).getGenre().charAt(0)+"";
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
        holder.mContentView.setText(mValues.get(position).getGenre());

        Genre temp = mValues.get(position);
        String t = "";  //mValues.size() + " song" + ( (mValues.size() == 1) ? "" : "s");
        holder.mLine2.setText(t);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onGenreClicked(holder.mItem);
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
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slidenscale);
            viewToAnimate.startAnimation(animation);
            //  lastPosition = position;  //with this items are only animated the first time you scroll.
            //now it always animates. up and down. no animation on fast scroll.
        }
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
        public Genre mItem;

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
