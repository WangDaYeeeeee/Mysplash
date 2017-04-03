package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Badge.
 * */

public class Badge implements Parcelable {

    /**
     * title : Book contributor
     * primary : true
     * slug : book-contributor
     * link : https://book.unsplash.com
     */
    public String title;
    public boolean primary;
    public String slug;
    public String link;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
        dest.writeString(this.slug);
        dest.writeString(this.link);
    }

    public Badge() {
    }

    protected Badge(Parcel in) {
        this.title = in.readString();
        this.primary = in.readByte() != 0;
        this.slug = in.readString();
        this.link = in.readString();
    }

    public static final Parcelable.Creator<Badge> CREATOR = new Parcelable.Creator<Badge>() {
        @Override
        public Badge createFromParcel(Parcel source) {
            return new Badge(source);
        }

        @Override
        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };
}
