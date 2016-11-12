package com.wangdaye.mysplash._common.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User links.
 * */

public class UserLinks implements Parcelable {

    /**
     * self : https://api.unsplash.com/users/jimmyexample
     * html : https://unsplash.com/jimmyexample
     * photos : https://api.unsplash.com/users/jimmyexample/photos
     * likes : https://api.unsplash.com/users/jimmyexample/likes
     * portfolio : https://api.unsplash.com/users/jimmyexample/portfolio
     */
    public String self;
    public String html;
    public String photos;
    public String likes;
    public String portfolio;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.self);
        dest.writeString(this.html);
        dest.writeString(this.photos);
        dest.writeString(this.likes);
        dest.writeString(this.portfolio);
    }

    public UserLinks() {
    }

    protected UserLinks(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.photos = in.readString();
        this.likes = in.readString();
        this.portfolio = in.readString();
    }

    public static final Parcelable.Creator<UserLinks> CREATOR = new Parcelable.Creator<UserLinks>() {
        @Override
        public UserLinks createFromParcel(Parcel source) {
            return new UserLinks(source);
        }

        @Override
        public UserLinks[] newArray(int size) {
            return new UserLinks[size];
        }
    };
}
