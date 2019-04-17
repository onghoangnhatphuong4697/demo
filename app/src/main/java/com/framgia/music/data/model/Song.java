package com.framgia.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable {
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    @SerializedName("id")
    @Expose
    private int mId;
    @SerializedName("genre")
    @Expose
    private String mGenre;
    @SerializedName("title")
    @Expose
    private String mTitle;
    @SerializedName("stream_url")
    @Expose
    private String mStreamUrl;
    @SerializedName("artwork_url")
    @Expose
    private String mArtworkUrl;
    @SerializedName("duration")
    @Expose
    private int mDuration;
    @SerializedName("user")
    @Expose
    private Artist mArtist;

    public Song() {
    }

    private Song(Parcel in) {
        mId = in.readInt();
        mGenre = in.readString();
        mTitle = in.readString();
        mStreamUrl = in.readString();
        mArtworkUrl = in.readString();
        mDuration = in.readInt();
        mArtist = in.readParcelable(Artist.class.getClassLoader());
    }

    protected Song(Builder builder) {
        mId = builder.mId;
        mGenre = builder.mGenre;
        mTitle = builder.mTitle;
        mStreamUrl = builder.mStreamUrl;
        mArtworkUrl = builder.mArtworkUrl;
        mDuration = builder.mDuration;
        mArtist = builder.mArtist;
    }

    public static Creator<Song> getCREATOR() {
        return CREATOR;
    }

    public int getId() {
        return mId;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public int getDuration() {
        return mDuration;
    }

    public Artist getArtist() {
        return mArtist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mGenre);
        dest.writeString(mTitle);
        dest.writeString(mStreamUrl);
        dest.writeString(mArtworkUrl);
        dest.writeInt(mDuration);
        dest.writeParcelable(mArtist, flags);
    }

    public static class Builder {
        private int mId;
        private String mGenre;
        private String mTitle;
        private String mStreamUrl;
        private String mArtworkUrl;
        private int mDuration;
        private Artist mArtist;

        public Builder() {
        }

        public Builder(int id, String genre, String title, String streamUrl, String artworkUrl,
            int duration, Artist artist) {
            mId = id;
            mGenre = genre;
            mTitle = title;
            mStreamUrl = streamUrl;
            mArtworkUrl = artworkUrl;
            mDuration = duration;
            mArtist = artist;
        }

        public Builder withId(int id) {
            mId = id;
            return this;
        }

        public Builder withGenre(String genre) {
            mGenre = genre;
            return this;
        }

        public Builder withTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder withStreamUrl(String streamUrl) {
            mStreamUrl = streamUrl;
            return this;
        }

        public Builder withArtworkUrl(String artworkUrl) {
            mArtworkUrl = artworkUrl;
            return this;
        }

        public Builder withDuration(int duration) {
            mDuration = duration;
            return this;
        }

        public Builder withArtist(Artist artist) {
            mArtist = artist;
            return this;
        }

        public Song build() {
            return new Song(this);
        }
    }

    public class SongComponent {
        public static final String ID = "id";
        public static final String DURATION = "duration";
        public static final String TITLE = "title";
        public static final String STREAM_URL = "stream_url";
        public static final String ARTWORK_URL = "artwork_url";
        public static final String GENRE = "genre";
    }
}
