package com.example.nhatphuong.demodagger2andrealm.data.source;

import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import com.example.nhatphuong.demodagger2andrealm.data.model.Movie;
import io.reactivex.Observable;

public interface MovieDataSource {

    interface RemoteDataSoure{
        Observable<MoreMovie> getMovieData(int numPage);
    }

    interface LocalDataSource{

    }
}
