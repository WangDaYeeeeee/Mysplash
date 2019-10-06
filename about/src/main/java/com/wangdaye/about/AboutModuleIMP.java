package com.wangdaye.about;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.about.activity.AboutActivity;
import com.wangdaye.about.activity.IntroduceActivity;
import com.wangdaye.component.module.AboutModule;

public class AboutModuleIMP implements AboutModule {

    @Override
    public void startAboutActivity(Activity a) {
        ARouter.getInstance()
                .build(AboutActivity.ABOUT_ACTIVITY)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    @Override
    public void checkAndStartIntroduce(Activity a) {
        IntroduceActivity.checkAndStartIntroduce(a);
    }

    @Override
    public void watchAllIntroduce(Activity a) {
        IntroduceActivity.watchAllIntroduce(a);
    }
}
