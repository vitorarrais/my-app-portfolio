package com.vitorarrais.spotify_streamer.activity.fragment;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.vitorarrais.spotify_streamer.model.TrackModel;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by vitor on 04/10/2015.
 */
public class PlaybackFragment extends DialogFragment {

    public static final String EXTRA_TRACK_TAG = "track";

    public static final String EXTRA_START_POS_TAG = "start_pos";


    @Bind(R.id.playback_album)
    TextView mAlbumNameView;

    @Bind(R.id.playback_album_img)
    ImageView mAlbumImageView;

    @Bind(R.id.playback_artist)
    TextView mArstistNameView;

    @Bind(R.id.playback_song)
    TextView mSongView;

    @Bind(R.id.playback_pause)
    ImageView mPauseView;

    @Bind(R.id.playback_play)
    ImageView mPlayView;

    @Bind(R.id.playback_previous)
    ImageView mPreviousView;

    @Bind(R.id.playback_seekbar)
    SeekBar mSeekBar;


    //variable for counting two successive up-down events
    int clickCount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration = 0;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 160;

    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    clickCount++;

                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();

                    break;
                case MotionEvent.ACTION_UP:
                    long time = System.currentTimeMillis() - startTime;
                    duration = duration + time;
                    if (clickCount == 2) {
                        if (duration <= MAX_DURATION) {

                            mPosition = mPosition - 1;
                            if (mPosition >= 0) {
                                TrackModel trackModel = TrackModel.from(mTopTracksList.get(mPosition));
                                startMediaPlayer(trackModel);
                            } else {
                                mPosition = mPosition + 1;
                            }
                        }
                        clickCount = 0;
                        duration = 0;
                        break;
                    }
            }
            return true;
        }
    };

    /**
     * Media player
     */
    MediaPlayer mMediaPlayer;

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

    Handler mHandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_playback, container, false);
        ButterKnife.bind(this, root);

        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mCurrentTrack = (TrackModel) bundle.get(PlaybackFragment.EXTRA_TRACK_TAG);
            mPosition = bundle.getInt(PlaybackFragment.EXTRA_START_POS_TAG);

            startMediaPlayer(mCurrentTrack);

        }

        TracksFragment tracksFragment = (TracksFragment)getActivity().getSupportFragmentManager()
                .findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);

        if (tracksFragment!=null)
                mTopTracksList = tracksFragment.getTopTracks();

        mPreviousView.setOnTouchListener(MyOnTouchListener);

        return root;
    }

    /** The system calls this only when creating the layout in a dialog. */
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
    }


    /**
     * Starts a new media player
     *
     * @param track
     */
    private void startMediaPlayer(TrackModel track) {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                if (mPlayView.getVisibility() == View.VISIBLE) {
                    mPlayView.setVisibility(View.GONE);
                    mPauseView.setVisibility(View.VISIBLE);
                }
                startSeekBar();
            }
        });

        // provide error feedback to user
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_LONG);
                return false;
            }
        });

        // next song automatically
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // call next song when current song is completed
                next(null);
            }
        });
        try {
            // load song URL
            mMediaPlayer.setDataSource(track.getPreview_url());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();

        // update UI with track information
        updateUi(track);
    }


    /**
     * Provide a seek bar to track song progress
     */
    private void startSeekBar() {

        mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);

        mHandler = new Handler();

        // update seek bar progress every 100 ms
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 100);
            }
        });

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
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress * 1000);
                }
            }
        });
    }


    /**
     * Next button
     */
    @OnClick(R.id.playback_next)
    void next(View v) {
        if (v != null)
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        mPosition = mPosition + 1;
        if (mPosition < mTopTracksList.size()) {
            TrackModel trackModel = TrackModel.from(mTopTracksList.get(mPosition));
            startMediaPlayer(trackModel);
        }
    }


    /**
     * Pause button
     *
     * @param v
     */
    @OnClick(R.id.playback_pause)
    void pause(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        v.setVisibility(View.GONE);
        mPlayView.setVisibility(View.VISIBLE);
        mMediaPlayer.pause();
    }


    /**
     * Play button
     *
     * @param v
     */
    @OnClick(R.id.playback_play)
    void play(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        v.setVisibility(View.GONE);
        mPauseView.setVisibility(View.VISIBLE);
        mMediaPlayer.start();
    }


    @OnClick(R.id.playback_close)
    void close(View v) {

        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        TracksFragment tracksFragment = (TracksFragment)getActivity().getSupportFragmentManager()
                .findFragmentByTag(MainActivity.TAG_TRACKS_FRAG);
        getActivity().getSupportFragmentManager().beginTransaction().attach(tracksFragment).commit();

        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
