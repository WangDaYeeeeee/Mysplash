package com.wangdaye.mysplash.common.ui.activity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.ReadWriteActivity;
import com.wangdaye.mysplash.common.ui.dialog.WallpaperWhereDialog;
import com.wangdaye.mysplash.common.ui.popup.WallpaperAlignPopupWindow;
import com.wangdaye.mysplash.common.ui.popup.WallpaperClipPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.photoView.Info;
import com.wangdaye.mysplash.common.ui.widget.photoView.PhotoView;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Set wallpaper activity.
 *
 * This activity is used to set a photo as a wallpaper.
 *
 * */

public class SetWallpaperActivity extends ReadWriteActivity
        implements WallpaperClipPopupWindow.OnClipTypeChangedListener,
        WallpaperAlignPopupWindow.OnAlignTypeChangedListener,
        WallpaperWhereDialog.OnWhereSelectedListener, SafeHandler.HandlerContainer {

    @BindView(R.id.activity_set_wallpaper_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_set_wallpaper_closeBtn)
    AppCompatImageButton closeBtn;

    @BindView(R.id.activity_set_wallpaper_typeBtn)
    AppCompatImageView typeBtn;

    @BindView(R.id.activity_set_wallpaper_alignBtn)
    AppCompatImageView alignBtn;

    @BindView(R.id.activity_set_wallpaper_setBtn)
    Button setBtn;

    @BindView(R.id.activity_set_wallpaper_photoView)
    PhotoView photoView;

    private SafeHandler<SetWallpaperActivity> handler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestReadWritePermission();
            } else {
                initData();
                initWidget();
            }
        }
    }

    @Override
    protected void setTheme() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // do nothing.
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
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

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        initData();
        initWidget();
    }

    @Override
    protected void requestReadWritePermissionFailed(int requestCode) {
        super.requestReadWritePermissionFailed(requestCode);
        finishSelf(true);
    }

    // init.

    private void initData() {
        light = false;
    }

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        setTypeIcon(clipType);
        setAlignIcon(alignType);

        photoView.enable();
        photoView.setMaxScale(2.5f);
        photoView.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
        ImageHelper.loadBitmap(this, photoView, getIntent().getData());
        ImageHelper.loadBitmap(
                this,
                new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        int color = computeBackgroundColor(resource);
                        container.setBackgroundColor(color);
                        light = isLightColor(color);
                        setStyle();
                    }
                },
                getIntent().getData());
    }

    // control.

    /**
     * Change text and icon color when loading picture.
     * */
    private void setStyle() {
        if (light) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            closeBtn.setImageResource(R.drawable.ic_toolbar_close_light);
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark2nd));
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            closeBtn.setImageResource(R.drawable.ic_toolbar_close_dark);
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight2nd));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
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

    @Nullable
    private InputStream getPhotoStream() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.getScheme() != null && uri.getScheme().equals("file")) {
            File file = new File(uri.getSchemeSpecificPart());
            if (file.exists()) {
                String path = FileUtils.uriToFilePath(this, uri);
                if (path != null) {
                    file = new File(path);
                    try {
                        return new FileInputStream(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (uri != null && uri.getScheme() != null && uri.getScheme().equals("content")) {
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    if (fileDescriptor != null) {
                        return new FileInputStream(fileDescriptor);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void loadSourceBitmap(final OnLoadSourceListener l) {
        InputStream stream = getPhotoStream();
        if (stream == null) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options);

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WallpaperManager manager = WallpaperManager.getInstance(this);
        int width;
        int height;
        if (1.0 * options.outWidth / options.outHeight
                > 1.0 * manager.getDesiredMinimumWidth() / manager.getDesiredMinimumHeight()) {
            width = (int) (1.0 * options.outWidth / options.outHeight * manager.getDesiredMinimumHeight());
            height = manager.getDesiredMinimumHeight();
        } else {
            width = manager.getDesiredMinimumWidth();
            height = (int) (1.0 * options.outHeight / options.outWidth * manager.getDesiredMinimumWidth());
        }
        ImageHelper.loadBitmap(
                this,
                new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (l != null) {
                            l.onLoadSourceBitmap(resource);
                        }
                    }
                },
                getIntent().getData());
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
        Info info = photoView.getInfo();
        RectF imageBound = info.getImageBound();
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

    private interface OnLoadSourceListener {
        void onLoadSourceBitmap(Bitmap source);
    }

    // on click listener.

    @OnClick(R.id.activity_set_wallpaper_closeBtn) void close() {
        finishSelf(true);
    }

    @OnClick(R.id.activity_set_wallpaper_typeBtn) void showTypePopup() {
        WallpaperClipPopupWindow popup = new WallpaperClipPopupWindow(this, typeBtn, clipType);
        popup.setOnClipTypeChangedListener(this);
    }

    @OnClick(R.id.activity_set_wallpaper_alignBtn) void showAlignPopup() {
        WallpaperAlignPopupWindow popup = new WallpaperAlignPopupWindow(this, alignBtn, alignType);
        popup.setAlignTypeChangedListener(this);
    }

    @OnClick(R.id.activity_set_wallpaper_setBtn) void set() {
        WallpaperWhereDialog dialog = new WallpaperWhereDialog();
        dialog.setOnWhereSelectedListener(this);
        dialog.show(getSupportFragmentManager(), null);
    }

    // on clip type changed listener.

    @Override
    public void onClipTypeChanged(int type) {
        clipType = type;
        setTypeIcon(type);
    }

    // on align type changed listener.

    @Override
    public void onAlignTypeChanged(int type) {
        alignType = type;
        setAlignIcon(type);
    }

    // on where selected listener.

    @Override
    public void onWhereSelected(int where) {
        switch (where) {
            case WHERE_WALLPAPER:
                loadSourceBitmap(new OnLoadSourceListener() {
                    @Override
                    public void onLoadSourceBitmap(final Bitmap source) {
                        ThreadManager.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                setWallpaper(source, true);
                                handler.obtainMessage(WHERE_WALLPAPER).sendToTarget();
                            }
                        });
                    }
                });
                break;

            case WHERE_LOCKSCREEN:
                loadSourceBitmap(new OnLoadSourceListener() {
                    @Override
                    public void onLoadSourceBitmap(final Bitmap source) {
                        ThreadManager.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                setWallpaper(source, false);
                                handler.obtainMessage(WHERE_LOCKSCREEN).sendToTarget();
                            }
                        });
                    }
                });
                break;

            case WHERE_WALL_LOCK:
                loadSourceBitmap(new OnLoadSourceListener() {
                    @Override
                    public void onLoadSourceBitmap(final Bitmap source) {
                        ThreadManager.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                setWallpaper(source, true);
                                setWallpaper(source, false);
                                handler.obtainMessage(WHERE_WALL_LOCK).sendToTarget();
                            }
                        });
                    }
                });
                break;
        }
    }

    // handler container.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case WHERE_WALLPAPER:
            case WHERE_LOCKSCREEN:
            case WHERE_WALL_LOCK:
                IntentHelper.backToHome(this);
                finishSelf(true);
                break;
        }
    }
}
