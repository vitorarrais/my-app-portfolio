package com.vitorarrais.spotify_streamer.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.MainActivity;
import com.vitorarrais.spotify_streamer.adapter.ArtistAdapter;
import com.vitorarrais.spotify_streamer.api.ApiManager;
import com.vitorarrais.spotify_streamer.model.ArtistModel;
import com.vitorarrais.spotify_streamer.ui.DividerItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.client.Response;

/**
 * Created by vitor on 05/10/2015.
 */
public class ArtistsFragment extends Fragment implements ArtistAdapter.ArtistViewHolder.OnArtistViewHolderClick {

    @Bind(R.id.artists_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.artists_progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.artists_view_switcher)
    ViewSwitcher mSwitcher;

    ArtistAdapter mAdapter;

    Menu mMenu;

    SearchView mSearchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_artists, container, false);

        ButterKnife.bind(this, root);

        // create adapter with a on click listener parameter
        mAdapter = new ArtistAdapter(getActivity(), this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mRecyclerView.setAdapter(mAdapter);

        // check for extras
        // in this case, the extra is a query string from home activity
        if (getArguments() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            Bundle bundle = getArguments();
            // make api call for search artists with the query string
            ApiManager.getInstance().searchArtists(
                    bundle.getString(App.EXTRA_ARTIST_NAME_TAG),
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

        return root;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        // create new searchView programmatically
        mSearchView = new SearchView(getActivity());

        // place the searchView in the toolbar
        ((MainActivity) getActivity()).getSupportActionBar().setCustomView(mSearchView);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);

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
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

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
    }

    /**
     * Implement on recycler view item click event listener that will be passed as parameter
     * to artist adapter
     */
    @Override
    public void onClickArtist(ArtistModel artist) {

        ((MainActivity)getActivity()).openTracks(artist);
/*
        Intent intent = new Intent(getActivity(), TracksActivity.class);
        intent.putExtra(App.EXTRA_ARTIST_TAG, artist);
        startActivity(intent);*/
    }
}
