package com.example.nhatphuong.demodagger2andrealm.data.source.remote;

import com.example.nhatphuong.demodagger2andrealm.data.model.MoreMovie;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiMovie {
    @GET("movie/popular" + "?api_key=f63b434c2eae10c869016441dc039f7b")
    Observable<MoreMovie> getPopularMovie(@Query("page") int NumPage);
}
