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

    public static final String BASE_URL = "http://api.soundcloud.com";
    public static final String TRACK_GENRES_URL =
            BASE_URL + "/tracks?client_id=" + BuildConfig.API_KEY + "&genres=";
    public static final String MUSIC_SEARCH_URL =
            BASE_URL + "/tracks?client_id=" + BuildConfig.API_KEY + "&q=";

    private Constant() {
    }
}
