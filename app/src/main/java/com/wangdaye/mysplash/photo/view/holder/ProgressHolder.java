package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;
import android.widget.Button;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

/**
 * Progress holder.
 * */

public class ProgressHolder extends PhotoInfoAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private CircularProgressView progress;
    private Button button;

    // data
    private boolean failed;
    public static final int TYPE_PROGRESS = 3;

    /** <br> life cycle. */

    public ProgressHolder(View itemView) {
        super(itemView);

        this.progress = (CircularProgressView) itemView.findViewById(R.id.item_photo_progress_progressView);
        this.button = (Button) itemView.findViewById(R.id.item_photo_progress_button);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        if (a instanceof PhotoActivity) {
            failed = ((PhotoActivity) a).isLoadFailed();
        }
        if (failed) {
            progress.setAlpha(0f);
            button.setAlpha(1f);
            button.setVisibility(View.VISIBLE);
        } else {
            progress.setAlpha(1f);
            button.setAlpha(0f);
            button.setVisibility(View.GONE);
        }
    }

    public void setFailedState() {
        if (!failed) {
            failed = true;
            AnimUtils.animShow(button, 150, button.getAlpha(), 1f);
            AnimUtils.animHide(progress, 150, progress.getAlpha(), 0f, false);
        }
    }

    private void setProgressState() {
        if (failed) {
            failed = false;
            AnimUtils.animShow(progress, 150, progress.getAlpha(), 1f);
            AnimUtils.animHide(button, 150, button.getAlpha(), 0f, true);
        }
    }

    /** <br> interface. */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_photo_progress_button:
                setProgressState();
                ((PhotoActivity) Mysplash.getInstance().getTopActivity()).initRefresh();
                break;
        }
    }
}
