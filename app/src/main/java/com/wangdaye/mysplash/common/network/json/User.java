package com.wangdaye.mysplash.common.network.json;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.Previewable;

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
     * id : QV5S1rtoUJ0
     * updated_at : 2018-01-03T18:50:04-05:00
     * numeric_id : 8586
     * username : unsplash
     * name : Unsplash
     * first_name : Unsplash
     * last_name : null
     * twitter_username : unsplash
     * portfolio_url : http://unsplash.com
     * bio : Make something awesome.
     * location : Montreal, Canada
     * total_likes : 18717
     * total_photos : 0
     * total_collections : 126
     * followed_by_user : false
     * following_count : 329
     * followers_count : 1115681
     * downloads : 0
     * profile_image : {"small":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=6676f08bc1f6638d9d97e28f53252937","medium":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=fb59ebefbd52e943eb5abf68d7edc020","large":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=a506ec7dcb2fe02cb7089bea78c4df68"}
     * photos : []
     * completed_onboarding : true
     * badge : null
     * tags : {"custom":[{"title":"wallpaper"},{"title":"color"},{"title":"outdoor"},{"title":"forest"},{"title":"travel"}],"aggregated":[{"title":"light"},{"title":"wallpaper"},{"title":"blue"},{"title":"color"},{"title":"design"},{"title":"outdoor"},{"title":"forest"},{"title":"sun"},{"title":"outside"},{"title":"green"},{"title":"cloud"},{"title":"wood"},{"title":"dark"},{"title":"travel"},{"title":"summer"},{"title":"lake"},{"title":"grass"},{"title":"silhouette"},{"title":"sunset"},{"title":"city"},{"title":"business"},{"title":"street"},{"title":"night"},{"title":"hand"},{"title":"sunrise"},{"title":"texture"},{"title":"pattern"},{"title":"building"},{"title":"shadow"},{"title":"plant"}]}
     * links : {"self":"https://api.unsplash.com/users/unsplash","html":"https://unsplash.com/@unsplash","photos":"https://api.unsplash.com/users/unsplash/photos","likes":"https://api.unsplash.com/users/unsplash/likes","portfolio":"https://api.unsplash.com/users/unsplash/portfolio","following":"https://api.unsplash.com/users/unsplash/following","followers":"https://api.unsplash.com/users/unsplash/followers"}
     */
    public String id;
    public String updated_at;
    public int numeric_id;
    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public String twitter_username;
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
    public boolean completed_onboarding;
    public Badge badge;
    public UserTags tags;
    public UserLinks links;
    public List<Photo> photos;

    public static class UserTags implements Parcelable {
        public List<Tag> custom;
        public List<Tag> aggregated;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.custom);
            dest.writeTypedList(this.aggregated);
        }

        public UserTags() {
        }

        protected UserTags(Parcel in) {
            this.custom = in.createTypedArrayList(Tag.CREATOR);
            this.aggregated = in.createTypedArrayList(Tag.CREATOR);
        }

        public static final Creator<UserTags> CREATOR = new Creator<UserTags>() {
            @Override
            public UserTags createFromParcel(Parcel source) {
                return new UserTags(source);
            }

            @Override
            public UserTags[] newArray(int size) {
                return new UserTags[size];
            }
        };
    }

    // parcel.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.hasFadedIn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.complete ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.updated_at);
        dest.writeInt(this.numeric_id);
        dest.writeString(this.username);
        dest.writeString(this.name);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.twitter_username);
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
        dest.writeByte(this.completed_onboarding ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.badge, flags);
        dest.writeParcelable(this.tags, flags);
        dest.writeParcelable(this.links, flags);
        dest.writeTypedList(this.photos);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.hasFadedIn = in.readByte() != 0;
        this.complete = in.readByte() != 0;
        this.id = in.readString();
        this.updated_at = in.readString();
        this.numeric_id = in.readInt();
        this.username = in.readString();
        this.name = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.twitter_username = in.readString();
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
        this.completed_onboarding = in.readByte() != 0;
        this.badge = in.readParcelable(Badge.class.getClassLoader());
        this.tags = in.readParcelable(UserTags.class.getClassLoader());
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

    // interface.

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

    /**
     * Update load information for user.
     *
     * @return Return true when load information has been changed. Otherwise return false.
     * */
    public boolean updateLoadInformation(User user) {
        if (this.hasFadedIn != user.hasFadedIn) {
            this.hasFadedIn = user.hasFadedIn;
            return true;
        } else {
            return false;
        }
    }
}
