package com.framgia.music.data.repository;

import android.content.Context;
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

    @Override
    public void getTrackListByGenre(String genre, RequestDataCallback<Collection> callback) {
        mTrackRemoteDataSource.getTrackListByGenre(genre, callback);
    }

    @Override
    public void loadMoreDataTrackList(String nextHref, RequestDataCallback<Collection> callback) {
        mTrackRemoteDataSource.loadMoreDataTrackList(nextHref, callback);
    }

    @Override
    public void downloadTrack(Context context, String url, String fileName,
            RequestDataCallback<String> callback) {
        mTrackRemoteDataSource.downloadTrack(context, url, fileName, callback);
    }

    @Override
    public void searchTracks(String href, RequestDataCallback<Collection> callback) {
        mTrackRemoteDataSource.searchTracks(href, callback);
    }
    @Override
    public void getAllTracksFromLocal(Context context, RequestDataCallback<Collection> callback) {
        mTrackLocalDataSource.getAllTracksFromLocal(context, callback);
    }
}
