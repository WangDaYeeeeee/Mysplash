package com.wangdaye.photo.ui.adapter.photo.holder;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.model.LocationModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Location holder.
 * */

public class LocationHolder extends PhotoInfoAdapter3.ViewHolder {

    @OnClick(R2.id.item_photo_3_location_container) void click() {
        if (!TextUtils.isEmpty(title.getText().toString())) {
            ComponentFactory.getSearchModule().startSearchActivity(
                    MysplashApplication.getInstance().getTopActivity(),
                    title.getText().toString());
        }
    }
    @BindView(R2.id.item_photo_3_location_title) TextView title;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new LocationHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof LocationModel;
        }
    }

    public LocationHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_location);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        LocationModel model = (LocationModel) viewModel;
        if (!TextUtils.isEmpty(model.title)) {
            title.setText(model.title);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
