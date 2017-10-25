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

    private OnSelectItemListener listener;

    public static final int ITEM_DOWNLOAD_PAGE = 1;
    public static final int ITEM_STORY_PAGE = 2;
    @IntDef({ITEM_DOWNLOAD_PAGE, ITEM_STORY_PAGE})
    private @interface MenuItemRule {}

    public PhotoMenuPopupWindow(Context c, View anchor) {
        super(c);
        this.initialize(c, anchor);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_photo_menu, null);
        setContentView(v);

        ButterKnife.bind(this, v);
        initWidget();
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_photo_menu_downloadPage).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_storyPage).setOnClickListener(this);

        TextView downloadPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_downloadPageTxt);
        DisplayUtils.setTypeface(v.getContext(), downloadPageTxt);

        TextView storyPageTxt = ButterKnife.findById(v, R.id.popup_photo_menu_storyPageTxt);
        DisplayUtils.setTypeface(v.getContext(), storyPageTxt);

        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon),
                R.drawable.ic_image_light, R.drawable.ic_image_dark);
        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_photo_menu_storyPageIcon),
                R.drawable.ic_book_light, R.drawable.ic_book_dark);
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
