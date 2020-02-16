package com.wangdaye.base.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Category.
 * */

public class Category implements Parcelable, Serializable {

    /**
     * id : 2
     * title : Buildings
     * photo_count : 3428
     * links : {"self":"https://api.unsplash.com/categories/2","photos":"https://api.unsplash.com/categories/2/photos"}
     */
    public int id;
    public String title;
    public int photo_count;

    public CategoryLinks links;

    // parcel.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.photo_count);
        dest.writeParcelable(this.links, flags);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.photo_count = in.readInt();
        this.links = in.readParcelable(CategoryLinks.class.getClassLoader());
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
