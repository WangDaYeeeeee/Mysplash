package com.wangdaye.mysplash._common.data.tools;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.ThinDownloadManager;

/**
 * Download manager.
 * */

public class DownloadManager {
    // data
    private ThinDownloadManager manager;

    /** <br> life cycle. */

    private DownloadManager() {
        this.manager = new ThinDownloadManager();
    }

    /** <br> data. */

    public int add(DownloadRequest request) {
        return manager.add(request);
    }

    public int cancel(int id) {
        return manager.cancel(id);
    }

    public void cancelAll() {
        manager.cancelAll();
    }

    /** <br> singleton. */

    private static DownloadManager instance;

    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }
}
