package com.wangdaye.mysplash._common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tag.
 * */

public class Tag implements com.wangdaye.mysplash._common._basic.Tag, Parcelable {

    /**
     * title : frozen
     * url : https://images.unsplash.com/photo-1420466721261-818d807296a1
     */

    public String title;
    public String url;

    /** <br> parcelable. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    /** <br> interface. */

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
