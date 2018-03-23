package com.framgia.music.screen.tabhome;

import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.RequestDataCallback;
import com.framgia.music.utils.Constant;

/**
 * Created by Admin on 3/20/2018.
 */

public class TabHomePresenter implements TabHomeContract.Presenter {

    private TabHomeContract.View mView;
    private TrackRepository mTrackRepository;

    TabHomePresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(TabHomeContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
        
    }

    @Override
    public void onStop() {

    }

    @Override
    public void getTrendingTrackList() {
        mTrackRepository.getTrendingTrackList(new RequestDataCallback<Collection>() {
            @Override
            public void onSuccess(Collection data) {
                mView.showSlider(data.getTrackList());
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void getTrackListByGenre(String genre) {
        mTrackRepository.getTrackListByGenre(Constant.Genres.ALTERNATIVEROCK,
                new RequestDataCallback<Collection>() {
                    @Override
                    public void onSuccess(Collection data) {
                        mView.showAlternativeTrackList(data);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mView.onError(e);
                    }
                });

        mTrackRepository.getTrackListByGenre(Constant.Genres.AMBIENT,
                new RequestDataCallback<Collection>() {
                    @Override
                    public void onSuccess(Collection data) {
                        mView.showAmbientTrackList(data);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mView.onError(e);
                    }
                });

        mTrackRepository.getTrackListByGenre(Constant.Genres.CLASSICAL,
                new RequestDataCallback<Collection>() {
                    @Override
                    public void onSuccess(Collection data) {
                        mView.showClassicalTrackList(data);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mView.onError(e);
                    }
                });

        mTrackRepository.getTrackListByGenre(Constant.Genres.COUNTRY,
                new RequestDataCallback<Collection>() {
                    @Override
                    public void onSuccess(Collection data) {
                        mView.showCountryTrackList(data);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mView.onError(e);
                    }
                });
    }

    @Override
    public void loadMoreDataTrackList(String nextHref, String genre) {
        mTrackRepository.loadMoreDataTrackList(nextHref, new RequestDataCallback<Collection>() {
            @Override
            public void onSuccess(Collection data) {
                mView.updateTrackList(data, genre);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
