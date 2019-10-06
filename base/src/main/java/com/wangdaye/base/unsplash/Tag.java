package com.wangdaye.base.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tag.
 * */

public class Tag implements com.wangdaye.base.i.Tag, Parcelable {

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

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    // interface.

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getRegularUrl() {
        return url + "?fm=jpg&w=720&fit=max";
    }

    @Override
    public String getThumbnailUrl() {
        return url + "?fm=jpg&w=360&fit=max";
    }

}
