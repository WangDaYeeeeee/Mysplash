package com.wangdaye.mysplash.collection.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.muzei.MuzeiOptionManager;

/**
 * Collection menu popup window.
 *
 * This popup window is used to show the menu in
 * {@link CollectionActivity}.
 *
 * */

public class CollectionMenuPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnSelectItemListener listener;

    public static final int ITEM_EDIT = 1;
    public static final int ITEM_DOWNLOAD = 2;
    public static final int ITEM_SET_AS_SOURCE = 3;
    public static final int ITEM_REMOVE_SOURCE = 4;
    @IntDef({ITEM_EDIT, ITEM_DOWNLOAD, ITEM_SET_AS_SOURCE, ITEM_REMOVE_SOURCE})
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
        v.findViewById(R.id.popup_collection_menu_removeSource).setOnClickListener(this);
        if (!MuzeiOptionManager.isInstalledMuzei(c)
                || !MuzeiOptionManager.getInstance(c).getSource().equals("collection")) {
            v.findViewById(R.id.popup_collection_menu_setAsSource).setVisibility(View.GONE);
            v.findViewById(R.id.popup_collection_menu_removeSource).setVisibility(View.GONE);
        } else {
            WallpaperSource source = DatabaseHelper.getInstance(c).readWallpaperSource(collection.id);
            if (source != null) {
                v.findViewById(R.id.popup_collection_menu_setAsSource).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.popup_collection_menu_removeSource).setVisibility(View.GONE);
            }
        }
    }

    public static boolean isUsable(Context c, Collection collection) {
        return isMyCollection(collection)
                || isCurate(collection)
                || (
                        MuzeiOptionManager.isInstalledMuzei(c)
                                && MuzeiOptionManager.getInstance(c).getSource().equals("collection")
                );
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

            case R.id.popup_collection_menu_removeSource:
                if (listener != null) {
                    listener.onSelectItem(ITEM_REMOVE_SOURCE);
                }
                break;
        }
        dismiss();
    }
}
