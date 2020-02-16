package com.wangdaye.photo.ui.adapter.photo.model;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class LocationModel extends PhotoInfoAdapter3.ViewModel {

    public String title;

    public LocationModel(Photo photo) {
        super(photo);
        this.title = photo.location.title;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof LocationModel;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ((LocationModel) newModel).title.equals(title);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
