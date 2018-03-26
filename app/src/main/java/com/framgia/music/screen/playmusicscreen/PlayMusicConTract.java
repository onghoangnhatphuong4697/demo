package com.framgia.music.screen.playmusicscreen;

import android.content.Context;
import com.framgia.music.data.model.Collection;
import com.framgia.music.screen.BasePresenter;

/**
 * Created by Admin on 3/12/2018.
 */

public interface PlayMusicConTract {
    interface View {

        void updateTrackList(Collection collection);

        void onError(Exception e);

        void downloadSuccess(String message);

        void downloadFail();
    }

    interface Presenter extends BasePresenter<View> {

        void loadMoreDataTrackList(String nextHref);

        void downloadTrack(Context context, String url, String fileName);
    }
}
