package com.framgia.music.utils;

import android.support.annotation.StringDef;
import com.framgia.music.BuildConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.framgia.music.utils.Constant.Genres.ALTERNATIVEROCK;
import static com.framgia.music.utils.Constant.Genres.AMBIENT;
import static com.framgia.music.utils.Constant.Genres.CLASSICAL;
import static com.framgia.music.utils.Constant.Genres.COUNTRY;

/**
 * Created by Admin on 3/8/2018.
 */

public final class Constant {

    @StringDef({ ALTERNATIVEROCK, AMBIENT, CLASSICAL, COUNTRY })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Genres {
        String ALTERNATIVEROCK = "alternativerock";
        String AMBIENT = "ambient";
        String CLASSICAL = "classical";
        String COUNTRY = "country";
    }

    public static final String CLIENT_ID = "?client_id=" + BuildConfig.API_KEY;
    public static final String KIND = "kind";
    public static final String ID = "id";
    public static final String URI = "uri";
    public static final String USER_ID = "user_id";
    public static final String GENRE = "genre";
    public static final String TITLE = "title";
    public static final String STREAM_URL = "stream_url";
    public static final String ARTWORK_URL = "artwork_url";
    public static final String USER = "user";
    public static final String USER_NAME = "username";
    public static final String AVATAR_URL = "avatar_url";
    public static final String DOWNLOADABLE = "downloadable";
    public static final String NEXT_HREF = "next_href";
    public static final String COLLECTION = "collection";
    public static final String EXTRAS_COLLECTION = "EXTRAS_COLLECTION";
    public static final String POSITION = "position";
    public static final String TAG_PLAY_FRAGMENT = "tag_play_fragment";
    public static final int DURATION_1500 = 1500;
    public static final float FROM_0 = 0.0f;
    public static final float TO_360 = 360.0f;
    public static final float PIVOT_0_5 = 0.5f;
    public static final String TRACK_GENRES_URL =
            "http://api.soundcloud.com/tracks" + CLIENT_ID + "&linked_partitioning=1&genres=";
    public static final String TRACK_SEARCH_URL =
            "http://api.soundcloud.com/tracks" + CLIENT_ID + "&linked_partitioning=1&q=";
    public static final String TRENDING_TRACK_URL = "http://api.soundcloud.com/tracks"
            + CLIENT_ID + "&kind=trending&limit=5&order=created_at&linked_partitioning=1";
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";
    public static final String REPEAT_ONE = "repeat_one";
    public static final String NON_REPEAT = "non_repeat";
    public static final String SETUP = "setup";
    public static final String SETUP_MUSIC_PREFERENCES = "setup_music_preferences";
    public static final String ISLOCALTRACK = "isLocalTrack";
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Constant() {
    }
}
