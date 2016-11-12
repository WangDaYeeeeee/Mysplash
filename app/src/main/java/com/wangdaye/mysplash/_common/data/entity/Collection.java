package com.wangdaye.mysplash._common.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Collection.
 * */

public class Collection implements Parcelable {

    /**
     * id : 206
     * title : Makers: Cat and Ben
     * description : Behind-the-scenes photos from the Makers interview with designers Cat Noone and Benedikt Lehnert.
     * published_at : 2016-01-12T18:16:09-05:00
     * curated : false
     * featured : false
     * total_photos : 12
     * private : false
     * share_key : 312d188df257b957f8b86d2ce20e4766
     * cover_photo : {"id":"xCmvrpzctaQ","width":7360,"height":4912,"color":"#040C14","likes":12,"liked_by_user":false,"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank. Creators of Unsplash.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}},"urls":{"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"categories":[{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download","download_location":"https://api.unsplash.com/photos/xCmvrpzctaQ/download"}}
     * user : {"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank. Creators of Unsplash.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}}
     * links : {"self":"https://api.unsplash.com/collections/206","html":"https://unsplash.com/collections/206/makers-cat-and-ben","photos":"https://api.unsplash.com/collections/206/photos"}
     */
    public int id;
    public String title;
    public String description;
    public String published_at;
    public boolean curated;
    public boolean featured;
    public int total_photos;
    @SerializedName("private")
    public boolean privateX;
    public String share_key;

    public Photo cover_photo;
    public User user;
    public CollectionLinks links;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.published_at);
        dest.writeByte(this.curated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
        dest.writeInt(this.total_photos);
        dest.writeByte(this.privateX ? (byte) 1 : (byte) 0);
        dest.writeString(this.share_key);
        dest.writeParcelable(this.cover_photo, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.links, flags);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.published_at = in.readString();
        this.curated = in.readByte() != 0;
        this.featured = in.readByte() != 0;
        this.total_photos = in.readInt();
        this.privateX = in.readByte() != 0;
        this.share_key = in.readString();
        this.cover_photo = in.readParcelable(Photo.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.links = in.readParcelable(CollectionLinks.class.getClassLoader());
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
