package com.wangdaye.photo.ui.adapter.pager;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.photo.ui.photoView.PhotoView;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PagerHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.container_photo_pager) PhotoView regularImage;
    @OnClick(R2.id.container_photo_pager)
    void clickTouchView() {
        activity.switchComponentsVisibility();
    }
    private @Nullable ImageHelper.BitmapTarget fullSizePhotoTarget;

    private PhotoActivity activity;

    public PagerHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.container_photo_pager, parent, false));
        ButterKnife.bind(this, itemView);
    }
    
    protected void onBindViewHolder(PhotoActivity activity, PagerModel model, boolean update,
                                    boolean executeEnterTransition) {
        this.activity = activity;

        if (!update) {
            resetPhotoImage(model.photo);
        }

        // init animation.
        if (executeEnterTransition) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                regularImage.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                regularImage.getViewTreeObserver().removeOnPreDrawListener(this);
                                activity.startPostponedEnterTransition();
                                return false;
                            }
                        }
                );
            }
        }
    }

    protected void onRecycledView() {
        ImageHelper.releaseImageView(regularImage);
        if (fullSizePhotoTarget != null) {
            ImageHelper.releaseImageView(fullSizePhotoTarget);
        }
    }
    
    private void resetPhotoImage(@NonNull Photo photo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            regularImage.setTransitionName(
                    activity.getString(R.string.transition_photo_image) + "_" + photo.id);
        }
        regularImage.setScale(1f, false);
        regularImage.setZoomTransitionDuration(300);
        regularImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        regularImage.post(() -> {
            int[] viewSize = new int[] {
                    regularImage.getMeasuredWidth(), regularImage.getMeasuredHeight()};
            setRegularImageScaleLevels(viewSize, photo.getRegularSize());
        });

        fullSizePhotoTarget = null;
        LoadImagePresenter.loadPhotoImage(activity, regularImage, photo, () ->
                fullSizePhotoTarget = ImageHelper.loadBitmap(
                        activity, Uri.parse(photo.getFullUrl()), fullSizeHandler, photo.width, photo.height
                )
        );
    }

    private ImageHelper.OnLoadImageHandler fullSizeHandler = resource -> {
        ImageHelper.releaseImageView(regularImage);
        regularImage.updateImageDrawable(new BitmapDrawable(activity.getResources(), resource));
    };

    private void setRegularImageScaleLevels(int[] viewSize, int[] photoSize) {
        float screenRatio = 1.f * viewSize[0] / viewSize[1];
        float photoRatio = 1.f * photoSize[0] / photoSize[1];

        if (screenRatio == photoRatio) {
            regularImage.setScaleLevels(1f, 1.5f, 2f);
        } else if (screenRatio < photoRatio) {
            // port screen, land photo.
            float mediumScale = 1.f * viewSize[1] / (viewSize[0] / photoRatio);
            regularImage.setScaleLevels(
                    1f, mediumScale, mediumScale * 2.5f);
        } else {
            // land screen, port photo.
            float mediumScale = 1.f * viewSize[0] / (viewSize[1] * photoRatio);
            regularImage.setScaleLevels(
                    1f, mediumScale, mediumScale * 2.5f);
        }
    }
}
