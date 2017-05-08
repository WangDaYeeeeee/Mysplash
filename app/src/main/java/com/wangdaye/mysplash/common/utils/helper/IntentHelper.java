package com.wangdaye.mysplash.common.utils.helper;

import android.app.Activity;
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
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.activity.CustomApiActivity;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.main.view.activity.NotificationActivity;
import com.wangdaye.mysplash.tag.view.activity.TagActivity;
import com.wangdaye.mysplash.common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash.common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash.common.ui.activity.LoginActivity;
import com.wangdaye.mysplash.common.ui.activity.PreviewActivity;
import com.wangdaye.mysplash.common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash.common.ui.activity.UpdateMeActivity;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
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
 *
 * This helper that can build {@link Intent} and make start {@link MysplashActivity} easier.
 *
 * */

public class IntentHelper {

    public static void startMainActivity(Activity a) {
        Intent intent = new Intent(a, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(intent);
    }

    public static void startNotificationActivity(MysplashActivity a) {
        Intent intent = new Intent(a, NotificationActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
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
            ActivityCompat.startActivityForResult(
                    a, intent, Mysplash.PHOTO_ACTIVITY, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(image, a.getString(R.string.transition_photo_image)),
                            Pair.create(background, a.getString(R.string.transition_photo_background)));
            ActivityCompat.startActivityForResult(
                    a, intent, Mysplash.PHOTO_ACTIVITY, options.toBundle());
        }
    }

    public static void startPhotoActivity(MysplashActivity a, String photoId) {
        Intent intent = new Intent(a, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_ID, photoId);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startPreviewActivity(MysplashActivity a, Photo photo, boolean showIcon) {
        Intent intent = new Intent(a, PreviewActivity.class);
        intent.putExtra(PreviewActivity.KEY_PREVIEW_ACTIVITY_PREVIEW, photo);
        intent.putExtra(PreviewActivity.KEY_PREVIEW_ACTIVITY_SHOW_ICON, showIcon);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startPreviewActivity(MysplashActivity a, User user, boolean showIcon) {
        Intent intent = new Intent(a, PreviewActivity.class);
        intent.putExtra(PreviewActivity.KEY_PREVIEW_ACTIVITY_PREVIEW, user);
        intent.putExtra(PreviewActivity.KEY_PREVIEW_ACTIVITY_SHOW_ICON, showIcon);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startCollectionActivity(MysplashActivity a,
                                               View avatar, View background, Collection c) {
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

        ActivityCompat.startActivityForResult(
                a, intent, Mysplash.COLLECTION_ACTIVITY, options.toBundle());
    }

    public static void startCollectionActivity(MysplashActivity a, Collection c) {
        Intent intent = new Intent(a, CollectionActivity.class);
        intent.putExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION, c);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startCollectionActivity(MysplashActivity a, String collectionId) {
        Intent intent = new Intent(a, CollectionActivity.class);
        intent.putExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_ID, collectionId);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startUserActivity(MysplashActivity a,
                                         View avatar, User u, @UserActivity.UserPageRule int page) {
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
                ActivityCompat.startActivityForResult(
                        a, intent, Mysplash.USER_ACTIVITY, options.toBundle());
            }
        }
    }

    public static void startLoginActivity(MysplashActivity a) {
        Intent intent = new Intent(a, LoginActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startMeActivity(MysplashActivity a,
                                       View avatar, @UserActivity.UserPageRule int page) {
        if (!AuthManager.getInstance().isAuthorized()) {
            startLoginActivity(a);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(a, MeActivity.class);
            intent.putExtra(MeActivity.KEY_ME_ACTIVITY_PAGE_POSITION, page);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(avatar, a.getString(R.string.transition_me_avatar)));
            ActivityCompat.startActivityForResult(
                    a, intent, Mysplash.ME_ACTIVITY, options.toBundle());
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
        Intent intent = new Intent(context, DownloadManageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DownloadManageActivity.EXTRA_NOTIFICATION, true);
        context.startActivity(intent);
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
        /*
        Uri uri = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + title + Mysplash.DOWNLOAD_PHOTO_FORMAT);*/
        Uri uri = FileUtils.filePathToUri(
                c,
                Environment.getExternalStorageDirectory()
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

    public static void startTagActivity(MysplashActivity a, String tag) {
        Intent intent = new Intent(a, TagActivity.class);
        intent.putExtra(TagActivity.KEY_TAG_ACTIVITY_TAG, tag);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void startCustomApiActivity(SettingsActivity a) {
        Intent intent = new Intent(a, CustomApiActivity.class);
        a.startActivityForResult(intent, Mysplash.CUSTOM_API_ACTIVITY);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    public static void backToHome(MysplashActivity a) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        a.startActivity(intent);
    }
}
