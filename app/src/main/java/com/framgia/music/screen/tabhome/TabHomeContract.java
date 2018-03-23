package com.framgia.music.screen.tabhome;

import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BasePresenter;
import com.framgia.music.utils.Constant;
import java.util.List;

/**
 * Created by Admin on 3/20/2018.
 */

public interface TabHomeContract {

    interface View {

        void showSlider(List<Track> trackList);

        void showAlternativeTrackList(Collection collection);

        void showAmbientTrackList(Collection collection);

        void showClassicalTrackList(Collection collection);

        void showCountryTrackList(Collection collection);

        void updateTrackList(Collection collection, @Constant.Genres String genre);

        void onError(Exception e);
    }

    interface Presenter extends BasePresenter<View> {

        void getTrendingTrackList();

        void getTrackListByGenre(@Constant.Genres String genre);

        void loadMoreDataTrackList(String nextHref, @Constant.Genres String genre);
    }
}
