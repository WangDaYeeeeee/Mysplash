package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.TranslatorObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translator holder.
 * */

public class TranslatorHolder extends AboutAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private CircleImageView avatar;
    private ImageView flag;
    private TextView title;
    private TextView subtitle;

    // data
    private String url;

    /** <br> life cycle. */

    public TranslatorHolder(View itemView) {
        super(itemView);

        itemView.findViewById(R.id.item_about_translator_container).setOnClickListener(this);

        this.avatar = (CircleImageView) itemView.findViewById(R.id.item_about_translator_avatar);
        this.title = (TextView) itemView.findViewById(R.id.item_about_translator_title);
        this.flag = (ImageView) itemView.findViewById(R.id.item_about_translator_flag);

        this.subtitle = (TextView) itemView.findViewById(R.id.item_about_translator_subtitle);
        DisplayUtils.setTypeface(itemView.getContext(), subtitle);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        TranslatorObject object = (TranslatorObject) model;

        ImageHelper.loadAvatar(a, avatar, object.avatarUrl, null);
        ImageHelper.loadIcon(a, flag, object.flagId);

        title.setText(object.title);
        subtitle.setText(object.subtitle);
        url = object.subtitle;
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
        ImageHelper.releaseImageView(flag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_translator_container:
                String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                Pattern regex = Pattern.compile(check);
                Matcher matcher = regex.matcher(url);
                IntentHelper.startWebActivity(
                        v.getContext(),
                        matcher.matches() ? "mailto:" + url : url);
                break;
        }
    }
}
