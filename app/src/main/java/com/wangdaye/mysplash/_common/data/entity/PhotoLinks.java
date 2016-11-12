package com.wangdaye.mysplash._common.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Photo links.
 * */

public class PhotoLinks implements Parcelable {

    /**
     * self : https://api.unsplash.com/photos/Dwu85P9SOIk
     * html : https://unsplash.com/photos/Dwu85P9SOIk
     * download : https://unsplash.com/photos/Dwu85P9SOIk/download
     * download_location : https://api.unsplash.com/photos/Dwu85P9SOIk/download
     */
    public String self;
    public String html;
    public String download;
    public String download_location;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.self);
        dest.writeString(this.html);
        dest.writeString(this.download);
        dest.writeString(this.download_location);
    }

    public PhotoLinks() {
    }

    protected PhotoLinks(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.download = in.readString();
        this.download_location = in.readString();
    }

    public static final Parcelable.Creator<PhotoLinks> CREATOR = new Parcelable.Creator<PhotoLinks>() {
        @Override
        public PhotoLinks createFromParcel(Parcel source) {
            return new PhotoLinks(source);
        }

        @Override
        public PhotoLinks[] newArray(int size) {
            return new PhotoLinks[size];
        }
    };
}
