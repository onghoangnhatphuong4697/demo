package com.framgia.music.data.source;

/**
 * Created by Admin on 3/20/2018.
 */

public interface RequestDataCallback<T> {
    void onSuccess(T data);

    void onFail(Exception e);
}
