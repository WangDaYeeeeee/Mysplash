package com.wangdaye.base;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.wangdaye.base.i.Downloadable;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.Collection;

/**
 * Download task.
 * */

public class DownloadTask implements Downloadable {

    public long taskId;
    public String title;
    public String photoUri;
    public String downloadUrl;

    @DownloadTypeRule public int downloadType;
    @DownloadResultRule public int result;
    @FloatRange(from = 0.0, to = 100.0) public float process;

    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;
    public static final int COLLECTION_TYPE = 4;
    @IntDef({
            DOWNLOAD_TYPE,
            SHARE_TYPE,
            WALLPAPER_TYPE,
            COLLECTION_TYPE,
    })
    public @interface DownloadTypeRule {}

    public static final int RESULT_SUCCEED = 1;
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_DOWNLOADING = 0;
    @IntDef({
            RESULT_DOWNLOADING,
            RESULT_SUCCEED,
            RESULT_FAILED
    })
    public @interface DownloadResultRule {}

    public static final String DOWNLOAD_TYPE_PATH = Environment.DIRECTORY_PICTURES;
    public static final String DOWNLOAD_SUB_PATH = "Mysplash";
    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_COLLECTION_FORMAT = ".zip";
    @StringDef({DOWNLOAD_PHOTO_FORMAT, DOWNLOAD_COLLECTION_FORMAT})
    public @interface DownloadFormatRule {}

    public DownloadTask(Context context, @NonNull Photo p, @DownloadTypeRule int type, String downloadScale) {
        this.title = p.id;
        this.photoUri = p.getRegularSizeUrl(context);
        switch (downloadScale) {
            case "tiny":
                this.downloadUrl = p.getWallpaperSizeUrl(context);
                break;

            case "compact":
                this.downloadUrl = p.urls.full;
                break;

            default:
                this.downloadUrl = p.urls.raw;
                break;
        }
        this.downloadType = type;
        this.result = RESULT_DOWNLOADING;
        this.process = 0;
    }

    public DownloadTask(Collection c) {
        this.title = String.valueOf(c.id);
        this.photoUri = c.cover_photo.urls.regular;
        this.downloadUrl = c.links.download;
        this.downloadType = COLLECTION_TYPE;
        this.result = RESULT_DOWNLOADING;
        this.process = 0;
    }

    public DownloadTask(long taskId,
                        String title, String photoUri, String downloadUrl,
                        int downloadType, int result) {
        this(taskId, title, photoUri, downloadUrl, downloadType, result, 0);
    }

    public DownloadTask(long taskId,
                        String title, String photoUri, String downloadUrl,
                        int downloadType, int result, float process) {
        this.taskId = taskId;
        this.title = title;
        this.photoUri = photoUri;
        this.downloadUrl = downloadUrl;
        this.downloadType = downloadType;
        this.result = result;
        switch (this.result) {
            case RESULT_DOWNLOADING:
                this.process = Math.max(0, Math.min(process, 100));
                break;

            case RESULT_FAILED:
                this.process = 0;
                break;

            case RESULT_SUCCEED:
                this.process = 100;
                break;
        }
    }

    // data.

    /**
     * Get the file path of the downloaded file.
     *
     * @return file path.
     * */
    public String getFilePath(Context context) {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, title + getFormat());
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + DOWNLOAD_SUB_PATH);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            Uri uri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                return uri.toString();
            }
        }*/
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/" + DOWNLOAD_SUB_PATH
                + "/" + title + getFormat();
    }

    /**
     * Get the title of downloading notification description title text.
     *
     * @return notification description title text.
     * */
    public String getNotificationTitle() {
        if (downloadType == COLLECTION_TYPE) {
            return "COLLECTION #" + title;
        } else {
            return title;
        }
    }

    /**
     * Get the downloaded file's format.
     *
     * @return format of downloaded file.
     * */
    @DownloadFormatRule
    public String getFormat() {
        if (downloadType == COLLECTION_TYPE) {
            return DOWNLOAD_COLLECTION_FORMAT;
        } else {
            return DOWNLOAD_PHOTO_FORMAT;
        }
    }
}
