package com.framgia.music.data.repository;

import com.framgia.music.data.model.Collection;
import com.framgia.music.data.source.RequestDataCallback;
import com.framgia.music.data.source.TrackDataSource;
import com.framgia.music.data.source.local.TrackLocalDataSource;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;

/**
 * Created by Admin on 3/20/2018.
 */

public final class TrackRepository
        implements TrackDataSource.RemoteDataSource, TrackDataSource.LocalDataSource {

    private static TrackRepository mTrackRepository;
    private TrackRemoteDataSource mTrackRemoteDataSource;
    private TrackLocalDataSource mTrackLocalDataSource;

    private TrackRepository(TrackRemoteDataSource trackRemoteDataSource,
            TrackLocalDataSource trackLocalDataSource) {
        mTrackRemoteDataSource = trackRemoteDataSource;
        mTrackLocalDataSource = trackLocalDataSource;
    }

    public static synchronized TrackRepository getInstance(
            TrackRemoteDataSource trackRemoteDataSource,
            TrackLocalDataSource trackLocalDataSource) {
        if (mTrackRepository == null) {
            mTrackRepository = new TrackRepository(trackRemoteDataSource, trackLocalDataSource);
        }
        return mTrackRepository;
    }

    public static void destroyInstance() {
        mTrackRepository = null;
    }

    @Override
    public void getTrendingTrackList(RequestDataCallback<Collection> callback) {
        mTrackRemoteDataSource.getTrendingTrackList(callback);
    }
}
