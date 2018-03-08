package com.framgia.music.screen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Admin on 3/8/2018.
 */

public abstract class BaseRecyclerViewAdapter<V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> {

    private final Context mContext;

    protected BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }
}
