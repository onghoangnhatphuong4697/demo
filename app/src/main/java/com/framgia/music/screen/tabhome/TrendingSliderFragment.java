package com.framgia.music.screen.tabhome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BaseFragment;

/**
 * Created by Admin on 3/9/2018.
 */

public class TrendingSliderFragment extends BaseFragment {

    private static final String ARGUMENT_ARTWORK = "BUNDLE_ARTWORK_URL";
    private ImageView mImageViewSlider;

    public static TrendingSliderFragment newInstance(String track) {
        TrendingSliderFragment fragment = new TrendingSliderFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_ARTWORK, track);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slider_fragment, container, false);
        initViews(view);
        Bundle args = getArguments();
        if (args != null) {
            Glide.with(view.getContext())
                    .load(args.getString(ARGUMENT_ARTWORK))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                    .into(mImageViewSlider);
        }
        return view;
    }

    private void initViews(View view) {
        mImageViewSlider = view.findViewById(R.id.image_slider);
    }
}
