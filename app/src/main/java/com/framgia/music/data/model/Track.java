package com.framgia.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 3/8/2018.
 */

public class Track implements Parcelable {

    private Integer mId;
    private String mKind;
    private String mCreateAt;
    private String mTitle;
    private Integer mDuration;
    private String mStreamUrl;
    private String mUri;
    private Integer mUserId;
    private String mArtworkUrl;
    private Boolean mDownloadable;
    private String mGenre;
    private Artist mArtist;
    private String mVideoUrl;
    private Object mDownloadUrl;

    public Track() {}

    private Track(Parcel in) {
        if (in.readByte() != 0) {
            mId = in.readInt();
        }
        mKind = in.readString();
        mCreateAt = in.readString();
        mTitle = in.readString();
        if (in.readByte() != 0) {
            mDuration = in.readInt();
        }
        mStreamUrl = in.readString();
        mUri = in.readString();
        if (in.readByte() != 0) {
            mUserId = in.readInt();
        }
        mArtworkUrl = in.readString();
        byte tmpMDownloadable = in.readByte();
        mDownloadable = tmpMDownloadable == 0 ? null : tmpMDownloadable == 1;
        mGenre = in.readString();
        mArtist = in.readParcelable(Artist.class.getClassLoader());
        mVideoUrl = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (mId != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mId);
        }
        parcel.writeString(mKind);
        parcel.writeString(mCreateAt);
        parcel.writeString(mTitle);
        if (mDuration != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mDuration);
        }
        parcel.writeString(mStreamUrl);
        parcel.writeString(mUri);
        if (mUserId != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mUserId);
        }
        parcel.writeString(mArtworkUrl);
        parcel.writeByte((byte) (mDownloadable == null ? 0 : mDownloadable ? 1 : 2));
        parcel.writeString(mGenre);
        parcel.writeParcelable(mArtist, i);
        parcel.writeString(mVideoUrl);
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String kind) {
        mKind = kind;
    }

    public String getCreateAt() {
        return mCreateAt;
    }

    public void setCreateAt(String createAt) {
        mCreateAt = createAt;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Integer getDuration() {
        return mDuration;
    }

    public void setDuration(Integer duration) {
        mDuration = duration;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        mStreamUrl = streamUrl;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {
        mUserId = userId;
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        mArtworkUrl = artworkUrl;
    }

    public Boolean getDownloadable() {
        return mDownloadable;
    }

    public void setDownloadable(Boolean downloadable) {
        mDownloadable = downloadable;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public Artist getArtist() {
        return mArtist;
    }

    public void setUser(Artist artist) {
        mArtist = artist;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        mVideoUrl = videoUrl;
    }

    public Object getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(Object downloadUrl) {
        mDownloadUrl = downloadUrl;
    }

    public static Creator<Track> getCREATOR() {
        return CREATOR;
    }
}
