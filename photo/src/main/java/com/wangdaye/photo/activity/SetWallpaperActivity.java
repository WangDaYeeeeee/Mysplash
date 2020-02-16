package com.wangdaye.photo.activity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.dialog.WallpaperWhereDialog;
import com.wangdaye.photo.ui.photoView.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Set wallpaper activity.
 *
 * This activity is used to set a photo as a wallpaper.
 *
 * */

@Route(path = SetWallpaperActivity.SET_WALLPAPER_ACTIVITY)
public class SetWallpaperActivity extends ReadWriteActivity
        implements WallpaperWhereDialog.OnWhereSelectedListener {

    @BindView(R2.id.activity_set_wallpaper_container) CoordinatorLayout container;

    @BindView(R2.id.activity_set_wallpaper_closeBtn) AppCompatImageButton closeBtn;
    @OnClick(R2.id.activity_set_wallpaper_closeBtn) void close() {
        finishSelf(true);
    }

    @BindView(R2.id.activity_set_wallpaper_typeBtn) AppCompatImageView typeBtn;
    @OnClick(R2.id.activity_set_wallpaper_typeBtn) void showTypePopup() {
        MysplashPopupWindow.show(this, typeBtn, R.menu.activity_set_wallpaper_clip, item -> {
            if (item.getItemId() == R.id.action_clip_square) {
                clipType = CLIP_TYPE_SQUARE;
            } else {
                clipType = CLIP_TYPE_RECT;
            }
            setTypeIcon(clipType);
            return true;
        });
    }

    @BindView(R2.id.activity_set_wallpaper_alignBtn) AppCompatImageView alignBtn;
    @OnClick(R2.id.activity_set_wallpaper_alignBtn) void showAlignPopup() {
        MysplashPopupWindow.show(this, alignBtn, R.menu.activity_set_wallpaper_alignment, item -> {
            if (item.getItemId() == R.id.action_align_left) {
                alignType = ALIGN_TYPE_LEFT;
            } else if (item.getItemId() == R.id.action_align_center) {
                clipType = ALIGN_TYPE_CENTER;
            } else {
                clipType = ALIGN_TYPE_RIGHT;
            }
            setAlignIcon(clipType);
            return true;
        });
    }

    @BindView(R2.id.activity_set_wallpaper_setBtn) Button setBtn;
    @OnClick(R2.id.activity_set_wallpaper_setBtn) void set() {
        WallpaperWhereDialog dialog = new WallpaperWhereDialog();
        dialog.setOnWhereSelectedListener(this);
        dialog.show(getSupportFragmentManager(), null);
    }

    @BindView(R2.id.activity_set_wallpaper_photoView) PhotoView photoView;

    private boolean light;

    @ClipRule
    private int clipType = CLIP_TYPE_SQUARE;

    @AlignRule
    private int alignType = ALIGN_TYPE_CENTER;

    public static final int CLIP_TYPE_SQUARE = 1;
    public static final int CLIP_TYPE_RECT = 2;

    @IntDef({CLIP_TYPE_SQUARE, CLIP_TYPE_RECT})
    private @interface ClipRule {}

    public static final int ALIGN_TYPE_LEFT = 1;
    public static final int ALIGN_TYPE_CENTER = 2;
    public static final int ALIGN_TYPE_RIGHT = 3;
    @IntDef({ALIGN_TYPE_LEFT, ALIGN_TYPE_CENTER, ALIGN_TYPE_RIGHT})
    private @interface AlignRule {}

    public static final int WHERE_WALLPAPER = 1;
    public static final int WHERE_LOCKSCREEN = 2;
    public static final int WHERE_WALL_LOCK = 3;
    @IntDef({WHERE_WALLPAPER, WHERE_LOCKSCREEN, WHERE_WALL_LOCK})
    public  @interface WallpaperWhereRule {}

    public static final String SET_WALLPAPER_ACTIVITY = "/photo/SetWallpaperActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_wallpaper);
        ButterKnife.bind(this);

        requestReadWritePermission(null, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                initData();
                initWidget();
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                finishSelf(true);
            }
        });
    }

    @Override
    protected void initSystemBar() {
        DisplayUtils.setSystemBarStyle(this, true,
                true, false, true, false);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        // do nothing.
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return null;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        light = false;
    }

    private void initWidget() {
        setTypeIcon(clipType);
        setAlignIcon(alignType);

        photoView.setMaximumScale(2.5f);
        photoView.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
        ImageHelper.loadImage(this, photoView, getIntent().getData());
        ImageHelper.loadBitmap(
                this,
                getIntent().getData(),
                resource -> {
                    int color = computeBackgroundColor(resource);
                    container.setBackgroundColor(color);
                    light = isLightColor(color);
                    setStyle();
                },
                100,
                100
        );
    }

    // control.

    /**
     * Change text and icon color when loading picture.
     * */
    private void setStyle() {
        if (light) {
            closeBtn.setImageResource(R.drawable.ic_toolbar_close_light);
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark2nd));
        } else {
            closeBtn.setImageResource(R.drawable.ic_toolbar_close_dark);
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight2nd));
        }

        setTypeIcon(clipType);
        setAlignIcon(alignType);
    }

    private void setTypeIcon(int type) {
        switch (type) {
            case CLIP_TYPE_SQUARE:
                if (light) {
                    typeBtn.setImageResource(R.drawable.ic_orientation_squarish_light);
                } else {
                    typeBtn.setImageResource(R.drawable.ic_orientation_squarish_dark);
                }
                break;

            case CLIP_TYPE_RECT:
                if (light) {
                    typeBtn.setImageResource(R.drawable.ic_orientation_portrait_light);
                } else {
                    typeBtn.setImageResource(R.drawable.ic_orientation_portrait_dark);
                }
                break;
        }
    }

    private void setAlignIcon(int align) {
        switch (align) {
            case ALIGN_TYPE_LEFT:
                if (light) {
                    alignBtn.setImageResource(R.drawable.ic_align_left_light);
                } else {
                    alignBtn.setImageResource(R.drawable.ic_align_left_dark);
                }
                break;

            case ALIGN_TYPE_CENTER:
                if (light) {
                    alignBtn.setImageResource(R.drawable.ic_align_center_light);
                } else {
                    alignBtn.setImageResource(R.drawable.ic_align_center_dark);
                }
                break;

            case ALIGN_TYPE_RIGHT:
                if (light) {
                    alignBtn.setImageResource(R.drawable.ic_align_right_light);
                } else {
                    alignBtn.setImageResource(R.drawable.ic_align_right_dark);
                }
                break;
        }
    }

    private int computeBackgroundColor(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale((float) (1.0 / bitmap.getWidth()), (float) (1.0 / 2.0));
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 2, matrix, false);
        return bitmap.getPixel(0, 0);
    }

    private boolean isLightColor(int color) {
        int alpha = 0xFF << 24;
        int grey = color;
        int red = ((grey & 0x00FF0000) >> 16);
        int green = ((grey & 0x0000FF00) >> 8);
        int blue = (grey & 0x000000FF);

        grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        grey = alpha | (grey << 16) | (grey << 8) | grey;
        return grey > ContextCompat.getColor(this, R.color.colorTextGrey);
    }

    /**
     * Set picture as a wallpaper.
     *
     * @param wallpaper if set true, it means this picture needs to be set as a wallpaper,
     *                  otherwise, this picture needs to be set as a background in lock screen.
     * */
    private void setWallpaper(Bitmap source, boolean wallpaper) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // eliminate the error from PhotoView's bound.
        RectF imageBound = photoView.getDisplayRect();
        if (imageBound.left > 0) {
            float delta = -imageBound.left;
            imageBound.left += delta;
            imageBound.right += delta;
        } else if (imageBound.right < screenWidth) {
            float delta = screenWidth - imageBound.right;
            imageBound.left += delta;
            imageBound.right += delta;
        }
        if (imageBound.top > 0) {
            float delta = -imageBound.top;
            imageBound.top += delta;
            imageBound.bottom += delta;
        } else if (imageBound.bottom < screenHeight) {
            float delta = screenHeight - imageBound.bottom;
            imageBound.top += delta;
            imageBound.bottom += delta;
        }

        // screen width + delta width = wallpaper width.
        // for example, when the wallpaper is align left. ■□□
        // the black part is screen area, we need ensure the delta area in other area.
        int leftDeltaWidth = 0;
        int rightDeltaWidth = 0;
        if (wallpaper) {
            if (clipType == CLIP_TYPE_SQUARE) {
                switch (alignType) {
                    case ALIGN_TYPE_LEFT: {
                        // ■□□
                        int deltaWidth = (int) Math.abs(imageBound.right - screenWidth);
                        if (screenWidth + deltaWidth > screenHeight) {
                            // wallpaper's width cannot > wallpaper's height.
                            deltaWidth = screenHeight - screenWidth;
                        }
                        leftDeltaWidth = 0;
                        rightDeltaWidth = deltaWidth;
                        break;
                    }
                    case ALIGN_TYPE_CENTER: {
                        // □■□
                        int deltaWidth = (int) Math.min(
                                Math.abs(imageBound.left),
                                Math.abs(imageBound.right - screenWidth));
                        if (screenWidth + 2 * deltaWidth > screenHeight) {
                            // wallpaper's width cannot > wallpaper's height.
                            deltaWidth = (int) ((screenHeight - screenWidth) / 2.0);
                        }
                        leftDeltaWidth = rightDeltaWidth = deltaWidth;
                        break;
                    }
                    case ALIGN_TYPE_RIGHT: {
                        // □□■
                        int deltaWidth = (int) Math.abs(imageBound.left);
                        if (screenWidth + deltaWidth > screenHeight) {
                            // wallpaper's width cannot > wallpaper's height.
                            deltaWidth = screenHeight - screenWidth;
                        }
                        leftDeltaWidth = deltaWidth;
                        rightDeltaWidth = 0;
                        break;
                    }
                }
            }
        }

        // compute the percentage of left, right, top, bottom coordinates.
        float leftPercent = (-leftDeltaWidth - imageBound.left) / imageBound.width();
        float rightPercent = (screenWidth + rightDeltaWidth - imageBound.left) / imageBound.width();
        float topPercent = (-imageBound.top) / imageBound.height();
        float bottomPercent = (imageBound.bottom - imageBound.top) / imageBound.height();

        Rect wallpaperCorp = new Rect(
                (int) (source.getWidth() * leftPercent),
                (int) (source.getHeight() * topPercent),
                (int) (source.getWidth() * rightPercent),
                (int) (source.getHeight() * bottomPercent));

        Rect outPadding = new Rect(
                Math.max(0, wallpaperCorp.left),
                Math.max(0, wallpaperCorp.top),
                Math.max(0, source.getWidth() - wallpaperCorp.right),
                Math.max(0, source.getHeight() - wallpaperCorp.bottom));

        Bitmap bitmap = Bitmap.createBitmap(
                source,
                outPadding.left,
                outPadding.top,
                source.getWidth() - outPadding.right - outPadding.left,
                source.getHeight() - outPadding.bottom - outPadding.top);
        try {
            if (wallpaper) {
                WallpaperManager.getInstance(this).setBitmap(bitmap);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                WallpaperManager.getInstance(this).setBitmap(
                        bitmap,
                        new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        true,
                        WallpaperManager.FLAG_LOCK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // interface.

    // on where selected listener.

    @Override
    public void onWhereSelected(int where) {
        Observable.create(emitter -> {
            Bitmap b = ImageHelper.loadBitmap(this, getIntent().getData(), null);
            if (b == null) {
                emitter.onError(new NullPointerException());
                return;
            }
            switch (where) {
                case WHERE_WALLPAPER:
                    setWallpaper(b, true);
                    break;

                case WHERE_LOCKSCREEN:
                    setWallpaper(b, false);
                    break;

                case WHERE_WALL_LOCK:
                    setWallpaper(b, true);
                    setWallpaper(b, false);
                    break;
            }
            emitter.onComplete();
        }).compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    RoutingHelper.backToHome(this);
                    finishSelf(true);
                }).doOnError(throwable -> finish())
                .subscribe();
    }
}
