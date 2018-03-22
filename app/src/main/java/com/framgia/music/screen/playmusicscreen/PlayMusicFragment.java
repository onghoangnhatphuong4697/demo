package com.framgia.music.screen.playmusicscreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music.R;
import com.framgia.music.data.model.Collection;
import com.framgia.music.screen.BaseFragment;
import com.framgia.music.utils.Constant;

/**
 * Created by Admin on 3/12/2018.
 */

public class PlayMusicFragment extends BaseFragment {

    private static final String ARGUMENT_COLLECTION = "BUNDLE_ARGUMENT_COLLECTION";

    public static PlayMusicFragment newInstance(Collection collection, int position) {
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
        return view;
    }
}
