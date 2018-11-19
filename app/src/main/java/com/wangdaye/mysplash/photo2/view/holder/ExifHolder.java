package com.wangdaye.mysplash.photo2.view.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Exif holder.
 *
 * This view holder is used to show the exif information of the photo.
 *
 * */

public class ExifHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_exif)
    LinearLayout container;

    @BindView(R.id.item_photo_2_exif_icon)
    ImageView icon;

    @BindView(R.id.item_photo_2_exif_title)
    TextView title;

    @BindView(R.id.item_photo_2_exif_content)
    TextView content;

    @BindView(R.id.item_photo_2_exif_color)
    FrameLayout color;

    private int position;
    public static final int TYPE_EXIF = 50;

    public ExifHolder(View itemView, int marginHorizontal, int columnCount, int viewType) {
        super(itemView, marginHorizontal, columnCount);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), content);

        position = viewType - TYPE_EXIF;
        if (marginHorizontal > 0 && columnCount == PhotoInfoAdapter2.COLUMN_COUNT_HORIZONTAL) {
            if (position % (columnCount / 2) == 0) {
                // set start margin.
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
                params.setMarginStart(marginHorizontal);
                container.setLayoutParams(params);
            } else {
                // set end margin.
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
                params.setMarginEnd(marginHorizontal);
                container.setLayoutParams(params);
            }
        }
        setIsRecyclable(false);
    }

    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        // do nothing.
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    @SuppressLint("SetTextI18n")
    public void drawExif(Context context, int viewType, Photo photo) {
        position = viewType - TYPE_EXIF;
        if (position == 7) {
            color.setVisibility(View.VISIBLE);
            color.setBackground(new ColorDrawable(Color.parseColor(photo.color)));
        } else {
            color.setVisibility(View.GONE);
        }
        switch (position) {
            case 0:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_camera_light);
                } else {
                    icon.setImageResource(R.drawable.ic_camera_dark);
                }
                title.setText(context.getString(R.string.feedback_camera_make));
                content.setText(photo.exif.make == null ? "Unknown" : photo.exif.make);
                break;

            case 1:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_film_light);
                } else {
                    icon.setImageResource(R.drawable.ic_film_dark);
                }
                title.setText(context.getString(R.string.feedback_camera_model));
                content.setText(photo.exif.model == null ? "Unknown" : photo.exif.model);
                break;

            case 2:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_size_light);
                } else {
                    icon.setImageResource(R.drawable.ic_size_dark);
                }
                title.setText(context.getString(R.string.feedback_size));
                content.setText(photo.width + " × " + photo.height);
                break;

            case 3:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_focal_light);
                } else {
                    icon.setImageResource(R.drawable.ic_focal_dark);
                }
                title.setText(context.getString(R.string.feedback_focal));
                content.setText(photo.exif.focal_length == null ? "Unknown" : (photo.exif.focal_length + "mm"));
                break;

            case 4:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_aperture_light);
                } else {
                    icon.setImageResource(R.drawable.ic_aperture_dark);
                }
                title.setText(context.getString(R.string.feedback_aperture));
                content.setText(photo.exif.aperture == null ? "Unknown" : ("f/" + photo.exif.aperture));
                break;

            case 5:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_exposure_light);
                } else {
                    icon.setImageResource(R.drawable.ic_exposure_dark);
                }
                title.setText(context.getString(R.string.feedback_exposure));
                content.setText(photo.exif.exposure_time == null ? "Unknown" : (photo.exif.exposure_time + "s"));
                break;

            case 6:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_iso_light);
                } else {
                    icon.setImageResource(R.drawable.ic_iso_dark);
                }
                title.setText(context.getString(R.string.feedback_iso));
                content.setText(photo.exif.iso == 0 ? "Unknown" : String.valueOf(photo.exif.iso));
                break;

            case 7:
                if (ThemeManager.getInstance(context).isLightTheme()) {
                    icon.setImageResource(R.drawable.ic_color_light);
                } else {
                    icon.setImageResource(R.drawable.ic_color_dark);
                }
                title.setText(context.getString(R.string.feedback_color));
                content.setText(photo.color);
                break;
        }
    }

    private void showExifDescription(String title, String content) {
        NotificationHelper.showSnackbar(title + " : " + content);
    }

    // interface.

    @OnClick(R.id.item_photo_2_exif) void checkExif() {
        switch (position) {
            case 0:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_make),
                        content.getText().toString());
                break;

            case 1:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_model),
                        content.getText().toString());
                break;

            case 2:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_size),
                        content.getText().toString());
                break;

            case 3:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_focal),
                        content.getText().toString());
                break;

            case 4:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_aperture),
                        content.getText().toString());
                break;

            case 5:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_exposure),
                        content.getText().toString());
                break;

            case 6:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_iso),
                        content.getText().toString());
                break;

            case 7:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_color),
                        content.getText().toString());
                break;
        }
    }
}
