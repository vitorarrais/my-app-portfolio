package com.vitorarrais.spotify_streamer.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.fragment.TracksFragment;
import com.vitorarrais.spotify_streamer.model.ArtistModel;

/**
 * The type Top tracks activity. Display top tracks of an artist
 */
public class TracksActivity extends AppCompatActivity {

    public static final String KEY_ARTIST = "artist";

    ArtistModel mArtistModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tracks);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if (getIntent().getExtras()!=null){
            mArtistModel = (ArtistModel) getIntent().getSerializableExtra(TracksActivity.KEY_ARTIST);
        }

        if (savedInstanceState==null){
            TracksFragment tracksFragment = new TracksFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(TracksActivity.KEY_ARTIST, mArtistModel);
            tracksFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_container, tracksFragment, MainActivity.TAG_TRACKS_FRAG).commit();
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
