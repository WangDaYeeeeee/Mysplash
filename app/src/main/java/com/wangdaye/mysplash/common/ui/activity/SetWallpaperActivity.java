package com.wangdaye.mysplash.common.ui.activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.dialog.WallpaperWhereDialog;
import com.wangdaye.mysplash.common.ui.popup.WallpaperAlignPopupWindow;
import com.wangdaye.mysplash.common.ui.popup.WallpaperClipPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.photoView.Info;
import com.wangdaye.mysplash.common.ui.widget.photoView.PhotoView;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Set wallpaper activity.
 *
 * This activity is used to set a photo as a wallpaper.
 *
 * */

public class SetWallpaperActivity extends MysplashActivity
        implements WallpaperClipPopupWindow.OnClipTypeChangedListener,
        WallpaperAlignPopupWindow.OnAlignTypeChangedListener,
        WallpaperWhereDialog.OnWhereSelectedListener, SafeHandler.HandlerContainer {
    // widget
    private SafeHandler<SetWallpaperActivity> handler;

    @BindView(R.id.activity_set_wallpaper_container) CoordinatorLayout container;
    @BindView(R.id.activity_set_wallpaper_closeBtn) ImageButton closeBtn;
    @BindView(R.id.activity_set_wallpaper_typeBtn) ImageView typeBtn;
    @BindView(R.id.activity_set_wallpaper_alignBtn) ImageView alignBtn;
    @BindView(R.id.activity_set_wallpaper_setBtn) Button setBtn;
    @BindView(R.id.activity_set_wallpaper_photoView) PhotoView photoView;

    // data
    private File photoFile;

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

    /** <br> life cycle. */

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
                requestPermission(Mysplash.READ_EXTERNAL_STORAGE, 0);
            } else {
                initData();
                initWidget();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // do nothing.
    }

    @Override
    public void finishActivity(int dir) {
        finish();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public void handleBackPressed() {
        finishActivity(0);
    }

    @Override
    protected void setTheme() {
        setTheme(R.style.MysplashTheme_dark_SetWallpaper);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
    }

    /** <br> UI. */

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        setTypeIcon(clipType);
        setAlignIcon(alignType);

        photoView.enable();
        photoView.setMaxScale(2.5f);
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageHelper.loadImage(this, photoView, photoFile);
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
                photoFile);
    }

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
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextContent_light));
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            closeBtn.setImageResource(R.drawable.ic_toolbar_close_dark);
            setBtn.setTextColor(ContextCompat.getColor(this, R.color.colorTextContent_dark));
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

    /** <br> data. */

    private void initData() {
        this.photoFile = new File(getIntent().getData().getSchemeSpecificPart());
        if (!photoFile.exists()) {
            photoFile = new File(FileUtils.uriToFilePath(this, getIntent().getData()));
        }
        light = false;
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
    private void setWallpaper(boolean wallpaper) {
        FileInputStream[] streams = new FileInputStream[2];
        try {
            streams[0] = new FileInputStream(photoFile);
            streams[1] = new FileInputStream(photoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (streams[0] == null || streams[1] == null) {
            return;
        }

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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(streams[0], new Rect(0, 0, 0, 0), options);

        Rect wallpaperCorp = new Rect(
                (int) (options.outWidth * leftPercent),
                (int) (options.outHeight * topPercent),
                (int) (options.outWidth * rightPercent),
                (int) (options.outHeight * bottomPercent));

        WallpaperManager manager = WallpaperManager.getInstance(this);
        Rect outPadding = new Rect(
                Math.max(0, wallpaperCorp.left),
                Math.max(0, wallpaperCorp.top),
                Math.max(0, options.outWidth - wallpaperCorp.right),
                Math.max(0, options.outHeight - wallpaperCorp.bottom));
        options.inJustDecodeBounds = false;
        options.inSampleSize = wallpaperCorp.width() / manager.getDesiredMinimumWidth();
        Bitmap bitmap = BitmapFactory.decodeStream(streams[1], outPadding, options);

        try {
            streams[0].close();
            streams[1].close();
            if (wallpaper) {
                manager.setBitmap(bitmap);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.setBitmap(
                        bitmap,
                        new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        true,
                        WallpaperManager.FLAG_LOCK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
        switch (permissionCode) {
            case Mysplash.READ_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            type);
                } else {
                    initData();
                    initWidget();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        initData();
                        initWidget();
                    } else {
                        NotificationHelper.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on click listener.

    @OnClick(R.id.activity_set_wallpaper_closeBtn) void close() {
        finishActivity(0);
    }

    @OnClick(R.id.activity_set_wallpaper_typeBtn) void showTypePopup() {
        WallpaperClipPopupWindow popup = new WallpaperClipPopupWindow(this, typeBtn, clipType);
        popup.setOnClipTypeChangedListener(this);
    }

    @OnClick(R.id.activity_set_wallpaper_closeBtn) void showAlignPopup() {
        WallpaperAlignPopupWindow popup = new WallpaperAlignPopupWindow(this, alignBtn, alignType);
        popup.setAlignTypeChangedListener(this);
    }

    @OnClick(R.id.activity_set_wallpaper_closeBtn) void set() {
        WallpaperWhereDialog dialog = new WallpaperWhereDialog();
        dialog.setOnWhereSelectedListener(this);
        dialog.show(getFragmentManager(), null);
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
                ThreadManager.getInstance().execute(setWallpaper);
                break;

            case WHERE_LOCKSCREEN:
                ThreadManager.getInstance().execute(setLockScreen);
                break;

            case WHERE_WALL_LOCK:
                ThreadManager.getInstance().execute(setWallAndLock);
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
                finishActivity(0);
                break;
        }
    }

    /** <br> inner class. */

    /**
     * A Runnable class to set picture as wallpaper.
     * */
    public Runnable setWallpaper = new Runnable() {
        @Override
        public void run() {
            setWallpaper(true);
            handler.obtainMessage(WHERE_WALLPAPER).sendToTarget();
        }
    };

    /**
     * A Runnable class to set picture as background in lock screen.
     * */
    public Runnable setLockScreen = new Runnable() {
        @Override
        public void run() {
            setWallpaper(false);
            handler.obtainMessage(WHERE_LOCKSCREEN).sendToTarget();
        }
    };

    /**
     * A Runnable class to set picture as wallpaper and background in lock screen.
     * */
    public Runnable setWallAndLock = new Runnable() {
        @Override
        public void run() {
            setWallpaper(true);
            setWallpaper(false);
            handler.obtainMessage(WHERE_WALL_LOCK).sendToTarget();
        }
    };
}
