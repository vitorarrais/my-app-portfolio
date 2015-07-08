package com.vitorarrais.spotify_streamer.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.adapter.TrackAdapter;
import com.vitorarrais.spotify_streamer.api.ApiManager;
import com.vitorarrais.spotify_streamer.ui.DividerItemDecoration;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

/**
 * The type Top tracks activity. Display top tracks of an artist
 */
public class TopTracksActivity extends AppCompatActivity {

    /**
     * The recycler view that display top tracks.
     */
    protected RecyclerView mRecyclerView;

    /**
     * The adapter for recycler view items.
     */
    protected TrackAdapter mAdapter;

    /**
     * The switcher to control layout for empty results.
     */
    protected ViewSwitcher mSwitcher;

    /**
     * The toolbar.
     */
    protected Toolbar mToolbar;

    ProgressBar mProgressBar;

    LinearLayout mTitleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        // setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup title of toolbar
        mTitleLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.element_top_tracks_title, null);
        TextView title = (TextView)mTitleLayout.findViewById(R.id.title);
        title.setText(getSupportActionBar().getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView subtitle = (TextView)mTitleLayout.findViewById(R.id.subtitle);
        getSupportActionBar().setCustomView(mTitleLayout);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mSwitcher = (ViewSwitcher) findViewById(R.id.topTracks_viewSwitcher);
        // change empty text displayed in empty results state
        TextView emptyText = (TextView) findViewById(R.id.empty_list_text);
        emptyText.setText(getText(R.string.empty_tracks_list));

        mRecyclerView = (RecyclerView) findViewById(R.id.topTracks_recyclerView);

        // create and setup new adapter
        mAdapter = new TrackAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)findViewById(R.id.tracks_progress_bar);


        // check for extras.
        // in this case the activity receive the spotify id of and artist that will
        // be used to fetch top tracks
        if (getIntent().getExtras() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // add subtitle
            subtitle.setText(getIntent().getStringExtra(App.EXTRA_STRING_NAME_TAG));
            ApiManager.getInstance().getTopTracks(getIntent().getStringExtra(App.EXTRA_STRING_ID_TAG),
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
                            // update recycler data
                            mAdapter.setData(tracks.tracks);
                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
