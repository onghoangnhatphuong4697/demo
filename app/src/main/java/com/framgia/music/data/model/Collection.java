package com.framgia.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by Admin on 3/11/2018.
 */

public final class Collection implements Parcelable {

    private List<Track> mTrackList;
    private String mNextHref;

    public Collection() {}

    private Collection(Parcel in) {
        mTrackList = in.createTypedArrayList(Track.CREATOR);
        mNextHref = in.readString();
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mTrackList);
        parcel.writeString(mNextHref);
    }

    public List<Track> getTrackList() {
        return mTrackList;
    }

    public void setTrackList(List<Track> trackList) {
        mTrackList = trackList;
    }

    public String getNextHref() {
        return mNextHref;
    }

    public void setNextHref(String nextHref) {
        mNextHref = nextHref;
    }

    public static Creator<Collection> getCREATOR() {
        return CREATOR;
    }
}
