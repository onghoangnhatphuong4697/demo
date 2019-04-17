package com.framgia.music.screen.tabsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BaseRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/10/2018.
 */

public class TrackListAdapter extends BaseRecyclerViewAdapter<TrackListAdapter.ViewHolder> {

    private List<Track> mTrackList = new ArrayList<>();
    private ItemClickListener mItemClickListener;

    public TrackListAdapter(Context context, ItemClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(getContext()).inflate(R.layout.track_item_search, parent, false);
        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.ViewHolder holder, int position) {
        holder.setData(mTrackList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrackList != null ? mTrackList.size() : 0;
    }

    public void addData(@NonNull List<Track> trackList) {
        mTrackList.addAll(trackList);
        notifyDataSetChanged();
    }

    List<Track> getData() {
        return mTrackList;
    }

    void clearData() {
        mTrackList.clear();
        notifyDataSetChanged();
    }

    boolean checkData() {
        return mTrackList != null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewTrack;
        private TextView mTextViewTrackName, mTextViewUserName;

        ViewHolder(View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            mImageViewTrack = itemView.findViewById(R.id.image_track);
            mTextViewTrackName = itemView.findViewById(R.id.text_track_title);
            mTextViewUserName = itemView.findViewById(R.id.text_user_name);

            itemView.setOnClickListener(view -> {
                itemClickListener.onItemClicked(getAdapterPosition());
            });
        }

        void setData(Track track) {
            Glide.with(itemView.getContext())
                .load(track.getArtworkUrl())
                .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                .into(mImageViewTrack);
            mTextViewTrackName.setText(track.getTitle());
            if(track.getArtist()!= null){
                mTextViewUserName.setText(track.getArtist().getUsername());

            }
        }
    }

    public interface ItemClickListener {
        void onItemClicked(int position);
    }
}
