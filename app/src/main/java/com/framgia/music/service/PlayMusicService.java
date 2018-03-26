package com.framgia.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.main.MainActivity;
import com.framgia.music.utils.Constant;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 3/23/2018.
 */

public class PlayMusicService extends Service implements MediaPlayer.OnPreparedListener {

    private static final int NOTIFICATION_ID = 101;
    private static final int PRIORITY_RECEIVE = 2;
    private static final String NULL = "null";
    private static final String TAG = "Exception";
    private MediaPlayer mMediaPlayer;
    private IBinder mIBinder = new LocalBinder();
    private int mTrackIndex;
    private List<Track> mTrackList;
    private Collection mCollection;
    private boolean isShuffle, isRepeat, isRepeatOne, isNonRepeat;
    private String mSetup;
    private SharedPreferences mSharedPreferences;
    private boolean isLocalTrack;
    private NotificationManager mNotificationManager;
    private Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        getDataFromSharedPreferences();
        registerBroadcastReceive();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCollection = intent.getParcelableExtra(Constant.EXTRAS_COLLECTION);
        mTrackIndex = intent.getIntExtra(Constant.POSITION, -1);
        isLocalTrack = intent.getBooleanExtra(Constant.ISLOCALTRACK, false);
        if (mCollection != null && mTrackIndex != -1) {
            mTrackList = mCollection.getTrackList();
            initMediaPlayer();
            buildNotification();
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

    public static Intent getTracksIntent(Context context, Collection collection, int position,
            boolean localTrack) {
        Intent intent = new Intent(context, PlayMusicService.class);
        intent.putExtra(Constant.EXTRAS_COLLECTION, collection);
        intent.putExtra(Constant.POSITION, position);
        intent.putExtra(Constant.ISLOCALTRACK, localTrack);
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
            if (isLocalTrack) {
                mMediaPlayer.setDataSource(mTrackList.get(mTrackIndex).getStreamUrl());
            } else {
                mMediaPlayer.setDataSource(
                        mTrackList.get(mTrackIndex).getStreamUrl() + Constant.CLIENT_ID);
            }
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
        updateNotification(Constant.ACTION_PLAY);
    }

    public void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            updateNotification(Constant.ACTION_PAUSE);
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
        updateNotification(Constant.ACTION_PREVIOUS);
    }

    public void playTrack(int index) {
        mTrackIndex = index;
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
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
        updateNotification(Constant.ACTION_NEXT);
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

    public void refreshData(List<Track> trackList, int position, boolean localTrack) {
        mTrackList = trackList;
        mTrackIndex = position;
        isLocalTrack = localTrack;
    }

    private void buildNotification() {
        String trackName = mTrackList.get(mTrackIndex).getTitle();
        String userName = mTrackList.get(mTrackIndex).getArtist().getUsername();
        String imageString = mTrackList.get(mTrackIndex).getArtworkUrl();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        mNotification = new Notification.Builder(getApplicationContext()).setSmallIcon(
                R.drawable.ic_skip_next_white_36dp)
                .setContentTitle(trackName)
                .setContentText(userName)
                .setContentIntent(pendingIntent)
                .build();
        mNotification.contentView = remoteViews;
        mNotification.contentView.setTextViewText(R.id.text_track_name, trackName);
        mNotification.contentView.setTextViewText(R.id.text_user_name, userName);
        NotificationTarget notificationTarget =
                new NotificationTarget(this, R.id.image_track, mNotification.contentView,
                        mNotification, NOTIFICATION_ID);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageString)
                .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                .into(notificationTarget);
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        setListener(mNotification.contentView);
        startForeground(NOTIFICATION_ID, mNotification);
    }

    public void setListener(RemoteViews views) {
        Intent previous = new Intent(Constant.ACTION_PREVIOUS);
        Intent pause = new Intent(Constant.ACTION_PAUSE);
        Intent next = new Intent(Constant.ACTION_NEXT);
        Intent play = new Intent(Constant.ACTION_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.image_previous, pPrevious);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.image_pause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.image_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.image_play, pPlay);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Constant.ACTION_PREVIOUS:
                        preTrack();
                        break;
                    case Constant.ACTION_NEXT:
                        nextTrack(true);
                        break;
                    case Constant.ACTION_PLAY:
                        playMedia();
                        break;
                    case Constant.ACTION_PAUSE:
                        pauseMedia();
                        break;
                }
            }
        }
    };

    private void registerBroadcastReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_PLAY);
        intentFilter.addAction(Constant.ACTION_NEXT);
        intentFilter.addAction(Constant.ACTION_PAUSE);
        intentFilter.addAction(Constant.ACTION_PREVIOUS);
        intentFilter.setPriority(PRIORITY_RECEIVE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void updateNotification(String action) {
        if (action.equals(Constant.ACTION_NEXT) || action.equals(Constant.ACTION_PREVIOUS)) {
            mNotification.contentView.setTextViewText(R.id.text_track_name,
                    mTrackList.get(mTrackIndex).getTitle());
            mNotification.contentView.setTextViewText(R.id.text_user_name,
                    mTrackList.get(mTrackIndex).getArtist().getUsername());
            NotificationTarget notificationTarget =
                    new NotificationTarget(this, R.id.image_track, mNotification.contentView,
                            mNotification, NOTIFICATION_ID);
            String art = getArt();
            if (art.equals(NULL)) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.ic_logo)
                        .into(notificationTarget);
            } else {
                Glide.with(getApplicationContext()).asBitmap().load(art).into(notificationTarget);
            }
        } else if (action.equals(Constant.ACTION_PLAY)) {
            mNotification.contentView.setTextViewText(R.id.text_track_name,
                    mTrackList.get(mTrackIndex).getTitle());
            mNotification.contentView.setTextViewText(R.id.text_user_name,
                    mTrackList.get(mTrackIndex).getArtist().getUsername());
            NotificationTarget notificationTarget =
                    new NotificationTarget(this, R.id.image_track, mNotification.contentView,
                            mNotification, NOTIFICATION_ID);
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(mTrackList.get(mTrackIndex).getArtworkUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                    .into(notificationTarget);
            mNotification.contentView.setViewVisibility(R.id.image_play, View.GONE);
            mNotification.contentView.setViewVisibility(R.id.image_pause, View.VISIBLE);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        } else if (action.equals(Constant.ACTION_PAUSE)) {
            mNotification.contentView.setViewVisibility(R.id.image_pause, View.GONE);
            mNotification.contentView.setViewVisibility(R.id.image_play, View.VISIBLE);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }
}
