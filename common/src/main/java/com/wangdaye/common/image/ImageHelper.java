package com.wangdaye.common.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.Size;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.image.transformation.NullTransformation;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.component.ComponentFactory;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Image helper.
 *
 * A helper class that makes operations of {@link Glide} easier.
 *
 * */

public class ImageHelper {

    public static final int AVATAR_SIZE = 128;

    private static class BaseRequestListener<T, R>
            implements RequestListener<T, R> {

        @Nullable private OnLoadImageListener listener;

        BaseRequestListener(@Nullable OnLoadImageListener l) {
            this.listener = l;
        }

        @Override
        public boolean onException(Exception e, T model, Target<R> target,
                                   boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(R resource, T model, Target<R> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            if (listener != null) {
                listener.onCompleted();
            }
            return false;
        }
    }

    public static class DrawableTarget extends SimpleTarget<GlideDrawable> {

        private OnLoadDrawableHandler handler;

        DrawableTarget(OnLoadDrawableHandler handler, int width, int height) {
            super(width, height);
            this.handler = handler;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            handler.onComplete(resource);
        }
    }

    public static class BitmapTarget extends SimpleTarget<Bitmap> {

        private OnLoadBitmapHandler handler;

        BitmapTarget(OnLoadBitmapHandler handler, int width, int height) {
            super(width, height);
            this.handler = handler;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            handler.onComplete(resource);
        }
    }

    private static Context getValidContext(Context context) {
        if (context == null
                || (context instanceof Activity && ((Activity) context).isDestroyed())) {
            return MysplashApplication.getInstance();
        } else {
            return context;
        }
    }

    public static void loadImage(Context context, ImageView view,
                                 @NonNull String url, @Nullable String thumbUrl, @Size(2) @Px int[] size,
                                 @Nullable BitmapTransformation[] ts, @Nullable OnLoadImageListener l) {
        loadImage(context, view, url, thumbUrl, size, null, ts, l);
    }

    public static void loadImage(Context context, ImageView view,
                                 @NonNull String url, @Nullable String thumbUrl,
                                 @Size(2) @Px int[] size, @Nullable @Size(2) @Px int[] thumbSize,
                                 @Nullable BitmapTransformation[] ts, @Nullable OnLoadImageListener l) {
        if (thumbSize == null) {
            thumbSize = new int[] {Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL};
        }
        DrawableRequestBuilder<String> thumb = TextUtils.isEmpty(thumbUrl) ? null : Glide.with(getValidContext(context))
                .load(ensureUrl(thumbUrl))
                .override(thumbSize[0], thumbSize[1])
                .diskCacheStrategy(
                        thumbSize[0] == Target.SIZE_ORIGINAL
                                ? DiskCacheStrategy.NONE
                                : DiskCacheStrategy.SOURCE
                ).listener(
                        new BaseRequestListener<>(() -> view.setTag(R.id.tag_item_image_fade_in_flag, false))
                );
        loadImage(context, view, url, thumb, size, ts, l);
    }

    public static void loadImage(Context context, ImageView view,
                                 @NonNull String url, @DrawableRes int thumbResId, @Size(2) @Px int[] size,
                                 @Nullable BitmapTransformation[] ts, @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<Integer> thumb = thumbResId == 0 ? null : Glide.with(getValidContext(context))
                .load(thumbResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new BaseRequestListener<>(() -> view.setTag(R.id.tag_item_image_fade_in_flag, false)));
        loadImage(context, view, url, thumb, size, ts, l);
    }

    private static void loadImage(Context context, ImageView view,
                                  @NonNull String url, @Nullable DrawableRequestBuilder thumbnailRequest,
                                  @Size(2) @Px int[] size,
                                  @Nullable BitmapTransformation[] ts, @Nullable OnLoadImageListener l) {
        view.setTag(R.id.tag_item_image_fade_in_flag, true);

        if (ts == null) {
            ts = new BitmapTransformation[] {new NullTransformation(context)};
        }

        Glide.with(getValidContext(context))
                .load(ensureUrl(url))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(size[0], size[1])
                .thumbnail(thumbnailRequest)
                .animate(v -> {
                    Boolean fadeInFlag = (Boolean) v.getTag(R.id.tag_item_image_fade_in_flag);
                    if (fadeInFlag == null || fadeInFlag) {
                        v.setTag(R.id.tag_item_image_fade_in_flag, false);
                        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                        animator.setDuration(300);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.start();
                    }
                }).transform(ts)
                .listener(new BaseRequestListener<>(l))
                .into(view);
    }

    public static void loadImage(Context context, ImageView view,
                                 @DrawableRes int resId, @Size(2) @Px int[] size,
                                 @Nullable BitmapTransformation[] ts, @Nullable OnLoadImageListener l) {
        if (ts == null) {
            ts = new BitmapTransformation[] {new NullTransformation(context)};
        }

        Glide.with(getValidContext(context))
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(size[0], size[1])
                .transform(ts)
                .listener(new BaseRequestListener<>(l))
                .into(view);
    }

    public static void loadImage(Context context, ImageView view, @NonNull String url) {
        Glide.with(getValidContext(context))
                .load(ensureUrl(url))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadImage(Context context, ImageView view, @DrawableRes int resId) {
        Glide.with(getValidContext(context))
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static void loadImage(Context context, ImageView view, Uri uri) {
        Glide.with(getValidContext(context))
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static DrawableTarget loadDrawable(Context context, Uri uri,
                                            @NonNull OnLoadDrawableHandler handler, int width, int height) {
        DrawableTarget target = new DrawableTarget(handler, width, height);
        Glide.with(getValidContext(context))
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);

        return target;
    }

    public static BitmapTarget loadBitmap(Context context, Uri uri,
                                          @NonNull OnLoadBitmapHandler handler, int width, int height) {
        BitmapTarget target = new BitmapTarget(handler, width, height);
        Glide.with(getValidContext(context))
                .load(uri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);

        return target;
    }

    public static Bitmap loadBitmap(Context context, Uri uri, @Nullable @Size(2) int[] size)
            throws ExecutionException, InterruptedException {
        if (size == null) {
            size = new int[] {Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL};
        }
        return Glide.with(getValidContext(context))
                .load(uri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(size[0], size[1])
                .get();
    }

    public static Bitmap loadBitmap(Context context, @DrawableRes int id, @Nullable @Size(2) int[] size)
            throws ExecutionException, InterruptedException {
        if (size == null) {
            size = new int[] {Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL};
        }
        return Glide.with(getValidContext(context))
                .load(id)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(size[0], size[1])
                .get();
    }

    public static void setImageViewSaturation(ImageView view,
                                              @FloatRange(from = 0, to = 1) float saturation) {
        AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
        matrix.setSaturation(saturation);
        view.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    /**
     * Execute a saturation animation to make a image from white and black into color.
     * */
    public static void startSaturationAnimation(Context context, ImageView target, long duration) {
        target.setHasTransientState(true);
        final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
        final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
        saturation.addUpdateListener(valueAnimator -> target.setColorFilter(new ColorMatrixColorFilter(matrix)));
        saturation.setDuration(duration);
        saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(context));
        saturation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.clearColorFilter();
                target.setHasTransientState(false);
            }
        });
        saturation.start();
    }

    /**
     * Compute the background color for item view in photo list or collection list.
     *
     * @param context Context.
     * @param color   A string that can be converted to a color without "#". For example, "000000".
     * */
    public static int computeCardBackgroundColor(Context context, String color) {
        if (TextUtils.isEmpty(color)
                || (!Pattern.compile("^#[a-fA-F0-9]{6}").matcher(color).matches()
                && !Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches())) {
            return Color.argb(0, 0, 0, 0);
        } else {
            if (Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches()) {
                color = "#" + color;
            }
            int backgroundColor = Color.parseColor(color);
            int red = ((backgroundColor & 0x00FF0000) >> 16);
            int green = ((backgroundColor & 0x0000FF00) >> 8);
            int blue = (backgroundColor & 0x000000FF);
            if (ThemeManager.getInstance(context).isLightTheme()) {
                return Color.rgb(
                        (int) (red + (255 - red) * 0.7),
                        (int) (green + (255 - green) * 0.7),
                        (int) (blue + (255 - blue) * 0.7));
            } else {
                return Color.rgb(
                        (int) (red * 0.3),
                        (int) (green * 0.3),
                        (int) (blue * 0.3));
            }
        }
    }

    /**
     * Release the {@link ImageView} from {@link Glide}.
     * A ViewHolder in {@link RecyclerView} need to call this method in
     * {@link RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)}.
     * Otherwise, there might be a OOM problem.
     *
     * @param view The ImageView to be released.
     * */
    public static void releaseImageView(@NonNull ImageView view) {
        Glide.clear(view);
    }

    public static void releaseImageView(@NonNull DrawableTarget target) {
        Glide.clear(target);
    }

    public static boolean isSameUrl(@Nullable String a, @Nullable String b) {
        if (a != null && b != null) {
            return a.equals(b);
        }
        return a == null && b == null;
    }

    private static String ensureUrl(@NonNull String url) {
        if (ComponentFactory.getSettingsService().isCDNEnabled()) {
            return url.replace(UrlCollection.UNSPLASH_IMAGE_HOST, UrlCollection.UNSPLASH_CDN_HOST);
        }
        return url;
    }

    // interface.

    // on load image listener.

    public interface OnLoadImageListener {
        void onCompleted();
    }

    public interface OnLoadDrawableHandler {
        void onComplete(Drawable drawable);
    }

    public interface OnLoadBitmapHandler {
        void onComplete(Bitmap bitmap);
    }
}
