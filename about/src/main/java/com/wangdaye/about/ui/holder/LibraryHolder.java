package com.wangdaye.about.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.about.R2;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.model.LibraryObject;
import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.utils.helper.RoutingHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Library holder.
 *
 * This ViewHolder is used to show library for {@link AboutAdapter}.
 *
 * */

public class LibraryHolder extends AboutAdapter.ViewHolder {

    @OnClick(R2.id.item_about_library_container) void clickItem() {
        RoutingHelper.startWebActivity(itemView.getContext(), uri);
    }

    @BindView(R2.id.item_about_library_title) TextView title;
    @BindView(R2.id.item_about_library_content) TextView content;

    private String uri;

    public LibraryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        LibraryObject object = (LibraryObject) model;

        title.setText(object.title);
        content.setText(object.subtitle);
        uri = object.uri;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
