package com.framgia.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 3/8/2018.
 */

public class Track implements Parcelable {

    private Integer mId;
    private String mKind;
    private String mUri;
    private Integer mUserId;
    private String mGenre;
    private String mTitle;
    private String mStreamUrl;
    private String mArtworkUrl;
    private Boolean mDownloadable;
    private Artist mArtist;

    public Track() {
    }

    private Track(Parcel in) {
        if (in.readByte() != 0) {
            mId = in.readInt();
        }
        mKind = in.readString();
        mUri = in.readString();
        if (in.readByte() != 0) {
            mUserId = in.readInt();
        }
        mGenre = in.readString();
        mTitle = in.readString();
        mStreamUrl = in.readString();
        mArtworkUrl = in.readString();
        byte tmpMDownloadable = in.readByte();
        mDownloadable = tmpMDownloadable == 0 ? null : tmpMDownloadable == 1;
        mArtist = in.readParcelable(Artist.class.getClassLoader());
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
        parcel.writeString(mUri);
        if (mUserId != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mUserId);
        }
        parcel.writeString(mGenre);
        parcel.writeString(mTitle);
        parcel.writeString(mStreamUrl);
        parcel.writeString(mArtworkUrl);
        parcel.writeByte((byte) (mDownloadable == null ? 0 : mDownloadable ? 1 : 2));
        parcel.writeParcelable(mArtist, i);
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
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

    public static Creator<Track> getCREATOR() {
        return CREATOR;
    }
}
