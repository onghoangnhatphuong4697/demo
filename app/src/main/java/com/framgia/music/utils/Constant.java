package com.framgia.music.utils;

import android.support.annotation.StringDef;
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

    private Constant() {
    }
}
