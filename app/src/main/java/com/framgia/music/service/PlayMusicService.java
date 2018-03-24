package com.framgia.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.utils.Constant;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 3/23/2018.
 */

public class PlayMusicService extends Service implements MediaPlayer.OnPreparedListener {

    private static final String TAG = "Exception";
    private MediaPlayer mMediaPlayer;
    private IBinder mIBinder = new LocalBinder();
    private int mTrackIndex;
    private List<Track> mTrackList;
    private Collection mCollection;
    private boolean isShuffle, isRepeat, isRepeatOne, isNonRepeat;
    private String mSetup;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        getDataFromSharedPreferences();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCollection = intent.getParcelableExtra(Constant.EXTRAS_COLLECTION);
        mTrackIndex = intent.getIntExtra(Constant.POSITION, -1);
        if (mCollection != null && mTrackIndex != -1) {
            mTrackList = mCollection.getTrackList();
            initMediaPlayer();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playMedia();
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }
        super.onDestroy();
    }

    public static Intent getTracksIntent(Context context, Collection collection, int position) {
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtra(Constant.EXTRAS_COLLECTION, collection);
        intent.putExtra(Constant.POSITION, position);
        return intent;
    }

    public class LocalBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }

    public void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(
                    mTrackList.get(mTrackIndex).getStreamUrl() + Constant.CLIENT_ID);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            stopSelf();
        }
    }

    public String getUserName() {
        return mTrackList.get(mTrackIndex) != null ? mTrackList.get(mTrackIndex)
                .getArtist()
                .getUsername() : "";
    }

    public String getSongName() {
        return mTrackList.get(mTrackIndex) != null ? mTrackList.get(mTrackIndex).getTitle() : "";
    }

    public int getSongDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void fastForward(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void playMedia() {
        if (!mMediaPlayer.isPlaying()) {
            Thread thread = new Thread(() -> mMediaPlayer.start());
            thread.start();
        }
    }

    public void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void stopMedia() {
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void preTrack() {
        if (mTrackIndex == 0) {
            mTrackIndex = mTrackList.size() - 1;
        } else {
            mTrackIndex--;
        }
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    public void playTrack(int index) {
        mTrackIndex = index;
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    public void nextTrack(boolean controlByHand) {
        if (!controlByHand) { // no action by user
            if (isShuffle) {
                Random rand = new Random();
                mTrackIndex = rand.nextInt(mTrackList.size());
            } else if (isRepeat) {
                if (mTrackIndex == mTrackList.size() - 1) {
                    mTrackIndex = 0;
                } else {
                    mTrackIndex++;
                }
            }
            if (isNonRepeat) {
                if (mTrackIndex == mTrackList.size() - 1) {
                    return;
                } else {
                    mTrackIndex++;
                }
            }
        } else { // action by user
            if (mTrackIndex == mTrackList.size() - 1) {
                mTrackIndex = 0;
            } else {
                mTrackIndex++;
            }
        }
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    public void setupMusic() {
        if (isRepeat) {
            isRepeatOne = true;
            isShuffle = false;
            isNonRepeat = false;
            isRepeat = false;
            mSetup = Constant.REPEAT_ONE;
        } else if (isRepeatOne) {
            isShuffle = true;
            isNonRepeat = false;
            isRepeat = false;
            isRepeatOne = false;
            mSetup = Constant.SHUFFLE;
        } else if (isShuffle) {
            isNonRepeat = true;
            isRepeatOne = false;
            isRepeat = false;
            isShuffle = false;
            mSetup = Constant.NON_REPEAT;
        } else if (isNonRepeat) {
            isRepeat = true;
            isNonRepeat = false;
            isShuffle = false;
            isRepeatOne = false;
            mSetup = Constant.REPEAT;
        }
        setupPreferences();
    }

    public String getSetup() {
        return mSetup;
    }

    private void setupPreferences() {
        mSharedPreferences = getSharedPreferences(Constant.SETUP_MUSIC_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constant.SHUFFLE, isShuffle);
        editor.putBoolean(Constant.REPEAT, isRepeat);
        editor.putBoolean(Constant.REPEAT_ONE, isRepeatOne);
        editor.putBoolean(Constant.NON_REPEAT, isNonRepeat);
        editor.putString(Constant.SETUP, mSetup);
        editor.apply();
    }

    private void getDataFromSharedPreferences() {
        mSharedPreferences = getSharedPreferences(Constant.SETUP_MUSIC_PREFERENCES, MODE_PRIVATE);
        isRepeat = mSharedPreferences.getBoolean(Constant.REPEAT, false);
        isRepeatOne = mSharedPreferences.getBoolean(Constant.REPEAT_ONE, false);
        isShuffle = mSharedPreferences.getBoolean(Constant.SHUFFLE, false);
        isNonRepeat = mSharedPreferences.getBoolean(Constant.NON_REPEAT, true);
    }

    public void updateTrackList(List<Track> trackList) {
        mTrackList = trackList;
    }

    public int getTrackIndex() {
        return mTrackIndex;
    }

    public String getArt() {
        return mTrackList.get(mTrackIndex) != null ? mTrackList.get(mTrackIndex).getArtworkUrl()
                : "";
    }

    public void refreshData(List<Track> trackList, int position) {
        mTrackList = trackList;
        mTrackIndex = position;
    }
}
