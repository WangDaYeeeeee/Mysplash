package com.wangdaye.mysplash.common.i.view;

/**
 * Me manage view.
 *
 * A view can manage {@link com.wangdaye.mysplash.common.data.entity.unsplash.Me} and user's own
 * {@link com.wangdaye.mysplash.common.data.entity.unsplash.User} information.
 *
 * */

public interface MeManageView {

    void drawMeAvatar();
    void drawMeTitle();
    void drawMeSubtitle();
    void drawMeButton();
}
