package com.wangdaye.mysplash.about.view.holder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Header holder.
 * */

public class HeaderHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public ImageView appIcon;

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

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_header_backButton:
                Mysplash.getInstance().getTopActivity().finish();
                Mysplash.getInstance().getTopActivity().overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;

            case R.id.item_about_header_unsplashContainer:
                Uri uri = Uri.parse(Mysplash.UNSPLASH_URL);
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;
        }
    }
}
