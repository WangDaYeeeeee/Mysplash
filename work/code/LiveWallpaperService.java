package com.wangdaye.mysplash.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FlagRunnable;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.LiveWallpaperHelper;
import com.wangdaye.mysplash.common.utils.manager.LiveWallpaperOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class LiveWallpaperService extends WallpaperService
        implements LiveWallpaperHelper.OnRequestPhotoListener {

    @Nullable
    private MysplashWallpaperEngine engine;
    @Nullable
    private DrawBitmapRunnable runnable;
    @Nullable
    private Bitmap bitmap;
    @Nullable
    private Bitmap lastBitmap;

    private TimeTickReceiver receiver;
    private LiveWallpaperOptionManager manager;
    private long lastUpdateTime;
    private String lastPhotoId;
    private long lastReadSettingsTime;

    private static final long HOUR = 60 * 60 * 1000;
    private static final long MINUTE = 60 * 1000;

    private static final long SWITCH_BITMAP_DURATION = 300;
    private static final long REFRESH_INTERVAL = 16;

    private static final int REFRESH_REASON_SWITCH_BITMAP = 1;
    private static final int REFRESH_REASON_OFFSET = 2;

    private static final String BITMAP_CACHE_NAME = "wallpaper_cache.png";

    private class MysplashWallpaperEngine extends Engine {

        @Nullable
        private Canvas canvas;

        private Paint textPaint;
        private Paint.FontMetrics fontMetrics;

        private Paint bitmapPaint;

        private Rect srcRect;
        private Rect lastSrcRect;
        private Rect dstRect;

        @FloatRange(from = -1, to = 1)
        private float horizontalOffset;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.content_text_size));
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);

            fontMetrics = textPaint.getFontMetrics();

            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);

            srcRect = new Rect();
            lastSrcRect = new Rect();
            dstRect = new Rect();

            horizontalOffset = 0;
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            updateWallpaper(LiveWallpaperService.this);
            bindEngine();
            startDrawRunnable();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            stopDrawRunnable();
            unbindEngine();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                bindEngine();
                startDrawRunnable();
            } else {
                stopDrawRunnable();
                unbindEngine();
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            horizontalOffset = xOffset;
            notifyBitmapChanged(REFRESH_REASON_OFFSET);
        }

        private void bindEngine() {
            if (engine == null || !engine.equals(this)) {
                engine = this;
            }
        }

        private void unbindEngine() {
            if (engine != null && engine.equals(this)) {
                engine = null;
            }
        }

        private void startDrawRunnable() {
            if (runnable == null || !runnable.isRunning()) {
                runnable = new DrawBitmapRunnable();
                ThreadManager.getInstance().execute(runnable);
            }
        }

        private void stopDrawRunnable() {
            if (runnable != null) {
                runnable.setRunning(false);
                runnable = null;
            }
        }

        @WorkerThread
        void drawBitmap(@FloatRange(from = 0, to = 1) float newBitmapAlpha) {
            canvas = getSurfaceHolder().lockCanvas();
            if (canvas == null) {
                return;
            }
            if (bitmap == null) {
                canvas.drawColor(Color.BLACK);
                canvas.drawText(
                        getString(R.string.feedback_downloading),
                        canvas.getWidth() / 2,
                        canvas.getHeight() / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2,
                        textPaint);
            } else {
                ensureDstRect(dstRect, canvas);
                // last bitmap.
                if (newBitmapAlpha < 1) {
                    if (lastBitmap == null) {
                        canvas.drawColor(Color.BLACK);
                    } else {
                        ensureSrcRect(lastBitmap, lastSrcRect, canvas);
                        canvas.drawBitmap(lastBitmap, lastSrcRect, dstRect, null);
                    }
                }
                // new bitmap.
                if (newBitmapAlpha > 0) {
                    bitmapPaint.setAlpha((int) (255 * newBitmapAlpha));
                    ensureSrcRect(bitmap, srcRect, canvas);
                    canvas.drawBitmap(bitmap, srcRect, dstRect, bitmapPaint);
                }
            }
            getSurfaceHolder().unlockCanvasAndPost(canvas);
        }

        private void ensureSrcRect(Bitmap bitmap, Rect src, Canvas canvas) {
            if (1.0 * bitmap.getWidth() / bitmap.getHeight()
                    > 1.0 * canvas.getWidth() / canvas.getHeight()) {
                // landscape.
                src.set(0,
                        0,
                        (int) (bitmap.getHeight() * 1.0 * canvas.getWidth() / canvas.getHeight()),
                        bitmap.getHeight());
                src.right = Math.min(src.right, bitmap.getWidth());
                src.offset((bitmap.getWidth() - src.width()) / 2, 0);
                src.offset((int) ((bitmap.getWidth() - src.width()) / 2 * horizontalOffset), 0);
            } else {
                // portrait.
                src.set(0,
                        0,
                        bitmap.getWidth(),
                        (int) (bitmap.getWidth() * 1.0 * canvas.getHeight() / canvas.getWidth()));
                src.bottom = Math.min(src.bottom, bitmap.getHeight());
                src.offset(0, (bitmap.getHeight() - src.height()) / 2);
            }
        }
    }

    private void ensureDstRect(Rect dst, Canvas canvas) {
        dst.set(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private class DrawBitmapRunnable extends FlagRunnable {

        private long timeNow;
        private long timeEnd;
        private boolean changed;

        DrawBitmapRunnable() {
            super();
            timeNow = System.currentTimeMillis();
            timeEnd = 0;
            changed = true;
        }

        @Override
        public void run() {
            long remaining;
            float newBitmapAlpha;
            while (isRunning()) {
                timeNow = System.currentTimeMillis();
                if (changed && engine != null) {
                    if (timeNow >= timeEnd) {
                        newBitmapAlpha = 1;
                        engine.drawBitmap(newBitmapAlpha);
                    } else {
                        newBitmapAlpha = (float) (1 - 1.0 * (timeEnd - timeNow) / SWITCH_BITMAP_DURATION);
                        newBitmapAlpha = Math.max(0, newBitmapAlpha);
                        newBitmapAlpha = Math.min(1, newBitmapAlpha);
                        engine.drawBitmap(newBitmapAlpha);
                    }
                }
                changed = timeNow < timeEnd;
                remaining = REFRESH_INTERVAL - (System.currentTimeMillis() - timeNow);
                if (remaining > 0) {
                    try {
                        Thread.sleep(remaining);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setChanged(int reason) {
            this.changed = true;
            if (reason == REFRESH_REASON_SWITCH_BITMAP) {
                timeEnd = timeNow + SWITCH_BITMAP_DURATION;
            }
        }
    }

    private class TimeTickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (intent.getAction()) {
                    case Intent.ACTION_TIME_TICK:
                        if (System.currentTimeMillis() - lastReadSettingsTime >= 15 * MINUTE) {
                            manager = LiveWallpaperOptionManager.getInstance(context);
                            lastReadSettingsTime = System.currentTimeMillis();
                        }
                        if (isRefreshTime()) {
                            updateWallpaper(context);
                        }
                        break;

                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIMEZONE_CHANGED:
                        manager = LiveWallpaperOptionManager.getInstance(context);
                        lastReadSettingsTime = System.currentTimeMillis();
                        updateWallpaper(context);
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        manager = LiveWallpaperOptionManager.getInstance(this);
        lastUpdateTime = manager.getLastUpdateTime();
        lastPhotoId = manager.getLastPhotoId();
        lastReadSettingsTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    public Engine onCreateEngine() {
        return new MysplashWallpaperEngine();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        receiver = new TimeTickReceiver();
        registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void updateWallpaper(final Context context) {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    // initial update request.
                    bitmap = FileUtils.readBitmap(getFilesDir(), BITMAP_CACHE_NAME);
                    if (bitmap != null) {
                        notifyBitmapChanged(REFRESH_REASON_SWITCH_BITMAP);
                        if (!isRefreshTime()) {
                            return;
                        }
                    }
                }
                if (bitmap == null
                        || !manager.isUpdateOnlyInWifi()
                        || LiveWallpaperHelper.isWifi(context)) {
                    lastUpdateTime = System.currentTimeMillis();
                    LiveWallpaperHelper.requestPhoto(context, manager, lastPhotoId, LiveWallpaperService.this);
                }
            }
        });
    }

    private void loadBitmap(@NonNull final Photo photo) {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap b = ImageHelper.loadBitmap(LiveWallpaperService.this, photo);
                    lastBitmap = bitmap;
                    bitmap = b;
                    FileUtils.writeBitmap(getFilesDir(), BITMAP_CACHE_NAME, bitmap);
                    notifyBitmapChanged(REFRESH_REASON_SWITCH_BITMAP);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void notifyBitmapChanged(int reason) {
        if (runnable != null && runnable.isRunning()) {
            runnable.setChanged(reason);
        }
    }

    private boolean isRefreshTime() {
        return System.currentTimeMillis() - lastUpdateTime >= HOUR * manager.getUpdateInterval();
    }

    // interface.

    @Override
    public void requestSucceed(@NonNull Photo photo) {
        lastPhotoId = photo.id;
        loadBitmap(photo);
    }

    @Override
    public void requestFailed() {
        lastUpdateTime = System.currentTimeMillis()
                - manager.getUpdateInterval() * HOUR
                + 15 * MINUTE;
    }
}
