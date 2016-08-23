package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.service.AuthorizeService;
import com.wangdaye.mysplash._common.data.service.UserService;

/**
 * Login model.
 * */

public interface LoginModel {

    AuthorizeService getAuthService();
    UserService getUserService();
}
