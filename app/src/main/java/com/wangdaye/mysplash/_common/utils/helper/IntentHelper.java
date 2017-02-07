package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash.category.view.activity.CategoryActivity;
import com.wangdaye.mysplash._common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash._common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash._common.ui.activity.LoginActivity;
import com.wangdaye.mysplash._common.ui.activity.PreviewPhotoActivity;
import com.wangdaye.mysplash._common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash._common.ui.activity.UpdateMeActivity;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash.about.view.activity.AboutActivity;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.me.view.activity.MyFollowActivity;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

/**
 * Intent helper.
 * */

public class IntentHelper {

    public static void startMainActivity(MysplashActivity a) {
        Intent intent = new Intent(a, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(intent);
    }

    public static void startPhotoActivity(MysplashActivity a, View image, View background, Photo p) {
        Mysplash.getInstance().setPhoto(p);

        Intent intent = new Intent(a, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO, p);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(
                            background,
                            (int) background.getX(), (int) background.getY(),
                            background.getMeasuredWidth(), background.getMeasuredHeight());
            ActivityCompat.startActivity(a, intent, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(image, a.getString(R.string.transition_photo_image)),
                            Pair.create(background, a.getString(R.string.transition_photo_background)));
            ActivityCompat.startActivity(a, intent, options.toBundle());
        }
    }

    public static void startPhotoActivity(MysplashActivity a, String photoId) {
        Intent intent = new Intent(a, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photoId);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startPreviewPhotoActivity(MysplashActivity a, Photo p) {
        Intent intent = new Intent(a, PreviewPhotoActivity.class);
        intent.putExtra(PreviewPhotoActivity.KEY_PREVIEW_PHOTO_ACTIVITY_PHOTO, p);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startCollectionActivity(MysplashActivity a, View avatar, View background, Collection c) {
        Intent intent = new Intent(a, CollectionActivity.class);
        intent.putExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION, c);

        ActivityOptionsCompat options;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptionsCompat
                    .makeScaleUpAnimation(
                            background,
                            (int) background.getX(), (int) background.getY(),
                            background.getMeasuredWidth(), background.getMeasuredHeight());
        } else {
            options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(avatar, a.getString(R.string.transition_collection_avatar)),
                            Pair.create(background, a.getString(R.string.transition_collection_background)));
        }

        if (AuthManager.getInstance().getUsername() != null
                &&
                AuthManager.getInstance()
                        .getUsername()
                        .equals(c.user.username)) {
            ActivityCompat.startActivityForResult(
                    a,
                    intent,
                    MeActivity.COLLECTION_ACTIVITY,
                    options.toBundle());
        } else {
            ActivityCompat.startActivity(a, intent, options.toBundle());
        }
    }

    public static void startCollectionActivity(MysplashActivity a, String collectionId) {
        Intent intent = new Intent(a, CollectionActivity.class);
        intent.putExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_ID, collectionId);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startUserActivity(MysplashActivity a, View avatar, User u, int page) {
        if (AuthManager.getInstance().isAuthorized()
                && !TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                && u.username.equals(AuthManager.getInstance().getUsername())) {
            startMeActivity(a, avatar, page);
        } else {
            Intent intent = new Intent(a, UserActivity.class);
            intent.putExtra(UserActivity.KEY_USER_ACTIVITY_USER, u);
            intent.putExtra(UserActivity.KEY_USER_ACTIVITY_PAGE_POSITION, page);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                a.startActivity(intent);
                a.overridePendingTransition(R.anim.activity_in, 0);
            } else {
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                a,
                                Pair.create(avatar, a.getString(R.string.transition_user_avatar)));
                ActivityCompat.startActivity(a, intent, options.toBundle());
            }
        }
    }

    public static void startLoginActivity(MysplashActivity a) {
        Intent intent = new Intent(a, LoginActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startMeActivity(MysplashActivity a, View avatar, int page) {
        if (!AuthManager.getInstance().isAuthorized()) {
            startLoginActivity(a);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(a, MeActivity.class);
            intent.putExtra(MeActivity.KEY_ME_ACTIVITY_PAGE_POSITION, page);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(avatar, a.getString(R.string.transition_me_avatar)));
            ActivityCompat.startActivity(a, intent, options.toBundle());
        } else {
            Intent intent = new Intent(a, MeActivity.class);
            intent.putExtra(MeActivity.KEY_ME_ACTIVITY_PAGE_POSITION, page);
            a.startActivity(intent);
            a.overridePendingTransition(R.anim.activity_in, 0);
        }
    }

    public static void startMyFollowActivity(MysplashActivity a) {
        if (!AuthManager.getInstance().isAuthorized()) {
            startLoginActivity(a);
        } else {
            Intent intent = new Intent(a, MyFollowActivity.class);
            a.startActivity(intent);
            a.overridePendingTransition(R.anim.activity_in, 0);
        }
    }

    public static void startUpdateMeActivity(MysplashActivity a) {
        Intent intent = new Intent(a, UpdateMeActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startDownloadManageActivity(MysplashActivity a) {
        Intent intent = new Intent(a, DownloadManageActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startDownloadManageActivityFromNotification(Context context) {
        Intent manageActivity = new Intent(context, DownloadManageActivity.class);
        manageActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        manageActivity.putExtra(DownloadManageActivity.EXTRA_NOTIFICATION, true);
        context.startActivity(manageActivity);
    }

    public static void startSettingsActivity(MysplashActivity a) {
        Intent intent = new Intent(a, SettingsActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startAboutActivity(MysplashActivity a) {
        Intent intent = new Intent(a, AboutActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startIntroduceActivity(MysplashActivity a) {
        Intent intent = new Intent(a, IntroduceActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startCheckPhotoActivity(Context c, String title) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
        intent.setDataAndType(uri, "image/jpg");

        c.startActivity(
                Intent.createChooser(
                        intent,
                        c.getString(R.string.check)));
    }

    public static void startCheckCollectionActivity(Context c, String title) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + title
                + ".zip");
        intent.setDataAndType(uri, "application/x-zip-compressed");

        c.startActivity(
                Intent.createChooser(
                        intent,
                        c.getString(R.string.check)));
    }

    public static void startWebActivity(Context c, String url) {
        String packageName = "com.android.chrome";
        Intent browserIntent = new Intent();
        browserIntent.setPackage(packageName);
        List<ResolveInfo> activitiesList = c.getPackageManager().queryIntentActivities(
                browserIntent, -1);
        if (activitiesList.size() > 0) {
            CustomTabHelper.startCustomTabActivity(c, url);
        } else {
            c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public static void startCategoryActivity(MysplashActivity a, int categoryId) {
        Intent intent = new Intent(a, CategoryActivity.class);
        intent.putExtra(CategoryActivity.KEY_CATEGORY_ACTIVITY_ID, categoryId);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }
}
