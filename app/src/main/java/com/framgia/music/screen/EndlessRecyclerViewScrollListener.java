package com.framgia.music.screen;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Admin on 3/11/2018.
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private int mVisibleThreshold = 5;
    private boolean mIsLoading = true;
    private int mPreviousTotal = 0;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        mVisibleThreshold = mVisibleThreshold * layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int fistVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();
        int visibleItemCount = mLayoutManager.getChildCount();
        if (mLayoutManager instanceof GridLayoutManager) {
            fistVisibleItemPosition =
                    ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            fistVisibleItemPosition =
                    ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }
        if (mIsLoading && totalItemCount > mPreviousTotal) {
            mIsLoading = false;
            mPreviousTotal = totalItemCount;
        }
        if (!mIsLoading && (totalItemCount - visibleItemCount) <= (fistVisibleItemPosition
                + mVisibleThreshold)) {
            onLoadMore();
            mIsLoading = true;
        }
    }

    public abstract void onLoadMore();
}
