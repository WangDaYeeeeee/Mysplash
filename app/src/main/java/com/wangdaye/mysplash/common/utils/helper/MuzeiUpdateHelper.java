package com.wangdaye.mysplash.common.utils.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;

import java.util.ArrayList;
import java.util.List;

public class MuzeiUpdateHelper {

    public static boolean update(Context context, @NonNull OnUpdateCallback callback) {
        switch (MuzeiOptionManager.getInstance(context).getSource()) {
            case "all":
                return updateFromPhotos(false, callback);

            case "featured":
                return updateFromPhotos(true, callback);

            case "collection":
                return updateFromCollection(context, callback);
        }
        return false;
    }

    private static boolean updateFromPhotos(boolean featured, @NonNull OnUpdateCallback callback) {
        List<Photo> photoList = PhotoService.getService().requestRandomPhotos(
                null, featured, null, null, null);
        if (photoList != null && photoList.size() > 1) {
            callback.onUpdateSucceed(photoList);
            return true;
        } else {
            callback.onUpdateFailed();
            return false;
        }
    }

    private static boolean updateFromCollection(Context context, @NonNull OnUpdateCallback callback) {
        List<WallpaperSource> sourceList = MuzeiOptionManager.getInstance(context)
                .getCollectionSourceList();
        List<Integer> collectionIdList = new ArrayList<>();
        for (WallpaperSource s : sourceList) {
            collectionIdList.add((int) s.collectionId);
        }

        List<Photo> photoList = PhotoService.getService().requestRandomPhotos(
                collectionIdList, false, null, null, null);
        if (photoList != null && photoList.size() > 1) {
            callback.onUpdateSucceed(photoList);
            return true;
        } else {
            callback.onUpdateFailed();
            return false;
        }
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
