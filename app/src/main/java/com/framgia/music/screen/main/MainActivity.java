package com.framgia.music.screen.main;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import com.framgia.music.R;
import com.framgia.music.screen.BaseActivity;

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int[] mIcons = {
            R.drawable.ic_home_grey_700_24dp, R.drawable.ic_search_grey_700_24dp,
            R.drawable.ic_file_download_grey_700_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showTabs();
        setupTabIcons();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.getIcon()
                .setColorFilter(getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon()
                .setColorFilter(getResources().getColor(R.color.colorGrayTab),
                        PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //TODO
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
    }

    private void showTabs() {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
    }

    private void setupTabIcons() {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(mIcons[i]);
            }
        }
        mTabLayout.getTabAt(mViewPager.getCurrentItem())
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_IN);
    }
}
