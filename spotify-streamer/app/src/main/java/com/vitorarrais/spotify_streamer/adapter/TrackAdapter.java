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
import com.vitorarrais.spotify_streamer.activity.TracksActivity;
import com.vitorarrais.spotify_streamer.model.TrackModel;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * The type Artist adapter, used to insert data in the RecyclerView  in {@link TracksActivity}
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    /**
     * Data that represent the list of Tracks
     */
    private List<Track> mDataset;

    /**
     * Context intance
     */
    private Context mContext;

    private TrackViewHolder.OnTrackClickListener mListener;

    /**
     * The type Track view holder. Provide a reference to the views for each data item.
     */
    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        /**
         * The track name.
         */
        public TextView mTrackName;

        /**
         * The track image.
         */
        public ImageView mTrackImage;

        /**
         * The track album name.
         */
        public TextView mTrackAlbum;

        /**
         * Placeholder
         */
        public TextView mTextPlaceholder;

        /**
         * Track model
         */
        public TrackModel mTrackModel;

        public int mCurrentPos;

        /**
         * On track click listener
         */
        public OnTrackClickListener mOnTrackClickListener;

        /**
         * Instantiates a new Track view holder.
         *
         * @param v the v
         */
        public TrackViewHolder(View v) {
            super(v);
            mTrackName = (TextView)v.findViewById(R.id.track_name);
            mTrackImage = (ImageView)v.findViewById(R.id.track_image);
            mTrackAlbum = (TextView)v.findViewById(R.id.track_album);
            mTextPlaceholder = (TextView)v.findViewById(R.id.text_placeholder);
        }

        public TrackViewHolder(View v, OnTrackClickListener listener) {
            super(v);
            v.setOnClickListener(this);
            mTrackName = (TextView)v.findViewById(R.id.track_name);
            mTrackImage = (ImageView)v.findViewById(R.id.track_image);
            mTrackAlbum = (TextView)v.findViewById(R.id.track_album);
            this.mOnTrackClickListener = listener;
            mTextPlaceholder = (TextView)v.findViewById(R.id.text_placeholder);
        }

        @Override
        public void onClick(View v) {
            mOnTrackClickListener.onClick(mTrackModel, mCurrentPos);
        }

        public interface OnTrackClickListener {

            void onClick(TrackModel track, int pos);
        }
    }


    /**
     * Instantiates a new Track adapter.
     *
     * @param context the context
     */
    public TrackAdapter(Context context) {
        mDataset = new ArrayList<>();
        mContext = context;
    }

    public TrackAdapter(Context context, TrackViewHolder.OnTrackClickListener listener) {
        mDataset = new ArrayList<>();
        this.setListener(listener);
        mContext = context;
    }

    /**
     * Instantiates a new Track adapter.
     *
     * @param dataSet the data set
     * @param context the context
     */
    public TrackAdapter(List<Track> dataSet, Context context) {
        mDataset = dataSet;
        mContext = context;
    }

    /**
     * Set data and notify change to update UI.
     *
     * @param dataSet the data set
     */
    public void setData(List<Track> dataSet){
        mDataset = dataSet;
        notifyDataSetChanged();
    }


    /**
     * Create new views(invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_track_list_row, parent, false);
        TrackViewHolder vh = new TrackViewHolder(v, mListener);
        return vh;
    }


    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element
        holder.mTrackModel = TrackModel.from(mDataset.get(position));
        holder.mCurrentPos = position;
        holder.mTrackName.setText(mDataset.get(position).name);
        holder.mTrackAlbum.setText(mDataset.get(position).album.name);
        if(!mDataset.get(position).album.images.isEmpty()){
            loadImage(mDataset.get(position).album.images.get(0).url, holder);
        }else {
            loadImage(null, holder);
        }
    }


    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Load image with Picasso.
     *
     * @param image the image
     * @param holder the holder view
     */
    public void loadImage(String image, final TrackViewHolder holder) {

        // get the image size (same of artist adapter)
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
                    .into(holder.mTrackImage, new Callback() {

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            loadImage(null, holder);
                        }
                    });

        } else {

            String placeholder = holder.mTrackName.getText().toString().substring(0,1);
            holder.mTextPlaceholder.setText(placeholder.toUpperCase());
            holder.mTextPlaceholder.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(R.drawable.img_placeholder)
                    .resize(width, height)
                    .centerInside()
                    .into(holder.mTrackImage);
        }
    }

    public void setListener(TrackViewHolder.OnTrackClickListener mListener) {
        this.mListener = mListener;
    }
}
