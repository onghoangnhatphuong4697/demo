package com.framgia.music.data.model;

import com.framgia.music.utils.Constant;

/**
 * Created by Admin on 3/8/2018.
 */

public class Genres {

    private @Constant.Genres
    String mGenre;

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(@Constant.Genres String genre) {
        mGenre = genre;
    }
}
