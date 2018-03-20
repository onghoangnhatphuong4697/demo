package com.framgia.music.screen.tabhome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import java.util.List;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabHomeFragment extends BaseFragment implements TabHomeContract.View {
    private TabHomeContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabhome_fragment, container, false);

        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(), null);
        mPresenter = new TabHomePresenter(trackRepository);
        mPresenter.setView(this);
        mPresenter.getTrendingTrackList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void showSlider(List<Track> trackList) {
        //TODO show slider
    }
}
