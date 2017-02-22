package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.ui.dialog.TotalDialog;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Header holder.
 * */

public class HeaderHolder extends AboutAdapter.ViewHolder
        implements View.OnClickListener {
    private ImageView appIcon;

    /** <br> life cycle. */

    public HeaderHolder(View itemView) {
        super(itemView);

        ImageButton backBtn = (ImageButton) itemView.findViewById(R.id.item_about_header_backButton);
        if (Mysplash.getInstance().isLightTheme()) {
            backBtn.setImageResource(R.drawable.ic_toolbar_back_light);
        } else {
            backBtn.setImageResource(R.drawable.ic_toolbar_back_dark);
        }
        backBtn.setOnClickListener(this);

        this.appIcon = (ImageView) itemView.findViewById(R.id.item_about_header_appIcon);

        TextView version = (TextView) itemView.findViewById(R.id.item_about_header_versionCode);
        DisplayUtils.setTypeface(itemView.getContext(), version);

        itemView.findViewById(R.id.item_about_header_unsplashContainer).setOnClickListener(this);

        TextView unsplashTitle = (TextView) itemView.findViewById(R.id.item_about_header_unsplashTitle);
        unsplashTitle.setText(itemView.getContext().getString(R.string.unsplash));
        DisplayUtils.setTypeface(itemView.getContext(), unsplashTitle);

        TextView unsplashContent = (TextView) itemView.findViewById(R.id.item_about_header_unsplashContent);
        unsplashContent.setText(itemView.getContext().getString(R.string.about_unsplash));
        DisplayUtils.setTypeface(itemView.getContext(), unsplashContent);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        Glide.with(a)
                .load(R.drawable.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(appIcon);
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_header_backButton:
                Mysplash.getInstance()
                        .getTopActivity()
                        .finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;

            case R.id.item_about_header_unsplashContainer:
                TotalDialog dialog = new TotalDialog();
                dialog.show(Mysplash.getInstance().getTopActivity().getFragmentManager(), null);
                break;
        }
    }
}
