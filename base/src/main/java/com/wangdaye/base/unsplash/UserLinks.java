package com.wangdaye.base.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User links.
 * */

public class UserLinks implements Parcelable {

    /**
     * self : https://api.unsplash.com/users/mattrobinjones
     * html : http://unsplash.com/@mattrobinjones
     * photos : https://api.unsplash.com/users/mattrobinjones/photos
     * likes : https://api.unsplash.com/users/mattrobinjones/likes
     * portfolio : https://api.unsplash.com/users/mattrobinjones/portfolio
     * following : https://api.unsplash.com/users/mattrobinjones/following
     * followers : https://api.unsplash.com/users/mattrobinjones/followers
     */

    public String self;
    public String html;
    public String photos;
    public String likes;
    public String portfolio;
    public String following;
    public String followers;

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
        dest.writeString(this.following);
        dest.writeString(this.followers);
    }

    public UserLinks() {
    }

    protected UserLinks(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.photos = in.readString();
        this.likes = in.readString();
        this.portfolio = in.readString();
        this.following = in.readString();
        this.followers = in.readString();
    }

    public static final Creator<UserLinks> CREATOR = new Creator<UserLinks>() {
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
