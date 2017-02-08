package com.wangdaye.mysplash._common.data.entity.unsplash;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo.
 * */

public class Photo
        implements Parcelable {
    // data
    public boolean loadPhotoSuccess = false;
    public boolean hasFadedIn = false;
    public boolean settingLike = false;

    /**
     * id : Dwu85P9SOIk
     * created_at : 2016-05-03T11:00:28-04:00
     * width : 2448
     * height : 3264
     * color : #6E633A
     * downloads : 1345
     * likes : 24
     * liked_by_user : false
     * exif : {"make":"Canon","model":"Canon EOS 40D","exposure_time":"0.011111111111111112","aperture":"4.970854","focal_length":"37","iso":100}
     * location : {"city":"Montreal","country":"Canada","position":{"latitude":45.4732984,"longitude":-73.6384879}}
     * current_user_collections : [{"id":206,"title":"Makers: Cat and Ben","published_at":"2016-01-12T18:16:09-05:00","curated":false,"cover_photo":{"id":"xCmvrpzctaQ","width":7360,"height":4912,"color":"#040C14","likes":12,"liked_by_user":false,"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}},"urls":{"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"categories":[{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download","download_location":"https://api.unsplash.com/photos/xCmvrpzctaQ/download"}},"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}},"links":{"self":"https://api.unsplash.com/collections/206","html":"https://unsplash.com/collections/206","photos":"https://api.unsplash.com/collections/206/photos"}}]
     * urls : {"raw":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d","full":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg","regular":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max","small":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max"}
     * categories : [{"id":4,"title":"Nature","photo_count":24783,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}]
     * links : {"self":"https://api.unsplash.com/photos/Dwu85P9SOIk","html":"https://unsplash.com/photos/Dwu85P9SOIk","download":"https://unsplash.com/photos/Dwu85P9SOIk/download","download_location":"https://api.unsplash.com/photos/Dwu85P9SOIk/download"}
     * user : {"id":"QPxL2MGqfrw","username":"exampleuser","name":"Joe Example","portfolio_url":"https://example.com/","bio":"Just an everyday Joe","location":"Montreal","total_likes":5,"total_photos":10,"total_collections":13,"links":{"self":"https://api.unsplash.com/users/exampleuser","html":"https://unsplash.com/exampleuser","photos":"https://api.unsplash.com/users/exampleuser/photos","likes":"https://api.unsplash.com/users/exampleuser/likes","portfolio":"https://api.unsplash.com/users/exampleuser/portfolio"}}
     */
    public String id;
    public String created_at;
    public int width;
    public int height;
    public String color;
    public int downloads;
    public int likes;
    public boolean liked_by_user;

    public Exif exif;
    public Location location;
    public PhotoUrls urls;
    public PhotoLinks links;
    public User user;
    public List<Collection> current_user_collections;
    public List<Category> categories;

    /** <br> data. */

    public int getRegularWidth() {
        try {
            int w = Integer.parseInt(Uri.parse(urls.regular).getQueryParameter("w"));
            return w == 0 ? 1080 : w;
        } catch (NumberFormatException e) {
            return 1080;
        }
    }

    public int getRegularHeight() {
        return (int) (1.0 * height * getRegularWidth() / width);
    }

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.loadPhotoSuccess ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasFadedIn ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.created_at);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.color);
        dest.writeInt(this.downloads);
        dest.writeInt(this.likes);
        dest.writeByte(this.liked_by_user ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.exif, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.urls, flags);
        dest.writeParcelable(this.links, flags);
        dest.writeParcelable(this.user, flags);
        if (current_user_collections == null) {
            current_user_collections = new ArrayList<>();
        }
        dest.writeList(this.current_user_collections);
        if (categories == null) {
            categories = new ArrayList<>();
        }
        dest.writeList(this.categories);
    }

    public Photo() {
    }

    protected Photo(Parcel in) {
        this.loadPhotoSuccess = in.readByte() != 0;
        this.hasFadedIn = in.readByte() != 0;
        this.id = in.readString();
        this.created_at = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.color = in.readString();
        this.downloads = in.readInt();
        this.likes = in.readInt();
        this.liked_by_user = in.readByte() != 0;
        this.exif = in.readParcelable(Exif.class.getClassLoader());
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.urls = in.readParcelable(PhotoUrls.class.getClassLoader());
        this.links = in.readParcelable(PhotoLinks.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.current_user_collections = new ArrayList<>();
        in.readList(this.current_user_collections, Collection.class.getClassLoader());
        this.categories = new ArrayList<>();
        in.readList(this.categories, Category.class.getClassLoader());
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
