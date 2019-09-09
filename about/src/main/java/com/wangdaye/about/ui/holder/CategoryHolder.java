package com.wangdaye.about.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.about.R2;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.model.CategoryObject;
import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.activity.MysplashActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Category holder.
 *
 * This ViewHolder is used to show category for {@link AboutAdapter}.
 *
 * */

public class CategoryHolder extends AboutAdapter.ViewHolder {

    @BindView(R2.id.item_about_category_title) TextView text;

    public CategoryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        text.setText(((CategoryObject) model).category);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
