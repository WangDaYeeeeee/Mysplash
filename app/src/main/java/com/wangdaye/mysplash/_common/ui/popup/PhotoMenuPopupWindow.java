package com.wangdaye.mysplash._common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

/**
 * Photo menu popup window.
 * */

public class PhotoMenuPopupWindow extends PopupWindow
        implements View.OnClickListener {
    // widget
    private OnSelectItemListener listener;

    // data
    public static final int ITEM_STATS = 1;
    public static final int ITEM_BROWSER = 2;
    public static final int ITEM_DOWNLOAD_PAGE = 3;

    /** <br> life cycle. */

    public PhotoMenuPopupWindow(Context c, View anchor) {
        super(c);
        this.initialize(c, anchor);
        Mysplash.getInstance().setActivityInBackstage(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Mysplash.getInstance().setActivityInBackstage(false);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_photo_menu, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initWidget();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
        showAsDropDown(anchor, 0, 0, Gravity.CENTER);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_photo_menu_stats).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_browser).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_menu_downloadPage).setOnClickListener(this);

        TextView allTxt = (TextView) v.findViewById(R.id.popup_photo_menu_statsTxt);
        TypefaceUtils.setTypeface(v.getContext(), allTxt);

        TextView curatedTxt = (TextView) v.findViewById(R.id.popup_photo_menu_browserTxt);
        TypefaceUtils.setTypeface(v.getContext(), curatedTxt);

        TextView featuredTxt = (TextView) v.findViewById(R.id.popup_photo_menu_downloadPageTxt);
        TypefaceUtils.setTypeface(v.getContext(), featuredTxt);

        if (ThemeUtils.getInstance(v.getContext()).isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_photo_menu_statsIcon)).setImageResource(R.drawable.ic_stats_light);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_browserIcon)).setImageResource(R.drawable.ic_earth_light);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon)).setImageResource(R.drawable.ic_image_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_photo_menu_statsIcon)).setImageResource(R.drawable.ic_stats_dark);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_browserIcon)).setImageResource(R.drawable.ic_earth_dark);
            ((ImageView) v.findViewById(R.id.popup_photo_menu_downloadPageIcon)).setImageResource(R.drawable.ic_image_dark);
        }
    }

    /** <br> interface. */

    public interface OnSelectItemListener {
        void onSelectItem(int id);
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

            case R.id.popup_photo_menu_browser:
                if (listener != null) {
                    listener.onSelectItem(ITEM_BROWSER);
                }
                break;

            case R.id.popup_photo_menu_downloadPage:
                if (listener != null) {
                    listener.onSelectItem(ITEM_DOWNLOAD_PAGE);
                }
                break;
        }
        dismiss();
    }
}
