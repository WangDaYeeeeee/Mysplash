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
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.MuzeiOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.ButterKnife;

/**
 * Collection menu popup window.
 *
 * This popup window is used to show the menu in
 * {@link com.wangdaye.mysplash.collection.view.activity.CollectionActivity}.
 *
 * */

public class CollectionMenuPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnSelectItemListener listener;

    public static final int ITEM_EDIT = 1;
    public static final int ITEM_DOWNLOAD = 2;
    public static final int ITEM_SET_AS_SOURCE = 3;
    @IntDef({ITEM_EDIT, ITEM_DOWNLOAD, ITEM_SET_AS_SOURCE})
    private @interface MenuItemRule {}

    public CollectionMenuPopupWindow(Context c, View anchor, Collection collection) {
        super(c);
        this.initialize(c, anchor, collection);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, Collection collection) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_collection_menu, null);
        setContentView(v);

        initWidget(c, collection);
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initWidget(Context c, Collection collection) {
        View v = getContentView();

        v.findViewById(R.id.popup_collection_menu_edit).setOnClickListener(this);
        if (!isMyCollection(collection)) {
            v.findViewById(R.id.popup_collection_menu_edit).setVisibility(View.GONE);
        }

        v.findViewById(R.id.popup_collection_menu_download).setOnClickListener(this);
        if (!isCurate(collection)) {
            v.findViewById(R.id.popup_collection_menu_download).setVisibility(View.GONE);
        }

        v.findViewById(R.id.popup_collection_menu_setAsSource).setOnClickListener(this);
        if (!MuzeiOptionManager.isInstalledMuzei(c)) {
            v.findViewById(R.id.popup_collection_menu_setAsSource).setVisibility(View.GONE);
        }

        TextView editTxt = ButterKnife.findById(v, R.id.popup_collection_menu_editTxt);
        DisplayUtils.setTypeface(v.getContext(), editTxt);

        TextView downloadTxt = ButterKnife.findById(v, R.id.popup_collection_menu_downloadTxt);
        DisplayUtils.setTypeface(v.getContext(), downloadTxt);

        TextView setAsSourceTxt = ButterKnife.findById(v, R.id.popup_collection_menu_setAsSourceTxt);
        DisplayUtils.setTypeface(v.getContext(), setAsSourceTxt);

        if (ThemeManager.getInstance(v.getContext()).isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_collection_menu_editIcon)).setImageResource(R.drawable.ic_pencil_light);
            ((ImageView) v.findViewById(R.id.popup_collection_menu_downloadIcon)).setImageResource(R.drawable.ic_download_light);
            ((ImageView) v.findViewById(R.id.popup_collection_menu_setAsSourceIcon)).setImageResource(R.drawable.ic_plus_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_collection_menu_editIcon)).setImageResource(R.drawable.ic_pencil_dark);
            ((ImageView) v.findViewById(R.id.popup_collection_menu_downloadIcon)).setImageResource(R.drawable.ic_download_dark);
            ((ImageView) v.findViewById(R.id.popup_collection_menu_setAsSourceIcon)).setImageResource(R.drawable.ic_plus_dark);
        }
    }

    public static boolean isUsable(Context c, Collection collection) {
        return isMyCollection(collection)
                || isCurate(collection)
                || MuzeiOptionManager.isInstalledMuzei(c);
    }

    private static boolean isMyCollection(Collection collection) {
        return AuthManager.getInstance().getUsername() != null
                && AuthManager.getInstance().getUsername().equals(collection.user.username);
    }

    private static boolean isCurate(Collection collection) {
        return collection.curated;
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
            case R.id.popup_collection_menu_edit:
                if (listener != null) {
                    listener.onSelectItem(ITEM_EDIT);
                }
                break;

            case R.id.popup_collection_menu_download:
                if (listener != null) {
                    listener.onSelectItem(ITEM_DOWNLOAD);
                }
                break;

            case R.id.popup_collection_menu_setAsSource:
                if (listener != null) {
                    listener.onSelectItem(ITEM_SET_AS_SOURCE);
                }
                break;
        }
        dismiss();
    }
}
