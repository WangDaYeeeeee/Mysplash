package com.wangdaye.mysplash.common.data.tools;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.ThinDownloadManager;

/**
 * Download manager.
 * */

public class DownloadTools {
    // data
    private ThinDownloadManager manager;

    /** <br> life cycle. */

    private DownloadTools() {
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

    private static DownloadTools instance;

    public static DownloadTools getInstance() {
        if (instance == null) {
            instance = new DownloadTools();
        }
        return instance;
    }
}
