package com.vitorarrais.spotify_streamer;

import android.app.Application;

import com.vitorarrais.spotify_streamer.api.ApiManager;

/**
 * Created by vitor on 05/07/2015.
 */
public class App extends Application {


    public static final String COUNTRY_MAP_TAG = "country";
    public static final String EXTRA_ARTIST_TAG = "artist";
    public static final String EXTRA_ARTIST_NAME_TAG = "artist_name";

    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(this);
    }
}
