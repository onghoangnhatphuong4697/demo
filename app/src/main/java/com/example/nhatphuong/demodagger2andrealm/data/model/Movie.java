package com.example.nhatphuong.demodagger2andrealm.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.nhatphuong.demodagger2andrealm.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.annotations.PrimaryKey;
import java.util.List;

public class Movie implements Parcelable {
    @SerializedName("vote_count")
    @Expose
    private Integer mVoteCount;
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private Integer mId;
    @SerializedName("vote_average")
    @Expose
    private Double mVoteAverage;
    @SerializedName("title")
    @Expose
    private String mTitle;
    @SerializedName("poster_path")
    @Expose
    private String mPosterPath;
    @SerializedName("original_language")
    @Expose
    private String mOriginalLanguage;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> mGenreIds;
    @SerializedName("backdrop_path")
    @Expose
    private String mBackdropPath;
    @SerializedName("overview")
    @Expose
    private String mOverview;
    @SerializedName("release_date")
    @Expose
    private String mReleaseDate;

    protected Movie(Parcel in) {
        if (in.readByte() == 0) {
            mVoteCount = null;
        } else {
            mVoteCount = in.readInt();
        }
        if (in.readByte() == 0) {
            mId = null;
        } else {
            mId = in.readInt();
        }
        if (in.readByte() == 0) {
            mVoteAverage = null;
        } else {
            mVoteAverage = in.readDouble();
        }
        mTitle = in.readString();
        mPosterPath = in.readString();
        mOriginalLanguage = in.readString();
        mBackdropPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * No args constructor for use in serialization
     */
    public String getImageUrl() {
        return getPosterPath();
    }

    /**
     *
     * @param genreIds
     * @param id
     * @param title
     * @param releaseDate
     * @param overview
     * @param posterPath
     * @param voteAverage
     * @param originalLanguage
     * @param backdropPath
     * @param voteCount
     * @param
     */
    public Movie(Integer voteCount, Integer id, Double voteAverage, String title, String posterPath,
            String originalLanguage, List<Integer> genreIds, String backdropPath, String overview,
            String releaseDate) {
        super();
        mVoteCount = voteCount;
        mId = id;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPosterPath = posterPath;
        mOriginalLanguage = originalLanguage;
        mGenreIds = genreIds;
        mBackdropPath = backdropPath;
        mOverview = overview;
        mReleaseDate = releaseDate;
    }

    public Integer getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(Integer voteCount) {
        mVoteCount = voteCount;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPosterPath() {
        return Constants.API_KEY + mPosterPath;
    }

    public String getFavoriteImage() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        mGenreIds = genreIds;
    }

    public String getBackdropPath() {
        return Constants.BASE_IMAGE_LINK + mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public String getFavoriteBackdrop() {
        return mBackdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mVoteCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mVoteCount);
        }
        if (mId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mId);
        }
        if (mVoteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(mVoteAverage);
        }
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mBackdropPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
    }
}
