package com.framgia.music.data.source.remote;

import com.framgia.music.data.model.Song;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @GET("tracks?client_id=d3bb97412667a7812924715ea66498af")
    Observable<List<Song>> getGenreList(
        //@Query("linked_partitioning") int page,
        @Query("genre") String genre);
}
