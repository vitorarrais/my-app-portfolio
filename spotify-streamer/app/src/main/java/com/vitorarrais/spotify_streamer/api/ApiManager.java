package com.vitorarrais.spotify_streamer.api;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;


/**
 * The type Api manager. This class handle all Spotify API methods. It is a singleton and keep a
 * instance of SpotifyApi.
 */
public class ApiManager {

    /**
     * Singleton instance of ApiManager.
     */
    private static ApiManager sInstance = null;

    /**
     * Init api manager.
     *
     * @param app the app
     * @return the api manager
     */
    public static ApiManager init(App app) {
        sInstance = new ApiManager(app);
        return sInstance;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ApiManager getInstance() {
        if (sInstance == null) throw new UnsupportedOperationException(
                "Please call init on ApiManager before getting the instance");
        return sInstance;
    }



    public static boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getInstance().mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Application instance.
     */
    public App mApp;

    /**
     * SpotifyApi instance.
     */
    SpotifyApi mApi = null;

    /**
     * Private Constructor
     * @param app
     */
    private ApiManager(App app) {
        mApp = app;
        mApi = new SpotifyApi();
    }

    /**
     * Search artists.
     *
     * @param name the name
     * @param callback the callback
     */
    public void searchArtists(String name, Callback<ArtistsPager> callback) {
        mApi.getService().searchArtists(name, callback);
    }

    /**
     * Gets top tracks.
     *
     * @param artistId the artist id
     * @param callback the callback
     */
    public void getTopTracks(String artistId, Callback<Tracks> callback) {
        // Fetch all available countries
        String[] countries = mApp.getResources().getStringArray(R.array.countries);
        Map<String, Object> map = new HashMap<>();
        for (String country : countries){
            Object o = country;
            map.put(App.COUNTRY_MAP_TAG, o);
        }
        mApi.getService().getArtistTopTrack(artistId, map, callback);
    }
}
