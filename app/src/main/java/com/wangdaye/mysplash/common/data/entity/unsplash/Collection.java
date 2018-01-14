package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Collection.
 * */

public class Collection implements Parcelable {
    // data
    public boolean editing = false;
    /**
     * id : 595970
     * title : Portrait Orientation
     * description : null
     * published_at : 2017-03-07T01:39:07-05:00
     * updated_at : 2018-01-03T16:10:14-05:00
     * curated : false
     * featured : true
     * total_photos : 3115
     * private : false
     * share_key : 83740db527816ece68a6b65d0dbd71f6
     * tags : [{"title":"portrait"},{"title":"flower"},{"title":"pink"},{"title":"plant"},{"title":"floral"},{"title":"purple"}]
     * cover_photo : {"id":"oLK5ovd1GZU","created_at":"2017-10-23T10:07:12-04:00","updated_at":"2017-11-01T08:36:03-04:00","width":2797,"height":4202,"color":"#01050A","likes":168,"liked_by_user":false,"description":null,"user":{"id":"z7tGVUlw3Ak","updated_at":"2017-10-31T03:18:32-04:00","username":"trevorbobyk","name":"Trevor Bobyk","first_name":"Trevor","last_name":"Bobyk","twitter_username":"trevorbobyk","portfolio_url":"https://itstrev.com/","bio":"Filmmaker & Photographer from Toronto, Canada. IG: @TrevorBobyk\r\nDownload my presets here: https://sellfy.com/itstrev","location":null,"total_likes":0,"total_photos":3,"total_collections":0,"profile_image":{"small":"https://images.unsplash.com/profile-fb-1507808054-555a76e0db85.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=c91f0dda566fdc2a2617bfecd64d08f3","medium":"https://images.unsplash.com/profile-fb-1507808054-555a76e0db85.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=a4d25302356e0dd5daba0b8922eb3fda","large":"https://images.unsplash.com/profile-fb-1507808054-555a76e0db85.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f609b2d3cbb03dc762793373ed5c11f6"},"links":{"self":"https://api.unsplash.com/users/trevorbobyk","html":"https://unsplash.com/@trevorbobyk","photos":"https://api.unsplash.com/users/trevorbobyk/photos","likes":"https://api.unsplash.com/users/trevorbobyk/likes","portfolio":"https://api.unsplash.com/users/trevorbobyk/portfolio","following":"https://api.unsplash.com/users/trevorbobyk/following","followers":"https://api.unsplash.com/users/trevorbobyk/followers"}},"urls":{"raw":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8","full":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=85&fm=jpg&crop=entropy&cs=srgb&s=9abf7df4062d76f50a0da8614285d480","regular":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=8b23cc86ea62cc7b6487d66d79f2a116","small":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=5e93a3dc63fe25200dec20b7de472d1c","thumb":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8e5d995ef2a40b2ea6bb403ce3dc4ac3"},"categories":[],"links":{"self":"https://api.unsplash.com/photos/oLK5ovd1GZU","html":"https://unsplash.com/photos/oLK5ovd1GZU","download":"https://unsplash.com/photos/oLK5ovd1GZU/download","download_location":"https://api.unsplash.com/photos/oLK5ovd1GZU/download"}}
     * preview_photos : [{"id":422783,"urls":{"raw":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8","full":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=85&fm=jpg&crop=entropy&cs=srgb&s=9abf7df4062d76f50a0da8614285d480","regular":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=8b23cc86ea62cc7b6487d66d79f2a116","small":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=5e93a3dc63fe25200dec20b7de472d1c","thumb":"https://images.unsplash.com/photo-1508767597875-c1c68f7b50a8?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8e5d995ef2a40b2ea6bb403ce3dc4ac3"}},{"id":412237,"urls":{"raw":"https://images.unsplash.com/photo-1508001300512-4763bb1c5583","full":"https://images.unsplash.com/photo-1508001300512-4763bb1c5583?ixlib=rb-0.3.5&q=85&fm=jpg&crop=entropy&cs=srgb&s=bd54c988a9e6afdf8b03f9882826b9b2","regular":"https://images.unsplash.com/photo-1508001300512-4763bb1c5583?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=2f6568b7a46d1b5c9af40b55fc1b375a","small":"https://images.unsplash.com/photo-1508001300512-4763bb1c5583?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=918bec187040ea6e181effd993736cdf","thumb":"https://images.unsplash.com/photo-1508001300512-4763bb1c5583?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=c4f12ba7f22cc2ab923abe13a1efcb8b"}},{"id":414110,"urls":{"raw":"https://images.unsplash.com/photo-1508131899480-eaad0e646088","full":"https://images.unsplash.com/photo-1508131899480-eaad0e646088?ixlib=rb-0.3.5&q=85&fm=jpg&crop=entropy&cs=srgb&s=ea7f54517b29ebec2f0722c418c540d7","regular":"https://images.unsplash.com/photo-1508131899480-eaad0e646088?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=12f9e05940539aabcca2987e52d55237","small":"https://images.unsplash.com/photo-1508131899480-eaad0e646088?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=37bba47bfc49324cc362e2d2f42aaece","thumb":"https://images.unsplash.com/photo-1508131899480-eaad0e646088?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=ee22202521561f7ba49fb4a7d1eabdd2"}},{"id":414247,"urls":{"raw":"https://images.unsplash.com/photo-1508138142660-302e69e74271","full":"https://images.unsplash.com/photo-1508138142660-302e69e74271?ixlib=rb-0.3.5&q=85&fm=jpg&crop=entropy&cs=srgb&s=5ebd6ec08c465dd8ee605a11e11e516e","regular":"https://images.unsplash.com/photo-1508138142660-302e69e74271?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=2080b5e3f51098a8a25682300dee8e95","small":"https://images.unsplash.com/photo-1508138142660-302e69e74271?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=96a098d696eda5c847a7b6985bc4d1bc","thumb":"https://images.unsplash.com/photo-1508138142660-302e69e74271?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=a3d143ee51b40862f11caf5d782469a1"}}]
     * user : {"id":"QV5S1rtoUJ0","updated_at":"2018-01-03T19:06:19-05:00","username":"unsplash","name":"Unsplash","first_name":"Unsplash","last_name":null,"twitter_username":"unsplash","portfolio_url":"http://unsplash.com","bio":"Make something awesome.","location":"Montreal, Canada","followed_by_user":false,"total_likes":18717,"total_photos":0,"total_collections":126,"profile_image":{"small":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=6676f08bc1f6638d9d97e28f53252937","medium":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=fb59ebefbd52e943eb5abf68d7edc020","large":"https://images.unsplash.com/profile-1441945026710-480e4372a5b5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=a506ec7dcb2fe02cb7089bea78c4df68"},"links":{"self":"https://api.unsplash.com/users/unsplash","html":"https://unsplash.com/@unsplash","photos":"https://api.unsplash.com/users/unsplash/photos","likes":"https://api.unsplash.com/users/unsplash/likes","portfolio":"https://api.unsplash.com/users/unsplash/portfolio","following":"https://api.unsplash.com/users/unsplash/following","followers":"https://api.unsplash.com/users/unsplash/followers"}}
     * links : {"self":"https://api.unsplash.com/collections/595970","html":"https://unsplash.com/collections/595970/portrait-orientation","photos":"https://api.unsplash.com/collections/595970/photos","related":"https://api.unsplash.com/collections/595970/related"}
     * keywords : ["wallpaper","tree","lock screen background","iphone background","iphone wallpaper","landscape","mountain","minimalist","countryside","city","nature","notepad","coffee cup","table","turntable"]
     */

    public int id;
    public String title;
    public String description;
    public String published_at;
    public String updated_at;
    public boolean curated;
    public boolean featured;
    public int total_photos;
    @SerializedName("private")
    public boolean privateX;
    public String share_key;
    public Photo cover_photo;
    public User user;
    public CollectionLinks links;
    public List<Tag> tags;
    public List<Photo> preview_photos;
    public List<String> keywords;

    // parcel.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.editing ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.published_at);
        dest.writeString(this.updated_at);
        dest.writeByte(this.curated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
        dest.writeInt(this.total_photos);
        dest.writeByte(this.privateX ? (byte) 1 : (byte) 0);
        dest.writeString(this.share_key);
        dest.writeParcelable(this.cover_photo, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.links, flags);
        dest.writeTypedList(this.tags);
        dest.writeTypedList(this.preview_photos);
        dest.writeStringList(this.keywords);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.editing = in.readByte() != 0;
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.published_at = in.readString();
        this.updated_at = in.readString();
        this.curated = in.readByte() != 0;
        this.featured = in.readByte() != 0;
        this.total_photos = in.readInt();
        this.privateX = in.readByte() != 0;
        this.share_key = in.readString();
        this.cover_photo = in.readParcelable(Photo.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.links = in.readParcelable(CollectionLinks.class.getClassLoader());
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.preview_photos = in.createTypedArrayList(Photo.CREATOR);
        this.keywords = in.createStringArrayList();
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
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
