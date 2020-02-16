package com.wangdaye.base.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Stats
 * */

public class Stats implements Parcelable, Serializable {

    /**
     * total : 2422
     */

    public Downloads downloads;
    /**
     * total : 472663
     */

    public Views views;
    /**
     * total : 283
     */

    public Likes likes;

    public static class Downloads implements Parcelable {
        public int total;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
        }

        public Downloads() {
        }

        protected Downloads(Parcel in) {
            this.total = in.readInt();
        }

        public static final Creator<Downloads> CREATOR = new Creator<Downloads>() {
            @Override
            public Downloads createFromParcel(Parcel source) {
                return new Downloads(source);
            }

            @Override
            public Downloads[] newArray(int size) {
                return new Downloads[size];
            }
        };
    }

    public static class Views implements Parcelable {
        public int total;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
        }

        public Views() {
        }

        protected Views(Parcel in) {
            this.total = in.readInt();
        }

        public static final Creator<Views> CREATOR = new Creator<Views>() {
            @Override
            public Views createFromParcel(Parcel source) {
                return new Views(source);
            }

            @Override
            public Views[] newArray(int size) {
                return new Views[size];
            }
        };
    }

    public static class Likes implements Parcelable {
        public int total;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
        }

        public Likes() {
        }

        protected Likes(Parcel in) {
            this.total = in.readInt();
        }

        public static final Creator<Likes> CREATOR = new Creator<Likes>() {
            @Override
            public Likes createFromParcel(Parcel source) {
                return new Likes(source);
            }

            @Override
            public Likes[] newArray(int size) {
                return new Likes[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.downloads, flags);
        dest.writeParcelable(this.views, flags);
        dest.writeParcelable(this.likes, flags);
    }

    public Stats() {
    }

    protected Stats(Parcel in) {
        this.downloads = in.readParcelable(Downloads.class.getClassLoader());
        this.views = in.readParcelable(Views.class.getClassLoader());
        this.likes = in.readParcelable(Likes.class.getClassLoader());
    }

    public static final Creator<Stats> CREATOR = new Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel source) {
            return new Stats(source);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };
}
