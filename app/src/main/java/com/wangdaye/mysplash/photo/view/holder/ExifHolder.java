package com.wangdaye.mysplash.photo.view.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

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

    @BindView(R.id.item_photo_exif_icon)
    ImageView icon;

    @BindView(R.id.item_photo_exif_text)
    TextView text;

    @BindView(R.id.item_photo_exif_colorSample)
    FrameLayout colorSample;

    private int position;
    public static final int TYPE_EXIF = 50;

    public ExifHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), text);
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        // do nothing.
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    @SuppressLint("SetTextI18n")
    public void drawExif(Context context, int viewType, Photo photo) {
        position = viewType - TYPE_EXIF;
        if (position == 11) {
            colorSample.setVisibility(View.VISIBLE);
            colorSample.setBackground(new ColorDrawable(Color.parseColor(photo.color)));
        } else {
            colorSample.setVisibility(View.GONE);
        }
        switch (position) {
            case 0:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_eye_light);
                } else {
                    icon.setImageResource(R.drawable.ic_eye_dark);
                }
                text.setText(String.valueOf(photo.views));
                break;

            case 1:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_download_light);
                } else {
                    icon.setImageResource(R.drawable.ic_download_dark);
                }
                text.setText(String.valueOf(photo.downloads));
                break;

            case 2:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_heart_light);
                } else {
                    icon.setImageResource(R.drawable.ic_heart_dark);
                }
                text.setText(String.valueOf(photo.likes));
                break;

            case 3:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_location_light);
                } else {
                    icon.setImageResource(R.drawable.ic_location_dark);
                }
                String locationText;
                if (photo.location == null
                        || (photo.location.city == null && photo.location.country == null)) {
                    locationText = "Unknown";
                } else {
                    locationText = photo.location.city == null ? "" : photo.location.city + ", ";
                    locationText = locationText + (photo.location.country == null ? "" : photo.location.country);
                }
                text.setText(locationText);
                break;

            case 4:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_camera_light);
                } else {
                    icon.setImageResource(R.drawable.ic_camera_dark);
                }
                text.setText(photo.exif.make == null ? "Unknown" : photo.exif.make);
                break;

            case 5:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_film_light);
                } else {
                    icon.setImageResource(R.drawable.ic_film_dark);
                }
                text.setText(photo.exif.model == null ? "Unknown" : photo.exif.model);
                break;

            case 6:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_size_light);
                } else {
                    icon.setImageResource(R.drawable.ic_size_dark);
                }
                text.setText(photo.width + " × " + photo.height);
                break;

            case 7:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_focal_light);
                } else {
                    icon.setImageResource(R.drawable.ic_focal_dark);
                }
                text.setText(photo.exif.focal_length == null ? "Unknown" : photo.exif.focal_length);
                break;

            case 8:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_aperture_light);
                } else {
                    icon.setImageResource(R.drawable.ic_aperture_dark);
                }
                text.setText(photo.exif.aperture == null ? "Unknown" : photo.exif.aperture);
                break;

            case 9:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_exposure_light);
                } else {
                    icon.setImageResource(R.drawable.ic_exposure_dark);
                }
                text.setText(photo.exif.exposure_time == null ? "Unknown" : photo.exif.exposure_time);
                break;

            case 10:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_iso_light);
                } else {
                    icon.setImageResource(R.drawable.ic_iso_dark);
                }
                text.setText(photo.exif.iso == 0 ? "Unknown" : String.valueOf(photo.exif.iso));
                break;

            case 11:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_color_light);
                } else {
                    icon.setImageResource(R.drawable.ic_color_dark);
                }
                text.setText(photo.color);
                break;
        }
    }

    private void showExifDescription(String title, String content) {
        NotificationHelper.showSnackbar(title + " : " + content);
    }

    // interface.

    @OnClick(R.id.item_photo_exif) void checkExif() {
        switch (position) {
            case 0:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_views),
                        text.getText().toString());
                break;

            case 1:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_downloads),
                        text.getText().toString());
                break;

            case 2:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_likes),
                        text.getText().toString());
                break;

            case 3:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_location),
                        text.getText().toString());
                break;

            case 4:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_make),
                        text.getText().toString());
                break;

            case 5:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_model),
                        text.getText().toString());
                break;

            case 6:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_size),
                        text.getText().toString());
                break;

            case 7:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_focal),
                        text.getText().toString());
                break;

            case 8:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_aperture),
                        text.getText().toString());
                break;

            case 9:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_exposure),
                        text.getText().toString());
                break;

            case 10:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_iso),
                        text.getText().toString());
                break;

            case 11:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_color),
                        text.getText().toString());
                break;
        }
    }
}
