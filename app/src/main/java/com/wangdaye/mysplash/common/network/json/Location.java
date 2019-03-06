package com.wangdaye.mysplash.common.network.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Location.
 * */

public class Location implements Parcelable {

    /**
     * title : Kitsuné Café, Montreal, Canada
     * name : Kitsuné Café
     * city : Montreal
     * country : Canada
     * position : {"latitude":45.4732984,"longitude":-73.6384879}
     */
    public String title;
    public String name;
    public String city;
    public String country;

    public Position position;

    // parcel.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeParcelable(this.position, flags);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.title = in.readString();
        this.name = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.position = in.readParcelable(Position.class.getClassLoader());
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
