package com.framgia.music.screen.playmusicscreen;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.data.model.Track;
import com.framgia.music.data.repository.TrackRepository;
import com.framgia.music.data.source.remote.TrackRemoteDataSource;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.EndlessRecyclerViewScrollListener;
import com.framgia.music.service.PlayMusicService;
import com.framgia.music.utils.Constant;
import com.framgia.music.utils.common.ConnectionUtils;
import com.framgia.music.utils.common.PermissionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Admin on 3/12/2018.
 */

public class PlayMusicFragment extends BaseFragment
        implements PlayMusicConTract.View, PlayMusicAdapter.ItemClickListener,
        CircularSeekBar.OnCircularSeekBarChangeListener, View.OnClickListener {

    private static final int PRIORITY_RECEIVE = 1;
    private static final String ARGUMENT_COLLECTION = "BUNDLE_ARGUMENT_COLLECTION";
    private static final int DURATION_15000 = 10000;
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
    private CircularSeekBar mSeekBarProgress;
    private PlayMusicConTract.Presenter mPresenter;
    private RecyclerView mRecyclerViewGenre;
    private PlayMusicAdapter mMusicAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Collection mCollection;
    private ImageView mImageButtonLike;
    private int mTrackIndex;
    private KenBurnsView mBackGround;
    private Handler mHandler = new Handler();
    private Utilities mUtilities;
    private PlayMusicService mPlayMusicService;
    private LinearLayout mLinearBottomParentActivity;
    private String mSetup;
    private boolean lastNonTrack;
    private List<Track> mTracks = new ArrayList<>();
    private boolean isLocalTrack;
    private boolean isCheckLike = false;
    private boolean isCheckliked = true;
    private SharedPreferences sharedPreferences;

    public static PlayMusicFragment newInstance(Collection collection, int position,
            boolean isLocalTrack) {
        PlayMusicFragment fragment = new PlayMusicFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_COLLECTION, collection);
        args.putInt(Constant.POSITION, position);
        args.putBoolean(Constant.ISLOCALTRACK, isLocalTrack);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        //((MainActivity) getActivity()).getSupportActionBar().hide();
        View view = inflater.inflate(R.layout.play_music_fragment, container, false);
        initViews(view);
        sharedPreferences =
                requireActivity().getSharedPreferences(Constant.USER_DETAIL, Context.MODE_PRIVATE);
        TrackRepository trackRepository =
                TrackRepository.getInstance(TrackRemoteDataSource.getInstance(), null);
        mPresenter = new PlayMusicPresenter(trackRepository);
        mPresenter.setView(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCollection = bundle.getParcelable(ARGUMENT_COLLECTION);
            mTrackIndex = bundle.getInt(Constant.POSITION, 0);
            isLocalTrack = bundle.getBoolean(Constant.ISLOCALTRACK, false);
        }

        if (mCollection != null) {
            showTrackList();
            playService();
            registerBroadcastReceiver();
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
    public void downloadSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void downloadFail() {
        //TODO
    }

    @Override
    public void onItemClicked(int trackIndex) {
        mTrackIndex = trackIndex;
        mPlayMusicService.playTrack(mTrackIndex);
        mMusicAdapter.addPosition(mTrackIndex);
        mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
        setupViews();
        requestState();
        checkLikeMusicWhennext();
    }

    public void requestState() {
        mTracks.clear();
        isCheckliked = true;
    }

    //        @Override
    //        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
    //            if (fromUser) {
    //                mHandler.removeCallbacks(mUpdateTimeTask);
    //                mPlayMusicService.fastForward(mUtilities.progressToTimer(seekBar.getProgress(),
    //                        mPlayMusicService.getSongDuration()));
    //                mTextViewTrackProgress.setText(
    //                        mUtilities.milliSecondsToTimer(mPlayMusicService.getCurrentPosition()));
    //            }
    //        }
    //
    //        @Override
    //        public void onStartTrackingTouch(SeekBar seekBar) {
    //            //No-op
    //        }
    //
    //        @Override
    //        public void onStopTrackingTouch(SeekBar seekBar) {
    //            updateProgressBar();
    //        }

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
                mPlayMusicService.nextTrack(true);
                requestState();
                setupViews();
                checkLikeMusicWhennext();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
                break;
            case R.id.image_previous:
                mPlayMusicService.preTrack();
                requestState();
                setupViews();
                checkLikeMusicWhennext();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
                break;
            case R.id.image_setup:
                mPlayMusicService.setupMusic();
                setupMusic(mPlayMusicService.getSetup());
                break;
            case R.id.image_download:
                if (PermissionUtils.requestPermission(getActivity()) && !isLocalTrack) {
                    startDownload();
                }
                break;
            case R.id.button_like:
                getListSongLiked();
                break;
        }
    }

    void dislikeSong() {
        String name = sharedPreferences.getString(Constant.USER_NAME_, "");
        db.collection("user")
                .document(name)
                .collection(name)
                .document(mPlayMusicService.getSongName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(mImageButtonLike, "Bỏ thích bài hát thành công !! ",
                                Snackbar.LENGTH_SHORT).show();
                        isCheckliked = false;
                        mImageButtonLike.setImageResource(R.drawable.ic_heart);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void getListSongLiked() {
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
                                // convert document to POJO
                                Track track = doc.toObject(Track.class);
                                //                                Log.d("phuong123", track.getArtist().getUsername() + "123123");
                                mTracks.add(track);
                            }
                        }
                        checklike(mPlayMusicService.getTrackCurrent());
                        if (isCheckliked) {
                            dislikeSong();
                        } else {
                            LikeSong();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void checkLikeMusicWhennext() {
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
                                Track track = doc.toObject(Track.class);
                                mTracks.add(track);
                            }
                        }
                        checklike(mPlayMusicService.getTrackCurrent());
                        if (isCheckliked) {
                            mImageButtonLike.setImageResource(R.drawable.ic_heart_checked);
                        } else {
                            mImageButtonLike.setImageResource(R.drawable.ic_heart);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checklike(Track Track) {
        int i;
        for (i = 0; i < mTracks.size(); i++) {
            if (Track.getTitle().equals(mTracks.get(i).getTitle())) {
                isCheckliked = true;
                return;
            } else {
                isCheckliked = false;
            }
        }
    }

    void LikeSong() {

        String name = sharedPreferences.getString(Constant.USER_NAME_, "");
        Log.d("phuong123", name);
        String randomId = db.collection("user").document().getId();

        db.collection("user")
                .document(name)
                .collection(name)
                .document(mPlayMusicService.getSongName())
                .set(mPlayMusicService.getTrackCurrent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(mImageButtonLike, "Thích bài hát thành công !! ",
                                Snackbar.LENGTH_SHORT).show();
                        mImageButtonLike.setImageResource(R.drawable.ic_heart_checked);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("phuong123", "thích that bai" + e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getContext(), R.string.granted, Toast.LENGTH_SHORT).show();
                    startDownload();
                } else {
                    Toast.makeText(getContext(), R.string.no_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        mSeekBarProgress = view.findViewById(R.id.seekBarProcess);
        mImageViewDownLoad = view.findViewById(R.id.image_download);
        mImageButtonLike = view.findViewById(R.id.button_like);
        mBackGround = view.findViewById(R.id.imageBackground);
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
        mImageButtonLike.setOnClickListener(this);
        mUtilities = new Utilities();
        setupMusic(getContext().getSharedPreferences(Constant.SETUP_MUSIC_PREFERENCES,
                Context.MODE_PRIVATE).getString(Constant.SETUP, Constant.NON_REPEAT));
    }

    private void playService() {
        getService();
        Intent intent = PlayMusicService.getTracksIntent(getContext(), mCollection, mTrackIndex,
                isLocalTrack);
        if (getActivity() != null) {
            getActivity().startService(intent);
            getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }
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
                //               mPlayMusicService.nextTrack(true);
                setupViews();
                mTrackIndex = mPlayMusicService.getTrackIndex();
                mMusicAdapter.addPosition(mTrackIndex);
                mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
            }
            mHandler.postDelayed(this, DELAY_TIME_100);
        }
    };

    private void startDownload() {
        mPresenter.downloadTrack(getContext(),
                mCollection.getTrackList().get(mTrackIndex).getStreamUrl() + Constant.CLIENT_ID,
                mCollection.getTrackList().get(mTrackIndex).getTitle());
    }

    private void showTrackList() {
        mMusicAdapter = new PlayMusicAdapter(getContext(), this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mMusicAdapter.addData(mCollection.getTrackList());
        mMusicAdapter.addPosition(mTrackIndex);

        mRecyclerViewGenre.setLayoutManager(mLayoutManager);
        mRecyclerViewGenre.setAdapter(mMusicAdapter);
 //       mMusicAdapter.notifyItemRangeChanged(POSITION_0, mMusicAdapter.getItemCount());
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
        //        mSeekBarProgress.getProgressDrawable().setColorFilter(
        //                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        //        try {
        //            URL url = new URL(mPlayMusicService.getArt());
        //            Drawable d =new BitmapDrawable(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
        //            mBackGround.setBackground(d);
        //            mBackGround.setAlpha((float) 0.5);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        Glide.with(getActivity())
        //                .load(mPlayMusicService.getArt())
        //                .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
        //                .into(mBackGround);
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

    public void refreshData(Collection collection, int position, boolean localTrack) {
        isLocalTrack = localTrack;
        mPlayMusicService.stopMedia();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mMusicAdapter.clearData();
        mPlayMusicService.refreshData(collection.getTrackList(), position, isLocalTrack);

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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Constant.ACTION_NEXT:
                        mTrackIndex = mPlayMusicService.getTrackIndex();
                        setupViews();
                        mMusicAdapter.addPosition(mTrackIndex);
                        mMusicAdapter.notifyItemRangeChanged(0, mMusicAdapter.getItemCount());
                        break;
                    case Constant.ACTION_PREVIOUS:
                        mTrackIndex = mPlayMusicService.getTrackIndex();
                        setupViews();
                        mMusicAdapter.addPosition(mTrackIndex);
                        mMusicAdapter.notifyItemRangeChanged(0, mMusicAdapter.getItemCount());
                        break;
                    case Constant.ACTION_PLAY:
                        mImageViewPlay.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                        mImageViewPlayParentActivity.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_pause_black_36dp));
                        mImageViewArt.startAnimation(addAnimation());
                        mImageViewArtParentActivity.startAnimation(addAnimation());
                        updateProgressBar();
                        break;
                    case Constant.ACTION_PAUSE:
                        if (!mPlayMusicService.isPlaying()) {
                            mImageViewPlay.setImageDrawable(getResources().getDrawable(
                                    R.drawable.ic_play_arrow_white_36dp));
                            mImageViewPlayParentActivity.setImageDrawable(
                                    getResources().getDrawable(
                                            R.drawable.ic_play_arrow_black_36dp));
                            mImageViewArt.clearAnimation();
                            mImageViewArtParentActivity.clearAnimation();
                        } else {
                            mImageViewPlay.setImageDrawable(
                                    getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                            mImageViewPlayParentActivity.setImageDrawable(
                                    getResources().getDrawable(R.drawable.ic_pause_black_36dp));
                            mImageViewArt.startAnimation(addAnimation());
                            mImageViewArtParentActivity.startAnimation(addAnimation());
                            updateProgressBar();
                        }

                        break;
                }
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_PAUSE);
        intentFilter.addAction(Constant.ACTION_NEXT);
        intentFilter.addAction(Constant.ACTION_PREVIOUS);
        intentFilter.addAction(Constant.ACTION_PLAY);
        intentFilter.setPriority(PRIORITY_RECEIVE);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, float progress,
            boolean fromUser) {
        if (fromUser) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mPlayMusicService.fastForward(
                    mUtilities.progressToTimer((int) circularSeekBar.getProgress(),
                            mPlayMusicService.getSongDuration()));
            mTextViewTrackProgress.setText(
                    mUtilities.milliSecondsToTimer(mPlayMusicService.getCurrentPosition()));
        }
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        updateProgressBar();
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {

    }
}
