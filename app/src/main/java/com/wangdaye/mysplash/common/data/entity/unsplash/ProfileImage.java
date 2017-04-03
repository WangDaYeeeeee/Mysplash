package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Profile image.
 * */

public class ProfileImage implements Parcelable {

    /**
     * small : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32
     * medium : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64
     * large : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128
     * custom: https://images.unsplash.com/your-custom-image.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=100&w=100
     */
    public String small;
    public String medium;
    public String large;
    public String custom;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.small);
        dest.writeString(this.medium);
        dest.writeString(this.large);
        dest.writeString(this.custom);
    }

    public ProfileImage() {
    }

    protected ProfileImage(Parcel in) {
        this.small = in.readString();
        this.medium = in.readString();
        this.large = in.readString();
        this.custom = in.readString();
    }

    public static final Parcelable.Creator<ProfileImage> CREATOR = new Parcelable.Creator<ProfileImage>() {
        @Override
        public ProfileImage createFromParcel(Parcel source) {
            return new ProfileImage(source);
        }

        @Override
        public ProfileImage[] newArray(int size) {
            return new ProfileImage[size];
        }
    };
}
