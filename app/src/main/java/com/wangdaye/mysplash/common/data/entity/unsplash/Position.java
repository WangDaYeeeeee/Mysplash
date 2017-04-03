package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Position.
 * */

public class Position implements Parcelable {

    /**
     * latitude : 45.4732984
     * longitude : -73.6384879
     */
    public double latitude;
    public double longitude;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public Position() {
    }

    protected Position(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel source) {
            return new Position(source);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}
