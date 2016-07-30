package com.wangdaye.mysplash.common.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simplified photo.
 * */

public class SimplifiedPhoto implements Parcelable {
    public String id;
    public String created_at;
    public int width;
    public int height;
    public String color;

    public String user_id;
    public String user_name;

    public String url_download;
    public String url_raw;
    public String url_full;
    public String url_regular;
    public String url_user_avatar;
    public String url_html;

    public SimplifiedPhoto(Photo p) {
        this.id = p.id;
        this.created_at = p.created_at;
        this.width = p.width;
        this.height = p.height;
        this.color = p.color;

        this.user_id = p.user.id;
        this.user_name = p.user.name;

        this.url_download = p.links.download;
        this.url_raw = p.urls.raw;
        this.url_full = p.urls.full;
        this.url_regular = p.urls.regular;
        this.url_user_avatar = p.user.profile_image.large;
        this.url_html = p.links.html;
    }

    /** <br> interface. */

    protected SimplifiedPhoto(Parcel in) {
        id = in.readString();
        created_at = in.readString();
        width = in.readInt();
        height = in.readInt();
        color = in.readString();
        user_id = in.readString();
        user_name = in.readString();
        url_download = in.readString();
        url_raw = in.readString();
        url_full = in.readString();
        url_regular = in.readString();
        url_user_avatar = in.readString();
        url_html = in.readString();
    }

    public static final Creator<SimplifiedPhoto> CREATOR = new Creator<SimplifiedPhoto>() {
        @Override
        public SimplifiedPhoto createFromParcel(Parcel in) {
            return new SimplifiedPhoto(in);
        }

        @Override
        public SimplifiedPhoto[] newArray(int size) {
            return new SimplifiedPhoto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(created_at);
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeString(color);
        parcel.writeString(user_id);
        parcel.writeString(user_name);
        parcel.writeString(url_download);
        parcel.writeString(url_raw);
        parcel.writeString(url_full);
        parcel.writeString(url_regular);
        parcel.writeString(url_user_avatar);
        parcel.writeString(url_html);
    }
}
