package com.framgia.music.screen.tabdownload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music.R;
import com.framgia.music.screen.BaseFragment;

/**
 * Created by Admin on 3/8/2018.
 */

public class TabDownloadFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabdownload_fragment, container, false);
        return view;
    }
}
