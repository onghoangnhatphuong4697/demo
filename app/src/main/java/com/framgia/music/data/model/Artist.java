package com.framgia.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 3/8/2018.
 */

public class Artist implements Parcelable {

    private String mAvatarUrl;
    private Integer mId;
    private String mUsername;

    public Artist() {}

    private Artist(Parcel in) {
        mAvatarUrl = in.readString();
        if (in.readByte() != 0) {
            mId = in.readInt();
        }
        mUsername = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAvatarUrl);
        if (mId != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(mId);
        }
        parcel.writeString(mUsername);
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public static Creator<Artist> getCREATOR() {
        return CREATOR;
    }
}
