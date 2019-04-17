package com.framgia.music.data.source.remote;

import com.framgia.music.data.model.Song;
import com.framgia.music.data.source.TrackDataSource;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.RetrofitData;
import io.reactivex.Observable;
import java.util.List;

public class SongRemoteDataSource implements TrackDataSource.SongRemoteDataSource {
    private static SongRemoteDataSource mSongRemoteDataSource;

    public static synchronized SongRemoteDataSource getInstance() {
        if (mSongRemoteDataSource == null) {
            mSongRemoteDataSource = new SongRemoteDataSource();
        }
        return mSongRemoteDataSource;
    }

    private Api getData() {
        return RetrofitData.getDataFromUrl(Constant.BASE_URL).create(Api.class);
    }

    @Override
    public Observable<List<Song>> getSongByGenre(String genre) {
        return getData().getGenreList(genre);
    }
}
