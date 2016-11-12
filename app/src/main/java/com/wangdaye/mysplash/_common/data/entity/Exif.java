package com.wangdaye.mysplash._common.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Exif.
 * */

public class Exif implements Parcelable {

    /**
     * make : Canon
     * model : Canon EOS 40D
     * exposure_time : 0.011111111111111112
     * aperture : 4.970854
     * focal_length : 37
     * iso : 100
     */
    public String make;
    public String model;
    public String exposure_time;
    public String aperture;
    public String focal_length;
    public int iso;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.make);
        dest.writeString(this.model);
        dest.writeString(this.exposure_time);
        dest.writeString(this.aperture);
        dest.writeString(this.focal_length);
        dest.writeInt(this.iso);
    }

    public Exif() {
    }

    protected Exif(Parcel in) {
        this.make = in.readString();
        this.model = in.readString();
        this.exposure_time = in.readString();
        this.aperture = in.readString();
        this.focal_length = in.readString();
        this.iso = in.readInt();
    }

    public static final Parcelable.Creator<Exif> CREATOR = new Parcelable.Creator<Exif>() {
        @Override
        public Exif createFromParcel(Parcel source) {
            return new Exif(source);
        }

        @Override
        public Exif[] newArray(int size) {
            return new Exif[size];
        }
    };
}
