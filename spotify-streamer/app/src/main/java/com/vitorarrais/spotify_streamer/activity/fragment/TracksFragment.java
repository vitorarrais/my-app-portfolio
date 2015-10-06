package com.vitorarrais.spotify_streamer.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.TracksActivity;
import com.vitorarrais.spotify_streamer.adapter.TrackAdapter;
import com.vitorarrais.spotify_streamer.api.ApiManager;
import com.vitorarrais.spotify_streamer.model.ArtistModel;
import com.vitorarrais.spotify_streamer.model.TrackModel;
import com.vitorarrais.spotify_streamer.ui.DividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

/**
 * Created by vitor on 05/10/2015.
 */
public class TracksFragment extends Fragment implements TrackAdapter.TrackViewHolder.OnTrackClickListener {

    public static final String TAG_PLAYBACK = "playback";


    @Bind(R.id.tracks_view_switcher)
    ViewSwitcher mSwitcher;

    @Bind(R.id.tracks_recycler_view)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.tracks_progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.empty_list_text)
    TextView mEmptyStateTextView;


    TrackAdapter mAdapter;

    List<Track> mTopTracks;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tracks, container, false);
        ButterKnife.bind(this, root);


        // change empty text displayed in empty results state
        mEmptyStateTextView.setText(getActivity().getResources().getText(R.string.empty_tracks_list));

        // create and setup new adapter
        mAdapter = new TrackAdapter(getActivity(), this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mRecyclerView.setAdapter(mAdapter);

        // check for extras.
        // in this case the activity receive the spotify id from an artist that will
        // be used to fetch top tracks
        if (getArguments() != null) {
            mProgressBar.setVisibility(View.VISIBLE);

            Bundle bundle = getArguments();
            ArtistModel artist = (ArtistModel) bundle.get(TracksActivity.KEY_ARTIST);

            ApiManager.getInstance().getTopTracks(artist.getId(),
                    new SpotifyCallback<Tracks>() {
                        @Override
                        public void failure(SpotifyError spotifyError) {
                            Log.d("ErrorTag", spotifyError.getMessage());
                        }

                        @Override
                        public void success(Tracks tracks, Response response) {
                            // switcher to control empty result states
                            if (!tracks.tracks.isEmpty() &&
                                    mSwitcher.getCurrentView().getId() == R.id.element_empty_list) {
                                mSwitcher.showNext();
                            } else if (tracks.tracks.isEmpty() &&
                                    mSwitcher.getCurrentView() == mRecyclerView) {
                                mSwitcher.showPrevious();
                            }
                            mProgressBar.setVisibility(View.GONE);
                            // set current top tracks
                            mTopTracks = tracks.tracks;
                            // update recycler data
                            mAdapter.setData(tracks.tracks);
                        }
                    });
        } else {
            mProgressBar.setVisibility(View.GONE);
            if ( mSwitcher.getCurrentView() == mRecyclerView){
                mSwitcher.showPrevious();
            }
        }
        return root;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ( ((AppCompatActivity)getActivity()).getSupportActionBar()!=null ){
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public void onClick(TrackModel current, int pos) {

        boolean isLargeLayout = getActivity().getResources().getBoolean(R.bool.large_layout);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        PlaybackFragment frag = new PlaybackFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(PlaybackFragment.EXTRA_TRACK_TAG, current);
        bundle.putInt(PlaybackFragment.EXTRA_START_POS_TAG, pos);
        frag.setArguments(bundle);

        if (isLargeLayout){
            frag.show(fm, "dialog");
        } else {
            PlaybackFragment pbf = (PlaybackFragment)fm.findFragmentByTag(TAG_PLAYBACK);
            if (pbf!=null){
                fm.beginTransaction().remove(pbf);
            }
            FragmentTransaction ft = fm.beginTransaction();
            ft.detach(this);
            ft.add(R.id.tracks_container, frag, TAG_PLAYBACK);
            ft.addToBackStack(null);
            ft.commit();
        }

    }


    public void atatch(){
        getActivity().getSupportFragmentManager().beginTransaction().attach(this).commit();
    }
    /**
     * Get current top tracks
     *
     * @return
     */
    public List<Track> getTopTracks() {
        return mTopTracks;
    }

}
