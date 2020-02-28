package com.wangdaye.photo.ui.adapter.pager;

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
import com.wangdaye.photo.ui.photoView.OnScaleChangedListener;
import com.wangdaye.photo.ui.photoView.NestedScrollingPhotoView;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PagerHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.container_photo_pager) NestedScrollingPhotoView regularImage;
    @OnClick(R2.id.container_photo_pager)
    void clickTouchView() {
        activity.switchComponentsVisibility();
    }

    private PhotoActivity activity;
    private Photo photo;
    private @Nullable ImageHelper.DrawableTarget fullSizePhotoTarget;
    private boolean attached;
    private boolean hasRegularImage;
    private boolean hasFullImage;

    public PagerHolder(@NonNull ViewGroup parent, OnScaleChangedListener l) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.container_photo_pager, parent, false));
        ButterKnife.bind(this, itemView);

        regularImage.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if (attached) {
                l.onScaleChange(scaleFactor, focusX, focusY);
            }
        });
    }
    
    protected void onBindView(PhotoActivity activity, PagerModel model, boolean update) {
        this.activity = activity;
        this.photo = model.photo;
        this.fullSizePhotoTarget = null;
        this.attached = false;
        this.hasRegularImage = false;
        this.hasFullImage = false;

        if (!update) {
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

            LoadImagePresenter.loadPhotoImage(activity, regularImage, photo, () -> {
                hasRegularImage = true;
                loadFullSizePhoto();
            });
        }
    }

    protected void onAttachView(boolean executeEnterTransition) {
        attached = true;
        loadFullSizePhoto();

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

    protected void onDetachView() {
        attached = false;
        if (!hasFullImage && fullSizePhotoTarget != null) {
            ImageHelper.releaseImageView(fullSizePhotoTarget);
            fullSizePhotoTarget = null;
        }
    }

    protected void onRecycledView() {
        ImageHelper.releaseImageView(regularImage);
    }

    private void loadFullSizePhoto() {
        if (attached && hasRegularImage && !hasFullImage && fullSizePhotoTarget == null) {
            fullSizePhotoTarget = ImageHelper.loadDrawable(
                    activity, Uri.parse(photo.getFullUrl(activity)), fullSizeHandler, photo.width, photo.height
            );
        }
    }

    private ImageHelper.OnLoadDrawableHandler fullSizeHandler = drawable -> {
        hasFullImage = true;
        regularImage.updateImageDrawable(drawable);
    };

    private void setRegularImageScaleLevels(int[] viewSize, int[] photoSize) {
        float screenRatio = 1.f * viewSize[0] / viewSize[1];
        float photoRatio = 1.f * photoSize[0] / photoSize[1];

        if (screenRatio == photoRatio) {
            regularImage.setScaleLevels(1f, 1.5f, Photo.MAX_SCALE);
        } else if (screenRatio < photoRatio) {
            // port screen, land photo.
            float mediumScale = 1.f * viewSize[1] / (viewSize[0] / photoRatio);
            regularImage.setScaleLevels(
                    1f, mediumScale, mediumScale * Photo.MAX_SCALE);
        } else {
            // land screen, port photo.
            float mediumScale = 1.f * viewSize[0] / (viewSize[1] * photoRatio);
            regularImage.setScaleLevels(
                    1f, mediumScale, mediumScale * Photo.MAX_SCALE);
        }
    }
}
