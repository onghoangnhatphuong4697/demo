package com.framgia.music.screen.tabhome;

import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BasePresenter;
import java.util.List;

/**
 * Created by Admin on 3/20/2018.
 */

public interface TabHomeContract {

    interface View {
        void showSlider(List<Track> trackList);
    }

    interface Presenter extends BasePresenter<View> {
        void getTrendingTrackList();
    }
}
