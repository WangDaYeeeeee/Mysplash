package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wangdaye.mysplash.common._basic.Previewable;

import java.util.List;

/**
 * User.
 * */

public class User
        implements Parcelable, Previewable {
    // data
    public boolean hasFadedIn = false;
    public boolean complete = false;

    /**
     * id : RfO4tDTEHg0
     * numeric_id : 14100
     * username : mattrobinjones
     * name : Matt Jones
     * first_name : Matt
     * last_name : Jones
     * portfolio_url : null
     * bio : amateur photographer from the lake-district UK
     * location : Lake District UK
     * total_likes : 34
     * total_photos : 43
     * total_collections : 0
     * followed_by_user : false
     * following_count : 1
     * followers_count : 9
     * downloads : 54559
     * profile_image : {"small":"https://images.unsplash.com/profile-1456255141980-24ad7b54fce1?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=b57ba82392b15475237bb207e80adfc4","medium":"https://images.unsplash.com/profile-1456255141980-24ad7b54fce1?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=7f88c95701761de8c8f4baec14fc8be3","large":"https://images.unsplash.com/profile-1456255141980-24ad7b54fce1?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=d2e435519c0e05681f997f3bba1b8597"}
     * photos : [{"id":"emZus7dBLIw","created_at":"2016-02-24T04:27:51-05:00","width":5456,"height":3632,"color":"#FFE2AA","likes":880,"liked_by_user":false,"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1456305951335-bb8134aeab8a","full":"https://images.unsplash.com/photo-1456305951335-bb8134aeab8a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=238b020b62f17cd5bb5c57273447319d","regular":"https://images.unsplash.com/photo-1456305951335-bb8134aeab8a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=04d7f8c330b76007181f31626b6994f9","small":"https://images.unsplash.com/photo-1456305951335-bb8134aeab8a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=bf8503dd7076c12c0b0ccaaef48feecd","thumb":"https://images.unsplash.com/photo-1456305951335-bb8134aeab8a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=88f19799fb82e32bee0a4f34cdbbd20e"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}],"links":{"self":"https://api.unsplash.com/photos/emZus7dBLIw","html":"http://unsplash.com/photos/emZus7dBLIw","download":"http://unsplash.com/photos/emZus7dBLIw/download","download_location":"https://api.unsplash.com/photos/emZus7dBLIw/download"}},{"id":"hzJi-v0wbRc","created_at":"2016-02-22T07:11:26-05:00","width":5456,"height":3632,"color":"#070D0F","likes":481,"liked_by_user":false,"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1456143077270-30de0a1bf7bc","full":"https://images.unsplash.com/photo-1456143077270-30de0a1bf7bc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=c1d66061cb01e915353b7a1afa0916bc","regular":"https://images.unsplash.com/photo-1456143077270-30de0a1bf7bc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=a5abf00285bf3cda04af71ca4b83f576","small":"https://images.unsplash.com/photo-1456143077270-30de0a1bf7bc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=53149339ff90de1b8a2663a5c3aaf825","thumb":"https://images.unsplash.com/photo-1456143077270-30de0a1bf7bc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=2f06d01c4278649ccf299c2500bbd6ed"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}],"links":{"self":"https://api.unsplash.com/photos/hzJi-v0wbRc","html":"http://unsplash.com/photos/hzJi-v0wbRc","download":"http://unsplash.com/photos/hzJi-v0wbRc/download","download_location":"https://api.unsplash.com/photos/hzJi-v0wbRc/download"}},{"id":"xpDHTc-pkog","created_at":"2015-10-12T15:19:17-04:00","width":4612,"height":3632,"color":"#575749","likes":140,"liked_by_user":false,"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1444676632488-26a136c45b9b","full":"https://images.unsplash.com/photo-1444676632488-26a136c45b9b?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=89553e1b090a7598ba394375a1dcdf48","regular":"https://images.unsplash.com/photo-1444676632488-26a136c45b9b?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=7a1ec0bb54516cce5416d1ef7efb60a4","small":"https://images.unsplash.com/photo-1444676632488-26a136c45b9b?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=238411c034c54872a0d20be5a488c29a","thumb":"https://images.unsplash.com/photo-1444676632488-26a136c45b9b?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=6fd0c61627f28486659d9303da23b713"},"categories":[{"id":2,"title":"Buildings","photo_count":22897,"links":{"self":"https://api.unsplash.com/categories/2","photos":"https://api.unsplash.com/categories/2/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xpDHTc-pkog","html":"http://unsplash.com/photos/xpDHTc-pkog","download":"http://unsplash.com/photos/xpDHTc-pkog/download","download_location":"https://api.unsplash.com/photos/xpDHTc-pkog/download"}}]
     * completed_onboarding : false
     * badge : null
     * links : {"self":"https://api.unsplash.com/users/mattrobinjones","html":"http://unsplash.com/@mattrobinjones","photos":"https://api.unsplash.com/users/mattrobinjones/photos","likes":"https://api.unsplash.com/users/mattrobinjones/likes","portfolio":"https://api.unsplash.com/users/mattrobinjones/portfolio","following":"https://api.unsplash.com/users/mattrobinjones/following","followers":"https://api.unsplash.com/users/mattrobinjones/followers"}
     */

    public String id;
    public int numeric_id;
    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public String portfolio_url;
    public String bio;
    public String location;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public boolean followed_by_user;
    public int following_count;
    public int followers_count;
    public int downloads;

    public ProfileImage profile_image;
    public Badge badge;
    public boolean completed_onboarding;

    public UserLinks links;
    public List<Photo> photos;

    /** <br> parcel. */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.hasFadedIn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.complete ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeInt(this.numeric_id);
        dest.writeString(this.username);
        dest.writeString(this.name);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.portfolio_url);
        dest.writeString(this.bio);
        dest.writeString(this.location);
        dest.writeInt(this.total_likes);
        dest.writeInt(this.total_photos);
        dest.writeInt(this.total_collections);
        dest.writeByte(this.followed_by_user ? (byte) 1 : (byte) 0);
        dest.writeInt(this.following_count);
        dest.writeInt(this.followers_count);
        dest.writeInt(this.downloads);
        dest.writeParcelable(this.profile_image, flags);
        dest.writeParcelable(this.badge, flags);
        dest.writeByte(this.completed_onboarding ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.links, flags);
        dest.writeTypedList(this.photos);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.hasFadedIn = in.readByte() != 0;
        this.complete = in.readByte() != 0;
        this.id = in.readString();
        this.numeric_id = in.readInt();
        this.username = in.readString();
        this.name = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.portfolio_url = in.readString();
        this.bio = in.readString();
        this.location = in.readString();
        this.total_likes = in.readInt();
        this.total_photos = in.readInt();
        this.total_collections = in.readInt();
        this.followed_by_user = in.readByte() != 0;
        this.following_count = in.readInt();
        this.followers_count = in.readInt();
        this.downloads = in.readInt();
        this.profile_image = in.readParcelable(ProfileImage.class.getClassLoader());
        this.badge = in.readParcelable(Badge.class.getClassLoader());
        this.completed_onboarding = in.readByte() != 0;
        this.links = in.readParcelable(UserLinks.class.getClassLoader());
        this.photos = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /** <br> interface. */

    @Override
    public String getRegularUrl() {
        return profile_image.large;
    }

    @Override
    public String getFullUrl() {
        if (TextUtils.isEmpty(profile_image.custom)) {
            return getRegularUrl();
        } else {
            return profile_image.custom;
        }
    }

    @Override
    public String getDownloadUrl() {
        return profile_image.large;
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 128;
    }
}
