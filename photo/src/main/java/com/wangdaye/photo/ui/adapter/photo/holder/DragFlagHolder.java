package com.wangdaye.photo.ui.adapter.photo.holder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wangdaye.photo.R;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.photo.ui.adapter.photo.model.DragFlagModel;

public class DragFlagHolder extends PhotoInfoAdapter3.ViewHolder {

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new DragFlagHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof DragFlagModel;
        }
    }

    public DragFlagHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_drag_flag);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        // do nothing.
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
