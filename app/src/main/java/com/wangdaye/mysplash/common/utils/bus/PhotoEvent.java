package com.wangdaye.mysplash.common.utils.bus;

import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PhotoEvent {

    @NonNull public Photo photo;
    @Nullable public Collection collection;

    public Event event;

    public enum Event {
        UPDATE, ADD_TO_COLLECTION, REMOVE_FROM_COLLECTION
    }

    public PhotoEvent(@NonNull Photo photo) {
        this.photo = photo;
        this.collection = null;
        this.event = Event.UPDATE;
    }

    public PhotoEvent(@NonNull Photo photo, @NonNull Collection collection, Event event) {
        this.photo = photo;
        this.collection = collection;
        this.event = event;
    }
}
