package com.wangdaye.mysplash._common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashPopupWindow;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Search category popup window.
 * */

public class SearchCategoryPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {
    // widget
    private OnSearchCategoryChangedListener listener;

    // data
    private int valueNow;
    private boolean showAll = true;

    /** <br> life cycle. */

    public SearchCategoryPopupWindow(Context c, View anchor, int valueNow, boolean showAll) {
        super(c);
        this.initialize(c, anchor, valueNow, showAll);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, int valueNow, boolean showAll) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_search_category, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initData(valueNow, showAll);
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

        v.findViewById(R.id.popup_search_category_all).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_buildings).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_food_drink).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_nature).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_object).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_people).setOnClickListener(this);
        v.findViewById(R.id.popup_search_category_technology).setOnClickListener(this);

        TextView allTxt = (TextView) v.findViewById(R.id.popup_search_category_allTxt);
        DisplayUtils.setTypeface(v.getContext(), allTxt);
        allTxt.setText(v.getContext().getText(R.string.all));
        if (valueNow == 0) {
            if (Mysplash.getInstance().isLightTheme()) {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView buildingsTxt = (TextView) v.findViewById(R.id.popup_search_category_buildingsTxt);
        DisplayUtils.setTypeface(v.getContext(), buildingsTxt);
        if (valueNow == Mysplash.CATEGORY_BUILDINGS_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                buildingsTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                buildingsTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView foodDrinkTxt = (TextView) v.findViewById(R.id.popup_search_category_food_drinkTxt);
        DisplayUtils.setTypeface(v.getContext(), foodDrinkTxt);
        if (valueNow == Mysplash.CATEGORY_FOOD_DRINK_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                foodDrinkTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                foodDrinkTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView naturedTxt = (TextView) v.findViewById(R.id.popup_search_category_natureTxt);
        DisplayUtils.setTypeface(v.getContext(), naturedTxt);
        if (valueNow == Mysplash.CATEGORY_NATURE_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                naturedTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                naturedTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView objectTxt = (TextView) v.findViewById(R.id.popup_search_category_objectTxt);
        DisplayUtils.setTypeface(v.getContext(), objectTxt);
        if (valueNow == Mysplash.CATEGORY_OBJECTS_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                objectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                objectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView peopleTxt = (TextView) v.findViewById(R.id.popup_search_category_peopleTxt);
        DisplayUtils.setTypeface(v.getContext(), peopleTxt);
        if (valueNow == Mysplash.CATEGORY_PEOPLE_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                peopleTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                peopleTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView technologyTxt = (TextView) v.findViewById(R.id.popup_search_category_technologyTxt);
        DisplayUtils.setTypeface(v.getContext(), technologyTxt);
        if (valueNow == Mysplash.CATEGORY_TECHNOLOGY_ID) {
            if (Mysplash.getInstance().isLightTheme()) {
                technologyTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                technologyTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_search_category_allIcon))
                    .setImageResource(R.drawable.ic_infinity_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_duck_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_light);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_search_category_allIcon))
                    .setImageResource(R.drawable.ic_infinity_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_buildingsIcon))
                    .setImageResource(R.drawable.ic_building_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_food_drinkIcon))
                    .setImageResource(R.drawable.ic_drink_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_natureIcon))
                    .setImageResource(R.drawable.ic_duck_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_objectIcon))
                    .setImageResource(R.drawable.ic_cube_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_peopleIcon))
                    .setImageResource(R.drawable.ic_face_dark);
            ((ImageView) v.findViewById(R.id.popup_search_category_technologyIcon))
                    .setImageResource(R.drawable.ic_technology_dark);
        }

        if (showAll) {
            v.findViewById(R.id.popup_search_category_all).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.popup_search_category_all).setVisibility(View.GONE);
        }
    }

    /** <br> data. */

    private void initData(int valueNow, boolean showAll) {
        this.valueNow = valueNow;
        this.showAll = showAll;
    }

    /** <br> interface. */

    public interface OnSearchCategoryChangedListener {
        void onSearchCategoryChanged(int categoryId);
    }

    public void setOnSearchCategoryChangedListener(OnSearchCategoryChangedListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        int newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_search_category_all:
                newValue = 0;
                break;

            case R.id.popup_search_category_buildings:
                newValue = Mysplash.CATEGORY_BUILDINGS_ID;
                break;

            case R.id.popup_search_category_food_drink:
                newValue = Mysplash.CATEGORY_FOOD_DRINK_ID;
                break;

            case R.id.popup_search_category_nature:
                newValue = Mysplash.CATEGORY_NATURE_ID;
                break;

            case R.id.popup_search_category_object:
                newValue = Mysplash.CATEGORY_OBJECTS_ID;
                break;

            case R.id.popup_search_category_people:
                newValue = Mysplash.CATEGORY_PEOPLE_ID;
                break;

            case R.id.popup_search_category_technology:
                newValue = Mysplash.CATEGORY_TECHNOLOGY_ID;
                break;
        }

        if (newValue != valueNow && listener != null) {
            listener.onSearchCategoryChanged(newValue);
            dismiss();
        }
    }
}
