package music.app.sc.music.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import music.app.sc.music.R;
import music.app.sc.music.types.Album;
import music.app.sc.music.ui.browser.baseListFragment;

public class AlbumAdapter   extends  RecyclerView.Adapter<AlbumAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        OnFastScrollStateChangeListener {

    private boolean isScrolling = false;
    @Override public void onFastScrollStart() {
        isScrolling = true;

    }
    @Override public void onFastScrollStop() {
        isScrolling = false;
    }

    private final List<Album> mValues;
    private final baseListFragment.OnListFragmentInteractionListener mListener;

    public  AlbumAdapter( ArrayList<Album> items, baseListFragment.OnListFragmentInteractionListener activity) {

        mValues = items;
        mListener = activity;
        context = (Context) activity;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mValues.get(position).getAlbum().charAt(0)+"";
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.albumfragment_listitem, parent, false);
        return new ViewHolder(view);
    }

    private static final int[] colors = { Color.BLACK,Color.RED,Color.MAGENTA,Color.CYAN,
            Color.BLUE,Color.GREEN,Color.YELLOW,Color.DKGRAY, Color.GRAY };
    private static int ci = 0;

    private int getArtColor(){
        if(++ci >= colors.length) ci = 0;
        return colors[ci];
    }

    private Bitmap createAlbumBit(String msg){
        final int width = 200, height = 200;
        final Bitmap rc = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas((rc));
        Paint p = new Paint();
        cc.drawARGB(0, 0, 0, 0);
        p.setStrokeWidth(3.0f);
        p.setColor(Color.WHITE);
        cc.drawCircle(width/2, height/2, width/2-10f, p);
        p.setColor(getArtColor());
        cc.drawCircle(width/2, height/2, width/2-10f, p);
        p.setColor(Color.WHITE);
        p.setTextSize(133f);
        cc.drawText(msg, width/3, height*.7f, p);
        return rc;
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

        if(mValues.get(position).getId() != null) {
            try {
                long p = Long.parseLong(mValues.get(position).getId());
                Uri albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        p
                ); // test edit

                Bitmap placeholder = createAlbumBit(holder.mItem.getAlbum().charAt(0) + "");
                BitmapDrawable placeholderDrawable = new BitmapDrawable(context.getResources(), placeholder);

                Log.d("Album adapter", "album" +  albumArtUri  );
                    Glide.with(context)
                            .load(albumArtUri)
                            .apply(new RequestOptions()
                                    .placeholder(placeholderDrawable)
                                    .error(placeholderDrawable)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .override(160, 160)) // Loading a bit larger for better quality, but still small
                            .into(holder.mIcon);


            } catch (Exception e) {
                e.printStackTrace();
                holder.mIcon.setImageBitmap(createAlbumBit(holder.mItem.getAlbum().charAt(0)+""));
            }

        } //else

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

//    @Override
//    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder)
//    {
//        ((CustomViewHolder)holder).clearAnimation();
//    }
//
//    public void clearAnimation()
//    {
//        mRootLayout.clearAnimation();
//    }
//

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
