package com.wangdaye.mysplash.photo3.ui.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.photo3.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.photo3.ui.PhotoActivity3;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Exif holder.
 *
 * This view holder is used to show the exif information of the photo.
 *
 * */

public class ExifHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R.id.item_photo_3_exif) LinearLayout container;
    @BindView(R.id.item_photo_3_exif_icon) AppCompatImageView icon;
    @BindView(R.id.item_photo_3_exif_title) TextView title;
    @BindView(R.id.item_photo_3_exif_content) TextView content;
    @BindView(R.id.item_photo_3_exif_color) FrameLayout color;

    private MysplashActivity a;
    private int position;
    public static final int TYPE_EXIF = 50;

    public ExifHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity3 a, Photo photo) {
        this.a = a;
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
                icon.setImageResource(R.drawable.ic_camera);
                title.setText(context.getString(R.string.feedback_camera_make));
                content.setText(photo.exif.make == null ? "Unknown" : photo.exif.make);
                break;

            case 1:
                icon.setImageResource(R.drawable.ic_film);
                title.setText(context.getString(R.string.feedback_camera_model));
                content.setText(photo.exif.model == null ? "Unknown" : photo.exif.model);
                break;

            case 2:
                icon.setImageResource(R.drawable.ic_size);
                title.setText(context.getString(R.string.feedback_size));
                content.setText(photo.width + " × " + photo.height);
                break;

            case 3:
                icon.setImageResource(R.drawable.ic_focal);
                title.setText(context.getString(R.string.feedback_focal));
                content.setText(photo.exif.focal_length == null ? "Unknown" : (photo.exif.focal_length + "mm"));
                break;

            case 4:
                icon.setImageResource(R.drawable.ic_aperture);
                title.setText(context.getString(R.string.feedback_aperture));
                content.setText(photo.exif.aperture == null ? "Unknown" : ("f/" + photo.exif.aperture));
                break;

            case 5:
                icon.setImageResource(R.drawable.ic_exposure);
                title.setText(context.getString(R.string.feedback_exposure));
                content.setText(photo.exif.exposure_time == null ? "Unknown" : (photo.exif.exposure_time + "s"));
                break;

            case 6:
                icon.setImageResource(R.drawable.ic_iso);
                title.setText(context.getString(R.string.feedback_iso));
                content.setText(photo.exif.iso == 0 ? "Unknown" : String.valueOf(photo.exif.iso));
                break;

            case 7:
                icon.setImageResource(R.drawable.ic_color);
                title.setText(context.getString(R.string.feedback_color));
                content.setText(photo.color);
                break;
        }
    }

    private void showExifDescription(String title, String content) {
        NotificationHelper.showSnackbar(a, title + " : " + content);
    }

    // interface.

    @OnClick(R.id.item_photo_3_exif) void checkExif() {
        switch (position) {
            case 0:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_make),
                        content.getText().toString()
                );
                break;

            case 1:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_camera_model),
                        content.getText().toString()
                );
                break;

            case 2:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_size),
                        content.getText().toString()
                );
                break;

            case 3:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_focal),
                        content.getText().toString()
                );
                break;

            case 4:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_aperture),
                        content.getText().toString()
                );
                break;

            case 5:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_exposure),
                        content.getText().toString()
                );
                break;

            case 6:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_iso),
                        content.getText().toString()
                );
                break;

            case 7:
                showExifDescription(
                        itemView.getContext().getString(R.string.feedback_color),
                        content.getText().toString()
                );
                break;
        }
    }
}
