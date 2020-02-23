package com.wangdaye.common.network;

import android.content.Context;
import android.text.TextUtils;

import com.wangdaye.common.utils.manager.CustomApiManager;

public class UrlCollection {

    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String STREAM_API_BASE_URL = "https://api.getstream.io/";
    public static final String UNSPLASH_FOLLOWING_FEED_URL = "feeds/following";
    public static final String UNSPLASH_NODE_API_URL = "";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_SUBMIT_URL = "https://unsplash.com/submit";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";
    public static final String UNSPLASH_IMAGE_HOST = "images.unsplash.com";
    public static final String UNSPLASH_CDN_HOST = "unsplash.nesnode.com";

    public static boolean hasNode() {
        return !TextUtils.isEmpty(UNSPLASH_NODE_API_URL);
    }

    public static String getLoginUrl(Context c) {
        return UNSPLASH_URL + "oauth/authorize"
                + "?client_id=" + CustomApiManager.getInstance(c).getAppId(c, true)
                + "&redirect_uri=" + "mysplash%3A%2F%2F" + UNSPLASH_LOGIN_CALLBACK
                + "&response_type=" + "code"
                + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes"
                + "+write_followers+read_collections+write_collections";
    }
}
