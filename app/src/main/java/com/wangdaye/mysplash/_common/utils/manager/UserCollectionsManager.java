package com.wangdaye.mysplash._common.utils.manager;

import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;

import java.util.ArrayList;
import java.util.List;

/**
 * User collections manager.
 * */

public class UserCollectionsManager {
    // data
    private List<Collection> collectionList;
    private boolean loadFinish;

    /** <br> life cycle. */

    UserCollectionsManager() {
        collectionList = new ArrayList<>();
        loadFinish = false;
    }

    /** <br> data. */

    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void addCollections(List<Collection> list) {
        for (int i = 0; i < list.size(); i ++) {
            collectionList.add(list.get(i));
        }
    }

    public void addCollectionToFirst(Collection c) {
        collectionList.add(0, c);
    }

    public void clearCollections() {
        collectionList.clear();
        setLoadFinish(false);
    }

    public boolean isLoadFinish() {
        return loadFinish;
    }

    public void setLoadFinish(boolean finish) {
        loadFinish = finish;
    }
}
