package com.vitorarrais.spotify_streamer;

import android.app.Application;
import android.content.Intent;

import com.vitorarrais.spotify_streamer.api.ApiManager;
import com.vitorarrais.spotify_streamer.service.PlaybackService;

/**
 * Created by vitor on 05/07/2015.
 */
public class App extends Application {


    /**
     * The constant COUNTRY_MAP_TAG.
     */
    public static final String COUNTRY_MAP_TAG = "country";
    /**
     * The constant EXTRA_ARTIST_NAME_TAG.
     */
    public static final String EXTRA_ARTIST_NAME_TAG = "artist_name";

    /**
     * The M playback service.
     */
    protected PlaybackService mPlaybackService;

    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // when app terminate, stop the playback service
        stopService(new Intent(this, PlaybackService.class));
    }

    /**
     * Set playback service.
     *
     * @param ps the ps
     */
    public void setPlaybackService(PlaybackService ps){
        this.mPlaybackService = ps;
    }

    /**
     * Get playback service playback service.
     *
     * @return the playback service
     */
    public PlaybackService getPlaybackService(){
        return this.mPlaybackService;
    }
}
