package com.framgia.music.data.source.local;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.framgia.music.data.model.Artist;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.source.RequestDataCallback;
import com.framgia.music.data.source.TrackDataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/9/2018.
 */

public class TrackLocalDataSource implements TrackDataSource.LocalDataSource {

    private static TrackLocalDataSource mTrackLocalDataSource;
    private static final String ARTWORK_URI = "content://media/external/audio/albumart";

    public static synchronized TrackLocalDataSource getInstance() {
        if (mTrackLocalDataSource == null) {
            mTrackLocalDataSource = new TrackLocalDataSource();
        }
        return mTrackLocalDataSource;
    }

    @Override
    public void getAllTracksFromLocal(Context context, RequestDataCallback<Collection> callback) {

        Collection collection = getData(context);
        if (collection.getTrackList() != null) {
            callback.onSuccess(collection);
        } else {
            callback.onFail(null);
        }
    }

    private Collection getData(Context context) {
        Collection collection = new Collection();
        List<Track> trackList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver()
                .query(uri, null, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Uri sArtworkUri = Uri.parse(ARTWORK_URI);
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, id);
                String artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                Track track = new Track();
                track.setId(id);
                track.setKind("");
                track.setUri("");
                track.setUserId(0);
                track.setGenre("");
                track.setTitle(title);
                track.setStreamUrl(path);
                track.setArtworkUrl(albumArtUri.toString());
                track.setDownloadable(false);
                Artist artist1 = new Artist();
                artist1.setAvatarUrl("");
                artist1.setId(0);
                artist1.setUsername(artist);
                track.setUser(artist1);
                trackList.add(track);
                cursor.moveToNext();
            }
            collection.setTrackList(trackList);
            collection.setNextHref("");
            cursor.close();
        }
        return collection;
    }
}
