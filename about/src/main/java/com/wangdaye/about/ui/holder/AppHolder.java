package com.wangdaye.about.ui.holder;

import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.about.R2;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.utils.helper.DonateHelper;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.about.model.AppObject;
import com.wangdaye.component.ComponentFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * App holder.
 *
 * This ViewHolder class is used to show app information for {@link AboutAdapter}.
 *
 * */

public class AppHolder extends AboutAdapter.ViewHolder {

    @BindView(R2.id.item_about_app_icon) AppCompatImageView icon;
    @BindView(R2.id.item_about_app_title) TextView text;

    private int id;

    public AppHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindView(MysplashActivity a, AboutModel model) {
        AppObject object = (AppObject) model;

        icon.setImageResource(object.iconId);
        text.setText(object.text);
        id = object.id;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    @OnClick(R2.id.item_about_app_container) void clickItem() {
        MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
        if (activity == null) {
            return;
        }

        switch (id) {
            case 1:
                ComponentFactory.getAboutModule().watchAllIntroduce(activity);
                break;

            case 2:
                RoutingHelper.startWebActivity(activity, "https://github.com/WangDaYeeeeee");
                break;

            case 3:
                RoutingHelper.startWebActivity(activity, "mailto:wangdayeeeeee@gmail.com");
                break;

            case 4:
                RoutingHelper.startWebActivity(activity, "https://github.com/WangDaYeeeeee/MySplash");
                break;

            case 5:
                DonateHelper.donateByAlipay(activity);
                break;
        }
    }
}
