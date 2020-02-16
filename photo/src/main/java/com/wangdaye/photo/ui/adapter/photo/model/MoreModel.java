package com.wangdaye.photo.ui.adapter.photo.model;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class MoreModel extends PhotoInfoAdapter3.ViewModel {

    public Collection collection;

    public MoreModel(Photo photo, int collectionIndex) {
        super(photo);
        assert photo.related_collections != null;
        this.collection = photo.related_collections.results.get(collectionIndex);
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof MoreModel
                && ((MoreModel) newModel).collection.id == collection.id;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return true;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
