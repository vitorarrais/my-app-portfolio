package com.vitorarrais.spotify_streamer.activity.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vitorarrais.spotify_streamer.R;
import com.vitorarrais.spotify_streamer.activity.MainActivity;
import com.vitorarrais.spotify_streamer.activity.TracksActivity;
import com.vitorarrais.spotify_streamer.model.TrackModel;
import com.vitorarrais.spotify_streamer.service.PlaybackService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by vitor on 04/10/2015.
 */
public class PlaybackFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    /**
     * The constant EXTRA_TRACK_TAG.
     */
    public static final String EXTRA_TRACK_TAG = "track";

    /**
     * The constant EXTRA_START_POS_TAG.
     */
    public static final String EXTRA_START_POS_TAG = "start_pos";


    /**
     * The M album name view.
     */
    @Bind(R.id.playback_album)
    TextView mAlbumNameView;

    /**
     * The M album image view.
     */
    @Bind(R.id.playback_album_img)
    ImageView mAlbumImageView;

    /**
     * The M arstist name view.
     */
    @Bind(R.id.playback_artist)
    TextView mArstistNameView;

    /**
     * The M song view.
     */
    @Bind(R.id.playback_song)
    TextView mSongView;

    /**
     * The M pause view.
     */
    @Bind(R.id.playback_pause)
    ImageView mPauseView;

    /**
     * The M play view.
     */
    @Bind(R.id.playback_play)
    ImageView mPlayView;

    /**
     * The M previous view.
     */
    @Bind(R.id.playback_previous)
    ImageView mPreviousView;

    /**
     * The M seek bar.
     */
    @Bind(R.id.playback_seekbar)
    SeekBar mSeekBar;

    /**
     * The M elapsed time view.
     */
    @Bind(R.id.playback_elapsed_time)
    TextView mElapsedTimeView;

    /**
     * The M track duration view.
     */
    @Bind(R.id.playback_track_duration)
    TextView mTrackDurationView;


    /**
     * Current track playing
     */
    TrackModel mCurrentTrack;

    /**
     * A list of all top tracks of an artist
     */
    List<Track> mTopTracksList;

    /**
     * Position of current track in top tracks list
     */
    int mPosition;

    /**
     * The M handler.
     */
    Handler mHandler;

    private boolean mIsBound = false;

    private PlaybackService mPlaybackService;

    private Intent mPlaybackIntent;


    private ServiceConnection mPlayBackServiceConnection;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        setServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_playback, container, false);

        ButterKnife.bind(this, root);


        // get all tracks from tracks fragment instance
        TracksFragment tracksFragment = (TracksFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);
        if (tracksFragment != null)
            mTopTracksList = tracksFragment.getTopTracks();


        // get arguments
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mCurrentTrack = (TrackModel) bundle.get(PlaybackFragment.EXTRA_TRACK_TAG);
            mPosition = bundle.getInt(PlaybackFragment.EXTRA_START_POS_TAG);
        }

        // if parent activity is a TrackActivity, so we have one pane layout
        // hide now playing
        if(getActivity().getClass().isAssignableFrom(TracksActivity.class)){

            getActivity().findViewById(R.id.now_playing_layout).setVisibility(View.GONE);

            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

            }
        }

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().findViewById(R.id.now_playing_layout).setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // bind the parent activity to the playback service
        if (mPlaybackIntent == null) {
            mPlaybackIntent = new Intent(getActivity().getApplicationContext(), PlaybackService.class);
            getActivity().getApplicationContext().bindService(mPlaybackIntent, getServiceConnection(), Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // when the Fragment is visible to the user
        // populate all UI elements with the current track information
        if (mPlaybackService !=null && mPlaybackService.getCurrentTrack()!=null)
            updateUi(mPlaybackService.getCurrentTrack());
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    /**
     * Update UI with track information. Artist name, album name, album artwork, etc.
     *
     * @param track
     */
    private void updateUi(TrackModel track) {


        mAlbumNameView.setText(track.getAlbum().getName());

        Picasso.with(getActivity())
                .load(track.getAlbum().getImages().get(0).getUrl())
                .into(mAlbumImageView);

        mArstistNameView.setText(track.getArtists().get(0).getName());

        mSongView.setText(track.getName());

        startSeekBar();
    }

    private List<Track> getTracks() {
        return this.mTopTracksList;
    }


    /**
     * The Current min.
     */
    int currentMin = 0;
    /**
     * The Current min string.
     */
    String currentMinString = "";

    /**
     * The Current sec.
     */
    int currentSec = 0;
    /**
     * The Current sec string.
     */
    String currentSecString = "";

    /**
     * The Current elapsed time string.
     */
    String currentElapsedTimeString = "";

    /**
     * Provide a seek bar to track song progress
     */
    private void startSeekBar() {

        // get the track time
        int trackDuration = mPlaybackService.getTrackDuration();

        mSeekBar.setMax(trackDuration);

        // build elapsed time and track duration string correctly
        int minutes = trackDuration / 60;
        trackDuration = trackDuration - minutes * 60;
        int seconds = trackDuration;

        String minString = String.valueOf(minutes);
        if (minutes < 10)
            minString = "0".concat(minString);

        String secString = String.valueOf(seconds);
        if (seconds < 10)
            secString = "0".concat(secString);

        String trackDurationString = minString.concat(":").concat(secString);

        mTrackDurationView.setText(trackDurationString);

        mElapsedTimeView.setText("00:00");
        currentMin = 0;
        currentSec = 0;

        // creates a new thread to update the elapsed time
        // and to update progress in the seekbar
        setSeekBarHandler();


        // set a listener to update track progress at media player when
        // user manually changes it
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // provide a touch feedaback
                seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlaybackService.setProgress(progress * 1000);
                    setElapsedTime(progress);
                    setSeekBarHandler();
                }
            }
        });
    }

    /**
     * Sets seek bar handler.
     */
    protected void setSeekBarHandler() {

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = new Handler();

        // update seek bar progress every 1 sec
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int mCurrentPosition = mPlaybackService.getCurrentPosition();
                if (mCurrentPosition >= 0) {
                    mSeekBar.setProgress(mCurrentPosition);
                    currentSec = currentSec + 1;
                    if (currentSec == 60) {
                        currentMin = currentMin + 1;
                        currentSec = 0;
                    }
                    currentMinString = String.valueOf(currentMin);
                    currentSecString = String.valueOf(currentSec);
                    if (currentMin < 10)
                        currentMinString = "0".concat(currentMinString);
                    if (currentSec < 10)
                        currentSecString = "0".concat(currentSecString);

                    currentElapsedTimeString = currentMinString.concat(":").concat(currentSecString);

                    mElapsedTimeView.setText(currentElapsedTimeString);
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    /**
     * Sets elapsed time.
     *
     * @param time the time
     */
    protected void setElapsedTime(int time) {

        currentMin = time / 60;
        time = time - currentMin * 60;
        currentSec = time;

        currentMinString = String.valueOf(currentMin);
        currentSecString = String.valueOf(currentSec);
        if (currentMin < 10)
            currentMinString = "0".concat(currentMinString);
        if (currentSec < 10)
            currentSecString = "0".concat(currentSecString);

        currentElapsedTimeString = currentMinString.concat(":").concat(currentSecString);

        mElapsedTimeView.setText(currentElapsedTimeString);
    }


    /**
     * Sets service connection.
     */
    protected void setServiceConnection() {
        mPlayBackServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlaybackService.PlaybackBinder binder = (PlaybackService.PlaybackBinder) service;

                mPlaybackService = binder.getService();

                mPlaybackService.setPlaybackFragment(PlaybackFragment.this);

                // set the tracks array list
                mPlaybackService.setTracks(getTracks());

                mPlaybackService.init(mPosition);

                mPlaybackService.start();

                mIsBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIsBound = false;
            }
        };
    }

    /**
     * Gets service connection.
     *
     * @return the service connection
     */
    protected ServiceConnection getServiceConnection() {
        return this.mPlayBackServiceConnection;
    }

    /**
     * Next button
     *
     * @param v the v
     */
    @OnClick(R.id.playback_next)
    void next(View v) {
        if (v != null)
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        if (mPlaybackService != null) {
            mPlaybackService.next();
        }
    }

    /**
     * Previous.
     *
     * @param v the v
     */
    @OnClick(R.id.playback_previous)
    void previous(View v){
        if (v != null)
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        if (mPlaybackService != null) {
            mPlaybackService.previous();
        }
    }


    /**
     * Pause button
     *
     * @param v the v
     */
    @OnClick(R.id.playback_pause)
    void pause(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        v.setVisibility(View.GONE);
        mPlayView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mPlaybackService.pause();
    }


    /**
     * Play button
     *
     * @param v the v
     */
    @OnClick(R.id.playback_play)
    void play(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        v.setVisibility(View.GONE);
        mPauseView.setVisibility(View.VISIBLE);
        mPlaybackService.play();
        setSeekBarHandler();
    }


    /**
     * Close.
     *
     * @param v the v
     */
    @OnClick(R.id.playback_close)
    void close(View v) {

        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        TracksFragment tracksFragment = (TracksFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);
        getActivity().getSupportFragmentManager().beginTransaction().attach(tracksFragment).commit();

        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (mPlayView.getVisibility() == View.VISIBLE) {
            mPlayView.setVisibility(View.GONE);
            mPauseView.setVisibility(View.VISIBLE);
        }
        updateUi(mPlaybackService.getCurrentTrack());

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_LONG);
        return false;
    }



    @Override
    public void onStop() {
        super.onStop();
        if(getActivity().getClass().isAssignableFrom(TracksActivity.class)){
            ((TracksActivity)getActivity()).updateNowPlaying(mPlaybackService.getTrackInfo());
        }
    }
}
