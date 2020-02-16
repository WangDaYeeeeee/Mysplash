package com.wangdaye.about.ui.holder;

import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.about.R2;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.about.model.TranslatorObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Translator holder.
 *
 * This ViewHolder class is used to show translator for {@link AboutAdapter}.
 *
 * */

public class TranslatorHolder extends AboutAdapter.ViewHolder {

    @OnClick(R2.id.item_about_translator_container) void clickItem() {
        String check = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(url);
        RoutingHelper.startWebActivity(
                itemView.getContext(),
                matcher.matches() ? "mailto:" + url : url);
    }

    @BindView(R2.id.item_about_translator_avatar) CircularImageView avatar;
    @BindView(R2.id.item_about_translator_flag) AppCompatImageView flag;
    @BindView(R2.id.item_about_translator_title) TextView title;
    @BindView(R2.id.item_about_translator_subtitle) TextView subtitle;

    private String url;

    public TranslatorHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        TranslatorObject object = (TranslatorObject) model;

        ImageHelper.loadImage(a, avatar, object.avatarUrl);
        ImageHelper.loadImage(a, flag, object.flagId);

        title.setText(object.title);
        subtitle.setText(object.subtitle);
        url = object.subtitle;
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
        ImageHelper.releaseImageView(flag);
    }
}
