package com.wangdaye.mysplash.common.muzei;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.service.PhotoService;

import java.util.ArrayList;
import java.util.List;

public class MuzeiUpdateHelper {

    public static boolean update(Context context, PhotoService service,
                                 @NonNull OnUpdateCallback callback) {
        switch (MuzeiOptionManager.getInstance(context).getSource()) {
            case "all":
                return updateFromPhotos(context, service, false, callback);

            case "featured":
                return updateFromPhotos(context, service, true, callback);

            case "collection":
                return updateFromCollection(context, service, callback);
        }
        return false;
    }

    private static boolean updateFromPhotos(Context context, PhotoService service, boolean featured,
                                            @NonNull OnUpdateCallback callback) {
        String query = MuzeiOptionManager.getInstance(context).getQuery();
        if (TextUtils.isEmpty(query)) {
            query = null;
        }

        List<Photo> photoList = service.requestRandomPhotos(
                null, featured, null, query, null);
        if (photoList != null && photoList.size() > 1) {
            callback.onUpdateSucceed(photoList);
            return true;
        } else {
            callback.onUpdateFailed();
            return false;
        }
    }

    private static boolean updateFromCollection(Context context, PhotoService service,
                                                @NonNull OnUpdateCallback callback) {
        List<WallpaperSource> sourceList = MuzeiOptionManager.getInstance(context)
                .getCollectionSourceList();
        List<Integer> collectionIdList = new ArrayList<>();
        for (WallpaperSource s : sourceList) {
            collectionIdList.add((int) s.collectionId);
        }

        List<Photo> photoList = service.requestRandomPhotos(
                collectionIdList, false, null, null, null);
        if (photoList != null && photoList.size() > 1) {
            callback.onUpdateSucceed(photoList);
            return true;
        } else {
            callback.onUpdateFailed();
            return false;
        }
    }

    @Size(2) /* width, height. */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getRealMetrics(metrics);
        return new int[] {metrics.widthPixels, metrics.heightPixels};
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public interface OnUpdateCallback {
        void onUpdateSucceed(@NonNull List<Photo> photoList);
        void onUpdateFailed();
    }
}
