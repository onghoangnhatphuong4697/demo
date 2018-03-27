package com.framgia.music.screen.tabdownload;

import android.content.Context;
import com.framgia.music.data.model.Collection;
import com.framgia.music.screen.BasePresenter;

/**
 * Created by Admin on 3/21/2018.
 */

public interface TabDownloadContract {

    interface View {
        void showAllTracksFromLocal(Collection collection);
        void showNoTracks();
    }

    interface Presenter extends BasePresenter<View> {
        void getTracksFromLocal(Context context);
    }
}
