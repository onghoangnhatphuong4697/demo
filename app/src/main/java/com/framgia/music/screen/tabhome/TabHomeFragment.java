package com.framgia.music.screen.tabhome;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.local.TrackLocalDataSource;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.EndlessRecyclerViewScrollListener;
import com.framgia.music.screen.playmusicscreen.PlayMusicFragment;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.ConnectionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabHomeFragment extends BaseFragment
        implements TabHomeContract.View, ViewPager.OnPageChangeListener,
        TrackListAdapter.ItemClickListener {

    private static final int DELAY_3000 = 3000;
    private TabHomeContract.Presenter mPresenter;
    private ViewPager mViewPagerSlider;
    private LinearLayout mLinearDot;
    private List<TrendingSliderFragment> mTrendingSliderFragmentList;
    private int mPagePosition = 0;
    private TextView[] mTextViewsDot;
    private RecyclerView mRecyclerViewRock, mRecyclerViewAmbient, mRecyclerViewClassical,
            mRecyclerViewCountry;
    private TrackListAdapter mAdapterRock, mAdapterAmbient, mAdapterClassical, mAdapterCountry;
    private Collection mRockCollection, mAmbientCollection, mClassicalCollection,
            mCountryCollection;
    private boolean isFragmentLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabhome_fragment, container, false);
        initViews(view);
        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance());
        mPresenter = new TabHomePresenter(trackRepository);
        mPresenter.setView(this);
        if (getUserVisibleHint() && !isFragmentLoaded) {
            if (ConnectionUtils.isInternetConnected(getContext())) {
                mPresenter.getTrendingTrackList();
                mPresenter.getTrackListByGenre(Constant.TRACK_GENRES_URL);
                onPageChanged();
                isFragmentLoaded = true;
            } else {
                Toast.makeText(getContext(), R.string.network_is_unavailable, Toast.LENGTH_SHORT)
                        .show();
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        mPresenter.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        mViewPagerSlider.removeOnPageChangeListener(this);
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded && isResumed()) {
            if (ConnectionUtils.isInternetConnected(getContext())) {
                mPresenter.getTrendingTrackList();
                mPresenter.getTrackListByGenre(Constant.TRACK_GENRES_URL);
                onPageChanged();
                isFragmentLoaded = true;
            } else {
                Toast.makeText(getContext(), R.string.network_is_unavailable, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void showSlider(List<Track> trackList) {
        mTrendingSliderFragmentList = new ArrayList<>();
        for (Track track : trackList) {
            mTrendingSliderFragmentList.add(TrendingSliderFragment.newInstance(track));
        }
        TrendingTrackViewPager trendingTrackViewPager =
                new TrendingTrackViewPager(getActivity().getSupportFragmentManager());
        trendingTrackViewPager.addData(mTrendingSliderFragmentList);
        mViewPagerSlider.setAdapter(trendingTrackViewPager);
        addDotIntoSlider();
    }

    @Override
    public void showAlternativeTrackList(Collection collection) {
        mRockCollection = collection;
        mAdapterRock = new TrackListAdapter(getContext(), this);
        mAdapterRock.addData(collection.getTrackList());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewRock.setLayoutManager(layoutManager);
        mRecyclerViewRock.setAdapter(mAdapterRock);
        mRecyclerViewRock.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (ConnectionUtils.isInternetConnected(getContext())) {
                            loadNextDataFromApi(mRockCollection.getNextHref(),
                                    Constant.Genres.ALTERNATIVEROCK);
                        }
                    }
                });
    }

    @Override
    public void showAmbientTrackList(Collection collection) {
        mAmbientCollection = collection;
        mAdapterAmbient = new TrackListAdapter(getContext(), this);
        mAdapterAmbient.addData(collection.getTrackList());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewAmbient.setLayoutManager(layoutManager);
        mRecyclerViewAmbient.setAdapter(mAdapterAmbient);
        mRecyclerViewAmbient.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (ConnectionUtils.isInternetConnected(getContext())) {
                            loadNextDataFromApi(mAmbientCollection.getNextHref(),
                                    Constant.Genres.AMBIENT);
                        }
                    }
                });

    }

    @Override
    public void showClassicalTrackList(Collection collection) {
        mClassicalCollection = collection;
        mAdapterClassical = new TrackListAdapter(getContext(), this);
        mAdapterClassical.addData(collection.getTrackList());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewClassical.setLayoutManager(layoutManager);
        mRecyclerViewClassical.setAdapter(mAdapterClassical);
        mRecyclerViewClassical.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (ConnectionUtils.isInternetConnected(getContext())) {
                            loadNextDataFromApi(mClassicalCollection.getNextHref(),
                                    Constant.Genres.CLASSICAL);
                        }
                    }
                });
    }

    @Override
    public void showCountryTrackList(Collection collection) {
        mCountryCollection = collection;
        mAdapterCountry = new TrackListAdapter(getContext(), this);
        mAdapterCountry.addData(collection.getTrackList());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewCountry.setLayoutManager(layoutManager);
        mRecyclerViewCountry.setAdapter(mAdapterCountry);
        mRecyclerViewCountry.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
                    @Override
                    public void onLoadMore() {
                        if (ConnectionUtils.isInternetConnected(getContext())) {
                            loadNextDataFromApi(mCountryCollection.getNextHref(),
                                    Constant.Genres.COUNTRY);
                        }
                    }
                });
    }

    @Override
    public void updateTrackList(Collection collection, String genre) {
        switch (genre) {
            case Constant.Genres.ALTERNATIVEROCK:
                mRockCollection.getTrackList().addAll(collection.getTrackList());
                mRockCollection.setNextHref(collection.getNextHref());
                mAdapterRock.addData(collection.getTrackList());
                break;
            case Constant.Genres.AMBIENT:
                mAmbientCollection.getTrackList().addAll(collection.getTrackList());
                mAmbientCollection.setNextHref(collection.getNextHref());
                mAdapterAmbient.addData(collection.getTrackList());
                break;
            case Constant.Genres.CLASSICAL:
                mClassicalCollection.getTrackList().addAll(collection.getTrackList());
                mClassicalCollection.setNextHref(collection.getNextHref());
                mAdapterClassical.addData(collection.getTrackList());
                break;
            case Constant.Genres.COUNTRY:
                mCountryCollection.getTrackList().addAll(collection.getTrackList());
                mCountryCollection.setNextHref(collection.getNextHref());
                mAdapterCountry.addData(collection.getTrackList());
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        //TODO
    }

    @Override
    public void onItemClicked(Track track, int position) {

        Collection collection;
        if (track.getGenre().equalsIgnoreCase(Constant.Genres.ALTERNATIVEROCK)) {
            collection = mRockCollection;
        } else if (track.getGenre().equalsIgnoreCase(Constant.Genres.AMBIENT)) {
            collection = mAmbientCollection;
        } else if (track.getGenre().equalsIgnoreCase(Constant.Genres.CLASSICAL)) {
            collection = mClassicalCollection;
        } else {
            collection = mCountryCollection;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down,
                R.anim.slide_out_down, R.anim.slide_out_up);
        PlayMusicFragment playMusicFragment =
                (PlayMusicFragment) fragmentManager.findFragmentByTag(Constant.TAG_PLAY_FRAGMENT);
        if (playMusicFragment == null) {
            fragmentTransaction.replace(R.id.frame_layout,
                    PlayMusicFragment.newInstance(collection, position, false),
                    Constant.TAG_PLAY_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            playMusicFragment.refreshData(collection, position, false);
            fragmentTransaction.show(playMusicFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //TODO
    }

    @Override
    public void onPageSelected(int position) {
        updatePagePosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //No-op
    }

    private void initViews(View view) {
        mRockCollection = new Collection();
        mAmbientCollection = new Collection();
        mClassicalCollection = new Collection();
        mCountryCollection = new Collection();
        mRecyclerViewRock = view.findViewById(R.id.recycler_alternative_rock);
        mRecyclerViewAmbient = view.findViewById(R.id.recycler_ambient);
        mRecyclerViewClassical = view.findViewById(R.id.recycler_classical);
        mRecyclerViewCountry = view.findViewById(R.id.recycler_country);
        mViewPagerSlider = view.findViewById(R.id.viewpager_slider);
        mLinearDot = view.findViewById(R.id.linear_dot);
        mViewPagerSlider.addOnPageChangeListener(this);
    }
//ss
    private void updatePagePosition(int position) {
        for (TextView textViewDView : mTextViewsDot) {
            textViewDView.setTextColor(getResources().getColor(R.color.color_slider_in_active));
        }
        mTextViewsDot[position].setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void addDotIntoSlider() {
        if (mTrendingSliderFragmentList != null) {
            mTextViewsDot = new TextView[mTrendingSliderFragmentList.size()];
            for (int i = 0; i < mTextViewsDot.length; i++) {
                mTextViewsDot[i] = new TextView(getContext());
                mTextViewsDot[i].setText(Html.fromHtml(getResources().getString(R.string.dot)));
                mTextViewsDot[i].setTextSize(getResources().getDimension(R.dimen.sp_11));
                mTextViewsDot[i].setTextColor(
                        getResources().getColor(R.color.color_slider_in_active));
                mLinearDot.addView(mTextViewsDot[i]);
            }
            mTextViewsDot[mPagePosition].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void onPageChanged() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPagerSlider.setCurrentItem(mPagePosition);
                mPagePosition++;
                if (mTrendingSliderFragmentList != null
                        && mPagePosition == mTrendingSliderFragmentList.size()) {
                    mPagePosition = 0;
                }

                handler.postDelayed(this, DELAY_3000);
            }
        }, DELAY_3000);
    }

    private void loadNextDataFromApi(String nextHref, @Constant.Genres String genre) {
        mPresenter.loadMoreDataTrackList(nextHref, genre);
    }
}
