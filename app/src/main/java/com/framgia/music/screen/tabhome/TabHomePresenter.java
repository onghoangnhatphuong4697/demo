package com.framgia.music.screen.tabhome;

import android.util.Log;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.RequestDataCallback;

import static android.content.ContentValues.TAG;

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
                Log.e(TAG, "onFail: ", e);
            }
        });
    }
}
