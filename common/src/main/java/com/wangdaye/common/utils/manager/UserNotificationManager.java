package com.wangdaye.common.utils.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.unsplash.NotificationFeed;
import com.wangdaye.base.unsplash.NotificationResult;
import com.wangdaye.base.unsplash.NotificationStream;
import com.wangdaye.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.observer.ResponseBodyObserver;
import com.wangdaye.common.network.service.GetStreamService;
import com.wangdaye.common.network.service.NotificationService;
import com.wangdaye.common.utils.helper.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

/**
 * User notification manager.
 *
 * A manager class that is used to manage the notifications for user.
 *
 * */

public class UserNotificationManager {

    @Inject GetStreamService streamService;
    @Inject NotificationService notificationService;

    private Gson gson;
    private SimpleDateFormat format;

    private OnRequestStreamObserver requestStreamObserver;
    private OnRequestNotificationObserver requestNotificationObserver;

    private List<OnUpdateNotificationListener> listenerList;

    // data
    private List<NotificationResult> notificationList;

    // this value is used to save the latest notification that was seen by user.
    // if a notification is update at a time later than the saved time, this notification must have
    // not been seen.
    private String latestSeenTime;

    // this value is used to save the latest refresh time.
    private long latestRefreshTime;

    private String nextPage;
    // primary key for the first result in the first page stream feed.
    // if the first result in a new feed has a id that's same as this value,
    // it means there is no unseen result.
    /** {@link NotificationStream.Results#id} */
    private String firstResultId;

    private boolean loadFinish;
    private boolean requesting;

    private static final String PREFERENCE_TIME = "mysplash_notification";
    private static final String KEY_LATEST_SEEN_TIME = "latest_seen_time";

    @SuppressLint("SimpleDateFormat")
    UserNotificationManager() {
        DaggerNetworkServiceComponent.create().inject(this);

        gson = new Gson();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        listenerList = new ArrayList<>();

        notificationList = new ArrayList<>();

        nextPage = null;
        firstResultId = null;

        loadFinish = false;
        requesting = false;

        SharedPreferences sharedPreferences = MysplashApplication.getInstance()
                .getSharedPreferences(PREFERENCE_TIME, Context.MODE_PRIVATE);
        latestSeenTime = sharedPreferences.getString(KEY_LATEST_SEEN_TIME, "");
        latestRefreshTime = -1;
    }

    // HTTP request.

    public void requestPersonalNotifications() {
        if (!loadFinish && !requesting) {
            cancelRequest(true);
            if (TextUtils.isEmpty(nextPage)) {
                requestFirstPageNotifications();
            } else {
                requestNextPageNotifications();
            }
        }
    }

    private void requestFirstPageNotifications() {
        requesting = true;
        latestRefreshTime = System.currentTimeMillis();
        requestStreamObserver = new OnRequestStreamObserver(true);
        streamService.requestFirstPageStream(requestStreamObserver);
    }

    private void requestNextPageNotifications() {
        requesting = true;
        requestStreamObserver = new OnRequestStreamObserver(false);
        streamService.requestNextPageStream(nextPage, requestStreamObserver);
    }

    void checkToRefreshNotification() {
        if (latestRefreshTime < 0) {
            requestPersonalNotifications();
        } else if (System.currentTimeMillis() - latestRefreshTime > 1000 * 60 * 15
                // TODO: 2017/4/22 alter time.
                && !requesting) {
            cancelRequest(true);
            requestFirstPageNotifications();
        }
    }

    /**
     * This method will cancel the HTTP request.
     *
     * @param force if set false, it means only cancel the next page HTTP request.
     * */
    public void cancelRequest(boolean force) {
        if (force || (requesting && !TextUtils.isEmpty(nextPage))) {
            requesting = false;
            if (requestStreamObserver != null) {
                requestStreamObserver.setCanceled();
            }
            if (requestNotificationObserver != null) {
                requestNotificationObserver.setCanceled();
            }
            streamService.cancel();
            notificationService.cancel();
        }
    }

    // manage.

    public List<NotificationResult> getNotificationList() {
        return notificationList;
    }

    private void addNotification(List<NotificationResult> list) {
        for (int i = 0; i < list.size(); i ++) {
            notificationList.add(list.get(i));
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onAddNotification(list.get(i), notificationList.size() - 1);
            }
        }
    }

    private void removeAllNotifications() {
        notificationList.clear();
        for (int j = 0; j < listenerList.size(); j ++) {
            listenerList.get(j).onClearNotification();
        }
    }

    public void clearNotifications(boolean logout) {
        removeAllNotifications();
        cancelRequest(true);
        nextPage = null;
        firstResultId = null;
        loadFinish = false;
        if (logout) {
            setLatestSeenTime();
        }
    }

    public boolean isLoadFinish() {
        return loadFinish;
    }

    // time.

    @SuppressLint("SimpleDateFormat")
    public boolean hasUnseenNotification() {
        return notificationList.size() != 0
                && (TextUtils.isEmpty(latestSeenTime) || isUnseenNotification(notificationList.get(0)));
    }

    public boolean isUnseenNotification(NotificationResult result) {
        if (TextUtils.isEmpty(latestSeenTime)) {
            return true;
        } else {
            try {
                String[] savedTimes = latestSeenTime.substring(0, 19).split("T");
                String[] resultTimes = result.time.substring(0, 19).split("T");
                Date savedDate = format.parse(savedTimes[0] + " " + savedTimes[1]);
                Date listDate = format.parse(resultTimes[0] + " " + resultTimes[1]);
                return savedDate.before(listDate);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void setLatestSeenTime() {
        if (notificationList.size() == 0) {
            latestSeenTime = "";
        } else {
            latestSeenTime = notificationList.get(0).time;
        }
        SharedPreferences.Editor editor = MysplashApplication.getInstance()
                .getSharedPreferences(PREFERENCE_TIME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_LATEST_SEEN_TIME, latestSeenTime);
        editor.apply();
        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onSetLatestTime();
        }
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    // interface.

    // on add notification listener.

    public interface OnUpdateNotificationListener {
        void onRequestNotificationSucceed(List<NotificationResult> resultList);
        void onRequestNotificationFailed();
        void onAddNotification(NotificationResult result, int position);
        void onClearNotification();
        void onSetLatestTime();
    }

    public void addOnUpdateNotificationListener(OnUpdateNotificationListener l) {
        this.listenerList.add(l);
    }

    public void removeOnUpdateNotificationListener(OnUpdateNotificationListener l) {
        this.listenerList.remove(l);
    }

    private class OnRequestStreamObserver extends ResponseBodyObserver {

        private boolean refresh;
        private boolean canceled;

        OnRequestStreamObserver(boolean refresh) {
            this.refresh = refresh;
            this.canceled = false;
        }

        void setCanceled() {
            this.canceled = true;
        }

        @Override
        public void onSucceed(ResponseBody responseBody) {
            if (canceled) {
                return;
            }
            String json = null;
            NotificationStream stream = null;
            try {
                json = responseBody.string();
                stream = gson.fromJson(json, NotificationStream.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stream == null || TextUtils.isEmpty(stream.next)) {
                nextPage = null;
                loadFinish = true;
            } else {
                nextPage = stream.next;
            }

            if (stream != null && stream.results != null && stream.results.size() != 0
                    && (!refresh
                    || TextUtils.isEmpty(firstResultId)
                    || !stream.results.get(0).id.equals(firstResultId))) {
                if (refresh) {
                    firstResultId = stream.results.get(0).id;
                }
                requestNotificationObserver = new OnRequestNotificationObserver(refresh);
                notificationService.requestNotificationFeed(
                        streamService.getStreamUsablePart(json),
                        requestNotificationObserver
                );
            } else {
                requesting = false;
                for (int j = 0; j < listenerList.size(); j ++) {
                    listenerList.get(j).onRequestNotificationSucceed(new ArrayList<>());
                }
            }
        }

        @Override
        public void onFailed() {
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onRequestNotificationFailed();
            }
            NotificationHelper.showSnackbar(
                    MysplashApplication.getInstance().getString(R.string.feedback_get_notification_failed));
        }
    }

    private class OnRequestNotificationObserver extends BaseObserver<NotificationFeed> {

        private boolean refresh;
        private boolean canceled;

        OnRequestNotificationObserver(boolean refresh) {
            this.refresh = refresh;
            this.canceled = false;
        }

        void setCanceled() {
            this.canceled = true;
        }

        @Override
        public void onSucceed(NotificationFeed notificationFeed) {
            if (canceled) {
                return;
            }
            requesting = false;
            // request successfully.
            if (notificationFeed.results != null && notificationFeed.results.size() > 0) {
                if (refresh
                        && notificationList.size() > 0
                        && !notificationFeed.results.get(0).id.equals(notificationList.get(0).id)) {
                    // already have notifications in the list.
                    // and there are some new feeds in the request result.
                    removeAllNotifications();
                }
                addNotification(notificationFeed.results);
            }
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onRequestNotificationSucceed(notificationFeed.results);
            }
        }

        @Override
        public void onFailed() {
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onRequestNotificationFailed();
            }
            NotificationHelper.showSnackbar(
                    MysplashApplication.getInstance().getString(R.string.feedback_get_notification_failed));
        }
    }
}
