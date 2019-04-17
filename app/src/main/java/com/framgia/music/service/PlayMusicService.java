package com.framgia.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.utils.Constant;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by Admin on 3/23/2018.
 */

public class PlayMusicService extends Service implements MediaPlayer.OnPreparedListener {

    private static final int NOTIFICATION_ID = 101;
    private static final int PRIORITY_RECEIVE = 2;
    private static final String NULL = "null";
    private static final String TAG = "Exception";
    private MediaPlayer mMediaPlayer;
    private String sChannelId = "music_app_id";
    private static final String sChannelName = "Music_App_Name";
    private IBinder mIBinder = new LocalBinder();
    private int pausePlayIcon = R.drawable.ic_pause_black_36dp;
    private int mTrackIndex;
    private List<Track> mTrackList;
    private Collection mCollection;
    private boolean isShuffle, isRepeat, isRepeatOne, isNonRepeat;
    private String mSetup;
    private SharedPreferences mSharedPreferences;
    private boolean isLocalTrack;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;

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
            loadBitmapToStartNotification(false);
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
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
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
            pausePlayIcon = R.drawable.ic_pause_black_36dp;
            thread.start();
            loadBitmapToStartNotification(true);
        }

        //updateNotification(Constant.ACTION_PLAY);
    }

    public Track getTrackCurrent() {
        return mTrackList.get(mTrackIndex);
    }

    public void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            pausePlayIcon = R.drawable.ic_play_arrow_white_36dp;
            //updateNotification(Constant.ACTION_PAUSE);
            loadBitmapToStartNotification(true);
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

    private Observable<Bitmap> getBitmapArtistAvatar(final String urlString) {
        return Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                URL url = new URL(urlString);
                InputStream inputStream = url.openConnection().getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        });
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannelAndroidO(String channelId, String channelName) {
        NotificationChannel channel =
                new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(true);
        channel.setSound(null, null);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
        return channelId;
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
                        if (mMediaPlayer.isPlaying()) {
                            pauseMedia();
                        } else {
                            playMedia();
                        }
                        break;
                }
            }
        }
    };

    public Notification initForegroundService(Bitmap bitmap) {

        Intent iPre = new Intent(Constant.ACTION_PREVIOUS);
        Intent iPause = new Intent(Constant.ACTION_PAUSE);
        Intent iNext = new Intent(Constant.ACTION_NEXT);

        PendingIntent pPrevious =
                PendingIntent.getBroadcast(this, 0, iPre, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pPause =
                PendingIntent.getBroadcast(this, 0, iPause, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pNext =
                PendingIntent.getBroadcast(this, 0, iNext, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action actionPrevious =
                new NotificationCompat.Action.Builder(R.drawable.ic_skip_previous_black_36dp,
                        Constant.PREVIOUS, pPrevious).build();
        NotificationCompat.Action actionPause =
                new NotificationCompat.Action.Builder(pausePlayIcon, Constant.PLAY, pPause).build();
        NotificationCompat.Action actionNext =
                new NotificationCompat.Action.Builder(R.drawable.ic_skip_next_black_36dp,
                        Constant.NEXT, pNext).build();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(Constant.GO_TO_PLAYER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sChannelId = createNotificationChannelAndroidO(sChannelId, sChannelName);
        }

        mNotification = new NotificationCompat.Builder(this, sChannelId).setContentTitle(
                getString(R.string.text_notification_playing))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_default)
                .setLargeIcon(bitmap)
                .setContentTitle(mTrackList.get(mTrackIndex).getTitle())
                .setContentIntent(pendingIntent)
                .addAction(actionPrevious)
                .addAction(actionPause)
                .addAction(actionNext)
                .setDefaults(0)
                .setColor(Color.GRAY)
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(
                                0, 1, 2))
                .build();
        return mNotification;
    }

    private void loadBitmapToStartNotification(final Boolean isThenStop) {
        Disposable disposable = getBitmapArtistAvatar(
                mTrackList.get(mTrackIndex).getArtist().getAvatarUrl()).subscribeOn(
                Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {

                        startForeground(NOTIFICATION_ID, initForegroundService(bitmap));
                        if (isThenStop) {
                            stopForeground(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        startForeground(NOTIFICATION_ID, initForegroundService(null));
                    }
                });
    }

    private void registerBroadcastReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_PLAY);
        intentFilter.addAction(Constant.ACTION_NEXT);
        intentFilter.addAction(Constant.ACTION_PAUSE);
        intentFilter.addAction(Constant.ACTION_PREVIOUS);
        intentFilter.setPriority(PRIORITY_RECEIVE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }
}
