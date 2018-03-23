package com.framgia.music.screen.playmusicscreen;

import com.framgia.music.data.model.Collection;
import com.framgia.music.screen.BasePresenter;

/**
 * Created by Admin on 3/12/2018.
 */

public interface PlayMusicConTract {
    interface View {

        void updateTrackList(Collection collection);

        void onError(Exception e);

    }

    interface Presenter extends BasePresenter<View> {

        void loadMoreDataTrackList(String nextHref);
    }
}
