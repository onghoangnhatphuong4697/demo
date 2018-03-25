package com.framgia.music.screen.playmusicscreen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.EndlessRecyclerViewScrollListener;
import com.framgia.music.service.PlayMusicService;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.ConnectionUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Admin on 3/12/2018.
 */

public class PlayMusicFragment extends BaseFragment
        implements PlayMusicConTract.View, PlayMusicAdapter.ItemClickListener,
        SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String ARGUMENT_COLLECTION = "BUNDLE_ARGUMENT_COLLECTION";
    private static final int DURATION_15000 = 15000;
    private static final int POSITION_0 = 0;
    private static final int MAX_100 = 100;
    private static final int DELAY_TIME_100 = 100;
    private static final String ZERO = "0:00";
    private ServiceConnection mServiceConnection;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTextViewTrackName, mTextViewUserName, mTextViewTrackProgress,
            mTextViewAllTime, mTextTrackNameParentActivity, mTextUserNameParentActivity;
    private ImageView mImageViewPrevious, mImageViewPlay, mImageViewNext, mImageViewSetup,
            mImageViewDownLoad, mImageViewPreviousParentActivity, mImageViewNextParentActivity,
            mImageViewPlayParentActivity;
    private CircleImageView mImageViewArt, mImageViewArtParentActivity;
    private SeekBar mSeekBarProgress;
    private PlayMusicConTract.Presenter mPresenter;
    private RecyclerView mRecyclerViewGenre;
    private PlayMusicAdapter mMusicAdapter;
    private Collection mCollection;
    private int mTrackIndex;
    private Handler mHandler = new Handler();
    private Utilities mUtilities;
    private PlayMusicService mPlayMusicService;
    private LinearLayout mLinearBottomParentActivity;
    private String mSetup;
    private boolean lastNonTrack;

    public static PlayMusicFragment newInstance(Collection collection, int position,
            boolean isLocalTrack) {
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
            playService();
        }
        return view;
    }

    @Override
    public void updateTrackList(Collection collection) {
        mCollection.getTrackList().addAll(collection.getTrackList());
        mCollection.setNextHref(collection.getNextHref());
        mMusicAdapter.addData(collection.getTrackList());
        mPlayMusicService.updateTrackList(mMusicAdapter.getData());
    }

    @Override
    public void onError(Exception e) {
        //TODO
    }

    @Override
    public void onItemClicked(int trackIndex) {
        mTrackIndex = trackIndex;
        mPlayMusicService.playTrack(mTrackIndex);
        mMusicAdapter.addPosition(mTrackIndex);
        mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
        setupViews();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mPlayMusicService.fastForward(mUtilities.progressToTimer(seekBar.getProgress(),
                    mPlayMusicService.getSongDuration()));
            mTextViewTrackProgress.setText(
                    mUtilities.milliSecondsToTimer(mPlayMusicService.getCurrentPosition()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //No-op
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateProgressBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_bottom:
                showFragment();
                break;
            case R.id.image_play:
                if (lastNonTrack) {
                    mPlayMusicService.playTrack(mTrackIndex);
                    mImageViewPlay.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                    mImageViewArtParentActivity.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_pause_black_36dp));
                    updateProgressBar();
                } else if (mPlayMusicService.isPlaying()) {
                    mPlayMusicService.pauseMedia();
                    mImageViewPlay.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                    mImageViewPlayParentActivity.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_play_arrow_black_36dp));
                    mImageViewArt.clearAnimation();
                    mImageViewArtParentActivity.clearAnimation();
                } else {
                    mPlayMusicService.playMedia();
                    mImageViewPlay.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                    mImageViewPlayParentActivity.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_pause_black_36dp));
                    updateProgressBar();
                    mImageViewArt.startAnimation(addAnimation());
                    mImageViewArtParentActivity.startAnimation(addAnimation());
                }
                break;
            case R.id.image_next:
                mPlayMusicService.nextTrack(false);
                setupViews();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
                break;
            case R.id.image_previous:
                mPlayMusicService.preTrack();
                setupViews();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
                break;
            case R.id.image_setup:
                mPlayMusicService.setupMusic();
                setupMusic(mPlayMusicService.getSetup());
            case R.id.image_download:
                break;
        }
    }

    private void initViews(View view) {
        mLinearBottomParentActivity = getActivity().findViewById(R.id.linear_bottom);
        mTextTrackNameParentActivity = getActivity().findViewById(R.id.text_track_name_parent);
        mTextUserNameParentActivity = getActivity().findViewById(R.id.text_user_name);
        mImageViewPreviousParentActivity = getActivity().findViewById(R.id.image_previous);
        mImageViewArtParentActivity = getActivity().findViewById(R.id.image_track_parent);
        mImageViewNextParentActivity = getActivity().findViewById(R.id.image_next);
        mImageViewPlayParentActivity = getActivity().findViewById(R.id.image_play);
        mRecyclerViewGenre = view.findViewById(R.id.recycler_genre);
        mTextViewTrackName = view.findViewById(R.id.text_track_name);
        mTextViewUserName = view.findViewById(R.id.text_user_name);
        mTextViewTrackProgress = view.findViewById(R.id.text_track_progress);
        mTextViewAllTime = view.findViewById(R.id.text_all_time);
        mImageViewNext = view.findViewById(R.id.image_next);
        mImageViewArt = view.findViewById(R.id.image_track);
        mImageViewPlay = view.findViewById(R.id.image_play);
        mImageViewPrevious = view.findViewById(R.id.image_previous);
        mImageViewSetup = view.findViewById(R.id.image_setup);
        mSeekBarProgress = view.findViewById(R.id.seek_progress);
        mImageViewDownLoad = view.findViewById(R.id.image_download);
        mImageViewDownLoad.setOnClickListener(this);
        mImageViewNext.setOnClickListener(this);
        mImageViewPlay.setOnClickListener(this);
        mImageViewPrevious.setOnClickListener(this);
        mSeekBarProgress.setOnSeekBarChangeListener(this);
        mImageViewSetup.setOnClickListener(this);
        mLinearBottomParentActivity.setOnClickListener(this);
        mImageViewPreviousParentActivity.setOnClickListener(this);
        mImageViewPlayParentActivity.setOnClickListener(this);
        mImageViewNextParentActivity.setOnClickListener(this);
        mLinearBottomParentActivity.setVisibility(View.VISIBLE);
        mUtilities = new Utilities();
        setupMusic(getContext().getSharedPreferences(Constant.SETUP_MUSIC_PREFERENCES,
                Context.MODE_PRIVATE).getString(Constant.SETUP, Constant.NON_REPEAT));
    }

    private void playService() {
        getService();
        Intent intent = PlayMusicService.getTracksIntent(getContext(), mCollection, mTrackIndex);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void getService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlayMusicService.LocalBinder binder = (PlayMusicService.LocalBinder) iBinder;
                mPlayMusicService = binder.getService();
                setupViews();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, DELAY_TIME_100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayMusicService.getSongDuration();
            long currentDuration = mPlayMusicService.getCurrentPosition();
            String currentPosition = mUtilities.milliSecondsToTimer(currentDuration);
            String totalTime = mUtilities.milliSecondsToTimer(totalDuration);
            if (currentDuration != 0) {
                mTextViewAllTime.setText(mUtilities.milliSecondsToTimer(totalDuration));
            }
            mTextViewTrackProgress.setText(mUtilities.milliSecondsToTimer(currentDuration));
            int progress = (mUtilities.getProgressPercentage(currentDuration, totalDuration));
            mSeekBarProgress.setProgress(progress);

            if (currentPosition.equals(totalTime)
                    && mTrackIndex == mCollection.getTrackList().size() - 1
                    && mSetup.equals(Constant.NON_REPEAT)) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                mImageViewPlay.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                mImageViewPlayParentActivity.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                mImageViewArt.clearAnimation();
                mImageViewArtParentActivity.clearAnimation();
                lastNonTrack = true;
                return;
            } else if (currentPosition.equals(totalTime)) {
                mPlayMusicService.nextTrack(false);
                setupViews();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
            }
            mHandler.postDelayed(this, DELAY_TIME_100);
        }
    };

    public void showTrackList() {
        mMusicAdapter = new PlayMusicAdapter(getContext(), this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mMusicAdapter.addData(mCollection.getTrackList());
        mMusicAdapter.addPosition(mTrackIndex);

        mRecyclerViewGenre.setLayoutManager(mLayoutManager);
        mRecyclerViewGenre.setAdapter(mMusicAdapter);
        mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
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

    private void setupViews() {
        Glide.with(getActivity())
                .load(mPlayMusicService.getArt())
                .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                .into(mImageViewArtParentActivity);
        Glide.with(this)
                .load(mPlayMusicService.getArt())
                .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                .into(mImageViewArt);
        mImageViewArtParentActivity.startAnimation(addAnimation());
        mImageViewArt.startAnimation(addAnimation());
        mTextViewUserName.setText(mPlayMusicService.getUserName());
        mTextViewTrackName.setText(mPlayMusicService.getSongName());
        mTextUserNameParentActivity.setText(mPlayMusicService.getUserName());
        mTextTrackNameParentActivity.setText(mPlayMusicService.getSongName());
        mTextViewAllTime.setText(ZERO);
        mImageViewPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_36dp));
        mImageViewPlayParentActivity.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_pause_black_36dp));
        mSeekBarProgress.setMax(MAX_100);
        mSeekBarProgress.setProgress(POSITION_0);
        updateProgressBar();
    }

    private void setupMusic(String setup) {
        switch (setup) {
            case Constant.REPEAT:
                mImageViewSetup.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_repeat_orange_900_36dp));
                break;
            case Constant.REPEAT_ONE:
                mImageViewSetup.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_repeat_one_orange_900_36dp));

                break;
            case Constant.SHUFFLE:
                mImageViewSetup.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_shuffle_orange_900_36dp));
                break;
            case Constant.NON_REPEAT:
                mImageViewSetup.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_repeat_white_36dp));
                break;
        }
        mSetup = setup;
    }

    private AnimationSet addAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation rotateAnimation =
                new RotateAnimation(Constant.FROM_0, Constant.TO_360, Animation.RELATIVE_TO_SELF,
                        Constant.PIVOT_0_5, Animation.RELATIVE_TO_SELF, Constant.PIVOT_0_5);
        rotateAnimation.setDuration(DURATION_15000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillAfter(true);
        animationSet.addAnimation(rotateAnimation);
        return animationSet;
    }

    private void loadNextDataFromApi(String nextHref) {
        mPresenter.loadMoreDataTrackList(nextHref);
    }

    public void refreshData(Collection collection, int position, boolean isLocalTrack) {
        mPlayMusicService.stopMedia();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mMusicAdapter.clearData();
        mPlayMusicService.refreshData(collection.getTrackList(), position);

        mMusicAdapter.addData(collection.getTrackList());
        mCollection = collection;
        mTrackIndex = position;
        mPlayMusicService.initMediaPlayer();
        showTrackList();
        setupViews();
    }

    private void showFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down,
                R.anim.slide_out_down, R.anim.slide_out_up);
        PlayMusicFragment playMusicFragment =
                (PlayMusicFragment) fragmentManager.findFragmentByTag(Constant.TAG_PLAY_FRAGMENT);
        if (playMusicFragment != null && playMusicFragment.isHidden()) {
            fragmentTransaction.show(playMusicFragment);
            fragmentTransaction.commit();
        }
    }
}
