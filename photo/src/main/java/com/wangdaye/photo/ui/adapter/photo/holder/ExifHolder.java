package com.wangdaye.photo.ui.adapter.photo.holder;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.model.ExifModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Exif holder.
 *
 * This view holder is used to show the exif information of the photo.
 *
 * */

public class ExifHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_exif) LinearLayout container;
    @BindView(R2.id.item_photo_3_exif_icon) AppCompatImageView icon;
    @BindView(R2.id.item_photo_3_exif_title) TextView title;
    @BindView(R2.id.item_photo_3_exif_content) TextView content;
    @BindView(R2.id.item_photo_3_exif_color) FrameLayout color;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new ExifHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof ExifModel;
        }
    }

    public ExifHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_exif);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        ExifModel model = (ExifModel) viewModel;

        container.setOnClickListener(v -> NotificationHelper.showSnackbar(
                a, model.title + " : " + model.content));

        icon.setImageResource(model.iconId);
        title.setText(model.title);
        content.setText(model.content);
        if (model.color != Color.TRANSPARENT) {
            color.setVisibility(View.VISIBLE);
            color.setBackground(new ColorDrawable(model.color));
        } else {
            color.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
