package com.framgia.music.screen.playmusicscreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.EndlessRecyclerViewScrollListener;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.ConnectionUtils;

/**
 * Created by Admin on 3/12/2018.
 */

public class PlayMusicFragment extends BaseFragment
        implements PlayMusicConTract.View, PlayMusicAdapter.ItemClickListener {

    private static final String ARGUMENT_COLLECTION = "BUNDLE_ARGUMENT_COLLECTION";
    private static final int POSITION_START = 0;
    private PlayMusicConTract.Presenter mPresenter;
    private RecyclerView mRecyclerViewGenre;
    private PlayMusicAdapter mMusicAdapter;
    private Collection mCollection;
    private int mTrackIndex;
    private RecyclerView.LayoutManager mLayoutManager;

    public static PlayMusicFragment newInstance(Collection collection, int position) {
        PlayMusicFragment fragment = new PlayMusicFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_COLLECTION, collection);
        args.putInt(Constant.POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_music_fragment, container, false);
        initViews(view);
        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(), null);
        mPresenter = new PlayMusicPresenter(trackRepository);
        mPresenter.setView(this);
        Bundle bundle = getArguments();
        mCollection = bundle.getParcelable(ARGUMENT_COLLECTION);
        mTrackIndex = bundle.getInt(Constant.POSITION, 0);
        if (mCollection != null) {
            showTrackList();
        }
        return view;
    }

    public void showTrackList() {
        mMusicAdapter = new PlayMusicAdapter(getContext(), this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mMusicAdapter.addData(mCollection.getTrackList());
        mMusicAdapter.addPosition(mTrackIndex);

        mRecyclerViewGenre.setLayoutManager(mLayoutManager);
        mRecyclerViewGenre.setAdapter(mMusicAdapter);
        mMusicAdapter.notifyItemRangeChanged(POSITION_START, mMusicAdapter.getItemCount());
        mRecyclerViewGenre.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (ConnectionUtils.isInternetConnected(getContext())
                                && !mCollection.getNextHref().equals("")) {
                            loadNextDataFromApi(mCollection.getNextHref());
                        }
                    }
                });
    }

    @Override
    public void updateTrackList(Collection collection) {
        mCollection.getTrackList().addAll(collection.getTrackList());
        mCollection.setNextHref(collection.getNextHref());
        mMusicAdapter.addData(collection.getTrackList());
    }

    @Override
    public void onError(Exception e) {
        //TODO
    }

    @Override
    public void onItemClicked(int trackIndex) {
        //TODO
    }

    private void initViews(View view) {
        mRecyclerViewGenre = view.findViewById(R.id.recycler_genre);
    }

    private void loadNextDataFromApi(String nextHref) {
        mPresenter.loadMoreDataTrackList(nextHref);
    }
}
