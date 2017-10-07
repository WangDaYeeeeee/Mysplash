package com.wangdaye.mysplash.common.utils.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;
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

    // photo.

    public static void loadRegularPhoto(Context context, final ImageView view, Photo photo,
                                        @Nullable OnLoadImageListener l) {
        if (photo != null && photo.urls != null
                && photo.width != 0 && photo.height != 0) {
            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(photo.urls.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
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
                    });
            if (l != null && !photo.hasFadedIn && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                matrix.setSaturation(0);
                view.setColorFilter(new ColorMatrixColorFilter(matrix));
                view.setEnabled(false);
            }
            loadImage(
                    context, view,
                    photo.getRegularSizeUrl(context), photo.getRegularWidth(), photo.getRegularHeight(), false, false,
                    l == null ? null : thumbnailRequest, null, l == null ? null : new FadeAnimator(),
                    l);
        }
    }

    public static void loadFullPhoto(Context context, ImageView view, String url, String thumbnail,
                                     @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<String> thumbnailRequest = Glide
                .with(context)
                .load(thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        loadImage(context, view, url, 0, 0, false, false, thumbnailRequest, null, null, l);
    }



    public static void loadBackgroundPhoto(Context context, final ImageView view, Photo photo) {
        DrawableRequestBuilder<String> thumbnailRequest = Glide
                .with(context)
                .load(photo.urls.thumb)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
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
                });
        view.setEnabled(false);
        loadImage(context, view, photo.getRegularSizeUrl(context), 0, 0, false, false, thumbnailRequest, null, new FadeAnimator(), null);
    }

    // collection cover.

    public static void loadCollectionCover(Context context, ImageView view, Collection collection,
                                           @Nullable OnLoadImageListener l) {
        if (collection != null) {
            loadRegularPhoto(context, view, collection.cover_photo, l);
        }
    }

    // avatar.

    public static void loadAvatar(Context context, ImageView view, User user,
                                  @Nullable OnLoadImageListener l) {
        if (user != null && user.profile_image != null) {
            loadAvatar(context, view, user.profile_image.large, l);
        } else {
            loadImage(
                    context, view,
                    R.drawable.default_avatar, 128, 128, false,
                    new CircleTransformation(context),
                    l);
        }
    }

    public static void loadAvatar(Context context, ImageView view, @NotNull String url,
                                  @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                .load(R.drawable.default_avatar)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        loadImage(
                context, view,
                url, 128, 128, false, false,
                thumbnailRequest, new CircleTransformation(context), null,
                l);
    }

    // resource.

    public static void loadResourceImage(Context context, ImageView view, int resId) {
        loadResourceImage(context, view, resId, null);
    }

    public static void loadResourceImage(Context context, ImageView view, int resId,
                                         @Nullable BitmapTransformation transformation) {
        loadImage(context, view, resId, 0, 0, true, transformation, null);
    }

    // bitmap.

    public static void loadBitmap(Context context, Target<Bitmap> target, Uri uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .into(target);
    }

    public static void loadBitmap(Context context, ImageView view, Uri uri) {
        Glide.with(context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

    public static Bitmap loadBitmap(Context context, @DrawableRes int id, int width, int height)
            throws ExecutionException, InterruptedException {
        return Glide.with(context)
                .load(id)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(width, height)
                .get();
    }

    // url.

    public static void loadImageFromUrl(Context context, ImageView view, String url, boolean lowPriority,
                                        @Nullable OnLoadImageListener l) {
        loadImage(context, view, url, 0, 0, false, lowPriority, null, null, null, l);
    }

    public static void loadImageFromUrl(Context context,
                                         Target<Bitmap> target, String url, boolean clipWithCircle) {
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

    // builder.

    private static void loadImage(Context context, ImageView view,
                                  String url, int width, int height, boolean dontAnimate, boolean lowPriority,
                                  @Nullable DrawableRequestBuilder thumbnail,
                                  @Nullable BitmapTransformation transformation,
                                  @Nullable ViewPropertyAnimation.Animator animator,
                                  @Nullable final OnLoadImageListener l) {
        DrawableRequestBuilder<String> builder = Glide
                .with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadSucceed();
                        }
                        return false;
                    }
                });
        if (width != 0 && height != 0) {
            builder.override(width, height);
        }
        if (dontAnimate) {
            builder.dontAnimate();
        }
        if (lowPriority) {
            builder.priority(Priority.LOW);
        } else {
            builder.priority(Priority.NORMAL);
        }
        if (thumbnail != null) {
            builder.thumbnail(thumbnail);
        }
        if (transformation != null) {
            builder.transform(transformation);
        }
        if (animator != null) {
            builder.animate(animator);
        }
        builder.into(view);
    }

    private static void loadImage(Context context, ImageView view,
                                  int resId, int width, int height, boolean dontAnimate,
                                  @Nullable BitmapTransformation transformation,
                                  @Nullable final OnLoadImageListener l) {
        DrawableRequestBuilder<Integer> builder = Glide
                .with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadSucceed();
                        }
                        return false;
                    }
                });
        if (width != 0 && height != 0) {
            builder.override(width, height);
        }
        if (dontAnimate) {
            builder.dontAnimate();
        }
        if (transformation != null) {
            builder.transform(transformation);
        }
        builder.into(view);
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
     * A ViewHolder in {@link android.support.v7.widget.RecyclerView} need to call this method in
     * {@link android.support.v7.widget.RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)}.
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
        void onLoadSucceed();
        void onLoadFailed();
    }
}
