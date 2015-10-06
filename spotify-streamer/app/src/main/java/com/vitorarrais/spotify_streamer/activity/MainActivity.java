package com.vitorarrais.spotify_streamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.fragment.ArtistsFragment;
import com.vitorarrais.spotify_streamer.activity.fragment.TracksFragment;
import com.vitorarrais.spotify_streamer.model.ArtistModel;

import butterknife.ButterKnife;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG_TRACKS_FRAG = "tracks_fragment";

    public static final String KEY_ARTIST_NAME = "artist_name";


    protected Toolbar mToolbar;

    protected boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

        if (findViewById(R.id.tracks_container) != null){
            mTwoPane = true;

            if (savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tracks_container, new TracksFragment(), MainActivity.TAG_TRACKS_FRAG).commit();
            }
        } else {
            mTwoPane = false;
        }

    }



    public void openTracks(ArtistModel model){
        if (mTwoPane){
            Fragment frag = getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);
            if (frag !=null){
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(TracksActivity.KEY_ARTIST, model);
            TracksFragment tracksFragment = new TracksFragment();
            tracksFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_container, tracksFragment, MainActivity.TAG_TRACKS_FRAG).commit();
        }else{
            Intent i = new Intent(MainActivity.this, TracksActivity.class);
            i.putExtra(TracksActivity.KEY_ARTIST, model);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }
    }

}
