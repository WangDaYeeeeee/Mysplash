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
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

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
    // widget
    private OnSelectItemListener listener;

    // data
    public static final int ITEM_STATS = 1;
    public static final int ITEM_DOWNLOAD_PAGE = 2;
    public static final int ITEM_STORY_PAGE = 3;
    @IntDef({ITEM_STATS, ITEM_DOWNLOAD_PAGE, ITEM_STORY_PAGE})
    private @interface MenuItemRule {}

    /** <br> life cycle. */

    public PhotoMenuPopupWindow(Context c, View anchor) {
        super(c);
        this.initialize(c, anchor);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_photo_menu, null);
        setContentView(v);

        initWidget();
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_photo_menu_stats).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_downloadPage).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_storyPage).setOnClickListener(this);

        TextView statsTxt = ButterKnife.findById(v, R.id.popup_photo_menu_statsTxt);
        DisplayUtils.setTypeface(v.getContext(), statsTxt);

        TextView downloadPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_downloadPageTxt);
        DisplayUtils.setTypeface(v.getContext(), downloadPageTxt);

        TextView storyPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_storyPageTxt);
        DisplayUtils.setTypeface(v.getContext(), storyPageTxt);

        if (ThemeManager.getInstance(v.getContext()).isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_photo_menu_statsIcon)).setImageResource(R.drawable.ic_stats_light);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon)).setImageResource(R.drawable.ic_image_light);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_storyPageIcon)).setImageResource(R.drawable.ic_book_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_photo_menu_statsIcon)).setImageResource(R.drawable.ic_stats_dark);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon)).setImageResource(R.drawable.ic_image_dark);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_storyPageIcon)).setImageResource(R.drawable.ic_book_dark);
        }
    }

    /** <br> interface. */

    public interface OnSelectItemListener {
        void onSelectItem(@MenuItemRule int id);
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
