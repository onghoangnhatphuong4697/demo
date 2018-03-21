package com.framgia.music.screen.tabhome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/10/2018.
 */

public class TrendingTrackViewPager extends FragmentPagerAdapter {

    private List<TrendingSliderFragment> mSliderFragmentList = new ArrayList<>();

    TrendingTrackViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mSliderFragmentList.size() > 0 ? mSliderFragmentList.get(position) : null;
    }

    @Override
    public int getCount() {
        return mSliderFragmentList != null ? mSliderFragmentList.size() : 0;
    }

    void addData(List<TrendingSliderFragment> trendingSliderFragments) {
        mSliderFragmentList.addAll(trendingSliderFragments);
    }
}
