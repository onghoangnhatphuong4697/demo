package com.framgia.music.screen;

/**
 * Created by Admin on 3/8/2018.
 */

public interface BasePresenter<T> {

    void setView(T view);

    void onStart();

    void onStop();
}
