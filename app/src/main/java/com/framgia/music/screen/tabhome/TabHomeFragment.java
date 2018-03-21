package com.framgia.music.screen.tabhome;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabHomeFragment extends BaseFragment
        implements TabHomeContract.View, ViewPager.OnPageChangeListener {

    private static final int DELAY_3000 = 3000;
    private TabHomeContract.Presenter mPresenter;
    private ViewPager mViewPagerSlider;
    private LinearLayout mLinearDot;
    private List<TrendingSliderFragment> mTrendingSliderFragmentList;
    private int mPagePosition = 0;
    private TextView[] mTextViewsDot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabhome_fragment, container, false);
        initViews(view);
        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(), null);
        mPresenter = new TabHomePresenter(trackRepository);
        mPresenter.setView(this);
        if (isInternetConnected()) {
            mPresenter.getTrendingTrackList();
            onPageChanged();
        } else {
            Toast.makeText(getContext(), R.string.network_is_unavailable, Toast.LENGTH_SHORT)
                    .show();
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //TODO
    }

    @Override
    public void onPageSelected(int position) {
        updatePagePosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //TODO
    }

    private void initViews(View view) {
        mViewPagerSlider = view.findViewById(R.id.viewpager_slider);
        mLinearDot = view.findViewById(R.id.linear_dot);
        mViewPagerSlider.addOnPageChangeListener(this);
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

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
}
