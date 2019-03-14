package com.wangdaye.mysplash.common.network.json;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Action object.
 * */

public class ActionObject {
    // comment part.
    public boolean hasFadedIn = false;
    public String id;
    public int downloads;
    public PhotoLinks links;

    // photo part.
    public boolean loadPhotoSuccess = false;
    public boolean settingLike = false;
    public String created_at;
    public int width;
    public int height;
    public String color;
    public int likes;
    public boolean liked_by_user;
    public Exif exif;
    public PhotoUrls urls;
    public User user;
    @Nullable public List<Collection> current_user_collections;
    @Nullable public List<Category> categories;

    // user part.
    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public String portfolio_url;
    public String bio;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public boolean followed_by_user;
    public ProfileImage profile_image;
    public Badge badge;

    public ActionObject(Photo p) {
        loadPhotoSuccess = p.loadPhotoSuccess;
        hasFadedIn = p.hasFadedIn;
        settingLike = p.settingLike;
        id = p.id;
        created_at = p.created_at;
        width = p.width;
        height = p.height;
        color = p.color;
        downloads = p.downloads;
        likes = p.likes;
        liked_by_user = p.liked_by_user;
        exif = p.exif;
        urls = p.urls;
        links = p.links;
        user = p.user;
        current_user_collections = new ArrayList<>(p.current_user_collections);
        categories = new ArrayList<>(p.categories);
    }

    public ActionObject(User u) {
        hasFadedIn = u.hasFadedIn;
        id = u.id;
        downloads = u.downloads;
        username = u.username;
        name = u.name;
        first_name = u.first_name;
        last_name = u.last_name;
        portfolio_url = u.portfolio_url;
        bio = u.bio;
        total_likes = u.total_likes;
        total_photos = u.total_photos;
        total_collections = u.total_collections;
        followed_by_user = u.followed_by_user;
        profile_image = u.profile_image;
        badge = u.badge;
    }

    public Photo castToPhoto() {
        Photo p = new Photo();
        p.loadPhotoSuccess = loadPhotoSuccess;
        p.hasFadedIn = hasFadedIn;
        p.settingLike = settingLike;
        p.id = id;
        p.created_at = created_at;
        p.width = width;
        p.height = height;
        p.color = color;
        p.downloads = downloads;
        p.likes = likes;
        p.liked_by_user = liked_by_user;
        p.exif = exif;
        p.urls = urls;
        p.links = links;
        p.user = user;
        p.current_user_collections = new ArrayList<>(current_user_collections);
        p.categories = new ArrayList<>(categories);
        return p;
    }

    public User castToUser() {
        User u = new User();
        u.id = id;
        u.username = username;
        u.name = name;
        u.first_name = first_name;
        u.last_name = last_name;
        u.portfolio_url = portfolio_url;
        u.bio = bio;
        u.total_likes = total_likes;
        u.total_photos = total_photos;
        u.total_collections = total_collections;
        u.followed_by_user = followed_by_user;
        u.downloads = downloads;
        u.profile_image = profile_image;
        u.badge = badge;
        return u;
    }
}
