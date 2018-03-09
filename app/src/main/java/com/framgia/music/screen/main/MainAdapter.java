package com.framgia.music.screen.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.framgia.music.screen.tabdownload.TabDownloadFragment;
import com.framgia.music.screen.tabhome.TabHomeFragment;
import com.framgia.music.screen.tabsearch.TabSearchFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/8/2018.
 */

public class MainAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();

     MainAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(new TabHomeFragment());
        mFragmentList.add(new TabSearchFragment());
        mFragmentList.add(new TabDownloadFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.size() > 0 ? mFragmentList.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }
}
