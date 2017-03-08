package com.wangdaye.mysplash._common.data.entity.unsplash;

import android.os.Parcel;
import android.os.Parcelable;

import com.wangdaye.mysplash.Mysplash;

/**
 * Category.
 * */

public class Category implements com.wangdaye.mysplash._common._basic.Tag, Parcelable {

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

    /** <br> parcel. */

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

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    /** <br> interface. */

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        switch (id) {
            case Mysplash.CATEGORY_BUILDINGS_ID:
                return "https://images.unsplash.com/photo-1481205009193-0b6b42cc81ac?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=02c02283298e6e2ca2e5a78daf4ca3c9";

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return "https://images.unsplash.com/photo-1453831362806-3d5577f014a4?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8174ca40677fa025950f31ba04166333";

            case Mysplash.CATEGORY_NATURE_ID:
                return "https://images.unsplash.com/photo-1433351120803-a29aeee7d1e7?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=1e4c3fed1ab6ecf6f2b469debf906e0f";

            case Mysplash.CATEGORY_OBJECTS_ID:
                return "https://images.unsplash.com/photo-1444881421460-d838c3b98f95?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=3699401d0a23c8b1d9e35beb4916f08c";

            case Mysplash.CATEGORY_PEOPLE_ID:
                return "https://images.unsplash.com/photo-1482028655172-fa4270a17164?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=49429cc70501f3648ada10e48a7782d6";

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return "https://images.unsplash.com/photo-1445620466293-d6316372ab59?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=a095c2ac8ba16956821be28e185f6833";

            default:
                return "https://images.unsplash.com/photo-1485282569499-bc24811e75ce?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=84b46b6eccfd6e591dfaa3945ababa6a";
        }
    }
}
