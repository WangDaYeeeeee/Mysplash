package com.wangdaye.mysplash.common.i.view;

import com.wangdaye.mysplash.common.data.entity.unsplash.User;

/**
 * User view.
 *
 * A view which can request {@link User} completely and show it.
 * When we request {@link User} in bulk, the {@link User} is incomplete, it doesn't include
 * {@link User#total_likes}, {@link User#total_photos}, {@link User#total_collections},
 * {@link User#followed_by_user}, {@link User#followers_count}, {@link User#following_count}.
 *
 * */

public interface UserView {

    void initRefreshStart();
    void drawUserInfo(User user);

    void followRequestSuccess(boolean follow);
    void followRequestFailed(boolean follow);
}
