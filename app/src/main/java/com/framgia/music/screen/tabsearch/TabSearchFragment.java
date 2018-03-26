package com.framgia.music.screen.tabsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.EndlessRecyclerViewScrollListener;
import com.framgia.music.screen.playmusicscreen.PlayMusicFragment;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.ConnectionUtils;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabSearchFragment extends BaseFragment
        implements SearchView.OnQueryTextListener, TabSearchContract.View,
        TrackListAdapter.ItemClickListener, SearchView.OnCloseListener {

    private RecyclerView mRecyclerViewSearch;
    private Toolbar mToolbar;
    private TabSearchContract.Presenter mPresenter;
    private Collection mCollection;
    private TrackListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabsearch_fragment, container, false);
        initViews(view);
        setHasOptionsMenu(true);
        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(), null);
        mPresenter = new TabSearchPresenter(trackRepository);
        mPresenter.setView(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.tabsearch_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mAdapter.checkData()) {
            mAdapter.clearData();
        }

        if (ConnectionUtils.isInternetConnected(getContext())) {
            mPresenter.searchTracks(Constant.TRACK_SEARCH_URL + query);
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void showNoTrack() {
        //No-op
    }

    @Override
    public void showTracks(Collection collection) {
        mCollection = new Collection();
        mCollection.setTrackList(collection.getTrackList());
        mCollection.setNextHref(collection.getNextHref());
        mAdapter.addData(collection.getTrackList());

        mRecyclerViewSearch.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (mCollection != null && ConnectionUtils.isInternetConnected(
                                getContext())) {
                            loadNextDataFromApi(mCollection.getNextHref());
                        }
                    }
                });
    }

    @Override
    public void updateTrackList(Collection collection) {
        mCollection.getTrackList().addAll(collection.getTrackList());
        mCollection.setNextHref(collection.getNextHref());
        mAdapter.addData(collection.getTrackList());
    }

    @Override
    public void onError(Exception e) {
        //TODO
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
                    PlayMusicFragment.newInstance(mCollection, position, false),
                    Constant.TAG_PLAY_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            playMusicFragment.refreshData(mCollection, position, false);
            fragmentTransaction.show(playMusicFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onClose() {
        mAdapter.clearData();
        mCollection = null;
        return false;
    }

    private void initViews(View view) {
        mRecyclerViewSearch = view.findViewById(R.id.recycler_tracks_search);
        mToolbar = view.findViewById(R.id.tool_bar);
        mToolbar.setTitle(getContext().getResources().getString(R.string.search));
        mToolbar.setTitleTextColor(getContext().getResources().getColor(R.color.colorGray));
        mToolbar.setTitleMarginStart((int) getResources().getDimension(R.dimen.dp_80));
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        mAdapter = new TrackListAdapter(getContext(), this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewSearch.setLayoutManager(mLayoutManager);
        mRecyclerViewSearch.setAdapter(mAdapter);
        mCollection = new Collection();
    }

    private void loadNextDataFromApi(String nextHref) {
        mPresenter.loadMoreDataTrackList(nextHref);
    }
}
