package com.wangdaye.mysplash.login.model;

import com.wangdaye.mysplash._common.data.service.AuthorizeService;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.LoginModel;

/**
 * Login object.
 * */

public class LoginObject
        implements LoginModel {
    // data
    private AuthorizeService authService;
    private UserService userService;

    /** <br> life cycle. */

    public LoginObject() {
        authService = AuthorizeService.getService();
        userService = UserService.getService().buildClient();
    }

    /** <br> model. */

    @Override
    public AuthorizeService getAuthService() {
        return authService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }
}
