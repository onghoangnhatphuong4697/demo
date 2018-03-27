package com.framgia.music.screen.tabdownload;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.local.TrackLocalDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.playmusicscreen.PlayMusicFragment;
import com.framgia.music.screen.tabsearch.TrackListAdapter;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.PermissionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabDownloadFragment extends BaseFragment
        implements TabDownloadContract.View, TrackListAdapter.ItemClickListener {

    private List<Track> mTrackList;
    private Collection mCollection;
    private RecyclerView mRecyclerView;
    private TrackListAdapter mTrackListAdapter;
    private TabDownloadContract.Presenter mPresenter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tabdownload_fragment, container, false);
        mTrackList = new ArrayList<>();
        mCollection = new Collection();
        mRecyclerView = view.findViewById(R.id.recycler_download);
        TrackRepository trackRepository =
                TrackRepository.getInstance(null, TrackLocalDataSource.getInstance());
        mPresenter = new TabDownloadPresenter(trackRepository);
        mPresenter.setView(this);
        if (getUserVisibleHint() && PermissionUtils.requestPermission(getActivity())) {
            mPresenter.getTracksFromLocal(getContext());
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.getTracksFromLocal(getContext());
                    Toast.makeText(getContext(), "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No granted", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClicked(int position) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down,
                R.anim.slide_out_down, R.anim.slide_out_up);
        PlayMusicFragment playMusicFragment =
                (PlayMusicFragment) fragmentManager.findFragmentByTag(Constant.TAG_PLAY_FRAGMENT);
        if (playMusicFragment == null) {
            fragmentTransaction.replace(R.id.frame_layout,
                    PlayMusicFragment.newInstance(mCollection, position, true),
                    Constant.TAG_PLAY_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            playMusicFragment.refreshData(mCollection, position, true);
            fragmentTransaction.show(playMusicFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showAllTracksFromLocal(Collection collection) {
        mCollection = collection;
        mTrackListAdapter = new TrackListAdapter(getContext(), this);
        mTrackListAdapter.addData(mCollection.getTrackList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTrackListAdapter);
    }

    @Override
    public void showNoTracks() {
        //No-op
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && PermissionUtils.requestPermission(getActivity())) {
            mPresenter.getTracksFromLocal(getContext());
        }
    }
}
