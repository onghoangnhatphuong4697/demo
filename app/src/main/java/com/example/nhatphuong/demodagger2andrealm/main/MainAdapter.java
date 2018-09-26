package com.example.nhatphuong.demodagger2andrealm.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.example.nhatphuong.demodagger2andrealm.R;
import com.example.nhatphuong.demodagger2andrealm.data.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<Movie> mMovies;

    MainAdapter() {
        mMovies = new ArrayList<>();
    }

    public void updateMovieList(List<Movie> movieList) {
        if (mMovies != null) {
            mMovies.clear();
        }
        mMovies = movieList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        TextView mTextViewTitle;
        ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_name);
            mTextViewTitle = itemView.findViewById(R.id.text_tile);
            mImageView = itemView.findViewById(R.id.Image_view);
        }

        void Bind(Movie movie) {
            mTextView.setText(movie.getVoteCount().toString());
            mTextViewTitle.setText(movie.getTitle());
        //    Glide.with(itemView.getContext()).load(movie.getImageUrl()).into(mImageView);
        }
    }
}

