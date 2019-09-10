package com.wangdaye.common.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
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

    private static class ExecuteSaturationAnimationListener
            extends BaseRequestListener<String, GlideDrawable> {

        private ImageView image;
        private Photo photo;
        private boolean executeAnimation;

        ExecuteSaturationAnimationListener(ImageView image, Photo photo, boolean executeAnimation,
                                           @Nullable OnLoadImageListener l) {
            super(l);
            this.image = image;
            this.photo = photo;
            this.executeAnimation = executeAnimation;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            super.onResourceReady(resource, model, target, isFromMemoryCache, isFirstResource);
            photo.loadPhotoSuccess = true;
            if (!photo.hasFadedIn) {
                photo.hasFadedIn = true;
                if (executeAnimation) {
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(image.getContext(), image, duration);
                }
            }
            return false;
        }
    }

    private static class CancelFadeInListener
            implements RequestListener<String, GlideDrawable> {

        private ImageView view;

        CancelFadeInListener(ImageView view) {
            this.view = view;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            view.setTag(R.id.tag_item_image_fade_in_flag, false);
            return false;
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

    // photo.

    /**
     * Load regular size photo image.
     *
     * The photo state:
     * 1. Null     (enable false)
     * 2. Thumb    (enable true)
     * 3. Regular  (enable true)
     * 4. Full     (enable true)
     *
     * The enable value is a flag for fade animation.
     * */
    public static void loadRegularPhoto(Context context, ImageView view, Photo photo,
                                        @Nullable OnLoadImageListener l) {
        loadRegularPhoto(context, view, photo, true, l);
    }

    public static void loadRegularPhoto(Context context, ImageView view, Photo photo,
                                        boolean saturation, @Nullable OnLoadImageListener l) {
        context = getValidContext(context);
        if (photo != null && photo.urls != null
                && photo.width != 0 && photo.height != 0) {
            // set fade in flag.
            // true --> execute fade in animation after loading.
            view.setTag(R.id.tag_item_image_fade_in_flag, true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && !photo.hasFadedIn && saturation && l != null) {
                AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                matrix.setSaturation(0);
                view.setColorFilter(new ColorMatrixColorFilter(matrix));
            }

            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(photo.urls.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new CancelFadeInListener(view));

            int[] size = photo.getRegularSize(context);
            Glide.with(context)
                    .load(photo.getRegularSizeUrl(size))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(size[0], size[1])
                    .thumbnail(thumbnailRequest)
                    .animate(new FadeAnimator())
                    .listener(new ExecuteSaturationAnimationListener(view, photo, saturation, l))
                    .into(view);
        }
    }

    public static void loadBackgroundPhoto(Context context, final ImageView view, Photo photo) {
        loadRegularPhoto(context, view, photo, false, null);
    }

    // collection cover.

    public static void loadCollectionCover(Context context, ImageView view, Collection collection,
                                           boolean saturation, @Nullable OnLoadImageListener l) {
        if (collection != null) {
            loadRegularPhoto(context, view, collection.cover_photo, saturation, l);
        }
    }

    // avatar.

    public static void loadAvatar(Context context, ImageView view, User user,
                                  @Nullable OnLoadImageListener l) {
        if (user != null && user.profile_image != null) {
            loadAvatar(context, view, user.profile_image.large, l);
        } else {
            context = getValidContext(context);
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(128, 128)
                    .transform(new CircleTransformation(context))
                    .listener(new BaseRequestListener<>(l))
                    .into(view);
        }
    }

    public static void loadAvatar(Context context, ImageView view, @NonNull String url,
                                  @Nullable OnLoadImageListener l) {
        context = getValidContext(context);
        DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                .load(R.drawable.default_avatar)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .thumbnail(thumbnailRequest)
                .listener(new BaseRequestListener<>(l))
                .into(view);
    }

    // resource.

    public static void loadResourceImage(Context context, ImageView view, int resId) {
        Glide.with(getValidContext(context))
                .load(resId)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(view);
    }

    // bitmap.

    public static void loadBitmap(Context context, Uri uri,
                                  @NonNull OnLoadImageHandler handler, int width, int height) {
        Glide.with(getValidContext(context))
                .load(uri)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        handler.onComplete(resource);
                    }
                });
    }

    public static void loadBitmap(Context context, ImageView view, Uri uri) {
        Glide.with(getValidContext(context))
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static Bitmap loadBitmap(Context context, Uri uri)
            throws ExecutionException, InterruptedException {
        return Glide.with(getValidContext(context))
                .load(uri)
                .asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
    }

    public static Bitmap loadBitmap(Context context, Uri uri, @Size(2) int[] size)
            throws ExecutionException, InterruptedException {
        return Glide.with(getValidContext(context))
                .load(uri)
                .asBitmap()
                .into(size[0], size[1])
                .get();
    }

    public static Bitmap loadBitmap(Context context, @DrawableRes int id, int width, int height)
            throws ExecutionException, InterruptedException {
        return Glide.with(getValidContext(context))
                .load(id)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(width, height)
                .get();
    }

    // url.

    public static void loadImageFromUrl(Context context, ImageView view, String url, boolean lowPriority,
                                        @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<String> request = Glide.with(getValidContext(context))
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        if (lowPriority) {
            request.priority(Priority.LOW);
        }
        if (l != null) {
            request.listener(new BaseRequestListener<>(l));
        }
        request.into(view);
    }

    // animation.

    /**
     * Execute a saturation animation to make a image from white and black into color.
     * */
    public static void startSaturationAnimation(Context context, ImageView target, long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }

    // data.

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
    public static void releaseImageView(ImageView view) {
        Glide.clear(view);
    }

    // interface.

    // on load image listener.

    public interface OnLoadImageListener {
        void onCompleted();
    }

    public interface OnLoadImageHandler {
        void onComplete(Bitmap resource);
    }
}
