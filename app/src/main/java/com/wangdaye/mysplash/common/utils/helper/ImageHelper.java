package com.wangdaye.mysplash.common.utils.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.widget.glide.CircleTransformation;
import com.wangdaye.mysplash.common.utils.widget.glide.FadeAnimator;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Image helper.
 *
 * A helper class that makes operations of {@link Glide} easier.
 *
 * */

public class ImageHelper {

    private static class PhotoSaturationListener
            implements RequestListener<String, GlideDrawable> {

        private Context context;
        private ImageView view;
        @Nullable
        private Photo photo;
        private int index;
        @Nullable
        private OnLoadImageListener<Photo> listener;

        PhotoSaturationListener(Context context, ImageView view, @Nullable Photo photo, int index,
                                @Nullable OnLoadImageListener<Photo> l) {
            this.context = context;
            this.view = view;
            this.photo = photo;
            this.index = index;
            this.listener = l;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            if (listener != null) {
                listener.onLoadImageFailed(photo, index);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            if (photo != null) {
                photo.loadPhotoSuccess = true;
                if (!photo.hasFadedIn) {
                    photo.hasFadedIn = true;
                    ImageHelper.startSaturationAnimation(context, view);
                }
            }
            if (listener != null) {
                listener.onLoadImageSucceed(photo, index);
            }
            return false;
        }
    }

    private static class UserSaturationListener
            implements RequestListener<String, GlideDrawable> {

        private Context context;
        private ImageView view;
        @Nullable
        private User user;
        private int index;
        @Nullable
        private OnLoadImageListener<User> listener;

        UserSaturationListener(Context context, ImageView view, @Nullable User user, int index,
                               @Nullable OnLoadImageListener<User> l) {
            this.context = context;
            this.view = view;
            this.user = user;
            this.index = index;
            this.listener = l;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            if (listener != null) {
                listener.onLoadImageFailed(user, index);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            if (user != null && !user.hasFadedIn) {
                user.hasFadedIn = true;
                ImageHelper.startSaturationAnimation(context, view);
            }
            if (listener != null) {
                listener.onLoadImageSucceed(user, index);
            }
            return false;
        }
    }

    private static class SetEnableListener
            implements RequestListener<String, GlideDrawable> {

        private ImageView view;

        SetEnableListener(ImageView view) {
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
            view.setEnabled(true);
            return false;
        }
    }

    private static class BaseRequestListener<T, M, R>
            implements RequestListener<M, R> {

        private T t;
        private int index;
        private OnLoadImageListener<T> listener;

        BaseRequestListener(T t, int index, OnLoadImageListener<T> l) {
            this.t = t;
            this.index = index;
            this.listener = l;
        }

        @Override
        public boolean onException(Exception e, M model, Target<R> target,
                                   boolean isFirstResource) {
            if (listener != null) {
                listener.onLoadImageFailed(t, index);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(R resource, M model, Target<R> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            if (listener != null) {
                listener.onLoadImageSucceed(t, index);
            }
            return false;
        }
    }

    private static Context checkContextNull(Context context) {
        if (context == null
                || (context instanceof Activity && ((Activity) context).isDestroyed())) {
            return Mysplash.getInstance().getApplicationContext();
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
    public static void loadRegularPhoto(Context context, ImageView view, Photo photo, int index,
                                        @Nullable OnLoadImageListener<Photo> l) {
        loadRegularPhoto(context, view, photo, index, true, l);
    }

    private static void loadRegularPhoto(Context context, ImageView view, Photo photo, int index,
                                         boolean saturation, @Nullable OnLoadImageListener<Photo> l) {
        context = checkContextNull(context);
        if (photo != null && photo.urls != null
                && photo.width != 0 && photo.height != 0) {

            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(photo.urls.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new SetEnableListener(view));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && !photo.hasFadedIn && saturation) {
                AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                matrix.setSaturation(0);
                view.setColorFilter(new ColorMatrixColorFilter(matrix));
            }
            view.setEnabled(false);

            DrawableRequestBuilder<String> regularRequest = Glide
                    .with(context)
                    .load(photo.getRegularSizeUrl(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(photo.getRegularWidth(), photo.getRegularHeight())
                    .thumbnail(thumbnailRequest)
                    .animate(new FadeAnimator());
            if (l != null && saturation) {
                regularRequest.listener(new PhotoSaturationListener(context, view, photo, index, l));
            } else if (l != null) {
                regularRequest.listener(new BaseRequestListener<Photo, String, GlideDrawable>(photo, 0, l));
            }
            regularRequest.into(view);
        }
    }

    public static void loadBackgroundPhoto(Context context, final ImageView view, Photo photo) {
        loadRegularPhoto(context, view, photo, 0, false, null);
    }

    // collection cover.

    public static void loadCollectionCover(Context context, ImageView view, Collection collection) {
        if (collection != null) {
            loadRegularPhoto(
                    context,
                    view, collection.cover_photo, 0, false, null);
        }
    }

    public static void loadCollectionCover(Context context, ImageView view, Collection collection,
                                           int index, @Nullable OnLoadImageListener<Photo> l) {
        if (collection != null) {
            loadRegularPhoto(context, view, collection.cover_photo, index, l);
        }
    }

    // avatar.

    public static void loadAvatar(Context context, ImageView view, User user) {
        loadAvatar(context, view, user, 0, null);
    }

    public static void loadAvatar(Context context, ImageView view, User user, int index,
                                  @Nullable OnLoadImageListener<User> l) {
        if (user != null && user.profile_image != null) {
            loadAvatar(context, view, user, user.profile_image.large, index, l);
        } else {
            context = checkContextNull(context);
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(128, 128)
                    .transform(new CircleTransformation(context))
                    .listener(new BaseRequestListener<User, Integer, GlideDrawable>(user, index, l))
                    .into(view);
        }
    }

    public static void loadAvatar(Context context, ImageView view, @NotNull String url) {
        loadAvatar(context, view, null, url, 0, null);
    }

    public static void loadAvatar(Context context, ImageView view,
                                  @Nullable User user, @NotNull String url, int index,
                                  @Nullable OnLoadImageListener<User> l) {
        context = checkContextNull(context);
        DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                .load(R.drawable.default_avatar)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        DrawableRequestBuilder<String> request = Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .thumbnail(thumbnailRequest);
        if (l != null) {
            request.listener(new UserSaturationListener(context, view, user, index, l));
        }
        request.into(view);
    }

    // resource.

    public static void loadResourceImage(Context context, ImageView view, int resId) {
        loadResourceImage(context, view, resId, null);
    }

    public static void loadResourceImage(Context context, ImageView view, int resId,
                                         @Nullable BitmapTransformation transformation) {
        context = checkContextNull(context);
        DrawableRequestBuilder<Integer> request = Glide.with(context)
                .load(resId)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        if (transformation != null) {
            request.transform(transformation);
        }
        request.into(view);
    }

    // bitmap.

    public static void loadBitmap(Context context, Target<Bitmap> target, Uri uri) {
        context = checkContextNull(context);
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .into(target);
    }

    public static void loadBitmap(Context context, ImageView view, Uri uri) {
        context = checkContextNull(context);
        Glide.with(context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static Bitmap loadBitmap(Context context, @NonNull Photo photo)
            throws ExecutionException, InterruptedException {
        context = checkContextNull(context);
        int[] size = photo.getWallpaperSize(context);
        return Glide.with(context)
                .load(photo.getWallpaperSizeUrl(context))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(size[0], size[1])
                .get();
    }

    public static Bitmap loadBitmap(Context context, @DrawableRes int id, int width, int height)
            throws ExecutionException, InterruptedException {
        context = checkContextNull(context);
        return Glide.with(context)
                .load(id)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(width, height)
                .get();
    }

    // url.

    public static void loadImageFromUrl(Context context, ImageView view, String url, boolean lowPriority,
                                        @Nullable OnLoadImageListener<String> l) {
        context = checkContextNull(context);
        DrawableRequestBuilder<String> request = Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        if (lowPriority) {
            request.priority(Priority.LOW);
        }
        if (l != null) {
            request.listener(new BaseRequestListener<String, String, GlideDrawable>(url, 0, l));
        }
        request.into(view);
    }

    public static void loadImageFromUrl(Context context,
                                         Target<Bitmap> target, String url, boolean clipWithCircle) {
        context = checkContextNull(context);
        if (clipWithCircle) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .transform(new CircleTransformation(context))
                    .into(target);
        } else {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(target);
        }
    }

    // animation.

    /**
     * Execute a saturation animation to make a image from white and black into color.
     *
     * @param c      Context.
     * @param target ImageView which will execute saturation animation.
     * */
    public static void startSaturationAnimation(Context c, final ImageView target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            target.setHasTransientState(true);
            final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
            final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                    matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
            saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                    () {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    target.setColorFilter(new ColorMatrixColorFilter(matrix));
                }
            });
            saturation.setDuration(
                    SettingsOptionManager.getInstance(c).getSaturationAnimationDuration());
            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(c));
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

    public interface OnLoadImageListener<T> {
        void onLoadImageSucceed(T newT, int index);
        void onLoadImageFailed(T originalT, int index);
    }
}
