package com.framgia.music.screen.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.framgia.music.R;
import com.framgia.music.screen.BaseActivity;
import com.framgia.music.screen.playmusicscreen.PlayMusicFragment;
import com.framgia.music.utils.Constant;
import java.util.List;

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    private static final int DELAY_TIME_2000 = 2000;
    private static final int OFF_SCREEN_PAGE_LIMIT = 2;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int[] mIcons = {
            R.drawable.ic_home_grey_700_24dp, R.drawable.ic_search_grey_700_24dp,
            R.drawable.ic_file_download_grey_700_24dp
    };
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showTabs();
        setupTabIcons();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down,
                R.anim.slide_out_down, R.anim.slide_out_up);
        PlayMusicFragment playMusicFragment =
                (PlayMusicFragment) fragmentManager.findFragmentByTag(Constant.TAG_PLAY_FRAGMENT);
        if (playMusicFragment != null && !playMusicFragment.isHidden()) {
            fragmentTransaction.hide(playMusicFragment);
            fragmentTransaction.commit();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.Please_press_Back_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DELAY_TIME_2000);
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
                .setColorFilter(getResources().getColor(R.color.color_gray_tab),
                        PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //TODO
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            showFragment();
    }

    private void showFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlayMusicFragment playMusicFragment =
                (PlayMusicFragment) fragmentManager.findFragmentByTag(Constant.TAG_PLAY_FRAGMENT);
        if (playMusicFragment != null && playMusicFragment.isHidden()) {
            fragmentTransaction.show(playMusicFragment);
            fragmentTransaction.commit();
        }
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
    }

    private void showTabs() {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
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
