package com.wangdaye.mysplash.common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Photo menu popup window.
 *
 * This popup window is used to show the menu in
 * {@link com.wangdaye.mysplash.photo.view.activity.PhotoActivity}.
 *
 * */

public class PhotoMenuPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    @BindView(R.id.popup_photo_menu_likeIcon)
    CircularProgressIcon likeIcon;

    @BindView(R.id.popup_photo_menu_collectIcon)
    ImageView collectIcon;

    private OnSelectItemListener listener;

    public static final int ITEM_LIKE = 1;
    public static final int ITEM_COLLECT = 2;
    public static final int ITEM_STATS = 3;
    public static final int ITEM_DOWNLOAD_PAGE = 4;
    public static final int ITEM_STORY_PAGE = 5;
    @IntDef({ITEM_LIKE, ITEM_COLLECT, ITEM_STATS, ITEM_DOWNLOAD_PAGE, ITEM_STORY_PAGE})
    private @interface MenuItemRule {}

    public PhotoMenuPopupWindow(Context c, View anchor, Photo photo) {
        super(c);
        this.initialize(c, anchor, photo);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, Photo photo) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_photo_menu, null);
        setContentView(v);

        ButterKnife.bind(this, v);
        initWidget(photo);
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initWidget(Photo photo) {
        View v = getContentView();

        v.findViewById(R.id.popup_photo_menu_like).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_collect).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_stats).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_downloadPage).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_storyPage).setOnClickListener(this);

        TextView likeTxt = ButterKnife.findById(v, R.id.popup_photo_menu_likeTxt);
        DisplayUtils.setTypeface(v.getContext(), likeTxt);

        TextView collectTxt = ButterKnife.findById(v, R.id.popup_photo_menu_collectTxt);
        DisplayUtils.setTypeface(v.getContext(), collectTxt);

        TextView statsTxt = ButterKnife.findById(v, R.id.popup_photo_menu_statsTxt);
        DisplayUtils.setTypeface(v.getContext(), statsTxt);

        TextView downloadPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_downloadPageTxt);
        DisplayUtils.setTypeface(v.getContext(), downloadPageTxt);

        TextView storyPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_storyPageTxt);
        DisplayUtils.setTypeface(v.getContext(), storyPageTxt);

        likeIcon.setProgressColor(ThemeManager.getTitleColor(v.getContext()));
        if (photo.settingLike) {
            likeIcon.forceSetProgressState();
        } else if (photo.liked_by_user) {
            likeIcon.forceSetResultState(R.drawable.ic_item_heart_red);
        } else if (ThemeManager.getInstance(v.getContext()).isLightTheme()) {
            likeIcon.forceSetResultState(R.drawable.ic_heart_outline_light);
        } else {
            likeIcon.forceSetResultState(R.drawable.ic_heart_outline_dark);
        }

        setCollectIcon(photo);

        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_photo_menu_statsIcon),
                R.drawable.ic_stats_light, R.drawable.ic_stats_dark);
        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon),
                R.drawable.ic_image_light, R.drawable.ic_image_dark);
        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_photo_menu_storyPageIcon),
                R.drawable.ic_book_light, R.drawable.ic_book_dark);
    }

    public void setLikeResult(Context context, Photo photo) {
        if (photo.liked_by_user) {
            likeIcon.setResultState(R.drawable.ic_item_heart_red);
        } else if (ThemeManager.getInstance(context).isLightTheme()) {
            likeIcon.setResultState(R.drawable.ic_heart_outline_light);
        } else {
            likeIcon.setResultState(R.drawable.ic_heart_outline_dark);
        }
    }

    public void forceSetLikeResult(Context context, Photo photo) {
        if (likeIcon.getState() != CircularProgressIcon.STATE_PROGRESS) {
            if (photo.liked_by_user) {
                likeIcon.forceSetResultState(R.drawable.ic_item_heart_red);
            } else if (ThemeManager.getInstance(context).isLightTheme()) {
                likeIcon.forceSetResultState(R.drawable.ic_heart_outline_light);
            } else {
                likeIcon.forceSetResultState(R.drawable.ic_heart_outline_dark);
            }
        }
    }

    public void setCollectIcon(Photo photo) {
        if (photo.current_user_collections.size() == 0) {
            ThemeManager.setImageResource(
                    collectIcon,
                    R.drawable.ic_collect_light, R.drawable.ic_collect_dark);
        } else {
            ThemeManager.setImageResource(
                    collectIcon,
                    R.drawable.ic_collected_light, R.drawable.ic_collected_dark);
        }
    }

    // interface.

    // on select item listener.

    public interface OnSelectItemListener {
        void onSelectItem(@MenuItemRule int id);
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_photo_menu_like:
                if (likeIcon.isUsable() && listener != null) {
                    listener.onSelectItem(ITEM_LIKE);
                }
                break;

            case R.id.popup_photo_menu_collect:
                if (listener != null) {
                    listener.onSelectItem(ITEM_COLLECT);
                }
                break;

            case R.id.popup_photo_menu_stats:
                if (listener != null) {
                    listener.onSelectItem(ITEM_STATS);
                }
                break;

            case R.id.popup_photo_menu_downloadPage:
                if (listener != null) {
                    listener.onSelectItem(ITEM_DOWNLOAD_PAGE);
                }
                break;

            case R.id.popup_photo_menu_storyPage:
                if (listener != null) {
                    listener.onSelectItem(ITEM_STORY_PAGE);
                }
                break;
        }
        dismiss();
    }
}
