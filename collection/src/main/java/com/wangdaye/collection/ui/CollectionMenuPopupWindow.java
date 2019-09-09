package com.wangdaye.collection.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.collection.CollectionActivity;
import com.wangdaye.collection.R;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.MuzeiService;

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

        MuzeiService muzeiService = ComponentFactory.getMuzeiService();
        v.findViewById(R.id.popup_collection_menu_setAsSource).setOnClickListener(this);
        v.findViewById(R.id.popup_collection_menu_removeSource).setOnClickListener(this);
        if (!muzeiService.isMuzeiInstalled(c)
                || !muzeiService.getSource(c).equals(MuzeiService.SOURCE_COLLECTION)) {
            v.findViewById(R.id.popup_collection_menu_setAsSource).setVisibility(View.GONE);
            v.findViewById(R.id.popup_collection_menu_removeSource).setVisibility(View.GONE);
        } else {
            if (ComponentFactory.getMuzeiService().getMuzeiWallpaperSource(c, collection) != null) {
                v.findViewById(R.id.popup_collection_menu_setAsSource).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.popup_collection_menu_removeSource).setVisibility(View.GONE);
            }
        }
    }

    public static boolean isUsable(Context c, Collection collection) {
        MuzeiService muzeiService = ComponentFactory.getMuzeiService();
        return isMyCollection(collection)
                || isCurate(collection)
                || (muzeiService.isMuzeiInstalled(c)
                && muzeiService.getSource(c).equals(MuzeiService.SOURCE_COLLECTION));
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
        if (listener != null) {
            int i = view.getId();
            if (i == R.id.popup_collection_menu_edit) {
                listener.onSelectItem(ITEM_EDIT);
            } else if (i == R.id.popup_collection_menu_download) {
                listener.onSelectItem(ITEM_DOWNLOAD);
            } else if (i == R.id.popup_collection_menu_setAsSource) {
                listener.onSelectItem(ITEM_SET_AS_SOURCE);
            } else if (i == R.id.popup_collection_menu_removeSource) {
                listener.onSelectItem(ITEM_REMOVE_SOURCE);
            }
        }
        dismiss();
    }
}
