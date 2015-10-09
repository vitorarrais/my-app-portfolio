package com.vitorarrais.spotify_streamer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.fragment.TracksFragment;
import com.vitorarrais.spotify_streamer.model.ArtistModel;
import com.vitorarrais.spotify_streamer.service.PlaybackService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The type Top tracks activity. Display top tracks of an artist
 */
public class TracksActivity extends AppCompatActivity {

    public static final String KEY_ARTIST = "artist";

    ArtistModel mArtistModel;

    @Bind(R.id.now_playing_layout)
    protected LinearLayout mNowPlayingLayout;

    /**
     * The M song view.
     */
    @Bind(R.id.now_playing)
    protected TextView mNowPlayingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tracks);

        ButterKnife.bind(this);

        if (!getResources().getBoolean(R.bool.large_layout)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {
            mArtistModel = (ArtistModel) getIntent().getSerializableExtra(TracksActivity.KEY_ARTIST);
        }

        if (savedInstanceState == null) {
            TracksFragment tracksFragment = new TracksFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(TracksActivity.KEY_ARTIST, mArtistModel);
            tracksFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_container, tracksFragment, MainActivity.TAG_TRACKS_FRAG).commit();
        }

        mNowPlayingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                Fragment frag = getSupportFragmentManager().findFragmentByTag(TracksFragment.TAG_PLAYBACK);
                if (frag != null) {
                    getSupportFragmentManager().beginTransaction().attach(frag).commit();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // after ui is exposed to the user, check if the playback service have
        // a media player playing something. In case the app is currently playing
        // somethig, whe register a broadcast receiver to the service, thus we can
        // show the current playing track at  'now playing' box
        final PlaybackService service = ((App) getApplication()).getPlaybackService();
        if (service != null) {
            // update the UI with the current track info
            updateNowPlaying(service.getTrackInfo());

            // create and register a broadcast receiver to receive info from the playback service
            // every time a track is changed
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    PlaybackService.TrackInfo info = (PlaybackService.TrackInfo) intent.getSerializableExtra(PlaybackService.TRACK_INFO);
                    updateNowPlaying(info);

                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                    new IntentFilter(PlaybackService.TRACK_INFO));

        } else {
            // if nothing is being played, just hide the 'now playing' box
            mNowPlayingLayout.setVisibility(View.GONE);
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


    /**
     * Update now playing views.
     *
     * @param info the info
     */
    public void updateNowPlaying(PlaybackService.TrackInfo info) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(TracksFragment.TAG_PLAYBACK);
        if (frag != null && frag.isVisible()) {
            mNowPlayingLayout.setVisibility(View.GONE);
        } else {
            mNowPlayingLayout.setVisibility(View.VISIBLE);
        }
        mNowPlayingView.setText(info.getArtist().concat(" - ".concat(info.getSong())));
        mNowPlayingView.setSelected(true);

    }



}
