package com.wangdaye.mysplash._common.i.view;

/**
 * Download view.
 * */

public interface DownloadView {

    void showDownloadDialog();
    void dismissDownloadDialog();
    void onDownloadProcess(int progress);
}
