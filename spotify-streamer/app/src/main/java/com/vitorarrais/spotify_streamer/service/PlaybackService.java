package com.vitorarrais.spotify_streamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.activity.fragment.PlaybackFragment;
import com.vitorarrais.spotify_streamer.model.TrackModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by vitor on 07/10/2015.
 */
public class PlaybackService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    /**
     * The constant TRACK_INFO.
     */
    public static final String TRACK_INFO = "track_info";

    /**
     * The M tracks.
     */
    protected List<Track> mTracks;

    /**
     * The M playback binder.
     */
    protected final IBinder mPlaybackBinder = new PlaybackBinder();

    /**
     * The M media player.
     */
    protected MediaPlayer mMediaPlayer;

    /**
     * The M current position.
     */
    protected int mCurrentPosition;

    /**
     * The M playback fragment.
     */
    protected PlaybackFragment mPlaybackFragment;

    /**
     * The M track info.
     */
    protected TrackInfo mTrackInfo;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ((App)getApplication()).setPlaybackService(this);
        return this.mPlaybackBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Initialise the media player and set track current position in the list.
     *
     * @param pos the position in the tracks list of first track to be played
     */
    public void init(int pos){
        mCurrentPosition = pos;
        release();
        mMediaPlayer = newMediaPlayer();
    }


    /**
     * Jump to next track in the list and play it.
     */
    public void next() {
        if(mCurrentPosition+1< mTracks.size()){
            mCurrentPosition = mCurrentPosition + 1;
            release();
            mMediaPlayer = newMediaPlayer();
            start();
        } else {
            release();
        }
    }

    /**
     * Back to preivous track in the list and play it.
     */
    public void previous(){
        if (mCurrentPosition-1>=0){
            mCurrentPosition = mCurrentPosition -1;
            release();
            mMediaPlayer = newMediaPlayer();
            start();
        }
    }

    /**
     * Pause the media player.
     */
    public void pause() {
        mMediaPlayer.pause();
    }

    /**
     * Start the media player from where it was paused
     */
    public void play() {
        mMediaPlayer.start();
    }

    /**
     * Release the media player.
     */
    public void release(){
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Create a new media player object and set some required listeners
     *
     * @return the media player
     */
    public MediaPlayer newMediaPlayer(){
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(this);

        // check whether the UI fragment with the player is current visible
        // to the user.
        if (mPlaybackFragment!=null && mPlaybackFragment.isVisible()){
            // when the PlaybackFragmet is visible to the user
            // We setup listeners defined there
            mp.setOnPreparedListener(mPlaybackFragment);
        } else {
            // when the PlaybackFragment isn't visible to the user
            // We set listener defined by the PlaybackService
            mp.setOnPreparedListener(this);
        }

        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        return mp;
    }


    /**
     * Sets tracks list.
     *
     * @param tracks the tracks
     */
    public void setTracks(List<Track> tracks) {
        this.mTracks = tracks;
    }

    /**
     * Load track in current positionn in the tracks list and start the player
     */
    public void start() {

        TrackModel model = TrackModel.from(mTracks.get(mCurrentPosition));

        updateTrackInfo(model);

        try {
            // load song URL
            mMediaPlayer.setDataSource(model.getPreview_url());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    /**
     * Set playback fragment.
     *
     * @param frag the frag
     */
    public void setPlaybackFragment(PlaybackFragment frag){
        this.mPlaybackFragment = frag;
    }

    /**
     * Get playback fragment playback fragment.
     *
     * @return the playback fragment
     */
    public PlaybackFragment getPlaybackFragment(){
        return this.mPlaybackFragment;
    }


    /**
     * Update track info.
     *
     * @param model the model
     */
    public void updateTrackInfo(TrackModel model){
        if (mTrackInfo==null)
            mTrackInfo = new TrackInfo();

        mTrackInfo.setArtist(model.getArtists().get(0).getName());
        mTrackInfo.setSong(model.getName());

        sendTrackInfoBroadcast();
    }

    /**
     * Get track info track info.
     *
     * @return the track info
     */
    public TrackInfo getTrackInfo(){
        return mTrackInfo;
    }

    /**
     * Get current track track model.
     *
     * @return the track model
     */
    public TrackModel getCurrentTrack(){
        return TrackModel.from(mTracks.get(mCurrentPosition));
    }

    /**
     * Set progress.
     *
     * @param progress the progress
     */
    public void setProgress(int progress){
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }

    }

    /**
     * Get current position int.
     *
     * @return the int
     */
    public int getCurrentPosition(){
        if (mMediaPlayer!=null)
            return mMediaPlayer.getCurrentPosition() / 1000;
        else
            return -1;
    }

    /**
     * Get track duration int.
     *
     * @return the int
     */
    public int getTrackDuration(){
        return mMediaPlayer.getDuration() / 1000;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    /**
     * The type Playback binder.
     */
    public class PlaybackBinder extends Binder {
        /**
         * Gets service.
         *
         * @return the service
         */
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    /**
     * Send track info broadcast.
     */
    public void sendTrackInfoBroadcast(){
        Intent i = new Intent(TRACK_INFO);
        i.putExtra(TRACK_INFO, getTrackInfo());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    /**
     * The type Track info.
     */
    public class TrackInfo implements Serializable {
        private String artist;
        private String song;

        /**
         * Gets artist.
         *
         * @return the artist
         */
        public String getArtist() {
            return artist;
        }

        /**
         * Sets artist.
         *
         * @param artist the artist
         */
        public void setArtist(String artist) {
            this.artist = artist;
        }

        /**
         * Gets song.
         *
         * @return the song
         */
        public String getSong() {
            return song;
        }

        /**
         * Sets song.
         *
         * @param song the song
         */
        public void setSong(String song) {
            this.song = song;
        }
    }

}
