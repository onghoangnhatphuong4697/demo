package com.framgia.music.data.source;

import android.content.Context;
import com.framgia.music.data.model.Collection;

/**
 * Created by Admin on 3/20/2018.
 */

public interface TrackDataSource {

    interface LocalDataSource extends TrackDataSource {

    }

    interface RemoteDataSource extends TrackDataSource {

        void getTrendingTrackList(RequestDataCallback<Collection> callback);

        void getTrackListByGenre(String genre, RequestDataCallback<Collection> callback);

        void loadMoreDataTrackList(String nextHref, RequestDataCallback<Collection> callback);

        void downloadTrack(Context context, String url, String fileName,
                RequestDataCallback<String> callback);

        void searchTracks(String href, RequestDataCallback<Collection> callback);
    }
}
