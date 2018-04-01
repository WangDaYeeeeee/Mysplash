package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.AboutModel;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash.common.ui.dialog.TotalDialog;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Header holder.
 *
 * This ViewHolder class is used to show header for {@link AboutAdapter}.
 *
 * */

public class HeaderHolder extends AboutAdapter.ViewHolder {

    @BindView(R.id.item_about_header_appIcon)
    ImageView appIcon;

    public HeaderHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        ImageButton backBtn = ButterKnife.findById(itemView, R.id.item_about_header_backButton);
        ThemeManager.setImageResource(
                backBtn, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);

        TextView version = itemView.findViewById(R.id.item_about_header_versionCode);
        DisplayUtils.setTypeface(itemView.getContext(), version);

        TextView unsplashTitle = itemView.findViewById(R.id.item_about_header_unsplashTitle);
        unsplashTitle.setText(itemView.getContext().getString(R.string.unsplash));
        DisplayUtils.setTypeface(itemView.getContext(), unsplashTitle);

        TextView unsplashContent = itemView.findViewById(R.id.item_about_header_unsplashContent);
        unsplashContent.setText(itemView.getContext().getString(R.string.about_unsplash));
        DisplayUtils.setTypeface(itemView.getContext(), unsplashContent);
    }

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        ImageHelper.loadResourceImage(a, appIcon, R.drawable.ic_launcher);
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(appIcon);
    }

    @OnClick(R.id.item_about_header_backButton) void close() {
        MysplashActivity activity = Mysplash.getInstance()
                .getTopActivity();
        if (activity != null) {
            activity.finishSelf(true);
        }
    }

    @OnClick(R.id.item_about_header_unsplashContainer) void checkTotal() {
        MysplashActivity activity = Mysplash.getInstance()
                .getTopActivity();
        if (activity != null) {
            TotalDialog dialog = new TotalDialog();
            dialog.show(activity.getFragmentManager(), null);
        }
    }
}
