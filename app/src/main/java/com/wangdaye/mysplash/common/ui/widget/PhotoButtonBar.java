package com.wangdaye.mysplash.common.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Photo button bar.
 * */

public class PhotoButtonBar extends RelativeLayout {

    @BindView(R.id.container_photo_button_bar)
    LinearLayout container;

    @BindView(R.id.container_photo_button_bar_likeButton)
    CircularProgressIcon likeButton;

    @BindView(R.id.container_photo_button_bar_collectButton)
    ImageButton collectButton;

    @BindView(R.id.container_photo_button_bar_downloadButton)
    CircularProgressIcon downloadButton;

    @BindView(R.id.container_photo_button_bar_likeTxt)
    TextView likeText;

    @BindView(R.id.container_photo_button_bar_collectTxt)
    TextView collectText;

    @BindView(R.id.container_photo_button_bar_downloadTxt)
    TextView downloadText;

    private int likeIconId;
    private int collectIconId;
    private int progress;

    private OnClickButtonListener listener;

    public PhotoButtonBar(Context context) {
        super(context);
        this.initialize();
    }

    public PhotoButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public PhotoButtonBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        addView(
                LayoutInflater.from(getContext())
                        .inflate(R.layout.container_photo_button_bar, null));

        ButterKnife.bind(this, this);
        initData();
        initWidget();
    }

    private void initData() {
        likeIconId = getLikeIcon(false);
        collectIconId = getCollectIcon(false);
        progress = -1;
    }

    private void initWidget() {
        likeButton.forceSetResultState(getLikeIcon(false));
        likeButton.setProgressColor(ThemeManager.getTitleColor(getContext()));

        collectButton.setImageResource(getCollectIcon(false));

        downloadButton.forceSetResultState(getDownloadIcon());
        downloadButton.setProgressColor(ThemeManager.getTitleColor(getContext()));

        setLikeText(false);
        setCollectText(false);
        downloadText.setText(getContext().getString(R.string.download).toUpperCase());
    }

    // draw.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LayoutParams params = (LayoutParams) container.getLayoutParams();
        if (DisplayUtils.isTabletDevice(getContext())
                || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.width = getResources()
                    .getDimensionPixelSize(R.dimen.tablet_download_button_bar_width);
        } else {
            params.width = getMeasuredWidth();
        }
        params.addRule(CENTER_IN_PARENT);
        container.setLayoutParams(params);
    }

    // control.

    public void setState(Photo photo) {
        setLikeState(photo);
        setCollectState(photo);
    }

    public void setLikeState(Photo photo) {
        if (photo != null) {
            if (photo.settingLike) {
                likeButton.setProgressState();
            } else {
                int newIconId = getLikeIcon(photo.liked_by_user);
                if (likeButton.getState() == CircularProgressIcon.STATE_PROGRESS) {
                    likeIconId = newIconId;
                    likeButton.setResultState(newIconId);
                    setLikeText(photo.liked_by_user);
                } else if (likeIconId != newIconId) {
                    likeIconId = newIconId;
                    likeButton.forceSetResultState(newIconId);
                    setLikeText(photo.liked_by_user);
                }
            }
        }
    }

    private void setLikeText(boolean liked) {
        if (liked) {
            likeText.setText(getContext().getString(R.string.liked).toUpperCase());
        } else {
            likeText.setText(getContext().getString(R.string.like).toUpperCase());
        }
    }

    public void setCollectState(Photo photo) {
        if (photo != null) {
            boolean collected = photo.current_user_collections != null
                    && photo.current_user_collections.size() != 0;
            int newIconId = getCollectIcon(collected);
            if (newIconId != collectIconId) {
                collectIconId = newIconId;
                collectButton.setImageResource(
                        getCollectIcon(
                                photo.current_user_collections != null
                                        && photo.current_user_collections.size() != 0));
                setCollectText(collected);
            }
        }
    }

    private void setCollectText(boolean collected) {
        if (collected) {
            collectText.setText(getContext().getString(R.string.collected).toUpperCase());
        } else {
            collectText.setText(getContext().getString(R.string.collect).toUpperCase());
        }
    }

    @SuppressLint("SetTextI18n")
    public void setDownloadState(boolean downloading, int progress) {
        if (downloading && downloadButton.getState() == CircularProgressIcon.STATE_RESULT) {
            this.progress = -1;
            downloadButton.setProgressState();
        } else if (!downloading && downloadButton.getState() == CircularProgressIcon.STATE_PROGRESS) {
            this.progress = -1;
            downloadButton.setResultState(getDownloadIcon());
            downloadText.setText(getContext().getString(R.string.download).toUpperCase());
        }

        if (downloadButton.getState() == CircularProgressIcon.STATE_PROGRESS
                && progress != this.progress
                && progress >= 0) {
            this.progress = progress;
            downloadText.setText(progress + " %");
        }
    }

    private int getLikeIcon(boolean liked) {
        boolean light = ThemeManager.getInstance(getContext()).isLightTheme();
        if (liked) {
            return R.drawable.ic_item_heart_red;
        } else {
            return light ? R.drawable.ic_heart_outline_light : R.drawable.ic_heart_outline_dark;
        }
    }

    private int getCollectIcon(boolean collected) {
        boolean light = ThemeManager.getInstance(getContext()).isLightTheme();
        if (collected) {
            return light ? R.drawable.ic_collected_large_light : R.drawable.ic_collected_large_dark;
        } else {
            return light ? R.drawable.ic_collect_large_light : R.drawable.ic_collect_large_dark;
        }
    }

    private int getDownloadIcon() {
        return ThemeManager.getInstance(getContext()).isLightTheme()
                ? R.drawable.ic_download_png_light : R.drawable.ic_download_png_dark;
    }

    // interface.

    public interface OnClickButtonListener {
        void onLikeButtonClicked();
        void onCollectButtonClicked();
        void onDownloadButtonClicked();
        void onDownloadButtonLongClicked();
    }

    public void setOnClickButtonListener(OnClickButtonListener l) {
        listener = l;
    }

    @OnClick(R.id.container_photo_button_bar_likeButton)
    void likePhoto() {
        if (listener != null && likeButton.getState() == CircularProgressIcon.STATE_RESULT) {
            listener.onLikeButtonClicked();
        }
    }

    @OnClick(R.id.container_photo_button_bar_collectButton)
    void collectPhoto() {
        if (listener != null) {
            listener.onCollectButtonClicked();
        }
    }

    @OnClick(R.id.container_photo_button_bar_downloadButton)
    void downloadPhoto() {
        if (listener != null && downloadButton.getState() == CircularProgressIcon.STATE_RESULT) {
            listener.onDownloadButtonClicked();
        }
    }

    @OnLongClick(R.id.container_photo_button_bar_downloadButton)
    boolean downloadPhotoDirectly() {
        if (listener != null && downloadButton.getState() == CircularProgressIcon.STATE_RESULT) {
            listener.onDownloadButtonLongClicked();
        }
        return true;
    }
}
