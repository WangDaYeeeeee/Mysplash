package com.wangdaye.common.bus.event;

import androidx.annotation.Nullable;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;

public class PhotoEvent {

    public String photoId;
    public boolean like;
    @Nullable public Photo photo;
    @Nullable public Collection collection;
    public Event event;

    public enum Event {
        COMPLETE, LIKE_OR_CANCEL, COLLECT_OR_REMOVE
    }

    private PhotoEvent(String photoId, boolean like,
                       @Nullable Photo photo, @Nullable Collection collection, Event event) {
        this.photoId = photoId;
        this.like = like;
        this.photo = photo;
        this.collection = collection;
        this.event = event;
    }

    public static PhotoEvent complete(Photo photo) {
        return new PhotoEvent(photo.id, photo.liked_by_user, photo, null, Event.COMPLETE);
    }

    public static PhotoEvent likeOrCancel(String photoId, boolean like) {
        return new PhotoEvent(photoId, like, null, null, Event.LIKE_OR_CANCEL);
    }

    public static PhotoEvent collectOrRemove(Photo photo, Collection collection) {
        return new PhotoEvent(photo.id, photo.liked_by_user, photo, collection, Event.COLLECT_OR_REMOVE);
    }
}
