package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.TranslatorObject;

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
    private String email;

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

        DisplayUtils.loadAvatar(a, avatar, object.avatarUrl);
        Glide.with(a)
                .load(object.flagId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(flag);
        title.setText(object.title);
        subtitle.setText(object.subtitle);
        email = object.subtitle;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_translator_container:
                IntentHelper.startWebActivity(v.getContext(), "mailto:" + this.email);
                break;
        }
    }
}
