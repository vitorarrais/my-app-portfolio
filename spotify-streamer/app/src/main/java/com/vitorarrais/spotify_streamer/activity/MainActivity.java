package com.vitorarrais.spotify_streamer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.fragment.ArtistsFragment;
import com.vitorarrais.spotify_streamer.activity.fragment.TracksFragment;
import com.vitorarrais.spotify_streamer.model.ArtistModel;
import com.vitorarrais.spotify_streamer.service.PlaybackService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The constant TAG_TRACKS_FRAG.
     */
    public static final String TAG_TRACKS_FRAG = "tracks_fragment";

    /**
     * The constant KEY_ARTIST_NAME.
     */
    public static final String KEY_ARTIST_NAME = "artist_name";


    /**
     * The M toolbar.
     */
    protected Toolbar mToolbar;

    /**
     * The M two pane.
     */
    protected boolean mTwoPane;

    /**
     * The M now playing layout.
     */
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
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // check if is large screen or not
        if (!getResources().getBoolean(R.bool.large_layout)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // set the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        Bundle bundle = new Bundle();

        // check for extras
        if (getIntent().getExtras() != null) {
            String extra = getIntent().getExtras().getString(KEY_ARTIST_NAME);
            bundle.putString(KEY_ARTIST_NAME, extra);
            ArtistsFragment artistsFragment = new ArtistsFragment();
            artistsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artists_container, artistsFragment).commit();
        }

        // tries to find tracks container layout in the root view.
        // when it is not present, so we hava a small screen, otherwise
        // is a large screen
        if (findViewById(R.id.tracks_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tracks_container, new TracksFragment(), MainActivity.TAG_TRACKS_FRAG).commit();
            }
        } else {
            mTwoPane = false;
        }

        mNowPlayingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                Fragment frag = getSupportFragmentManager().findFragmentByTag(TracksFragment.TAG_PLAYBACK);
                if (frag!=null){
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
                    PlaybackService.TrackInfo info = (PlaybackService.TrackInfo)intent.getSerializableExtra(PlaybackService.TRACK_INFO);
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

    /**
     * Open tracks Fragment based on the screen size. For large screens just create a new
     * TracksFragment and atatch it to main activity layout. Otherwise, for small screens,
     * call TracksActitivity to manage the TracksFragment separetedly.
     *
     * @param model the model
     */
    public void openTracks(ArtistModel model) {
        if (mTwoPane) {
            Fragment frag = getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);
            if (frag != null) {
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(TracksActivity.KEY_ARTIST, model);
            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_container, tracksFragment, MainActivity.TAG_TRACKS_FRAG).commit();
        } else {
            Intent i = new Intent(MainActivity.this, TracksActivity.class);
            i.putExtra(TracksActivity.KEY_ARTIST, model);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }
    }

    /**
     * Update now playing views.
     *
     * @param info the info
     */
    public void updateNowPlaying(PlaybackService.TrackInfo info) {
        mNowPlayingView.setText(info.getArtist().concat(" - ".concat(info.getSong())));
        mNowPlayingView.setSelected(true);
        mNowPlayingLayout.setVisibility(View.VISIBLE);

    }


}
