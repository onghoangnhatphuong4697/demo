package com.framgia.music.screen.playmusicscreen;

import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.RequestDataCallback;

/**
 * Created by Admin on 3/12/2018.
 */

public class PlayMusicPresenter implements PlayMusicConTract.Presenter {

    private TrackRepository mTrackRepository;
    private PlayMusicConTract.View mView;

    PlayMusicPresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(PlayMusicConTract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
        //TODO
    }

    @Override
    public void onStop() {
        //TODO
    }

    @Override
    public void loadMoreDataTrackList(String nextHref) {
        mTrackRepository.loadMoreDataTrackList(nextHref, new RequestDataCallback<Collection>() {
            @Override
            public void onSuccess(Collection data) {
                mView.updateTrackList(data);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
