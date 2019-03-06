package com.wangdaye.mysplash.common.network.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Photo urls.
 * */

public class PhotoUrls implements Parcelable {

    /**
     * raw : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d
     * full : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg
     * regular : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max
     * small : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max
     * thumb : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max
     * custom : https://images.unsplash.com/your-custom-image.jpg
     */
    public String raw;
    public String full;
    public String regular;
    public String small;
    public String thumb;
    public String custom;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.raw);
        dest.writeString(this.full);
        dest.writeString(this.regular);
        dest.writeString(this.small);
        dest.writeString(this.thumb);
        dest.writeString(this.custom);
    }

    public PhotoUrls() {
    }

    protected PhotoUrls(Parcel in) {
        this.raw = in.readString();
        this.full = in.readString();
        this.regular = in.readString();
        this.small = in.readString();
        this.thumb = in.readString();
        this.custom = in.readString();
    }

    public static final Creator<PhotoUrls> CREATOR = new Creator<PhotoUrls>() {
        @Override
        public PhotoUrls createFromParcel(Parcel source) {
            return new PhotoUrls(source);
        }

        @Override
        public PhotoUrls[] newArray(int size) {
            return new PhotoUrls[size];
        }
    };
}
