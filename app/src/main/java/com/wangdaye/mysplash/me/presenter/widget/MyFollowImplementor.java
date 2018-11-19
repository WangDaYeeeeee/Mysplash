package com.wangdaye.mysplash.me.presenter.widget;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.UserService;
import com.wangdaye.mysplash.common.i.model.MyFollowModel;
import com.wangdaye.mysplash.common.i.presenter.MyFollowPresenter;
import com.wangdaye.mysplash.common.i.view.MyFollowView;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.MyFollowAdapter;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.me.model.widget.MyFollowObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * My follow implementor.
 * */

public class MyFollowImplementor
        implements MyFollowPresenter {

    private MyFollowModel model;
    private MyFollowView view;

    private OnRequestUsersListener listener;

    public MyFollowImplementor(MyFollowModel model, MyFollowView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestMyFollow(Context c, int page, boolean refresh) {
        if (!model.isRefreshing() && !model.isLoading()) {
            if (refresh) {
                model.setRefreshing(true);
            } else {
                model.setLoading(true);
            }
            switch (model.getFollowType()) {
                case MyFollowObject.FOLLOW_TYPE_FOLLOWERS:
                    page = Math.max(1, refresh ? 1: page + 1);
                    listener = new OnRequestUsersListener(c, page, refresh);
                    model.getService()
                            .requestFollowers(
                                    AuthManager.getInstance().getUsername(),
                                    page,
                                    Mysplash.DEFAULT_PER_PAGE,
                                    listener);
                    break;

                case MyFollowObject.FOLLOW_TYPE_FOLLOWING:
                    page = Math.max(1, refresh ? 1: page + 1);
                    listener = new OnRequestUsersListener(c, page, refresh);
                    model.getService()
                            .requestFollowing(
                                    AuthManager.getInstance().getUsername(),
                                    page,
                                    Mysplash.DEFAULT_PER_PAGE,
                                    listener);
                    break;
            }
        }
    }

    @Override
    public void cancelRequest() {
        if (listener != null) {
            listener.cancel();
        }
        model.getService().cancel();
        model.setRefreshing(false);
        model.setLoading(false);
    }

    @Override
    public void refreshNew(Context c, boolean notify) {
        if (notify) {
            view.setRefreshingFollow(true);
        }
        requestMyFollow(c, model.getUsersPage(), true);
    }

    @Override
    public void loadMore(Context c, boolean notify) {
        if (notify) {
            view.setLoadingFollow(true);
        }
        requestMyFollow(c, model.getUsersPage(), false);
    }

    @Override
    public void initRefresh(Context c) {
        cancelRequest();
        refreshNew(c, false);
        view.initRefreshStart();
    }

    @Override
    public boolean canLoadMore() {
        return !model.isRefreshing() && !model.isLoading() && !model.isOver();
    }

    @Override
    public boolean isRefreshing() {
        return model.isRefreshing();
    }

    @Override
    public boolean isLoading() {
        return model.isLoading();
    }

    @Override
    public int getDeltaValue() {
        return model.getDeltaValue();
    }

    @Override
    public void setDeltaValue(int delta) {
        model.setDeltaValue(delta);
    }

    @Override
    public void setActivityForAdapter(MysplashActivity a) {
        model.getAdapter().setActivity(a);
    }

    @Override
    public MyFollowAdapter getAdapter() {
        return model.getAdapter();
    }

    // interface.

    private class OnRequestUsersListener implements UserService.OnRequestUsersListener {

        private Context c;
        private int page;
        private boolean refresh;
        private boolean canceled;

        OnRequestUsersListener(Context c, int page, boolean refresh) {
            this.c = c;
            this.page = page;
            this.refresh = refresh;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestUsersSuccess(Call<List<User>> call, Response<List<User>> response) {
            if (canceled) {
                return;
            }

            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                model.getAdapter().clearItem();
                model.setOver(false);
                view.setRefreshingFollow(false);
                view.setPermitLoading(true);
            } else {
                view.setLoadingFollow(false);
            }
            if (response.isSuccessful()) {
                model.setUsersPage(page);
                for (int i = 0; i < response.body().size(); i ++) {
                    model.getAdapter().insertItem(
                            response.body().get(i),
                            model.getAdapter().getItemCount());
                }
                if (response.body().size() < Mysplash.DEFAULT_PER_PAGE) {
                    model.setOver(true);
                    view.setPermitLoading(false);
                }
                view.requestMyFollowSuccess();
            } else {
                view.requestMyFollowFailed(c.getString(R.string.feedback_load_nothing_tv));
            }
        }

        @Override
        public void onRequestUsersFailed(Call<List<User>> call, Throwable t) {
            if (canceled) {
                return;
            }
            model.setRefreshing(false);
            model.setLoading(false);
            if (refresh) {
                view.setRefreshingFollow(false);
            } else {
                view.setLoadingFollow(false);
            }
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_load_failed_toast)
                            + " (" + t.getMessage() + ")");
            view.requestMyFollowFailed(c.getString(R.string.feedback_load_nothing_tv));
        }
    }
}
