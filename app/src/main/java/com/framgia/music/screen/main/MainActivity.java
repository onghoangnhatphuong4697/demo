package com.framgia.music.screen.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.playmusicscreen.PlayMusicFragment;
import com.framgia.music.screen.testDB.LoginActivity;
import com.framgia.music.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final int DELAY_TIME_2000 = 2000;
    private static final int OFF_SCREEN_PAGE_LIMIT = 2;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Track> mTracks = new ArrayList<>();
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
        showNaviagtionView();

        showTabs();
        setupTabIcons();
        //  showNaviagtionView();
    }

    public void hideActionBar() {
        getSupportActionBar().hide();
    }

    private void showNaviagtionView() {

//        mActionBarDrawerToggle =
//                new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
//        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
//        mActionBarDrawerToggle.syncState();
//        assert getSupportActionBar() != null;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.button_login:
                                startActivity(LoginActivity.newInstance(MainActivity.this, true));
                                return true;
                            case R.id.button_sign_up:
                                startActivity(LoginActivity.newInstance(MainActivity.this, false));
                                return true;

                            case R.id.button_infor:

                                return true;
                            case R.id.button_favorite:
                                mDrawerLayout.closeDrawers();
                                playLikeSong();
                                return true;

                            case R.id.button_sign_out:
                                startActivity(MainActivity.LogOut(MainActivity.this, true));
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SharedPreferences sharedPreferences =
                getSharedPreferences(Constant.USER_DETAIL, Context.MODE_PRIVATE);
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //mDrawerLayout.closeDrawer(GravityCompat.END);
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        hideKeyboardFrom(this, mNavigationView);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon()
                .setColorFilter(getResources().getColor(R.color.color_gray_tab),
                        PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

    public void playLikeSong() {
        String name = sharedPreferences.getString(Constant.USER_NAME_, "");
        mTracks.clear();
        db.collection("user")
                .document(name)
                .collection(name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            if (doc.exists()) {
                                //doc.get("artist")
                                Track track = doc.toObject(Track.class);
                                mTracks.add(track);
                                //                                Log.d("phuong123", track.getArtist().getUsername());
                            }
                        }
                        Collection collection = new Collection();
                        collection.setTrackList(mTracks);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction =
                                fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,
                                R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
                        PlayMusicFragment playMusicFragment =
                                (PlayMusicFragment) fragmentManager.findFragmentByTag(
                                        Constant.TAG_PLAY_FRAGMENT);
                        if (playMusicFragment == null) {
                            fragmentTransaction.replace(R.id.frame_layout,
                                    PlayMusicFragment.newInstance(collection, 0, false),
                                    Constant.TAG_PLAY_FRAGMENT);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            playMusicFragment.refreshData(collection, 0, false);
                            playMusicFragment.checkLikeMusicWhennext();
                            fragmentTransaction.show(playMusicFragment);
                            fragmentTransaction.commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mDrawerLayout = findViewById(R.id.layout_drawer);
        mNavigationView = findViewById(R.id.navigation_view);
        boolean isCheckLogin = getIntent().getBooleanExtra(Constant.BUNDLE_LOGIN, false);
        boolean isCheckLogout = getIntent().getBooleanExtra(Constant.IS_CHECK_LOCK_OUT, false);
        //  boolean aBoolean = getIntent().getBooleanExtra(Constant.BUNDLE_LOGIN,false);
        if (isCheckLogin) {
            mNavigationView.getMenu().getItem(0).getSubMenu().getItem(0).setVisible(false);
            mNavigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(false);
        } else {
            mNavigationView.getMenu().getItem(1).getSubMenu().getItem(1).setVisible(false);
        }
        if (isCheckLogout) {
            mNavigationView.getMenu().getItem(1).getSubMenu().getItem(1).setVisible(false);
            mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).setVisible(false);
            mNavigationView.getMenu().getItem(0).getSubMenu().getItem(0).setVisible(true);
            mNavigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(true);
        }
        sharedPreferences = getSharedPreferences(Constant.USER_DETAIL, Context.MODE_PRIVATE);
        //        mNavigationView.getMenu().getItem(0)..setVisible(false);
        //        mNavigationView.getMenu().getItem(1).setVisible(false);
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

    public static Intent newInstance(Context context, Boolean isLogin) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.BUNDLE_LOGIN, isLogin);
        return intent;
    }

    public static Intent LogOut(Context context, Boolean isCheckLogOut) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.IS_CHECK_LOCK_OUT, isCheckLogOut);
        return intent;
    }
}
