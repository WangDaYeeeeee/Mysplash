package com.wangdaye.mysplash.common.utils.presenter;

import com.wangdaye.mysplash.common.network.callback.NoBodyCallback;
import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.me.ui.MyFollowAdapter;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class FollowOrCancelFollowPresenter {

    private FollowService service;

    @Inject
    public FollowOrCancelFollowPresenter(FollowService service) {
        this.service = service;
    }

    public void followOrCancelFollowUser(MyFollowAdapter adapter,
                                         MyFollowAdapter.MyFollowUser myFollowUser, boolean setToFollow) {
        NoBodyCallback<ResponseBody> callback = new NoBodyCallback<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody responseBody) {
                myFollowUser.requesting = false;
                myFollowUser.user.followed_by_user = setToFollow;
                myFollowUser.user.followers_count += setToFollow ? 1 : -1;
                adapter.updateItem(myFollowUser.user, true, false);
            }

            @Override
            public void onFailed() {
                myFollowUser.requesting = false;
                adapter.updateItem(myFollowUser.user, true, false);
            }
        };

        if (setToFollow) {
            service.followUser(myFollowUser.user.username, callback);
        } else {
            service.cancelFollowUser(myFollowUser.user.username, callback);
        }
    }
}
