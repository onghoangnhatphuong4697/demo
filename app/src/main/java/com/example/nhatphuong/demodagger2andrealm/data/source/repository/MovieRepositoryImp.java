package com.example.nhatphuong.demodagger2andrealm.data.source.repository;

import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import com.example.nhatphuong.demodagger2andrealm.data.source.MovieDataSource;
import com.example.nhatphuong.demodagger2andrealm.data.source.remote.MovieRemoteDataSource;
import io.reactivex.Observable;

public class MovieRepositoryImp implements MovieDataSource.RemoteDataSoure {
    private MovieRemoteDataSource mMovieRemoteDataSource;

    public MovieRepositoryImp(MovieRemoteDataSource movieRemoteDataSource) {
        mMovieRemoteDataSource = movieRemoteDataSource;
    }

    @Override
    public Observable<MoreMovie> getMovieData(int numPage) {
        return mMovieRemoteDataSource.getMovieData(numPage);
    }
}
