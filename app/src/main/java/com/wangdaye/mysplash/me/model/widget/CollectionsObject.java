package com.wangdaye.mysplash.me.model.widget;

import android.app.Activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.i.model.CollectionsModel;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;

import java.util.ArrayList;

/**
 * Collections object.
 * */

public class CollectionsObject
        implements CollectionsModel {

    private CollectionAdapter adapter;
    private CollectionService service;

    private int collectionsPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public CollectionsObject(Activity a) {
        this.adapter = new CollectionAdapter(a, new ArrayList<Collection>(Mysplash.DEFAULT_PER_PAGE));
        this.service = CollectionService.getService();

        this.collectionsPage = 0;

        this.loading = false;
        this.over = false;
    }

    @Override
    public CollectionAdapter getAdapter() {
        return adapter;
    }

    @Override
    public CollectionService getService() {
        return service;
    }

    @Override
    public Object getRequestKey() {
        return null;
    }

    @Override
    public void setRequestKey(Object key) {
        // do nothing.
    }

    @Override
    public int getCollectionsType() {
        return Mysplash.COLLECTION_TYPE_ALL;
    }

    @Override
    public void setCollectionsType(int order) {
        // do nothing.
    }

    @Override
    public int getCollectionsPage() {
        return collectionsPage;
    }

    @Override
    public void setCollectionsPage(int page) {
        collectionsPage = page;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }
}

