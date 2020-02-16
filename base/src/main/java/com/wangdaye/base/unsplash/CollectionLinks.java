package com.wangdaye.base.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Collection links.
 * */

public class CollectionLinks implements Parcelable, Serializable {

    /**
     * self : https://api.unsplash.com/collections/296
     * html : https://unsplash.com/collections/296
     * photos : https://api.unsplash.com/collections/296/photos
     * related : https://api.unsplash.com/collections/296/related
     * download : https://api.unsplash.com/collections/296/related
     */
    public String self;
    public String html;
    public String photos;
    public String related;
    public String download;

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
        dest.writeString(this.related);
        dest.writeString(this.download);
    }

    public CollectionLinks() {
    }

    protected CollectionLinks(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.photos = in.readString();
        this.related = in.readString();
        this.download = in.readString();
    }

    public static final Creator<CollectionLinks> CREATOR = new Creator<CollectionLinks>() {
        @Override
        public CollectionLinks createFromParcel(Parcel source) {
            return new CollectionLinks(source);
        }

        @Override
        public CollectionLinks[] newArray(int size) {
            return new CollectionLinks[size];
        }
    };
}
