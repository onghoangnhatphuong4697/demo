package com.framgia.music.screen.playmusicscreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.framgia.music.R;
import com.framgia.music.data.model.Track;
import com.framgia.music.screen.BaseRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/13/2018.
 */

public class PlayMusicAdapter extends BaseRecyclerViewAdapter<PlayMusicAdapter.ViewHolder> {

    private ItemClickListener mItemClickListener;
    private List<Track> mTrackList = new ArrayList<>();
    private int mGlobalIndex;

    PlayMusicAdapter(Context context, ItemClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PlayMusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.track_item_play_fragment, parent, false);
        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayMusicAdapter.ViewHolder holder, int position) {
        holder.setData(mTrackList.get(position));
        if (position == mGlobalIndex) {
            holder.changeColor();
        } else {
            holder.resetColor();
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    void addData(List<Track> trackList) {
        if (trackList == null) {
            return;
        }
        mTrackList.addAll(trackList);
        notifyDataSetChanged();
    }

    List<Track> getData() {
        return mTrackList;
    }

    void addPosition(int position) {
        mGlobalIndex = position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewTrackTitle, mTextViewUserName;

        ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            mTextViewTrackTitle = itemView.findViewById(R.id.text_track_title);
            mTextViewUserName = itemView.findViewById(R.id.text_user_name);
            itemView.setOnClickListener(view -> {
                itemClickListener.onItemClicked(getAdapterPosition());
            });
        }

        void changeColor() {

            mTextViewTrackTitle.setTextColor(
                    itemView.getContext().getResources().getColor(R.color.colorAccent));
            mTextViewUserName.setTextColor(
                    itemView.getContext().getResources().getColor(R.color.colorAccent));
        }

        void resetColor() {

            mTextViewTrackTitle.setTextColor(
                    itemView.getContext().getResources().getColor(R.color.color_white));
            mTextViewUserName.setTextColor(
                    itemView.getContext().getResources().getColor(R.color.color_white));
        }

        void setData(Track track) {
            mTextViewTrackTitle.setText(track.getTitle());
            mTextViewUserName.setText(track.getArtist().getUsername());
        }
    }

    public interface ItemClickListener {
        void onItemClicked(int index);
    }
}
