package com.wangdaye.mysplash.photo.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Exif holder.
 *
 * This view holder is used to show the exif information of the photo.
 *
 * */

public class ExifHolder extends PhotoInfoAdapter.ViewHolder {

    @BindView(R.id.item_photo_exif_leftText)
    TextView leftText;

    @BindView(R.id.item_photo_exif_rightText)
    TextView rightText;

    @BindView(R.id.item_photo_exif_leftImage)
    ImageView leftImage;

    @BindView(R.id.item_photo_exif_rightImage)
    ImageView rightImage;

    @BindView(R.id.item_photo_exif_colorSample)
    FrameLayout colorSample;

    private int position;
    public static final int TYPE_EXIF = 50;

    public ExifHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), leftText);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), rightText);
    }

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        // do nothing.
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    public void drawExif(Context context, int viewType, Photo photo) {
        position = viewType - TYPE_EXIF;
        switch (position) {
            case 0:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    leftImage.setImageResource(R.drawable.ic_size_light);
                    rightImage.setImageResource(R.drawable.ic_exposure_light);
                } else {
                    leftImage.setImageResource(R.drawable.ic_size_dark);
                    rightImage.setImageResource(R.drawable.ic_exposure_dark);
                }
                leftText.setText(photo.width + " × " + photo.height);
                rightText.setText(photo.exif.exposure_time == null ? "Unknown" : photo.exif.exposure_time);
                colorSample.setVisibility(View.GONE);
                break;

            case 1:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    leftImage.setImageResource(R.drawable.ic_color_light);
                    rightImage.setImageResource(R.drawable.ic_aperture_light);
                } else {
                    leftImage.setImageResource(R.drawable.ic_color_dark);
                    rightImage.setImageResource(R.drawable.ic_aperture_dark);
                }
                leftText.setText(photo.color);
                rightText.setText(photo.exif.aperture == null ? "Unknown" : photo.exif.aperture);
                colorSample.setVisibility(View.VISIBLE);
                colorSample.setBackground(new ColorDrawable(Color.parseColor(photo.color)));
                break;

            case 2:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    leftImage.setImageResource(R.drawable.ic_location_light);
                    rightImage.setImageResource(R.drawable.ic_focal_light);
                } else {
                    leftImage.setImageResource(R.drawable.ic_location_dark);
                    rightImage.setImageResource(R.drawable.ic_focal_dark);
                }
                String text;
                if (photo.location == null
                        || (photo.location.city == null && photo.location.country == null)) {
                    text = "Unknown";
                } else {
                    text = photo.location.city == null ? "" : photo.location.city + ", ";
                    text = text + (photo.location.country == null ? "" : photo.location.country);
                }
                leftText.setText(text);
                rightText.setText(photo.exif.focal_length == null ? "Unknown" : photo.exif.focal_length);
                colorSample.setVisibility(View.GONE);
                break;

            case 3:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    leftImage.setImageResource(R.drawable.ic_camera_light);
                    rightImage.setImageResource(R.drawable.ic_iso_light);
                } else {
                    leftImage.setImageResource(R.drawable.ic_camera_dark);
                    rightImage.setImageResource(R.drawable.ic_iso_dark);
                }
                leftText.setText(photo.exif.model == null ? "Unknown" : photo.exif.model);
                rightText.setText(photo.exif.iso == 0 ? "Unknown" : String.valueOf(photo.exif.iso));
                colorSample.setVisibility(View.GONE);
                break;
        }
    }

    private void showExifDescription(String title, String content) {
        NotificationHelper.showSnackbar(
                title + " : " + content,
                Snackbar.LENGTH_SHORT);
    }

    // interface.

    @OnClick(R.id.item_photo_exif_leftContainer) void clickLeft() {
        switch (position) {
            case 0:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_size),
                        leftText.getText().toString());
                break;

            case 1:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_color),
                        leftText.getText().toString());
                break;

            case 2:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_location),
                        leftText.getText().toString());
                break;

            case 3:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_model),
                        leftText.getText().toString());
                break;
        }
    }

    @OnClick(R.id.item_photo_exif_rightContainer) void clickRight() {
        switch (position) {
            case 0:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_exposure),
                        rightText.getText().toString());
                break;

            case 1:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_aperture),
                        rightText.getText().toString());
                break;

            case 2:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_focal),
                        rightText.getText().toString());
                break;

            case 3:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_iso),
                        rightText.getText().toString());
                break;
        }
    }
}
