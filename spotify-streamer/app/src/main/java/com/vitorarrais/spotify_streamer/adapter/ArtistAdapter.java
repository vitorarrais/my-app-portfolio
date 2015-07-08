package com.vitorarrais.spotify_streamer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.ui.CircleTransform;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * The type Artist adapter, used to insert data in the RecyclerView  in {@link com.vitorarrais.spotify_streamer.activity.MainActivity}
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    /**
     * Data that represent the list of Artists
     */
    private List<Artist> mDataset;

    /**
     * Context intance
     */
    private Context mContext;

    /**
     * Listener object to handle clicks on recycler items
     */
    private ArtistViewHolder.OnArtistViewHolderClick mListener;

    /**
     * The type Artist view holder. Provide a reference to the views for each data item.
     */
    public static class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * The artist name.
         */
        public TextView mArtistName;

        /**
         * The artist image.
         */
        public ImageView mArtistImage;

        /**
         * The artist id.
         */
        public String mArtistId;

        /**
         * The listener to handle click events.
         */
        public OnArtistViewHolderClick mListener;

        public TextView mTextPlaceholder;

        /**
         * Instantiates a new Artist view holder with an onClick event listener.
         *
         * @param v the v
         * @param listener the listener
         */
        public ArtistViewHolder(View v, OnArtistViewHolderClick listener) {
            super(v);
            mListener = listener;
            v.setOnClickListener(this);
            mArtistName = (TextView)v.findViewById(R.id.artist_name);
            mArtistImage = (ImageView)v.findViewById(R.id.artist_image);
            mTextPlaceholder = (TextView)v.findViewById(R.id.text_placeholder);
        }


        /**
         * Instantiates a new Artist view holder without a listener.
         *
         * @param v the v
         */
        public ArtistViewHolder(View v) {
            super(v);
            mArtistName = (TextView)v.findViewById(R.id.artist_name);
            mArtistImage = (ImageView)v.findViewById(R.id.artist_image);
        }

        /**
         * Handle click events on recycler items.
         * @param view
         */
        @Override
        public void onClick(View view) {
            mListener.onClickArtist(mArtistId, mArtistName.getText().toString());
        }

        /**
         * The interface that define methods for click listeners on artist view holder click.
         */
        public static interface OnArtistViewHolderClick {

            /**
             * On click artist.
             *
             * @param artistId the artist id
             * @param name the name
             */
            public void onClickArtist(String artistId, String name);
        }
    }


    /**
     * Instantiates a new Artist adapter.
     *
     * @param context the context
     */
    public ArtistAdapter(Context context) {
        mDataset = new ArrayList<>();
        mContext = context;
    }

    /**
     * Instantiates a new Artist adapter with an onClick listener.
     *
     * @param context the context
     * @param listener the listener
     */
    public ArtistAdapter(Context context, ArtistViewHolder.OnArtistViewHolderClick listener) {
        mDataset = new ArrayList<>();
        mContext = context;
        mListener = listener;
    }

    /**
     * Instantiates a new Artist adapter with a dataSet.
     *
     * @param dataSet the data set
     * @param context the context
     */
    public ArtistAdapter(List<Artist> dataSet, Context context) {
        mDataset = dataSet;
        mContext = context;
    }

    /**
     * Set data and notify changes to update UI.
     *
     * @param dataSet the data set
     */
    public void setData(Pager<Artist> dataSet){
        mDataset = dataSet.items;
        notifyDataSetChanged();
    }


    /**
     * Create new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_artist_list_row, parent, false);
        ArtistViewHolder vh = new ArtistViewHolder(v, mListener);
        return vh;
    }


    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element
        holder.mArtistId = mDataset.get(position).id;
        holder.mArtistName.setText(mDataset.get(position).name);
        if(!mDataset.get(position).images.isEmpty()){
            loadImage(mDataset.get(position).images.get(0).url, holder);
        }else {
            loadImage(null, holder);
        }
    }


    /**
     * Return the size of dataset (invoked by the layout manager)
     * @return
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    /**
     * Load image with Picasso
     *
     * @param image
     * @param holder
     */
    public void loadImage(String image, final ArtistViewHolder holder) {

        // get image size
        float size = mContext.getResources().getDimension(R.dimen.artist_image_list);

        // convert dp to pixels
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, mContext.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, mContext.getResources().getDisplayMetrics());

        if (image != null && !image.isEmpty()) {

            holder.mTextPlaceholder.setVisibility(View.GONE);

            Picasso.with(mContext)
                    .load(image)
                    .resize(width, height)
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(holder.mArtistImage, new Callback() {

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            loadImage(null, holder);
                        }
                    });

        } else {

            String placeholder = holder.mArtistName.getText().toString().substring(0,1);
            holder.mTextPlaceholder.setText(placeholder.toUpperCase());
            holder.mTextPlaceholder.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(R.drawable.img_placeholder)
                    .resize(width, height)
                    .centerInside()
                    .transform(new CircleTransform())
                    .into(holder.mArtistImage);
        }
    }
}