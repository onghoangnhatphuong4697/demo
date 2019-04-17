package com.framgia.music.screen.like;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.screen.tabsearch.TrackListAdapter;
import com.framgia.music.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class LikeFragment extends BaseFragment implements TrackListAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private TrackListAdapter mAdapter;
    private LinearLayout mLinearLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Track> mTracks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.like_fragment, container, false);
        initView(view);
        showData();
        return view;
    }

    public static LikeFragment newInstance() {
        LikeFragment fragment = new LikeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void initView(View v) {
        mRecyclerView = v.findViewById(R.id.recycle_like);
        mAdapter = new TrackListAdapter(getContext(), this);
        sharedPreferences =
                requireActivity().getSharedPreferences(Constant.USER_DETAIL, Context.MODE_PRIVATE);
    }

    public void showData() {
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
                                Log.d("phuong123",track.getArtist().getUsername());
                            }
                        }
                        mAdapter.addData(mTracks);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClicked(int position) {

    }
}
