package com.wangdaye.mysplash.common.utils.manager;

import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;

import java.util.ArrayList;
import java.util.List;

/**
 * User collections manager.
 *
 * A manager class that is used to manage collections cache for user.
 *
 * */

public class UserCollectionsManager {

    private List<Collection> collectionList;

    // if set true, it means all of the user's collections have been loaded. Next time, if a view
    // needs to show user's collections, it doesn't need to request them again but can read the
    // cache data here.
    private boolean loadFinish;

    UserCollectionsManager() {
        collectionList = new ArrayList<>();
        loadFinish = false;
    }

    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void addCollections(List<Collection> list) {
        collectionList.addAll(list);
    }

    public void addCollectionToFirst(Collection c) {
        collectionList.add(0, c);
    }

    public void updateCollection(Collection collection) {
        for (int i = 0; i < collectionList.size(); i ++) {
            if (collection.id == collectionList.get(i).id) {
                collectionList.set(i, collection);
                return;
            }
        }
    }

    public void deleteCollection(Collection collection) {
        for (int i = 0; i < collectionList.size(); i ++) {
            if (collection.id == collectionList.get(i).id) {
                collectionList.remove(i);
                return;
            }
        }
    }

    public void clearCollections() {
        collectionList.clear();
        setLoadFinish(false);
    }

    public void finishEdit(int id) {
        for (int i = 0; i < collectionList.size(); i ++) {
            if (id == collectionList.get(i).id) {
                collectionList.get(i).editing = false;
                return;
            }
        }
    }

    public void finishEdit() {
        for (int i = 0; i < collectionList.size(); i ++) {
            collectionList.get(i).editing = false;
        }
    }

    public boolean isLoadFinish() {
        return loadFinish;
    }

    public void setLoadFinish(boolean finish) {
        loadFinish = finish;
    }
}
