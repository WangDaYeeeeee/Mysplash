package com.wangdaye.mysplash.common.utils.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.NotificationFeed;
import com.wangdaye.mysplash.common.data.entity.unsplash.NotificationResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.NotificationStream;
import com.wangdaye.mysplash.common.data.service.network.GetStreamService;
import com.wangdaye.mysplash.common.data.service.network.NotificationService;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * User notification manager.
 *
 * A manager class that is used to manage the notifications for user.
 *
 * */

public class UserNotificationManager {

    private Gson gson;
    private SimpleDateFormat format;

    private GetStreamService streamService;
    private NotificationService notificationService;
    private OnRequestStreamListener requestStreamListener;
    private OnRequestNotificationListener requestNotificationListener;

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
    /** {@link com.wangdaye.mysplash.common.data.entity.unsplash.NotificationStream.Results#id} */
    private String firstResultId;

    private boolean loadFinish;
    private boolean requesting;

    private static final String PREFERENCE_TIME = "mysplash_notification";
    private static final String KEY_LATEST_SEEN_TIME = "latest_seen_time";

    @SuppressLint("SimpleDateFormat")
    UserNotificationManager() {
        gson = new Gson();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        streamService = GetStreamService.getService();
        notificationService = NotificationService.getService();

        listenerList = new ArrayList<>();

        notificationList = new ArrayList<>();

        nextPage = null;
        firstResultId = null;

        loadFinish = false;
        requesting = false;

        SharedPreferences sharedPreferences = Mysplash.getInstance().getSharedPreferences(
                PREFERENCE_TIME, Context.MODE_PRIVATE);
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
        requestStreamListener = new OnRequestStreamListener(true);
        streamService.requestFirstPageStream(requestStreamListener);
    }

    private void requestNextPageNotifications() {
        requesting = true;
        requestStreamListener = new OnRequestStreamListener(false);
        streamService.requestNextPageStream(nextPage, requestStreamListener);
    }

    void checkToRefreshNotification() {
        if (latestRefreshTime < 0) {
            requestPersonalNotifications();
        } else if (System.currentTimeMillis() - latestRefreshTime > 1000 * 60 * 15
                // // TODO: 2017/4/22 alter time.
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
            if (requestStreamListener != null) {
                requestStreamListener.setCanceled();
            }
            if (requestNotificationListener != null) {
                requestNotificationListener.setCanceled();
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
        SharedPreferences.Editor editor = Mysplash.getInstance().getSharedPreferences(
                PREFERENCE_TIME, Context.MODE_PRIVATE).edit();
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

    // on request stream listener.

    private class OnRequestStreamListener implements GetStreamService.OnRequestStreamListener {
        // data
        private boolean refresh;
        private boolean canceled;

        // life cycle.

        OnRequestStreamListener(boolean refresh) {
            this.refresh = refresh;
            this.canceled = false;
        }

        // data.

        void setCanceled() {
            this.canceled = true;
        }

        // interface.

        @Override
        public void onRequestEnrichSucceed(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (canceled) {
                return;
            }
            String json = null;
            NotificationStream stream = null;
            try {
                json = response.body().string();
                stream = gson.fromJson(json, NotificationStream.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!response.isSuccessful() || response.code() / 100 != 2
                    || TextUtils.isEmpty(json) || stream == null) {
                // failed to request.
                requesting = false;
                requestFailed(response.code() + "(" + response.message() + ")");
            } else {
                // request successfully.
                if (TextUtils.isEmpty(stream.next)) {
                    nextPage = null;
                    loadFinish = true;
                } else {
                    nextPage = stream.next;
                }

                if (stream.results != null && stream.results.size() != 0
                        && (!refresh
                        || TextUtils.isEmpty(firstResultId)
                        || !stream.results.get(0).id.equals(firstResultId))) {
                    if (refresh) {
                        firstResultId = stream.results.get(0).id;
                    }
                    requestNotificationListener = new OnRequestNotificationListener(refresh);
                    notificationService.requestNotificationFeed(
                            streamService.getStreamUsablePart(json),
                            requestNotificationListener);
                } else {
                    requesting = false;
                    for (int j = 0; j < listenerList.size(); j ++) {
                        listenerList.get(j).onRequestNotificationSucceed(
                                new ArrayList<NotificationResult>());
                    }
                }
            }
        }

        @Override
        public void onRequestEnrichFailed(Call<ResponseBody> call, Throwable t) {
            requestFailed(t.getMessage());
        }

        private void requestFailed(String msg) {
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onRequestNotificationFailed();
            }
            NotificationHelper.showSnackbar(
                    Mysplash.getInstance().getString(R.string.feedback_get_notification_failed)
                            + "(" + msg + ")");
        }
    }

    // on request notification listener.

    private class OnRequestNotificationListener implements NotificationService.OnRequestNotificationListener {
        // data
        private boolean refresh;
        private boolean canceled;

        // life cycle.

        OnRequestNotificationListener(boolean refresh) {
            this.refresh = refresh;
            this.canceled = false;
        }

        // data.

        void setCanceled() {
            this.canceled = true;
        }

        // interface.

        @Override
        public void onRequestNotificationSucceed(Call<NotificationFeed> call, Response<NotificationFeed> response) {
            if (canceled) {
                return;
            }
            requesting = false;
            if (!response.isSuccessful() || response.code() / 100 != 2
                    || response.body() == null) {
                requestFailed(response.code() + " " + response.message());
            } else {
                // request successfully.
                if (response.body().results != null && response.body().results.size() > 0) {
                    if (refresh
                            && notificationList.size() > 0
                            && !response.body().results.get(0).id.equals(notificationList.get(0).id)) {
                        // already have notifications in the list.
                        // and there are some new feeds in the request result.
                        removeAllNotifications();
                    }
                    addNotification(response.body().results);
                }
                for (int j = 0; j < listenerList.size(); j ++) {
                    listenerList.get(j).onRequestNotificationSucceed(response.body().results);
                }
            }
        }

        @Override
        public void onRequestNotificationFailed(Call<NotificationFeed> call, Throwable t) {
            requestFailed(t.getMessage());
        }

        private void requestFailed(String msg) {
            for (int j = 0; j < listenerList.size(); j ++) {
                listenerList.get(j).onRequestNotificationFailed();
            }
            NotificationHelper.showSnackbar(
                    Mysplash.getInstance().getString(R.string.feedback_get_notification_failed)
                            + "(" + msg + ")");
        }
    }
}
