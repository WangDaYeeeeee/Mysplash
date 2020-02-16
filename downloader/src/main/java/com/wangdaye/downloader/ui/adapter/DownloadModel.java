package com.wangdaye.downloader.ui.adapter;

import androidx.annotation.FloatRange;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;

class DownloadModel implements BaseAdapter.ViewModel {

    String coverUrl;
    String title;
    boolean downloading;
    boolean succeed;
    @FloatRange(from = 0, to = 100) float progress;

    DownloadTask task;

    DownloadModel(DownloadTask task) {
        this.coverUrl = task.photoUri;
        switch (task.result) {
            case DownloadTask.RESULT_DOWNLOADING:
                title = task.getNotificationTitle().toUpperCase()
                        + " : "
                        + ((int) (task.process)) + "%";
                downloading = true;
                succeed = false;
                progress = task.process;
                break;

            case DownloadTask.RESULT_SUCCEED:
                title = task.getNotificationTitle().toUpperCase();
                downloading = false;
                succeed = true;
                progress = 100;
                break;

            case DownloadTask.RESULT_FAILED:
                title = task.getNotificationTitle().toUpperCase();
                downloading = false;
                succeed = false;
                progress = 0;
                break;
        }
        this.task = task;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof DownloadModel
                && ((DownloadModel) newModel).task.taskId == task.taskId;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ImageHelper.isSameUrl(((DownloadModel) newModel).coverUrl, coverUrl)
                && ((DownloadModel) newModel).title.equals(title)
                && ((DownloadModel) newModel).downloading == downloading
                && ((DownloadModel) newModel).succeed == succeed
                && ((DownloadModel) newModel).progress == progress;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (ImageHelper.isSameUrl(((DownloadModel) newModel).coverUrl, coverUrl)) {
            return 1;
        }
        return null;
    }
}
