package com.example.nhatphuong.demodagger2andrealm.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MoreMovie implements Parcelable {

    @SerializedName("page")
    @Expose
    private Integer mPage;
    @SerializedName("total_results")
    @Expose
    private Integer mTotalMovies;
    @SerializedName("total_pages")
    @Expose
    private Integer mTotalPages;
    @SerializedName("results")
    @Expose
    private List<Movie> mMovieList;

    /**
     * No args constructor for use in serialization
     */
    public MoreMovie() {
    }

    /**
     *
     * @param movieList
     * @param totalMovies
     * @param page
     * @param totalPages
     */
    public MoreMovie(Integer page, Integer totalMovies, Integer totalPages, List<Movie> movieList) {
        super();
        mPage = page;
        mTotalMovies = totalMovies;
        mTotalPages = totalPages;
        mMovieList = movieList;
    }

    protected MoreMovie(Parcel in) {
        if (in.readByte() == 0) {
            mPage = null;
        } else {
            mPage = in.readInt();
        }
        if (in.readByte() == 0) {
            mTotalMovies = null;
        } else {
            mTotalMovies = in.readInt();
        }
        if (in.readByte() == 0) {
            mTotalPages = null;
        } else {
            mTotalPages = in.readInt();
        }
        mMovieList = in.createTypedArrayList(Movie.CREATOR);
    }

    public static final Creator<MoreMovie> CREATOR = new Creator<MoreMovie>() {
        @Override
        public MoreMovie createFromParcel(Parcel in) {
            return new MoreMovie(in);
        }

        @Override
        public MoreMovie[] newArray(int size) {
            return new MoreMovie[size];
        }
    };

    public Integer getPage() {
        return mPage;
    }

    public void setPage(Integer page) {
        mPage = page;
    }

    public Integer getTotalMovies() {
        return mTotalMovies;
    }

    public void setTotalMovies(Integer totalMovies) {
        mTotalMovies = totalMovies;
    }

    public Integer getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        mTotalPages = totalPages;
    }

    public List<Movie> getMovieList() {
        return mMovieList;
    }

    public void setMovieList(List<Movie> movieList) {
        mMovieList = movieList;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (mPage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mPage);
        }
        if (mTotalMovies == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mTotalMovies);
        }
        if (mTotalPages == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mTotalPages);
        }
        dest.writeTypedList(mMovieList);
    }
}
