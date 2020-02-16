package com.wangdaye.photo.ui.adapter.photo.model;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class DragFlagModel extends PhotoInfoAdapter3.ViewModel {

    public DragFlagModel(Photo photo) {
        super(photo);
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof DragFlagModel;
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
