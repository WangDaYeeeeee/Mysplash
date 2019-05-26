package com.wangdaye.mysplash.common.bus.event;

import com.wangdaye.mysplash.common.network.json.Collection;

public class CollectionEvent {

    public Collection collection;

    public Event event;

    public enum Event {
        UPDATE, CREATE, DELETE
    }

    public CollectionEvent(Collection collection, Event event) {
        this.collection = collection;
        this.event = event;
    }
}
