package com.vitorarrais.spotify_streamer;

import android.app.Application;

import com.vitorarrais.spotify_streamer.api.ApiManager;

/**
 * Created by vitor on 05/07/2015.
 */
public class App extends Application {


    public static final String COUNTRY_MAP_TAG = "country";
    public static final String EXTRA_STRING_ID_TAG = "extra_id_string";
    public static final String EXTRA_STRING_NAME_TAG = "extra_name_string";

    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(this);
    }
}
