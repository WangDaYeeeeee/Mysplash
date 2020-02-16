package com.wangdaye.photo.ui.adapter.photo.model;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class ProgressModel extends PhotoInfoAdapter3.ViewModel {

    public boolean failed;

    public ProgressModel(Photo photo, boolean failed) {
        super(photo);
        this.failed = failed;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof ProgressModel;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ((ProgressModel) newModel).failed == failed;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
