package com.wangdaye.mysplash.common.ui.adapter.tag;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

public class TagItemEventHelper implements TagItemEventCallback {

    private MysplashActivity activity;

    public TagItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClicked(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            IntentHelper.startSearchActivity(activity, tag);
        }
    }
}
