package com.framgia.music.data.repository;

import com.framgia.music.data.model.Song;
import com.framgia.music.data.source.TrackDataSource;
import com.framgia.music.data.source.remote.SongRemoteDataSource;
import io.reactivex.Observable;
import java.util.List;

public class SongRepository implements TrackDataSource.SongRemoteDataSource {
    private SongRemoteDataSource mSongRemoteDataSource;
    private static SongRepository mSongRepository;

    public static synchronized SongRepository getInstance(
        SongRemoteDataSource songRemoteDataSource) {
        if (mSongRepository == null) {
            mSongRepository = new SongRepository(songRemoteDataSource);
        }
        return mSongRepository;
    }

    private SongRepository(SongRemoteDataSource songRemoteDataSource) {
        mSongRemoteDataSource = songRemoteDataSource;
    }

    @Override
    public Observable<List<Song>> getSongByGenre(String genre) {
        return mSongRemoteDataSource.getSongByGenre(genre);
    }
}
