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

    public static final int DURATION_1500 = 1500;
    public static final float FROM_0 = 0.0f;
    public static final float TO_360 = 360.0f;
    public static final float PIVOT_0_5 = 0.5f;
    private static final String BASE_URL = "http://api.soundcloud.com";
    public static final String TRACK_GENRES_URL = BASE_URL
            + "/tracks?client_id="
            + BuildConfig.API_KEY
            + "&linked_partitioning=1&genres=";
    public static final String TRACK_SEARCH_URL =
            BASE_URL + "/tracks?client_id=" + BuildConfig.API_KEY + "&linked_partitioning=1&q=";
    public static final String TRENDING_TRACK_URL = BASE_URL
            + "/tracks?client_id="
            + BuildConfig.API_KEY
            + "&kind=trending&limit=5&order=created_at&linked_partitioning=1";

    private Constant() {
    }
}
