package com.example.nhatphuong.demodagger2andrealm.data.source.remote;

import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import com.example.nhatphuong.demodagger2andrealm.data.source.MovieDataSource;
import io.reactivex.Observable;

public class MovieRemoteDataSource implements MovieDataSource.RemoteDataSoure {

    private ApiMovie mApiMovie;

    public MovieRemoteDataSource(ApiMovie apiMovie) {
        mApiMovie = apiMovie;
    }

    @Override
    public Observable<MoreMovie> getMovieData(int numPage) {
        return mApiMovie.getPopularMovie(numPage);
    }
}
