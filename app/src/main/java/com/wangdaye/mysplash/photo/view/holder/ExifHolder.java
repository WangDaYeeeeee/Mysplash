package com.wangdaye.mysplash.photo.view.holder;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;

/**
 * Exif holder.
 * */

public class ExifHolder extends PhotoInfoAdapter.ViewHolder 
        implements View.OnClickListener {
    // widget
    private TextView leftText;
    private TextView rightText;
    private ImageView leftImage;
    private ImageView rightImage;
    private FrameLayout colorSample;

    // data
    private int position;
    public static final int TYPE_EXIF = 50;
    
    /** <br> life cycle. */
    
    public ExifHolder(View itemView) {
        super(itemView);

        itemView.findViewById(R.id.item_photo_exif_leftContainer).setOnClickListener(this);
        itemView.findViewById(R.id.item_photo_exif_rightContainer).setOnClickListener(this);

        this.leftText = (TextView) itemView.findViewById(R.id.item_photo_exif_leftText);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), leftText);

        this.rightText = (TextView) itemView.findViewById(R.id.item_photo_exif_rightText);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), rightText);

        this.leftImage = (ImageView) itemView.findViewById(R.id.item_photo_exif_leftImage);
        this.rightImage = (ImageView) itemView.findViewById(R.id.item_photo_exif_rightImage);

        this.colorSample = (FrameLayout) itemView.findViewById(R.id.item_photo_exif_colorSample);
    }

    /** <br> UI. */
    
    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        // do nothing.
    }

    public void drawExif(int viewType, Photo photo) {
        position = viewType - TYPE_EXIF;
        switch (position) {
            case 0:
                if (Mysplash.getInstance().isLightTheme()) {
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
                if (Mysplash.getInstance().isLightTheme()) {
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
                if (Mysplash.getInstance().isLightTheme()) {
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
                if (Mysplash.getInstance().isLightTheme()) {
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

    /** <br> interface. */
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_photo_exif_leftContainer:
                switch (position) {
                    case 0:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_size),
                                leftText.getText().toString());
                        break;

                    case 1:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_color),
                                leftText.getText().toString());
                        break;

                    case 2:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_location),
                                leftText.getText().toString());
                        break;

                    case 3:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_model),
                                leftText.getText().toString());
                        break;
                }
                break;

            case R.id.item_photo_exif_rightContainer:
                switch (position) {
                    case 0:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_exposure),
                                rightText.getText().toString());
                        break;

                    case 1:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_aperture),
                                rightText.getText().toString());
                        break;

                    case 2:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_focal),
                                rightText.getText().toString());
                        break;

                    case 3:
                        showExifDescription(
                                view.getContext().getString(R.string.feedback_iso),
                                rightText.getText().toString());
                        break;
                }
                break;
        }
    }
}
