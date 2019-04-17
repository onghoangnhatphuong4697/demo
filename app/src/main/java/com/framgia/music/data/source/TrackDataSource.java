package com.framgia.music.data.source;

import android.content.Context;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Song;
import com.framgia.music.data.model.Track;
import io.reactivex.Observable;
import java.util.List;

/**
 * Created by Admin on 3/20/2018.
 */

public interface TrackDataSource {

    interface LocalDataSource extends TrackDataSource {
        void getAllTracksFromLocal(Context context, RequestDataCallback<Collection> callback);
    }

    interface RemoteDataSource extends TrackDataSource {

        void getTrendingTrackList(RequestDataCallback<Collection> callback);

        void getTrackListByGenre(String genre, RequestDataCallback<Collection> callback);

        void loadMoreDataTrackList(String nextHref, RequestDataCallback<Collection> callback);

        void downloadTrack(Context context, String url, String fileName,
                RequestDataCallback<String> callback);

        void searchTracks(String href, RequestDataCallback<Collection> callback);
    }

    interface SongRemoteDataSource {
        Observable<List<Song>> getSongByGenre(String genre);
    }
}
