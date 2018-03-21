package com.framgia.music.data.source;

import com.framgia.music.data.model.Collection;

/**
 * Created by Admin on 3/20/2018.
 */

public interface TrackDataSource {

    interface LocalDataSource extends TrackDataSource {

    }

    interface RemoteDataSource extends TrackDataSource {
        void getTrendingTrackList(RequestDataCallback<Collection> callback);
    }
}
