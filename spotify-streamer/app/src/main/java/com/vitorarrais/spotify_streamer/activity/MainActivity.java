package com.vitorarrais.spotify_streamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.adapter.ArtistAdapter;
import com.vitorarrais.spotify_streamer.api.ApiManager;
import com.vitorarrais.spotify_streamer.ui.DividerItemDecoration;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.client.Response;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements ArtistAdapter.ArtistViewHolder.OnArtistViewHolderClick {

    /**
     * The recycler view.
     */
    protected RecyclerView mRecyclerView;

    /**
     * The recycler adapter.
     */
    protected ArtistAdapter mAdapter;

    /**
     * The toolbar.
     */
    protected Toolbar mToolbar;

    /**
     * The search view in toolbar.
     */
    protected SearchView mSearchView;

    /**
     * The switcher that controls empty state layout.
     */
    protected ViewSwitcher mSwitcher;

    /**
     * The menu.
     */
    protected Menu mMenu;

    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // create adapter with a on click listener parameter
        mAdapter = new ArtistAdapter(this, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        mRecyclerView.setAdapter(mAdapter);

        mSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        // check for extras
        // in this case, the extra is a query string from home activity
        if (getIntent().getExtras() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            // make api call for search artists with the query string
            ApiManager.getInstance().searchArtists(
                    getIntent().getStringExtra(App.EXTRA_STRING_NAME_TAG),
                    new SpotifyCallback<ArtistsPager>() {
                        @Override
                        public void failure(SpotifyError spotifyError) {
                            // the api call failed
                            Log.d("ErrorTag", spotifyError.getMessage());
                        }

                        @Override
                        public void success(ArtistsPager artists, Response response) {
                            // the api call has succed
                            if (!artists.artists.items.isEmpty() &&
                                    mSwitcher.getCurrentView().getId() == R.id.element_empty_list) {
                                // if the api call returned something and the screen is
                                // in the empty state, I need to change the switcher layout to display
                                // the result in the screen
                                mSwitcher.showPrevious();
                            } else if (artists.artists.items.isEmpty() &&
                                    mSwitcher.getCurrentView() == mRecyclerView) {
                                // if the api call returned an empty result, I need to change the switcher
                                // to display the empty state layout
                                mSwitcher.showNext();
                            }
                            // update recycler view with the api's call result
                            mProgressBar.setVisibility(View.GONE);
                            mAdapter.setData(artists.artists);

                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        // create new searchView programmatically
        mSearchView = new SearchView(this);

        // place the searchView in the toolbar
        getSupportActionBar().setCustomView(mSearchView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // set hint text
        mSearchView.setQueryHint(getText(R.string.search_hint));

        // change hint and text colors
        AutoCompleteTextView searchText = (AutoCompleteTextView) mSearchView.findViewById(R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.white));
        searchText.setHintTextColor(getResources().getColor(R.color.white_transparent));

        // set layout params
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        mSearchView.setLayoutParams(params);

        // when expanded, tha search view fill all space in the toolbar
        // and the title is gone
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT,
                        Toolbar.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

                mSearchView.setLayoutParams(params);
            }
        });

        // when searchView closed, I show title again
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

                mSearchView.setLayoutParams(params);
                return false;
            }
        });

        // set query listener
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProgressBar.setVisibility(View.VISIBLE);
                // use Spotify api to fetch data
                ApiManager.getInstance().searchArtists(query, new SpotifyCallback<ArtistsPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.d("ErrorTag", spotifyError.getMessage());
                    }

                    @Override
                    public void success(ArtistsPager artists, Response response) {
                        // switcher used to control empty result layout
                        if (!artists.artists.items.isEmpty() &&
                                mSwitcher.getCurrentView().getId() == R.id.element_empty_list) {
                            mSwitcher.showNext();
                        } else if (artists.artists.items.isEmpty() &&
                                mSwitcher.getCurrentView() == mRecyclerView) {
                            mSwitcher.showPrevious();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        // update recycler data
                        mAdapter.setData(artists.artists);

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    /**
     * Implement on recycler view item click event listener that will be passed as parameter
     * to artist adapter
     * @param artistId the artist id
     * @param name the name
     */
    @Override
    public void onClickArtist(String artistId, String name) {

        Intent intent = new Intent(MainActivity.this, TopTracksActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(App.EXTRA_STRING_ID_TAG, artistId);
        bundle.putString(App.EXTRA_STRING_NAME_TAG, name);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
